package cn.zswltech.mithras.creditreport.client.xj.handler;

import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.creditreport.enums.CreditApplyXJUrlENUM;
import cn.zswltech.mithras.third.retry.model.ExceptionRequestInfo;
import cn.zswltech.mithras.foundation.thirdparty.PlatformApiEnum;
import cn.zswltech.mithras.third.retry.application.ExceptionRequestRecordService;
import cn.zswltech.mithras.creditreport.client.xj.CreditReportApiHandler;
import cn.zswltech.mithras.creditreport.client.xj.CreditReportConfigService;
import cn.zswltech.mithras.creditreport.client.xj.req.CreditReportQueryReportReq;
import cn.zswltech.mithras.creditreport.client.xj.resp.CreditReportQueryReportResp;
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
 * @Description 征信解析-新增档案信
 * @Author jackerhe
 * @Date 2022/10/26 2:43 下午
 * @Version 1.0
 **/
@Component
public class CreditReportQueryReportHandle extends CreditReportApiHandler<CreditReportQueryReportReq, CreditReportQueryReportResp> {

    @Resource
    private CreditReportConfigService creditReportConfigService;

    @Resource
    private ExceptionRequestRecordService exceptionRequestInfoService;

    private static final ThreadPoolExecutor threadPool = new ThreadPoolExecutor(Runtime.getRuntime().availableProcessors(), Runtime.getRuntime().availableProcessors() + 1, 60, TimeUnit.SECONDS, new LinkedBlockingQueue<>(100));

    @Override
    public PlatformApiEnum platformApi() {
        return PlatformApiEnum.CREDIT_REPORT_QUERY_REPORT;
    }

    @Override
    public String getUrl() {
        return creditReportConfigService.getUrl(CreditApplyXJUrlENUM.CREDIT_REPORT_QUERY_REPORT.url);
    }

    @Override
    public CreditReportQueryReportResp analyResponseResult(String response) {
        return JSONObject.parseObject(response, CreditReportQueryReportResp.class);
    }

    @Override
    public CreditReportQueryReportResp execute(CreditReportQueryReportReq reqData) {
        CreditReportQueryReportResp execute = super.execute(reqData);
        //回调业务
        if (isExecuteSuccess(execute)) {
        }
        return execute;
    }

    @Override
    public Map<String, String> getHttpHeadParam() {
        return creditReportConfigService.getHttpHeadParam(CreditApplyXJUrlENUM.CREDIT_REPORT_QUERY_REPORT);
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
    public CreditReportQueryReportReq getReqFromString(String reqString) {
        return JSON.parseObject(reqString, CreditReportQueryReportReq.class);
    }


}
