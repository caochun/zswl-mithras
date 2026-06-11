package cn.zswltech.mithras.blackgray.client;

import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.blackgray.client.dto.JKBaseRSP;
import cn.zswltech.mithras.third.financialshare.enums.FinancialRSPENUM;
import cn.zswltech.mithras.foundation.thirdparty.PlatformApiHandler;
import cn.zswltech.mithras.foundation.thirdparty.RequestModeEnum;
import cn.zswltech.mithras.foundation.util.HttpUtil;
import com.alibaba.fastjson.JSONObject;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.Map;

public abstract class JKBlackGrayApiHandler<T, F extends JKBaseRSP> implements PlatformApiHandler<T, F> {


    @Value("${spring.profiles.active}")
    protected String active;

    protected static final String PRE = "pre";

    protected static final String PROD = "prod";

    @Value("${risk.control.baseUrl}")
    private String baseUrl;

    @Override
    @SneakyThrows
    @Transactional(rollbackFor = Throwable.class)
    public F execute(T reqData) {
        RequestModeEnum requestMode = platformApi().requestModeEnum;
        String url = baseUrl + getUrl();
        //此处有个特殊处理，需注意
        String requestData = this.request(reqData);
        String responseData = null;
        try {
            long startTime = System.currentTimeMillis();
            // 构造请求地址
            log.info("JKBlackGrayApiHandler send cq url {}, requestData {}", url, requestData);
            // 根据请求类型发起请求
            try {
                if (RequestModeEnum.GET.requestMode.equals(requestMode.requestMode)) {
                    responseData = HttpUtil.httpGetRequest(getAddParam(reqData, url), getHttpHeadParam());
                } else {
                    responseData = HttpUtil.httpPostJsonRequest(url, requestData, getHttpHeadParam());
                }
            } catch (IOException e) {
                log.info("JKBlackGrayApiHandler end http,url:{},time:{} error", url, e);
            }
            long endTime = System.currentTimeMillis();
            log.info("JKBlackGrayApiHandler end http,url:{},time:{}, responseData : {}", url, endTime - startTime, responseData);
        } catch (Exception e) {
            log.error("JKBlackGrayApiHandler connection fail, url:{}, request params:{}, error:{}", url, requestData, e);
        }
        F result = null;
        if (ObjectUtil.isNotEmpty(responseData)) {
            result = this.response(responseData);
        }
        return result;
    }

    @Override
    public boolean isExecuteSuccess(F resData) {
        // 权限验证成功
        if(ObjectUtil.isNotEmpty(resData)){
            if (FinancialRSPENUM.SUCCESS.getResult().equals(resData.getState()) || ObjectUtil.equals(resData.getSuccess(), Boolean.TRUE) || ObjectUtil.equals(Boolean.TRUE, resData.getStatus())) {
                return Boolean.TRUE;
            } else {
                log.info("FinancialApiHandler {} execute error errorCode : {} message : {}", platformApi().apiName, resData.getErrorCode(), resData.getMessage());
            }
        }
        return Boolean.FALSE;
    }

    public abstract String getUrl();

    public String getAddParam(T reqData, String url){
        return url;
    }

    public void cqRelatedMithras(F result){
    }

    //请求头携带参数
    public abstract Map<String,String> getHttpHeadParam();

    @Override
    public String request(T reqData) {
        return this.wrapRequestParam(reqData);
    }

    @Override
    public F response(String data) {
        return this.analyResponseResult(data);
    }

    public String wrapRequestParam(T reqData) {
        return JSONObject.toJSONString(reqData);
    }

    public abstract F analyResponseResult(String response);


}
