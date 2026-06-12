package cn.zswltech.mithras.third.financialshare.client.handle;

import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.third.financialshare.enums.FinancialDevUrlENUM;
import cn.zswltech.mithras.third.financialshare.enums.FinancialUrlENUM;
import cn.zswltech.mithras.third.retry.persistence.model.ExceptionRequestInfo;
import cn.zswltech.mithras.foundation.thirdparty.PlatformApiEnum;
import cn.zswltech.mithras.third.retry.application.ExceptionRequestRecordService;
import cn.zswltech.mithras.third.financialshare.client.FinancialApiHandler;
import cn.zswltech.mithras.third.financialshare.application.FinancialConfigService;
import cn.zswltech.mithras.third.financialshare.client.req.ReceiveProvisionREQ;
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
 * @Description 苍穹接收拨备信息
 * @Author jackerhe
 * @Date 2022/10/26 2:43 下午
 * @Version 1.0
 **/
@Component
public class ReceiveProvisionHandle extends FinancialApiHandler<List<ReceiveProvisionREQ>, FinancialCommonRSP> {

    @Resource
    private FinancialConfigService financialConfigService;
    @Resource
    private ExceptionRequestRecordService exceptionRequestInfoService;

    private static final ThreadPoolExecutor threadPool = new ThreadPoolExecutor(Runtime.getRuntime().availableProcessors(), Runtime.getRuntime().availableProcessors() + 1, 60, TimeUnit.SECONDS, new LinkedBlockingQueue<>(100));


    @Override
    public PlatformApiEnum platformApi() {
        return PlatformApiEnum.CQ_RECEIVE_PROVISION;
    }

    @Override
    public String getUrl() {
        //测试环境
        if (!PROD.equals(active)) {
            return financialConfigService.getUrl(FinancialDevUrlENUM.CQ_RECEIVE_PROVISION.url);
        }
        return financialConfigService.getUrl(FinancialUrlENUM.CQ_RECEIVE_PROVISION.url);
    }

    @Override
    public FinancialCommonRSP analyResponseResult(String response) {
        return JSONObject.parseObject(response, FinancialCommonRSP.class);
    }

    @Override
    public FinancialCommonRSP execute(List<ReceiveProvisionREQ> reqData) {
       return super.execute(reqData);
    }

    @Override
    public void failureRetry(String platform) {
        List<ExceptionRequestInfo> exceptionRequestInfos = exceptionRequestInfoService.listNeedRetry(platform);
        if (ObjectUtil.isEmpty(exceptionRequestInfos)) {
            return;
        }
       exceptionRequestInfos.forEach(base -> CompletableFuture.runAsync(()->execute(JSON.parseArray(base.getReqData(), ReceiveProvisionREQ.class)),
               threadPool));
    }

    @Override
    public boolean isFailureSave() {
        return true;
    }

    @Override
    public String getBusinessId(List<ReceiveProvisionREQ> reqData){
        if(ObjectUtil.isEmpty(reqData)){
            return null;
        }
        return reqData.get(0).getBizdate();
    }

    @Override
    public int getRetryCount(){
        return 10;
    }

    @Override
    public Map<String, String> getHttpHeadParam() {
        return financialConfigService.getHttpHeadParam(FinancialUrlENUM.CQ_RECEIVE_PROVISION);
    }
}
