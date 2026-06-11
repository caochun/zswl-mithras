package cn.zswltech.mithras.creditreport.client.xj.handler;

import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.foundation.enums.ContentTypeEnum;
import cn.zswltech.mithras.creditreport.enums.CreditApplyXJUrlENUM;
import cn.zswltech.mithras.third.retry.model.ExceptionRequestInfo;
import cn.zswltech.mithras.foundation.thirdparty.PlatformApiEnum;
import cn.zswltech.mithras.third.retry.application.ExceptionRequestRecordService;
import cn.zswltech.mithras.creditreport.client.xj.CreditReportApiHandler;
import cn.zswltech.mithras.creditreport.client.xj.CreditReportConfigService;
import cn.zswltech.mithras.creditreport.client.xj.req.CreditReportQueryEntFourEleAuthReq;
import cn.zswltech.mithras.creditreport.client.xj.resp.CreditReportQueryEntFourEleAuthResp;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import okhttp3.*;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static cn.zswltech.mithras.foundation.context.SpringContextHolder.getBean;

/**
 * @ClassName PaymentHandle
 * @Description 征信解析-新增档案信
 * @Author jackerhe
 * @Date 2022/10/26 2:43 下午
 * @Version 1.0
 **/
@Component
public class CreditReportQueryEntFourEleAuthHandle extends CreditReportApiHandler<CreditReportQueryEntFourEleAuthReq, CreditReportQueryEntFourEleAuthResp> {

    @Resource
    private CreditReportConfigService creditReportConfigService;

    @Resource
    private ExceptionRequestRecordService exceptionRequestInfoService;

    private static final ThreadPoolExecutor threadPool = new ThreadPoolExecutor(Runtime.getRuntime().availableProcessors(), Runtime.getRuntime().availableProcessors() + 1, 60, TimeUnit.SECONDS, new LinkedBlockingQueue<>(100));

    @Override
    public PlatformApiEnum platformApi() {
        return PlatformApiEnum.CREDIT_REPORT_QUERY_ENT;
    }

    @Override
    public String getUrl() {
        return creditReportConfigService.getUrl(CreditApplyXJUrlENUM.CREDIT_REPORT_QUERY_ENT_FOUR_ELE_AUTH.url);
    }

    @Override
    public CreditReportQueryEntFourEleAuthResp analyResponseResult(String response) {
        return JSONObject.parseObject(response, CreditReportQueryEntFourEleAuthResp.class);
    }

    @Override
    public ContentTypeEnum getContentType() {
        return ContentTypeEnum.from_data;
    }

    @Override
    public CreditReportQueryEntFourEleAuthResp execute(CreditReportQueryEntFourEleAuthReq reqData) {
        //保存结果
        ExceptionRequestInfo exceptionRequestInfo = respSave(getRetryCount(), reqData, getBusinessId(reqData));
        String url = getUrl();

        //此处有个特殊处理，需注意
        String responseData = null;
        try {
            long startTime = System.currentTimeMillis();
            // 构造请求地址
            log.info("CreditReportApiHandler send url {}, requestData {}", url, reqData);
            // 根据请求类型发起请求
            try {
                responseData = formData(url, reqData);
            } catch (IOException e) {
                log.info("CreditReportApiHandler end http,url:{},time:{} error", url, e);
            }
            long endTime = System.currentTimeMillis();
            log.info("CreditReportApiHandler end http,url:{},time:{}, responseData : {}", url, endTime - startTime, responseData);
        } catch (Exception e) {
            log.error("CreditReportApiHandler connection fail, url:{}, request params:{}, error:{}", url, reqData, e);
        }
        CreditReportQueryEntFourEleAuthResp result = null;
        if (ObjectUtil.isNotEmpty(responseData)) {
            result = this.response(responseData);
            //回写状态
            if(ObjectUtil.isNotEmpty(exceptionRequestInfo)){
                exceptionRequestInfo.setRetryFlag(isExecuteSuccess(result) ? 1 : 0);
                exceptionRequestInfo.setResponse(responseData);
                getBean(ExceptionRequestRecordService.class).updateById(exceptionRequestInfo);
            }
            //保存请求返回
            if (isExecuteSuccess(result))
                //建立映射关系
                relatedMithras(result);
        }
        return result;
    }

    public String formData(String url, CreditReportQueryEntFourEleAuthReq reqData) throws IOException {
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS) // 连接超时
                .readTimeout(30, TimeUnit.SECONDS)    // 读取超时
                .writeTimeout(30, TimeUnit.SECONDS)   // 写入超时
                .callTimeout(60, TimeUnit.SECONDS)    // 完整调用超时（OkHttp 4.0+）
                .build();;
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("entName", reqData.getEntName())
                .addFormDataPart("entCertType", reqData.getEntCertType())
                .addFormDataPart("entCertNum", reqData.getEntCertNum())
                .addFormDataPart("authStartDate", reqData.getAuthStartDate())
                .addFormDataPart("authExpiryDate", reqData.getAuthExpiryDate())
                .addFormDataPart("signature", reqData.getSignature())
                .addFormDataPart("businessLicenseCopy", reqData.getBusinessLicenseCopy().getName(),
                        RequestBody.create(reqData.getBusinessLicenseCopy(), MediaType.parse("application/octet-stream")))
                .addFormDataPart("legalIdCardFront", reqData.getLegalIdCardFront().getName(),
                        RequestBody.create(reqData.getLegalIdCardFront(), MediaType.parse("application/octet-stream")))
                .addFormDataPart("legalIdCardBack", reqData.getLegalIdCardBack().getName(),
                        RequestBody.create(reqData.getLegalIdCardBack(), MediaType.parse("application/octet-stream")))
                .addFormDataPart("legalAuthorize", reqData.getLegalAuthorize().getName(),
                        RequestBody.create(reqData.getLegalAuthorize(), MediaType.parse("application/octet-stream")))
                .build();

        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();

        try (Response response = client.newCall(request).execute()) {
            return response.body().string();
        } catch (Exception e) {
            log.warn("CreditReportQueryEntFourEleAuthHandle formData error {}", reqData, e);
        }
        return null;
    }

    @Override
    public Map<String, String> getHttpHeadParam() {
        return creditReportConfigService.getHttpHeadParam(CreditApplyXJUrlENUM.CREDIT_REPORT_QUERY_ENT_FOUR_ELE_AUTH);
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
    public CreditReportQueryEntFourEleAuthReq getReqFromString(String reqString) {
        return JSON.parseObject(reqString, CreditReportQueryEntFourEleAuthReq.class);
    }


}
