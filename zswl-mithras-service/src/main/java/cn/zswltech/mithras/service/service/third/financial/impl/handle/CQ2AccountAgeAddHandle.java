package cn.zswltech.mithras.service.service.third.financial.impl.handle;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.service.enums.third.FinancialAccountAgeSendStatusStatus;
import cn.zswltech.mithras.third.enums.FinancialDevUrlENUM;
import cn.zswltech.mithras.third.enums.FinancialUrlENUM;
import cn.zswltech.mithras.third.mapper.model.ExceptionRequestInfo;
import cn.zswltech.mithras.service.mapper.model.finance.FinanceAccountAgeItem;
import cn.zswltech.mithras.service.repository.PlatformApiEnum;
import cn.zswltech.mithras.third.service.ExceptionRequestRecordService;
import cn.zswltech.mithras.service.service.finance.FinanceAccountAgeItemService;
import cn.zswltech.mithras.third.financialshare.infrastructure.client.FinancialApiHandler;
import cn.zswltech.mithras.third.financialshare.application.FinancialConfigService;
import cn.zswltech.mithras.third.financialshare.infrastructure.client.req.CQ2AccountAgeAddREQ;
import cn.zswltech.mithras.third.financialshare.infrastructure.client.resp.CQ2AccountAgeRSP;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @ClassName PaymentHandle
 * @Description 苍穹应付单
 * @Author jackerhe
 * @Date 2022/10/26 2:43 下午
 * @Version 1.0
 **/
@Component
public class CQ2AccountAgeAddHandle extends FinancialApiHandler<List<CQ2AccountAgeAddREQ>, CQ2AccountAgeRSP> {

    @Resource
    private FinancialConfigService financialConfigService;

    @Resource
    private ExceptionRequestRecordService exceptionRequestInfoService;

    @Resource
    private FinanceAccountAgeItemService financeAccountAgeItemService;

    private static final ThreadPoolExecutor threadPool = new ThreadPoolExecutor(Runtime.getRuntime().availableProcessors(), Runtime.getRuntime().availableProcessors() + 1, 60, TimeUnit.SECONDS, new LinkedBlockingQueue<>(100));

    @Override
    public PlatformApiEnum platformApi() {
        return PlatformApiEnum.CQ2_ACCOUNT_AGE_ADD;
    }

    @Override
    public String getUrl() {
        //测试环境
        if (!PROD.equals(active)) {
            return financialConfigService.getUrl(FinancialDevUrlENUM.CQ2_ACCOUNT_AGE_ADD.url);
        }
        return financialConfigService.getUrl(FinancialUrlENUM.CQ2_ACCOUNT_AGE_ADD.url);
    }

    @Override
    public CQ2AccountAgeRSP analyResponseResult(String response) {
        return JSONObject.parseObject(response, CQ2AccountAgeRSP.class);
    }

    @Override
    public CQ2AccountAgeRSP execute(List<CQ2AccountAgeAddREQ> reqData) {
        CQ2AccountAgeRSP execute = null;
        try {
            execute = super.execute(reqData);
        } catch (Exception e) {
            log.warn("CQ2AccountAgeAddHandle execute error", e);
        } finally {
            Set<String> successSet = new HashSet<>();
            Map<Long, String> id2Key = reqData.stream().collect(Collectors.toMap(CQ2AccountAgeAddREQ::getBusinessId, CQ2AccountAgeAddREQ::getBusinessKey, (a, b) -> a));
            Map<Long, String> id2Number = new HashMap<>();
            if (execute != null && ObjectUtil.isNotEmpty(execute.getData()) && ObjectUtil.isNotEmpty(execute.getData().getResult())) {
                execute.getData().getResult().forEach(r -> {
                    if(r.isBillStatus()) {
                        successSet.add(id2Key.get(r.getId()));
                        id2Number.put(Long.parseLong(id2Key.get(r.getId())), r.getNumber());
                    }
                });
            }
            List<Long> failList = reqData.stream().map(CQ2AccountAgeAddREQ::getBusinessKey).filter(ObjectUtil::isNotEmpty).filter(e -> !successSet.contains(e)).map(Long::parseLong).collect(Collectors.toList());
            if (!successSet.isEmpty()) {
                List<FinanceAccountAgeItem> financeAccountAgeItems = financeAccountAgeItemService.listByIds(successSet.stream().map(Long::parseLong).collect(Collectors.toList()));
                if (ObjectUtil.isNotEmpty(financeAccountAgeItems)) {
                    financeAccountAgeItems.forEach(item -> {
                        item.setCqNumber(id2Number.get(item.getId()));
                        item.setSendStatus(FinancialAccountAgeSendStatusStatus.SUCCESS.name());
                    });
                }
                financeAccountAgeItemService.updateBatchById(financeAccountAgeItems);
            }
            if (ObjectUtil.isNotEmpty(failList)) {
                LambdaUpdateWrapper<FinanceAccountAgeItem> updateWrapper = new LambdaUpdateWrapper<>();
                updateWrapper.in(FinanceAccountAgeItem::getId, failList);
                updateWrapper.set(FinanceAccountAgeItem::getSendStatus, FinancialAccountAgeSendStatusStatus.FAIL.name());
                financeAccountAgeItemService.update(updateWrapper);
            }
        }
        return execute;
    }

    @Override
    public Map<String, String> getHttpHeadParam() {
        return financialConfigService.getHttpHeadParam(FinancialUrlENUM.CQ2_ACCOUNT_AGE_ADD);
    }

    @Override
    public void cqRelatedMithras(CQ2AccountAgeRSP result){
        //todo 这里目前没有明确的关系要对应
    }

    @Override
    public void failureRetry(String platform) {
        List<ExceptionRequestInfo> exceptionRequestInfos = exceptionRequestInfoService.listNeedRetry(platform);
        if (ObjectUtil.isEmpty(exceptionRequestInfos)) {
            return;
        }
        exceptionRequestInfos.forEach(base -> CompletableFuture.runAsync(()->execute(JSON.parseArray(base.getReqData(), CQ2AccountAgeAddREQ.class)),
                threadPool));
    }

    @Override
    public boolean isFailureSave() {
        return true;
    }

    @Override
    public String getBusinessId(List<CQ2AccountAgeAddREQ> reqData){
        if(ObjectUtil.isEmpty(reqData)){
            return null;
        }
        return String.valueOf(reqData.get(0).getCico_bussinessno());
    }

    @Override
    public ExceptionRequestInfo getExceptionBase(List<CQ2AccountAgeAddREQ> reqData) {
        ExceptionRequestInfo exceptionRequestInfo = null;
        if(CollectionUtil.isNotEmpty(reqData)) {
            exceptionRequestInfo = new ExceptionRequestInfo();
            CQ2AccountAgeAddREQ req = reqData.get(0);
            exceptionRequestInfo.setSource(req.getSource());
            exceptionRequestInfo.setBusinessKey(req.getBusinessKey());
            exceptionRequestInfo.setBusinessTitle(req.getBusinessTitle());
        }
        return exceptionRequestInfo;
    }

    @Override
    public List<CQ2AccountAgeAddREQ> getReqFromString(String reqString) {
        if (ObjectUtil.isEmpty(reqString)) {
            return Collections.emptyList();
        }
        return JSON.parseArray(reqString, CQ2AccountAgeAddREQ.class);
    }

}
