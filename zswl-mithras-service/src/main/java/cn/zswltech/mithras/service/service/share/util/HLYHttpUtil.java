package cn.zswltech.mithras.service.service.share.util;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

/**
 * @author: ldhu
 * @Data: 2026/3/3
 * @desc: 汇联易 接口工具类
 */
public class HLYHttpUtil {
    private static int MAX_TIME_OUT = 1000 * 120;  // 超时时间
    private static HttpComponentsClientHttpRequestFactory factory = null;

    static {
        factory = new HttpComponentsClientHttpRequestFactory();
        factory.setConnectionRequestTimeout(MAX_TIME_OUT);
        factory.setConnectTimeout(MAX_TIME_OUT);
        factory.setReadTimeout(MAX_TIME_OUT);
    }

    private static String getToken(String clientId, String securet, String host) {
        String tokenUrl = host + HLYEnum.TOKEN.display();//需要替换
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setRequestFactory(factory);

        HttpHeaders httpHeaders = new HttpHeaders();
        String authStr = clientId.concat(":").concat(securet);
        String authStrEnc = new String(Base64.encodeBase64(authStr.getBytes()));
        httpHeaders.set("Authorization", "Basic ".concat(authStrEnc));

        MultiValueMap<String, String> formData = new LinkedMultiValueMap();
        formData.add("grant_type", "client_credentials");
        formData.add("scope", "write");

        HttpEntity httpEntity = new HttpEntity(formData, httpHeaders);
        ResponseEntity<String> exchange = restTemplate.exchange(tokenUrl, HttpMethod.POST, httpEntity, String.class);
        String respone = exchange.getBody();
        JSONObject jsonObject = JSONUtil.parseObj(respone);
        if (jsonObject.containsKey("access_token")) {
            return jsonObject.getStr("access_token");
        } else {
            throw new RuntimeException("获取token失败" + respone);
        }
    }

    public static <T> String post(String clientId, String secret, String host, String url, T data) {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setRequestFactory(factory);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer ".concat(getToken(clientId, secret, host)));
        headers.set("Content-Type", "application/json");
        headers.set("x-forwarded-prefix", "gateway");

        HttpEntity<T> entity = new HttpEntity<>(data, headers);
        ResponseEntity<String> response = restTemplate.exchange(host + url, HttpMethod.POST, entity, String.class);

        return response.getBody();
    }


    public static String get(String clientId, String securet, String host, String url) {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setRequestFactory(factory);

        String token = getToken(clientId, securet, host);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer  ".concat(token));
        headers.set("Content-Type", "application/json");
        headers.set("x-forwarded-prefix", "gateway");
        url = url.concat("&access_token=").concat(token);

        HttpEntity httpEntity = new HttpEntity(null, headers);
        ResponseEntity<String> exchange = restTemplate.exchange(host + url, HttpMethod.GET, httpEntity, String.class);
        return exchange.getBody();
    }
}