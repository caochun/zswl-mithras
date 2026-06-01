package cn.zswltech.mithras.service.service.third.financial.impl.handle;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.service.enums.YesOrNoNumberEnum;
import cn.zswltech.mithras.service.enums.third.CQCollectionTypeENUM;
import cn.zswltech.mithras.service.enums.third.FinancialDevUrlENUM;
import cn.zswltech.mithras.service.enums.third.FinancialUrlENUM;
import cn.zswltech.mithras.service.mapper.model.ExceptionRequestInfo;
import cn.zswltech.mithras.service.repository.PlatformApiEnum;
import cn.zswltech.mithras.service.service.ExceptionRequestRecordService;
import cn.zswltech.mithras.service.service.third.FinanceFlowRecordService;
import cn.zswltech.mithras.service.service.third.financial.FinancialApiHandler;
import cn.zswltech.mithras.service.service.third.financial.impl.FinancialConfigService;
import cn.zswltech.mithras.service.service.third.financial.req.CQ2CollectionReq;
import cn.zswltech.mithras.service.service.third.financial.resp.CQ2FinancialBaseRSP;
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
public class CQ2CollectionHandle extends FinancialApiHandler<List<CQ2CollectionReq>, CQ2FinancialBaseRSP> {

    @Resource
    private FinancialConfigService financialConfigService;

    @Resource
    private ExceptionRequestRecordService exceptionRequestInfoService;

    @Resource
    private FinanceFlowRecordService financeFlowRecordService;

    private static final ThreadPoolExecutor threadPool = new ThreadPoolExecutor(Runtime.getRuntime().availableProcessors(), Runtime.getRuntime().availableProcessors() + 1, 60, TimeUnit.SECONDS, new LinkedBlockingQueue<>(100));

    @Override
    public PlatformApiEnum platformApi() {
        return PlatformApiEnum.CQ2_COLLECTION;
    }

    @Override
    public String getUrl() {
        //测试环境
        if (!PROD.equals(active)) {
            return financialConfigService.getUrl(FinancialDevUrlENUM.CQ2_COLLECTION.url);
        }
        return financialConfigService.getUrl(FinancialUrlENUM.CQ2_COLLECTION.url);
    }

    @Override
    public CQ2FinancialBaseRSP analyResponseResult(String response) {
        return JSONObject.parseObject(response, CQ2FinancialBaseRSP.class);
    }

    @Override
    public CQ2FinancialBaseRSP execute(List<CQ2CollectionReq> reqData) {
        CQ2FinancialBaseRSP execute = super.execute(reqData);
        //回调业务
        if (isExecuteSuccess(execute)) {
            financeFlowRecordService.modifySendFlag(reqData.stream().map(CQ2CollectionReq::getSourcebillnumber).collect(Collectors.toList()), YesOrNoNumberEnum.YES);
        }
        return execute;
    }

    @Override
    public Map<String, String> getHttpHeadParam() {
        return financialConfigService.getHttpHeadParam(FinancialUrlENUM.CQ2_COLLECTION);
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
        exceptionRequestInfos.forEach(base -> CompletableFuture.runAsync(()->execute(getReqFromString(base.getReqData())),
                threadPool));
    }

    @Override
    public boolean isFailureSave() {
        return true;
    }

    @Override
    public String getBusinessId(List<CQ2CollectionReq> reqData){
        if(ObjectUtil.isEmpty(reqData)){
            return null;
        }
        return reqData.get(0).getSourcebillnumber();
    }

    @Override
    public ExceptionRequestInfo getExceptionBase(List<CQ2CollectionReq> reqData) {
        ExceptionRequestInfo exceptionRequestInfo = null;
        if (CollectionUtil.isNotEmpty(reqData)) {
            exceptionRequestInfo = new ExceptionRequestInfo();
            CQ2CollectionReq req = reqData.get(0);
            exceptionRequestInfo.setSource(req.getSource());
            exceptionRequestInfo.setBusinessKey(req.getBusinessKey());
            exceptionRequestInfo.setBusinessTitle(req.getBusinessTitle());
        }
        return exceptionRequestInfo;
    }

    @Override
    public List<CQ2CollectionReq> getReqFromString(String reqString) {
        if (ObjectUtil.isEmpty(reqString)) {
            return Collections.emptyList();
        }
        return JSON.parseArray(reqString, CQ2CollectionReq.class);
    }

    @Override
    public String getSituationDescription(String reqString) {
        List<CQ2CollectionReq> reqFromString = getReqFromString(reqString);
        if (ObjectUtil.isEmpty(reqFromString)) {
            return null;
        }
        return Optional.ofNullable(CQCollectionTypeENUM.ofCode(reqFromString.get(0).getReceivingtype_number())).map(CQCollectionTypeENUM::getPaymentType).orElse(null);
    }

    @Override
    public List<String> getBillNo(String reqString){
        List<CQ2CollectionReq> reqFromString = getReqFromString(reqString);
        if (ObjectUtil.isEmpty(reqFromString)) {
            return Collections.emptyList();
        }
        return reqFromString.stream().map(CQ2CollectionReq::getCico_srcbillno).collect(Collectors.toList());
    }
}
