package cn.zswltech.mithras.third.financialshare.client.handle;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.third.financialshare.enums.CQRevenueItemENUM;
import cn.zswltech.mithras.third.financialshare.enums.FinancialDevUrlENUM;
import cn.zswltech.mithras.third.financialshare.enums.FinancialUrlENUM;
import cn.zswltech.mithras.third.retry.persistence.model.ExceptionRequestInfo;
import cn.zswltech.mithras.foundation.thirdparty.PlatformApiEnum;
import cn.zswltech.mithras.third.retry.application.ExceptionRequestRecordService;
import cn.zswltech.mithras.third.financialshare.client.FinancialApiHandler;
import cn.zswltech.mithras.third.financialshare.application.FinancialConfigService;
import cn.zswltech.mithras.third.financialshare.client.req.CQ2PlanCollectionReq;
import cn.zswltech.mithras.third.financialshare.client.resp.CQ2FinancialBaseRSP;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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
public class CQ2PlanCollectionHandle extends FinancialApiHandler<List<CQ2PlanCollectionReq>, CQ2FinancialBaseRSP> {

    @Resource
    private FinancialConfigService financialConfigService;

    @Resource
    private ExceptionRequestRecordService exceptionRequestInfoService;

    private static final ThreadPoolExecutor threadPool = new ThreadPoolExecutor(Runtime.getRuntime().availableProcessors(), Runtime.getRuntime().availableProcessors() + 1, 60, TimeUnit.SECONDS, new LinkedBlockingQueue<>(100));

    @Override
    public PlatformApiEnum platformApi() {
        return PlatformApiEnum.CQ2_PLAN_COLLECTION;
    }

    @Override
    public String getUrl() {
        //测试环境
        if (!PROD.equals(active)) {
            return financialConfigService.getUrl(FinancialDevUrlENUM.CQ2_PLAN_COLLECTION.url);
        }
        return financialConfigService.getUrl(FinancialUrlENUM.CQ2_PLAN_COLLECTION.url);
    }

    @Override
    public CQ2FinancialBaseRSP analyResponseResult(String response) {
        return JSONObject.parseObject(response, CQ2FinancialBaseRSP.class);
    }

    @Override
    public CQ2FinancialBaseRSP execute(List<CQ2PlanCollectionReq> reqData) {
       return super.execute(reqData);
    }

    @Override
    public Map<String, String> getHttpHeadParam() {
        return financialConfigService.getHttpHeadParam(FinancialUrlENUM.CQ2_PLAN_COLLECTION);
    }

    @Override
    public void cqRelatedMithras(CQ2FinancialBaseRSP result){
        //todo 这里目前没有明确的关系要对应
        //financialConfigService.cqRelatedMithras(result, RecordSourceEnum.PAYMENT.name());
    }

    @Override
    public void failureRetry(String platform) {
        List<ExceptionRequestInfo> exceptionRequestInfos = exceptionRequestInfoService.listNeedRetry(platform);
        if (ObjectUtil.isEmpty(exceptionRequestInfos)) {
            return;
        }
        exceptionRequestInfos.forEach(base -> CompletableFuture.runAsync(()->execute(JSON.parseArray(base.getReqData(), CQ2PlanCollectionReq.class)),
                threadPool));
    }

    @Override
    public boolean isFailureSave() {
        return true;
    }

    @Override
    public String getBusinessId(List<CQ2PlanCollectionReq> reqData){
        if(ObjectUtil.isEmpty(reqData)){
            return null;
        }
        //return reqData.get(0).getCico_paynum_rby();
        return reqData.get(0).getCico_srcbillno();
    }

    @Override
    public ExceptionRequestInfo getExceptionBase(List<CQ2PlanCollectionReq> reqData) {
        ExceptionRequestInfo exceptionRequestInfo = null;
        if(CollectionUtil.isNotEmpty(reqData)) {
            exceptionRequestInfo = new ExceptionRequestInfo();
            CQ2PlanCollectionReq cq2PlanCollectionReq = reqData.get(0);
            exceptionRequestInfo.setSource(cq2PlanCollectionReq.getSource());
            exceptionRequestInfo.setBusinessKey(cq2PlanCollectionReq.getBusinessKey());
            exceptionRequestInfo.setBusinessTitle(cq2PlanCollectionReq.getBusinessTitle());
        }
        return exceptionRequestInfo;
    }

    @Override
    public List<CQ2PlanCollectionReq> getReqFromString(String reqString) {
        if (ObjectUtil.isEmpty(reqString)) {
            return Collections.emptyList();
        }
        return JSON.parseArray(reqString, CQ2PlanCollectionReq.class);
    }

    @Override
    public String getSituationDescription(String reqString) {
        List<CQ2PlanCollectionReq> reqFromString = getReqFromString(reqString);
        if (ObjectUtil.isEmpty(reqFromString) || ObjectUtil.isEmpty(reqFromString.get(0).getEntry())) {
            return null;
        }
        List<CQ2PlanCollectionReq.CQCollectionBody> entry = reqFromString.get(0).getEntry();
        return Optional.ofNullable(CQRevenueItemENUM.of(entry.get(0).getCico_incomeitems_number())).map(CQRevenueItemENUM::getDescription).orElse(null);
    }

    @Override
    public List<String> getBillNo(String reqString){
        List<CQ2PlanCollectionReq> reqFromString = getReqFromString(reqString);
        if (ObjectUtil.isEmpty(reqFromString)) {
            return Collections.emptyList();
        }
        return reqFromString.stream().map(CQ2PlanCollectionReq::getCico_srcbillno).collect(Collectors.toList());
    }

}
