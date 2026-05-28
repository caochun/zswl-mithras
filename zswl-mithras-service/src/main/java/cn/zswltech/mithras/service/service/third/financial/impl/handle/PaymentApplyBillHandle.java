package cn.zswltech.mithras.service.service.third.financial.impl.handle;

import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.service.enums.contractcp.RecordSourceEnum;
import cn.zswltech.mithras.service.enums.third.FinancialDevUrlENUM;
import cn.zswltech.mithras.service.enums.third.FinancialUrlENUM;
import cn.zswltech.mithras.service.mapper.model.ExceptionRequestInfo;
import cn.zswltech.mithras.service.repository.PlatformApiEnum;
import cn.zswltech.mithras.service.service.ExceptionRequestInfoService;
import cn.zswltech.mithras.service.service.third.financial.FinancialApiHandler;
import cn.zswltech.mithras.service.service.third.financial.impl.FinancialConfigService;
import cn.zswltech.mithras.service.service.third.financial.req.CQBillPaymentREQ;
import cn.zswltech.mithras.service.service.third.financial.resp.FinancialCommonRSP;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @ClassName PaymentHandle
 * @Description 苍穹付款申请单
 * @Author jackerhe
 * @Date 2022/10/26 2:43 下午
 * @Version 1.0
 **/
@Component
public class PaymentApplyBillHandle extends FinancialApiHandler<List<CQBillPaymentREQ>, FinancialCommonRSP> {

    @Resource
    private FinancialConfigService financialConfigService;
    @Resource
    private ExceptionRequestInfoService exceptionRequestInfoService;

    private static final ThreadPoolExecutor threadPool = new ThreadPoolExecutor(Runtime.getRuntime().availableProcessors(), Runtime.getRuntime().availableProcessors() + 1, 60, TimeUnit.SECONDS, new LinkedBlockingQueue<>(100));


    @Override
    public PlatformApiEnum platformApi() {
        return PlatformApiEnum.CQ_BILL_PAYMENT;
    }

    @Override
    public String getUrl() {
        //测试环境
        if (!PROD.equals(active)) {
            return financialConfigService.getUrl(FinancialDevUrlENUM.PAYMENT_BILL_INFO.url);
        }
        return financialConfigService.getUrl(FinancialUrlENUM.PAYMENT_BILL_INFO.url);
    }

    @Override
    public FinancialCommonRSP analyResponseResult(String response) {
        return JSONObject.parseObject(response, FinancialCommonRSP.class);
    }

    @Override
    public FinancialCommonRSP execute(List<CQBillPaymentREQ> reqData) {
       return super.execute(reqData);
    }

    @Override
    public void failureRetry(String platform) {
        List<ExceptionRequestInfo> exceptionRequestInfos = exceptionRequestInfoService.listNeedRetry(platform);
        if (ObjectUtil.isEmpty(exceptionRequestInfos)) {
            return;
        }
       exceptionRequestInfos.forEach(base -> CompletableFuture.runAsync(()->execute(JSON.parseArray(base.getReqData(), CQBillPaymentREQ.class)),
               threadPool));
    }

    @Override
    public boolean isFailureSave() {
        return true;
    }

    @Override
    public String getBusinessId(List<CQBillPaymentREQ> reqData){
        if(ObjectUtil.isEmpty(reqData)){
            return null;
        }
        return reqData.get(0).getSourcebillno();
    }

    @Override
    public Map<String, String> getHttpHeadParam() {
        return financialConfigService.getHttpHeadParam(FinancialUrlENUM.PAYMENT_BILL_INFO);
    }

    @Override
    public void cqRelatedMithras(FinancialCommonRSP result){
        financialConfigService.cqRelatedMithras(result, RecordSourceEnum.PAYMENT.name());
    }
}
