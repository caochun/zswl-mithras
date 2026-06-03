package cn.zswltech.mithras.third.financialshare.infrastructure.client.handle;

import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.service.enums.YesOrNoNumberEnum;
import cn.zswltech.mithras.third.enums.FinancialDevUrlENUM;
import cn.zswltech.mithras.third.enums.FinancialRSPENUM;
import cn.zswltech.mithras.third.enums.FinancialUrlENUM;
import cn.zswltech.mithras.third.mapper.model.ExceptionRequestInfo;
import cn.zswltech.mithras.third.mapper.model.SyncCqRecord;
import cn.zswltech.mithras.service.repository.PlatformApiEnum;
import cn.zswltech.mithras.third.service.ExceptionRequestRecordService;
import cn.zswltech.mithras.third.financialshare.infrastructure.client.FinancialApiHandler;
import cn.zswltech.mithras.third.financialshare.application.FinancialConfigService;
import cn.zswltech.mithras.third.financialshare.application.SyncCqRecordService;
import cn.zswltech.mithras.third.financialshare.infrastructure.client.req.CQReceiveRentREQ;
import cn.zswltech.mithras.third.financialshare.infrastructure.client.resp.FinancialCommonRSP;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static cn.zswltech.mithras.service.others.SpringContextHolder.getBean;

/**
 * @ClassName ReceiveHandle
 * @Description 苍穹应收单
 * @Author jackerhe
 * @Date 2022/10/26 2:43 下午
 * @Version 1.0
 **/
@Component
public class ReceiveRentHandle extends FinancialApiHandler<List<CQReceiveRentREQ>, FinancialCommonRSP> {

    @Resource
    private FinancialConfigService financialConfigService;

    @Resource
    private ExceptionRequestRecordService exceptionRequestInfoService;

    @Resource
    private SyncCqRecordService syncCqRecordService;

    private static final ThreadPoolExecutor threadPool = new ThreadPoolExecutor(Runtime.getRuntime().availableProcessors(), Runtime.getRuntime().availableProcessors() + 1, 60, TimeUnit.SECONDS, new LinkedBlockingQueue<>(100));

    @Override
    public PlatformApiEnum platformApi() {
        return PlatformApiEnum.CQ_RENT_RECEIVE;
    }

    @Override
    public String getUrl() {
        //测试环境
        if (!PROD.equals(active)) {
            return financialConfigService.getUrl(FinancialDevUrlENUM.RECEIVVER_RENT_INFO.url);
        }
        return financialConfigService.getUrl(FinancialUrlENUM.RECEIVVER_RENT_INFO.url);
    }

    @Override
    public FinancialCommonRSP analyResponseResult(String response) {
        return JSONObject.parseObject(response, FinancialCommonRSP.class);
    }

    @Override
    public FinancialCommonRSP execute(List<CQReceiveRentREQ> reqData) {
        //保存结果
        ExceptionRequestInfo exceptionRequestInfo = respSave(getRetryCount(), reqData, getBusinessId(reqData));
        syncCqRecord(reqData);
        exceptionRequestInfo.setRetryFlag(1);
        getBean(ExceptionRequestRecordService.class).updateById(exceptionRequestInfo);
        return new FinancialCommonRSP();
    }

    @Override
    public Map<String, String> getHttpHeadParam() {
        return financialConfigService.getHttpHeadParam(FinancialUrlENUM.RECEIVVER_RENT_INFO);
    }

    @Override
    public void cqRelatedMithras(FinancialCommonRSP result){
        financialConfigService.cqRelatedMithras(result, "COLLECTION_RENT");
    }

    @Override
    public void failureRetry(String platform) {
        List<ExceptionRequestInfo> exceptionRequestInfos = exceptionRequestInfoService.listNeedRetry(platform);
        if (ObjectUtil.isEmpty(exceptionRequestInfos)) {
            return;
        }
        exceptionRequestInfos.forEach(base -> CompletableFuture.runAsync(()->execute(JSON.parseArray(base.getReqData(), CQReceiveRentREQ.class)), threadPool));
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void syncCqRecord(List<CQReceiveRentREQ> reqData) {
        List<String> contractCodes = reqData.stream().map(CQReceiveRentREQ::getContractCode).collect(Collectors.toList());
        Set<String> syncSet = syncCqRecordService.list(Wrappers.<SyncCqRecord>lambdaQuery()
                .in(ObjectUtil.isNotEmpty(contractCodes), SyncCqRecord::getContractCode, contractCodes)).stream().map(SyncCqRecord::getRecordId).collect(Collectors.toSet());
        List<SyncCqRecord> addList = new ArrayList<>();
        reqData.forEach(req -> {
            req.getEntry().forEach(paymentBody -> {
                //新增保存
                if (ObjectUtil.isNotEmpty(paymentBody) && ObjectUtil.equal(YesOrNoNumberEnum.NO.getCode(), paymentBody.getBilling()) && !syncSet.contains(paymentBody.getRentActualid())) {
                    addList.add(new SyncCqRecord()
                            .setContractCode(req.getContractCode())
                            .setSourcebillno(req.getSourcebillno())
                            .setRecordId(paymentBody.getRentActualid())
                            .setRentActualCode(paymentBody.getRentActualCode())
                            .setLeaseRate(String.valueOf(paymentBody.getLeaseRate()))
                            .setCico_isinvoice(paymentBody.getCico_isinvoice())
                            .setDate(paymentBody.getDate())
                            .setPhase(paymentBody.getPhase())
                            .setRent(String.valueOf(paymentBody.getRent()))
                            .setPrincipal(String.valueOf(paymentBody.getPrincipal()))
                            .setInterest(String.valueOf(paymentBody.getInterest()))
                            .setLastAmount(String.valueOf(paymentBody.getLastAmount().toString()))
                            .setChangeState(String.valueOf(paymentBody.getChangeState())));
                }
            });
        });
        if (ObjectUtil.isNotEmpty(addList)) {
            syncCqRecordService.saveBatch(addList);
        }
    }

    @Override
    public boolean isFailureSave() {
        return true;
    }

    @Override
    public String getBusinessId(List<CQReceiveRentREQ> reqData){
        if(ObjectUtil.isEmpty(reqData)){
            return null;
        }
        return reqData.get(0).getSourcebillno();
    }

    @Override
    public boolean isExecuteSuccess(FinancialCommonRSP resData) {
        // 权限验证成功
        if (ObjectUtil.isNotEmpty(resData)) {
            if ((FinancialRSPENUM.SUCCESS.getResult().equals(resData.getState()) || resData.getSuccess()) && ObjectUtil.isNotEmpty(resData.getData())) {
                List<FinancialCommonRSP.FinancialRSPBody> dataList = resData.getData();
                for (FinancialCommonRSP.FinancialRSPBody data : dataList) {
                    if (!data.getSuccess()) {
                        log.error("ReceiveRentHandle isExecuteSuccess result error {}", data);
                        return false;
                    }
                }
                return Boolean.TRUE;
            } else {
                log.info("FinancialApiHandler {} execute error errorCode : {} message : {}", platformApi().apiName, resData.getErrorCode(), resData.getMessage());
            }
        }
        return Boolean.FALSE;
    }
}
