package cn.zswltech.mithras.third.financialshare.client.handle;

import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.third.financialshare.enums.FinancialDevUrlENUM;
import cn.zswltech.mithras.third.financialshare.enums.FinancialUrlENUM;
import cn.zswltech.mithras.third.retry.mapper.model.ExceptionRequestInfo;
import cn.zswltech.mithras.foundation.thirdparty.PlatformApiEnum;
import cn.zswltech.mithras.third.retry.application.ExceptionRequestRecordService;
import cn.zswltech.mithras.third.financialshare.client.FinancialApiHandler;
import cn.zswltech.mithras.third.financialshare.application.FinancialConfigService;
import cn.zswltech.mithras.third.financialshare.client.req.CQRefundREQ;
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
 * @ClassName ReceiveHandle
 * @Description 退款
 * @Author jackerhe
 * @Date 2022/10/26 2:43 下午
 * @Version 1.0
 **/
@Component
public class RefundHandle extends FinancialApiHandler<List<CQRefundREQ>, FinancialCommonRSP> {

    @Resource
    private FinancialConfigService financialConfigService;

    @Resource
    private ExceptionRequestRecordService exceptionRequestInfoService;

    private static final ThreadPoolExecutor threadPool = new ThreadPoolExecutor(Runtime.getRuntime().availableProcessors(), Runtime.getRuntime().availableProcessors() + 1, 60, TimeUnit.SECONDS, new LinkedBlockingQueue<>(100));

    @Override
    public PlatformApiEnum platformApi() {
        return PlatformApiEnum.CQ_REFUND;
    }

    @Override
    public String getUrl() {
        //测试环境
        if (!PROD.equals(active)) {
            return financialConfigService.getUrl(FinancialDevUrlENUM.REFUND_INFO.url);
        }
        return financialConfigService.getUrl(FinancialUrlENUM.REFUND_INFO.url);
    }

    @Override
    public FinancialCommonRSP analyResponseResult(String response) {
        return JSONObject.parseObject(response, FinancialCommonRSP.class);
    }

    @Override
    public FinancialCommonRSP execute(List<CQRefundREQ> reqData) {
       return super.execute(reqData);
    }

    @Override
    public Map<String, String> getHttpHeadParam() {
        return financialConfigService.getHttpHeadParam(FinancialUrlENUM.REFUND_INFO);
    }

    @Override
    public void cqRelatedMithras(FinancialCommonRSP result){
        financialConfigService.cqRelatedMithras(result, "MARGIN");
    }

    @Override
    public void failureRetry(String platform) {
        List<ExceptionRequestInfo> exceptionRequestInfos = exceptionRequestInfoService.listNeedRetry(platform);
        if (ObjectUtil.isEmpty(exceptionRequestInfos)) {
            return;
        }
        exceptionRequestInfos.forEach(base -> CompletableFuture.runAsync(()->execute(JSON.parseArray(base.getReqData(), CQRefundREQ.class)),
                threadPool));
    }

    @Override
    public boolean isFailureSave() {
        return true;
    }

    @Override
    public String getBusinessId(List<CQRefundREQ> reqData){
        if(ObjectUtil.isEmpty(reqData)){
            return null;
        }
        return reqData.get(0).getSourcebillno();
    }
}
