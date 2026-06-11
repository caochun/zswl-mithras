package cn.zswltech.mithras.third.financialshare.client.handle;

import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.third.financialshare.enums.FinancialDevUrlENUM;
import cn.zswltech.mithras.third.financialshare.enums.FinancialUrlENUM;
import cn.zswltech.mithras.third.retry.model.ExceptionRequestInfo;
import cn.zswltech.mithras.foundation.thirdparty.PlatformApiEnum;
import cn.zswltech.mithras.third.retry.application.ExceptionRequestRecordService;
import cn.zswltech.mithras.third.financialshare.client.FinancialApiHandler;
import cn.zswltech.mithras.third.financialshare.application.FinancialConfigService;
import cn.zswltech.mithras.third.financialshare.client.req.CQPaymentREQ;
import cn.zswltech.mithras.third.financialshare.client.resp.FinancialCommonRSP;
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
 * @Description 苍穹应付单
 * @Author jackerhe
 * @Date 2022/10/26 2:43 下午
 * @Version 1.0
 **/
@Component
public class PaymentHandle extends FinancialApiHandler<List<CQPaymentREQ>, FinancialCommonRSP> {

    @Resource
    private FinancialConfigService financialConfigService;

    @Resource
    private ExceptionRequestRecordService exceptionRequestInfoService;

    private static final ThreadPoolExecutor threadPool = new ThreadPoolExecutor(Runtime.getRuntime().availableProcessors(), Runtime.getRuntime().availableProcessors() + 1, 60, TimeUnit.SECONDS, new LinkedBlockingQueue<>(100));

    @Override
    public PlatformApiEnum platformApi() {
        return PlatformApiEnum.CQ_PAYMENT;
    }

    @Override
    public String getUrl() {
        //测试环境
        if (!PROD.equals(active)) {
            return financialConfigService.getUrl(FinancialDevUrlENUM.PAYMENT_INFO.url);
        }
        return financialConfigService.getUrl(FinancialUrlENUM.PAYMENT_INFO.url);
    }

    @Override
    public FinancialCommonRSP analyResponseResult(String response) {
        return JSONObject.parseObject(response, FinancialCommonRSP.class);
    }

    @Override
    public FinancialCommonRSP execute(List<CQPaymentREQ> reqData) {
       return super.execute(reqData);
    }

    @Override
    public Map<String, String> getHttpHeadParam() {
        return financialConfigService.getHttpHeadParam(FinancialUrlENUM.PAYMENT_INFO);
    }

    @Override
    public void cqRelatedMithras(FinancialCommonRSP result){
        financialConfigService.cqRelatedMithras(result, "PAYMENT");
    }

    @Override
    public void failureRetry(String platform) {
        List<ExceptionRequestInfo> exceptionRequestInfos = exceptionRequestInfoService.listNeedRetry(platform);
        if (ObjectUtil.isEmpty(exceptionRequestInfos)) {
            return;
        }
        exceptionRequestInfos.forEach(base -> CompletableFuture.runAsync(()->execute(JSON.parseArray(base.getReqData(), CQPaymentREQ.class)),
                threadPool));
    }

    @Override
    public boolean isFailureSave() {
        return true;
    }

    @Override
    public String getBusinessId(List<CQPaymentREQ> reqData){
        if(ObjectUtil.isEmpty(reqData)){
            return null;
        }
        return reqData.get(0).getSourcebillno();
    }
}
