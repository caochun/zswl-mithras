package cn.zswltech.mithras.third.financialshare.infrastructure.client.handle;

import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.third.enums.FinancialDevUrlENUM;
import cn.zswltech.mithras.third.enums.FinancialUrlENUM;
import cn.zswltech.mithras.third.mapper.model.ExceptionRequestInfo;
import cn.zswltech.mithras.service.repository.PlatformApiEnum;
import cn.zswltech.mithras.third.service.ExceptionRequestRecordService;
import cn.zswltech.mithras.third.financialshare.infrastructure.client.FinancialApiHandler;
import cn.zswltech.mithras.third.financialshare.application.FinancialConfigService;
import cn.zswltech.mithras.third.financialshare.infrastructure.client.req.CQ2WithdrawReq;
import cn.zswltech.mithras.third.financialshare.infrastructure.client.resp.CQ2WithdrawRSP;
import cn.zswltech.mithras.third.financialshare.infrastructure.client.config.AppAuthConfig;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.HashMap;
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
public class CQ2WithdrawHandle extends FinancialApiHandler<CQ2WithdrawReq, CQ2WithdrawRSP> {

    @Resource
    private FinancialConfigService financialConfigService;

    @Resource
    private AppAuthConfig appAuthConfig;

    @Resource
    private ExceptionRequestRecordService exceptionRequestInfoService;

    private static final ThreadPoolExecutor threadPool = new ThreadPoolExecutor(Runtime.getRuntime().availableProcessors(), Runtime.getRuntime().availableProcessors() + 1, 60, TimeUnit.SECONDS, new LinkedBlockingQueue<>(100));

    @Override
    public PlatformApiEnum platformApi() {
        return PlatformApiEnum.CQ2_WITHDRAW;
    }

    @Override
    public String getUrl() {
        //测试环境
        if (!PROD.equals(active)) {
            return financialConfigService.getUrl(FinancialDevUrlENUM.CQ2_WITHDRAW.url);
        }
        return financialConfigService.getUrl(FinancialUrlENUM.CQ2_WITHDRAW.url);
    }

    @Override
    public CQ2WithdrawRSP analyResponseResult(String response) {
        return JSONObject.parseObject(response, CQ2WithdrawRSP.class);
    }

    @Override
    public CQ2WithdrawRSP execute(CQ2WithdrawReq reqData) {
        reqData.setAppId(appAuthConfig.getAppId());
        reqData.setAppSecret(appAuthConfig.getAppSecuret());
        reqData.setAppSecuret(appAuthConfig.getAppSecuret());
        reqData.setFromSys(appAuthConfig.getAppId());
        return super.execute(reqData);
        /*try {
            super.execute(reqData);
        } catch (Exception e) {
            log.info("-----数据撤回 {}", reqData, e);
        }
        CQ2WithdrawRSP rsp = new CQ2WithdrawRSP();
        rsp.setSuccess(true);
        rsp.setState("200");
        rsp.setStatus(true);
        rsp.setData("删除成功");
       return rsp;*/
    }

    @Override
    public Map<String, String> getHttpHeadParam() {
        return financialConfigService.getHttpHeadParam(FinancialUrlENUM.CQ2_COLLECTION);
    }


    @Override
    public void failureRetry(String platform) {
        List<ExceptionRequestInfo> exceptionRequestInfos = exceptionRequestInfoService.listNeedRetry(platform);
        if (ObjectUtil.isEmpty(exceptionRequestInfos)) {
            return;
        }
        exceptionRequestInfos.forEach(base -> CompletableFuture.runAsync(()->execute(JSON.parseObject(base.getReqData(), CQ2WithdrawReq.class)),
                threadPool));
    }

    @Override
    public boolean isFailureSave() {
        return true;
    }

    @Override
    public String getBusinessId(CQ2WithdrawReq reqData){
        if(ObjectUtil.isEmpty(reqData)){
            return null;
        }
        return ObjectUtil.isEmpty(reqData.getBillNo()) ? null : reqData.getBillNo().toString();
    }

    public String wrapRequestParam(CQ2WithdrawReq reqData) {
        HashMap<String , CQ2WithdrawReq> hashMap = new HashMap();
        hashMap.put("request" ,reqData);
        //统一处理为苍穹格式
        return JSONObject.toJSONString(hashMap);
    }
}
