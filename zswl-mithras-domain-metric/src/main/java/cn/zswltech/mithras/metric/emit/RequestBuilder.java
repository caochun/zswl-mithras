package cn.zswltech.mithras.metric.emit;

import cn.hutool.json.JSONConfig;
import cn.hutool.json.JSONUtil;
import cn.zswltech.lib.crypto.client.CryptoRequest;
import cn.zswltech.lib.crypto.client.CryptoResponse;
import cn.zswltech.lib.crypto.crypto.SM2;
import cn.zswltech.lib.crypto.crypto.SM3;
import cn.zswltech.mithras.metric.emit.config.EmitRemoteCfg;
import cn.zswltech.mithras.metric.emit.model.req.EmitReq;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

/**
 * @author yibin
 */
@Component
public class RequestBuilder {

    @Resource
    private SM3 sm3;

    @Resource
    private SM2 sm2;

    public Map<String, String> buildHeaders(EmitRemoteCfg cfg) {
        long now = System.currentTimeMillis();
        Map<String, String> headerMap = new HashMap<>(4);
        headerMap.put("Auth-Org", cfg.getAuthOrg());
        headerMap.put("Auth-Time", String.valueOf(now));

        //encrypt
        String str = cfg.getAuthOrg() + now + cfg.getPubKey();
        CryptoResponse res = sm3.encrypt(new CryptoRequest(str.getBytes(StandardCharsets.UTF_8)));
        String sign = new String(Base64.getEncoder().encode(res.getResult()), StandardCharsets.UTF_8);

        headerMap.put("Auth-Sign", sign);
        return headerMap;
    }


    public EmitReq buildBody(Object obj) {
        String str = JSONUtil.toJsonStr(obj, new JSONConfig().setDateFormat("yyyy-MM-dd HH:mm:ss"));
        //encrypt
        CryptoResponse res = sm2.encrypt(new CryptoRequest(str.getBytes(StandardCharsets.UTF_8)));
        String params = new String(Base64.getEncoder().encode(res.getResult()), StandardCharsets.UTF_8);
        return new EmitReq(params);
    }


}
