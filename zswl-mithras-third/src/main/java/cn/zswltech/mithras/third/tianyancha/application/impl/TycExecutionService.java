package cn.zswltech.mithras.third.tianyancha.application.impl;

import cn.zswltech.mithras.client.externaldata.tianyancha.infrastructure.model.*;
import cn.zswltech.mithras.service.constant.ResultMsg;
import cn.zswltech.mithras.service.enums.client.ClientType;
import cn.zswltech.mithras.service.mapper.client.ClientMapper;
import cn.zswltech.mithras.service.mapper.model.client.Client;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.third.tianyancha.application.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * 天眼查 汇总处理
 *
 * @author wangchuanhao
 * @date 2022/6/21 3:36 PM
 */
@Service
@Slf4j
public class TycExecutionService {

    @Resource
    private ClientMapper clientMapper;
    @Resource
    private TycAbnormalService tycAbnormalService;
    @Resource
    private TycConsumptionRestrictionService tycConsumptionRestrictionService;
    @Resource
    private TycDishonestService tycDishonestService;
    @Resource
    private TycEquityInfoService tycEquityInfoService;
    @Resource
    private TycJudicialService tycJudicialService;
    @Resource
    private TycLawSuitService tycLawSuitService;
    @Resource
    private TycMortgageInfoService tycMortgageInfoService;
    @Resource
    private TycPunishmentInfoService tycPunishmentInfoService;
    @Resource
    private TycZhixingInfoService tycZhixingInfoService;
    @Resource
    @Qualifier("tycThreadPool")
    private ThreadPoolTaskExecutor threadPoolTaskExecutor;

    /**
     * 同步外部信息
     */
    @Transactional(rollbackFor = Throwable.class)
    public void syncExternal(Long clientId) {

        Client client = clientMapper.selectById(clientId);
        if (Objects.isNull(client)) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        if (!ClientType.CORPORATION.name().equals(client.getClientType())) {
            // 只有法人能从天眼查同步到数据
            throw new MithrasException("只允许法人客户同步公开信息数据");
        }
        String clientName = client.getClientName();
        String uscCode = client.getUscCode();

        CompletableFuture<List<TycAbnormal>> abnormalFuture = CompletableFuture.supplyAsync(() -> tycAbnormalService.queryAllFromTyc(clientId, clientName, uscCode), threadPoolTaskExecutor);
        CompletableFuture<List<TycConsumptionRestriction>> consumptionRestrictionFuture = CompletableFuture.supplyAsync(() -> tycConsumptionRestrictionService.queryAllFromTyc(clientId, clientName, uscCode), threadPoolTaskExecutor);
        CompletableFuture<List<TycDishonest>> dishonestFuture = CompletableFuture.supplyAsync(() -> tycDishonestService.queryAllFromTyc(clientId, clientName, uscCode), threadPoolTaskExecutor);
        CompletableFuture<List<TycEquityInfo>> equityInfoFuture = CompletableFuture.supplyAsync(() -> tycEquityInfoService.queryAllFromTyc(clientId, clientName, uscCode), threadPoolTaskExecutor);
        CompletableFuture<List<TycJudicial>> judicialFuture = CompletableFuture.supplyAsync(() -> tycJudicialService.queryAllFromTyc(clientId, clientName, uscCode), threadPoolTaskExecutor);
        CompletableFuture<List<TycMortgageInfo>> mortgageInfoFuture = CompletableFuture.supplyAsync(() -> tycMortgageInfoService.queryAllFromTyc(clientId, clientName, uscCode), threadPoolTaskExecutor);
        CompletableFuture<List<TycPunishmentInfo>> punishmentInfoFuture = CompletableFuture.supplyAsync(() -> tycPunishmentInfoService.queryAllFromTyc(clientId, clientName, uscCode), threadPoolTaskExecutor);
        CompletableFuture<List<TycZhixingInfo>> zhixingInfoFuture = CompletableFuture.supplyAsync(() -> tycZhixingInfoService.queryAllFromTyc(clientId, clientName, uscCode), threadPoolTaskExecutor);
        CompletableFuture<List<TycLawSuit>> lawSuitFuture = CompletableFuture.supplyAsync(() -> tycLawSuitService.queryAllFromTyc(clientId, clientName, uscCode), threadPoolTaskExecutor).thenApply(lawSuitList -> {
            List<CompletableFuture<Void>> lawSuitDetailFutureList = new ArrayList<>();
            for (TycLawSuit lawSuit : lawSuitList) {
                CompletableFuture<Void> lawSuitDetailFuture = CompletableFuture.runAsync(() -> tycLawSuitService.fillDetail(lawSuit), threadPoolTaskExecutor);
                lawSuitDetailFutureList.add(lawSuitDetailFuture);
            }
            for (CompletableFuture<Void> lawSuitDetailFuture : lawSuitDetailFutureList) {
                lawSuitDetailFuture.join();
            }
            return lawSuitList;
        });

        List<TycAbnormal> abnormalList = null;
        List<TycConsumptionRestriction> consumptionRestrictioList = null;
        List<TycDishonest> dishonestList = null;
        List<TycEquityInfo> equityInfoList = null;
        List<TycJudicial> judicialList = null;
        List<TycLawSuit> lawSuitList = null;
        List<TycMortgageInfo> mortgageInfoList = null;
        List<TycPunishmentInfo> punishmentInfoList = null;
        List<TycZhixingInfo> zhixingInfoList = null;
        try {
            abnormalList = abnormalFuture.get();
            consumptionRestrictioList = consumptionRestrictionFuture.get();
            dishonestList = dishonestFuture.get();
            equityInfoList = equityInfoFuture.get();
            judicialList = judicialFuture.get();
            lawSuitList = lawSuitFuture.get();
            mortgageInfoList = mortgageInfoFuture.get();
            punishmentInfoList = punishmentInfoFuture.get();
            zhixingInfoList = zhixingInfoFuture.get();
        } catch (MithrasException e) {
            throw e;
        } catch (Exception e) {
            log.error("天眼查同步客户数据失败", e);
            throw new MithrasException("天眼查接口调用失败");
        }

        tycAbnormalService.flushData(clientId, abnormalList);
        tycConsumptionRestrictionService.flushData(clientId, consumptionRestrictioList);
        tycDishonestService.flushData(clientId, dishonestList);
        tycEquityInfoService.flushData(clientId, equityInfoList);
        tycJudicialService.flushData(clientId, judicialList);
        tycLawSuitService.flushData(clientId, lawSuitList);
        tycMortgageInfoService.flushData(clientId, mortgageInfoList);
        tycPunishmentInfoService.flushData(clientId, punishmentInfoList);
        tycZhixingInfoService.flushData(clientId, zhixingInfoList);

    }

}
