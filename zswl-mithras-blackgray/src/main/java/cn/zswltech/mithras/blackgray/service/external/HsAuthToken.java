package cn.zswltech.mithras.blackgray.service.external;

import cn.zswltech.gruul.common.util.StringUtil;
import cn.zswltech.mithras.blackgray.service.RedisService;
import cn.zswltech.mithras.blackgray.service.external.remote.AccessToken;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;

/**
 * 恒生接口认证token获取服务
 */
@Slf4j
@Component
public class HsAuthToken {

    @Resource
    private RestTemplate restTemplate;

    @Autowired
    private RedisService redisService;

    @Value("${hengsheng.service.url}")
    private String opiUrl;

    @Value("${hengsheng.service.appKey}")
    private String appKey;

    @Value("${hengsheng.service.appSecret}")
    private String appSecret;

    private static final String HS_AUTH_TOKEN_KEY = "HsAuthToken";

    public String getAuthToken() {
        String hsAuthToken = accessToken();
        /*String hsAuthToken = redisService.get(HS_AUTH_TOKEN_KEY);
        if (StringUtil.isBlank(hsAuthToken)) {
            hsAuthToken = accessToken();
            if (StringUtil.isNotBlank(hsAuthToken)) {
                redisService.set(HS_AUTH_TOKEN_KEY, hsAuthToken, 2 * 60 * 60L);
            }
        }*/
        return hsAuthToken;
    }

    public String refreshAuthToken() {
        String hsAuthToken = accessToken();
        if (StringUtil.isNotBlank(hsAuthToken)) {
            redisService.set(HS_AUTH_TOKEN_KEY, hsAuthToken, 2 * 60 * 60L);
        }
        return hsAuthToken;
    }

    private String accessToken() {
        String str = appKey + ":" + appSecret;
        byte[] encodeBase64 = Base64.encodeBase64(str.getBytes(StandardCharsets.UTF_8));
        String basicHeader = "Basic " + new String(encodeBase64);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/x-www-form-urlencoded");
        headers.add("Authorization",basicHeader);
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "client_credentials");
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);
        AccessToken accessToken = restTemplate.exchange(opiUrl + "/oauth2/oauth2/token", HttpMethod.POST, request, AccessToken.class).getBody();
        log.info("accessToken result : {}", JSONObject.toJSONString(accessToken));
        return accessToken == null ? null : accessToken.getAccess_token();
    }

    public String getAppKey() {
        return appKey;
    }
}
