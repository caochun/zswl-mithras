package cn.zswltech.mithras.service.service.creditreport.handle;

import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.service.enums.creditreport.CreditApplyXJUrlENUM;
import cn.zswltech.mithras.service.mapper.model.ExceptionRequestInfo;
import cn.zswltech.mithras.service.repository.PlatformApiEnum;
import cn.zswltech.mithras.service.service.ExceptionRequestRecordService;
import cn.zswltech.mithras.service.service.creditreport.CreditReportApiHandler;
import cn.zswltech.mithras.service.service.creditreport.CreditReportConfigService;
import cn.zswltech.mithras.service.service.creditreport.req.CreditReportObtainResultJSONReq;
import cn.zswltech.mithras.service.service.creditreport.resp.CreditReportObtainResultJSONResp;
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
public class CreditReportObtainResultJSONHandle extends CreditReportApiHandler<CreditReportObtainResultJSONReq, CreditReportObtainResultJSONResp> {

    @Resource
    private CreditReportConfigService creditReportConfigService;

    @Resource
    private ExceptionRequestRecordService exceptionRequestInfoService;

    private static final ThreadPoolExecutor threadPool = new ThreadPoolExecutor(Runtime.getRuntime().availableProcessors(), Runtime.getRuntime().availableProcessors() + 1, 60, TimeUnit.SECONDS, new LinkedBlockingQueue<>(100));

    @Override
    public PlatformApiEnum platformApi() {
        return PlatformApiEnum.CREDIT_REPORT_OBTAIN_JSON;
    }

    @Override
    public String getUrl() {
        return creditReportConfigService.getUrl(CreditApplyXJUrlENUM.CREDIT_REPORT_OBTAIN_RESULT_JSON.url);
    }

    @Override
    public CreditReportObtainResultJSONResp analyResponseResult(String response) {
        return JSONObject.parseObject(response, CreditReportObtainResultJSONResp.class);
    }

    @Override
    public CreditReportObtainResultJSONResp execute(CreditReportObtainResultJSONReq reqData) {
        CreditReportObtainResultJSONResp execute = super.execute(reqData);
        return execute;
    }

    @Override
    public Map<String, String> getHttpHeadParam() {
        return creditReportConfigService.getHttpHeadParam(CreditApplyXJUrlENUM.CREDIT_REPORT_OBTAIN_RESULT_JSON);
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
    public CreditReportObtainResultJSONReq getReqFromString(String reqString) {
        return JSON.parseObject(reqString, CreditReportObtainResultJSONReq.class);
    }


}
