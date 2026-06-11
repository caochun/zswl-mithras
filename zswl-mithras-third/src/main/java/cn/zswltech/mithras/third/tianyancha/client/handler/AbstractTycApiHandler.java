package cn.zswltech.mithras.third.tianyancha.client.handler;

import cn.hutool.core.lang.Pair;
import cn.hutool.core.map.MapUtil;
import cn.zswltech.mithras.third.tianyancha.enums.TycErrorEnum;
import cn.zswltech.mithras.customer.externaldata.tianyancha.mapper.TycMockDataMapper;
import cn.zswltech.mithras.customer.externaldata.tianyancha.mapper.model.TycMockData;
import cn.zswltech.mithras.foundation.thirdparty.PlatformApiHandler;
import cn.zswltech.mithras.foundation.thirdparty.RequestModeEnum;
import cn.zswltech.mithras.third.tianyancha.client.TycMockDataType;
import cn.zswltech.mithras.third.tianyancha.client.req.TycBaseReq;
import cn.zswltech.mithras.third.tianyancha.client.resp.TycBaseResp;
import cn.zswltech.mithras.foundation.util.HttpUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;

import javax.annotation.Resource;
import java.io.IOException;

import static cn.hutool.core.util.ObjectUtil.isNotNull;

/**
 * 天眼查api处理器
 *
 * @author wang
 * @date 2022/4/11 7:10 PM
 */
@Slf4j
public abstract class AbstractTycApiHandler<T, F extends TycBaseResp> implements PlatformApiHandler<T, F> {

    protected static final String defaultBody = "{\"reason\":\"ok\",\"error_code\":400001}";

    @Value("${mithras.tyc.mock}")
    protected Boolean tycMock;
    @Resource
    protected TycMockDataMapper mockDataMapper;

    @Override
    public F execute(T reqData) {

        RequestModeEnum requestMode = platformApi().requestModeEnum;
        String url = getUrl(reqData);

        String requestData = this.request(reqData);
        String responseData = null;

        try {
            long startTime = System.currentTimeMillis();
            // 构造请求地址

            // mock拦截
            if (Boolean.TRUE.equals(tycMock)) {
                responseData = defaultBody;
                responseData = mockHandleBeforeResponse(reqData, responseData);
                long endTime = System.currentTimeMillis();
                log.info("mock data,url:{},time:{}", url, endTime - startTime);
                F result = this.response(responseData);
                return result;
            }

            // 根据请求类型发起请求
            if (RequestModeEnum.GET.requestMode.equals(requestMode.requestMode)) {
                responseData = HttpUtil.httpGetRequest(url, MapUtil.of(
                        Pair.of("Authorization", "3a416222-5335-4e18-a656-bd97464a6943")
                ));
            } else {
                responseData = HttpUtil.httpPostJsonRequest(url, requestData, MapUtil.of(
                        Pair.of("Authorization", "3a416222-5335-4e18-a656-bd97464a6943")
                ));
            }
            long endTime = System.currentTimeMillis();
            log.info("request http,url:{},time:{}", url, endTime - startTime);
        } catch (IOException e) {
            log.error("AbstractTycApiHandler connection fail, url:{}, request params:{}, error:{}", url, requestData, e);
        }

        F result = this.response(responseData);
        return result;
    }

    @Override
    public boolean isExecuteSuccess(F resData) {
        // 成功或无数据 则为调用成功
        return TycErrorEnum.SUCCESS.getCode().equals(resData.getErrorCode()) || TycErrorEnum.NO_DATA.getCode().equals(resData.getErrorCode());
    }

    public abstract String getUrl(T reqData);

    @Override
    public String request(T reqData) {
        return this.wrapRequestParam(reqData);
    }

    @Override
    public F response(String data) {
        return this.analyResponseResult(data);
    }

    public String wrapRequestParam(T reqData){
        return JSONObject.toJSONString(reqData);
    }

    public abstract F analyResponseResult(String response);

    public String mockHandleBeforeResponse(T reqData, String responseData) {
        String mockDataType = TycMockDataType.convert(platformApi());
        if (StringUtils.isNotBlank(mockDataType) && reqData instanceof TycBaseReq) {
            // mock情况下 此处只处理根据关键字的查询 其他的到子类自己去处理
            TycMockData mockData = mockDataMapper.selectOne(Wrappers.<TycMockData>lambdaQuery()
                    .eq(TycMockData::getDataType, mockDataType)
                    .eq(TycMockData::getKeyword, ((TycBaseReq) reqData).getKeyword())
            );
            if (isNotNull(mockData)) {
                responseData = mockData.getJsonData();
            }
        }
        return responseData;
    }

}
