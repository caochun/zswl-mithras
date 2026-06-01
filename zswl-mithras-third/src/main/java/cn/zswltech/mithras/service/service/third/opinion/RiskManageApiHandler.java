package cn.zswltech.mithras.service.service.third.opinion;

import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.service.repository.PlatformApiHandler;
import cn.zswltech.mithras.service.repository.RequestModeEnum;
import cn.zswltech.mithras.service.service.third.opinion.resp.RiskControlCommRSP;
import cn.zswltech.mithras.service.util.HttpUtil;
import com.alibaba.fastjson.JSONObject;

import java.io.IOException;
import java.util.Map;

public abstract class RiskManageApiHandler<T, F extends RiskControlCommRSP> implements PlatformApiHandler<T, F> {


    @Override
    public F execute(T reqData) {
        RequestModeEnum requestMode = platformApi().requestModeEnum;
        String url = getUrl();

        //此处有个特殊处理，需注意
        String requestData = this.request(reqData);
        String responseData = null;

        try {
            long startTime = System.currentTimeMillis();
            // 构造请求地址

            log.info("RiskManageApiHandler send url {}, requestData {}", url, requestData);
            // 根据请求类型发起请求
            if (RequestModeEnum.GET.requestMode.equals(requestMode.requestMode)) {
                responseData = HttpUtil.httpGetRequest(url, getHttpHeadParam());
            } else {
                responseData = HttpUtil.httpPostJsonRequest(url, requestData, getHttpHeadParam());
            }
            long endTime = System.currentTimeMillis();
            log.info("RiskManageApiHandler end http,url:{},time:{}, responseData : {}", url, endTime - startTime, responseData);
        } catch (IOException e) {
            log.error("RiskManageApiHandler connection fail, url:{}, request params:{}, error:{}", url, requestData, e);
        }
        F result = this.response(responseData);
        if(!isExecuteSuccess(result)){
            //todo 保存异常数据等待重试
        }
        return result;
    }

    @Override
    public boolean isExecuteSuccess(F resData) {
        // 权限验证成功
        if(ObjectUtil.isNotEmpty(resData) && resData.getSuccess()){
            log.info("RiskManageApiHandler {} execute success message : {}", platformApi().apiName, resData);
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

    public abstract String getUrl();

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
