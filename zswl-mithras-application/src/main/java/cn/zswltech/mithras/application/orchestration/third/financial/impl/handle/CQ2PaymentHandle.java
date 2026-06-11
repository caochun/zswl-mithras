package cn.zswltech.mithras.application.orchestration.third.financial.impl.handle;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.foundation.enums.YesOrNoNumberEnum;
import cn.zswltech.mithras.third.financialshare.enums.FinancialDevUrlENUM;
import cn.zswltech.mithras.third.financialshare.enums.FinancialUrlENUM;
import cn.zswltech.mithras.third.retry.model.ExceptionRequestInfo;
import cn.zswltech.mithras.foundation.context.SpringContextHolder;
import cn.zswltech.mithras.foundation.thirdparty.PlatformApiEnum;
import cn.zswltech.mithras.third.retry.application.ExceptionRequestRecordService;
import cn.zswltech.mithras.application.orchestration.third.FinanceFlowRecordService;
import cn.zswltech.mithras.third.financialshare.client.FinancialApiHandler;
import cn.zswltech.mithras.third.financialshare.application.FinancialConfigService;
import cn.zswltech.mithras.third.financialshare.client.req.CQ2PaymentReq;
import cn.zswltech.mithras.third.financialshare.client.resp.CQ2FinancialBaseRSP;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
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
public class CQ2PaymentHandle extends FinancialApiHandler<List<CQ2PaymentReq>, CQ2FinancialBaseRSP> {

    @Resource
    private FinancialConfigService financialConfigService;

    @Resource
    private ExceptionRequestRecordService exceptionRequestInfoService;

    private static final ThreadPoolExecutor threadPool = new ThreadPoolExecutor(Runtime.getRuntime().availableProcessors(), Runtime.getRuntime().availableProcessors() + 1, 60, TimeUnit.SECONDS, new LinkedBlockingQueue<>(100));

    @Override
    public PlatformApiEnum platformApi() {
        return PlatformApiEnum.CQ2_PAYMENT;
    }

    @Override
    public String getUrl() {
        //测试环境
        if (!PROD.equals(active)) {
            return financialConfigService.getUrl(FinancialDevUrlENUM.CQ2_PAYMENT.url);
        }
        return financialConfigService.getUrl(FinancialUrlENUM.CQ2_PAYMENT.url);
    }

    @Override
    public CQ2FinancialBaseRSP analyResponseResult(String response) {
        return JSONObject.parseObject(response, CQ2FinancialBaseRSP.class);
    }

    @Override
    public CQ2FinancialBaseRSP execute(List<CQ2PaymentReq> reqData) {
        CQ2FinancialBaseRSP execute = super.execute(reqData);
        //回调业务
        if (isExecuteSuccess(execute)) {
            List<String> billnos = new ArrayList<>();
            reqData.forEach(vo -> {
                if (ObjectUtil.isNotEmpty(vo) && ObjectUtil.isNotEmpty(vo.getEntry())) {
                    vo.getEntry().forEach(e -> {
                        if (ObjectUtil.isNotEmpty(e.getCico_uniquecode())) {
                            billnos.add(e.getCico_uniquecode());
                        }
                    });
                }
            });
            if (!billnos.isEmpty()) {
                SpringContextHolder.getBean(FinanceFlowRecordService.class).modifySendFlag(billnos, YesOrNoNumberEnum.YES);
            }
        }
        return execute;
    }

    @Override
    public Map<String, String> getHttpHeadParam() {
        return financialConfigService.getHttpHeadParam(FinancialUrlENUM.CQ2_PAYMENT);
    }

    @Override
    public void cqRelatedMithras(CQ2FinancialBaseRSP result){
        //todo 这里目前没有明确的关系要对应
        //financialConfigService.cqRelatedMithras(result, RecordSourceEnum.CQ2_PAYMENT.name());
    }

    @Override
    public void failureRetry(String platform) {
        List<ExceptionRequestInfo> exceptionRequestInfos = exceptionRequestInfoService.listNeedRetry(platform);
        if (ObjectUtil.isEmpty(exceptionRequestInfos)) {
            return;
        }
        exceptionRequestInfos.forEach(base -> CompletableFuture.runAsync(()->execute(JSON.parseArray(base.getReqData(), CQ2PaymentReq.class)),
                threadPool));
    }

    @Override
    public boolean isFailureSave() {
        return true;
    }

    @Override
    public String getBusinessId(List<CQ2PaymentReq> reqData){
        if(ObjectUtil.isEmpty(reqData)){
            return null;
        }
        return reqData.get(0).getCico_srcbillno();
    }

    @Override
    public ExceptionRequestInfo getExceptionBase(List<CQ2PaymentReq> reqData) {
        ExceptionRequestInfo exceptionRequestInfo = null;
        if(CollectionUtil.isNotEmpty(reqData)) {
            exceptionRequestInfo = new ExceptionRequestInfo();
            CQ2PaymentReq req = reqData.get(0);
            exceptionRequestInfo.setSource(req.getSource());
            exceptionRequestInfo.setBusinessKey(req.getBusinessKey());
            exceptionRequestInfo.setBusinessTitle(req.getBusinessTitle());
        }
        return exceptionRequestInfo;
    }

    @Override
    public List<CQ2PaymentReq> getReqFromString(String reqString) {
        if (ObjectUtil.isEmpty(reqString)) {
            return Collections.emptyList();
        }
        return JSON.parseArray(reqString, CQ2PaymentReq.class);
    }

    @Override
    public String getSituationDescription(String reqString) {
        List<CQ2PaymentReq> reqFromString = getReqFromString(reqString);
        if (ObjectUtil.isEmpty(reqFromString)) {
            return null;
        }
        return reqFromString.get(0).getApplycause();
    }

    @Override
    public List<String> getBillNo(String reqString){
        List<CQ2PaymentReq> reqFromString = getReqFromString(reqString);
        if (ObjectUtil.isEmpty(reqFromString)) {
            return Collections.emptyList();
        }
        return reqFromString.stream().map(CQ2PaymentReq::getCico_srcbillno).collect(Collectors.toList());
    }
}
