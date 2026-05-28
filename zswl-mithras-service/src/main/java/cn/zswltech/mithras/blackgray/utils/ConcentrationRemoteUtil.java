package cn.zswltech.mithras.blackgray.utils;

import cn.zswltech.mithras.blackgray.service.external.HsAuthToken;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;

/**
 * 恒生数据服务调用包装
 */
@Slf4j
@Component
public class ConcentrationRemoteUtil {

    @Resource
    private RestTemplate restTemplate;

    @Value("${hengsheng.service.url}")
    private String opiUrl;

    @Autowired
    private HsAuthToken hsAuthToken;

    public HttpHeaders commonHeaders(){
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + hsAuthToken.getAuthToken());
        headers.add("Content-Type", "application/x-www-form-urlencoded");
        return headers;
    }


    /**
     * 通用get请求
     * @param url
     * @param clazz
     * @param <T>
     * @return
     */
    public <T> T warpHttpGet(String url,Class<T> clazz) {
        HttpHeaders headers = commonHeaders();
        HttpEntity httpEntity = new HttpEntity(headers);
        long start = System.currentTimeMillis();
        String content = restTemplate.exchange(opiUrl+"/rdm/v1/"+url, HttpMethod.GET, httpEntity, String.class).getBody();
        long cost = System.currentTimeMillis() - start;
        log.info("concentration warpHttpGet url:{},cost:{}, res:{}", url, cost, content);
        return JSONObject.parseObject(content,clazz);
    }
}
