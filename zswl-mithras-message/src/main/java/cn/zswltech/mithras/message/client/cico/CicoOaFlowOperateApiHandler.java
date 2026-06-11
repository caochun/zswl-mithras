package cn.zswltech.mithras.message.client.cico;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import cn.zswltech.mithras.foundation.thirdparty.PlatformApiEnum;
import cn.zswltech.mithras.foundation.thirdparty.PlatformApiHandler;
import cn.zswltech.mithras.message.client.cico.dto.CicoOaFlowOperateREQ;
import cn.zswltech.mithras.message.client.cico.dto.CicoOaFlowOperateRSP;
import cn.zswltech.mithras.foundation.util.HttpUtil;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * 交投oa
 *
 * @date 2022/7/20 2:14 PM
 */
@Component
public class CicoOaFlowOperateApiHandler implements PlatformApiHandler<CicoOaFlowOperateREQ, CicoOaFlowOperateRSP> {

    @Value("${remote.publishNotice.esbUrl}")
    private String esbUrl;

    @Override
    public PlatformApiEnum platformApi() {
        return PlatformApiEnum.CICO_OA_FLOW_OPERATE_LIST;
    }

    @Override
    public CicoOaFlowOperateRSP response(String data) {
        try {
            return JSONObject.parseObject(data, CicoOaFlowOperateRSP.class);
        } catch (Exception e) {
            log.warn("集团消息异常 {}", data);
            return null;
        }
    }

    @Override
    public CicoOaFlowOperateRSP execute(CicoOaFlowOperateREQ reqData) {
        String url = String.format("%s/bpm-http/portal/ofs/ReceiveRequestInfoByJson", esbUrl);
        //此处有个特殊处理，需注意
        String requestData = this.request(reqData);
        String responseData = null;
        try {
            long startTime = System.currentTimeMillis();
            // 构造请求地址
            log.info("CicoOaFlowOperateApiHandler send url {}, requestData {}", url, requestData);
            // 根据请求类型发起请求
            try {
                responseData = HttpUtil.httpPostJsonRequest(url, requestData, getHttpHeadParam());
            } catch (IOException e) {
                log.info("CicoOaFlowOperateApiHandler end http,url:{},time:{} error", url, e);
            }
            long endTime = System.currentTimeMillis();
            log.info("CicoOaFlowOperateApiHandler end http,url:{},time:{}, requestData {}, responseData : {}", url, endTime - startTime, requestData,responseData);
        } catch (Exception e) {
            log.error("CicoOaFlowOperateApiHandler connection fail, url:{}, requestData {}, request params:{}, error:{}", url, requestData, requestData, e);
        }
        CicoOaFlowOperateRSP result = null;
        if (ObjectUtil.isNotEmpty(responseData)) {
            result = this.response(responseData);
        }
        return result;
    }

    public Map<String, String> getHttpHeadParam() {
        Map<String, String> map = new HashMap<String, String>();
        map.put("ClientId", "com.cncico.esb.opr.zszl");
        map.put("OperationCode", "com.cncico.esb.bpm.portal.ReceiveRequestInfoByJson.get");
        return map;
    }

    @Override
    public String request(CicoOaFlowOperateREQ reqData) {
        return JSONUtil.toJsonStr(reqData);
    }
}
