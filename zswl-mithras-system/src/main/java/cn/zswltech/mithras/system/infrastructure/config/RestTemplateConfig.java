package cn.zswltech.mithras.system.infrastructure.config;


import cn.hutool.core.util.StrUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.Charset;

@Configuration
public class RestTemplateConfig {

    @Value("${spring.profiles.active}")
    private String active;

    private static final String UAT = "uat";

    private static final String PRE = "pre";

    private static final String PROD = "prod";

    @Bean
    public RestTemplate restTemplate(ClientHttpRequestFactory factory) {
        RestTemplate restTemplate = new RestTemplate(factory);
        // 支持中文编码
        restTemplate.getMessageConverters().set(1, new StringHttpMessageConverter(Charset.forName("UTF-8")));
        return restTemplate;
    }

    @Bean
    public ClientHttpRequestFactory simpleClientHttpRequestFactory() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        //设置超时时间
        //预发生产
        if (StrUtil.equalsAny(active,"sit", UAT, PRE, PROD)) {
            factory.setReadTimeout(45000);//单位为ms
            factory.setConnectTimeout(45000);//单位为ms
        } else {
            factory.setReadTimeout(3000);//单位为ms
            factory.setConnectTimeout(3000);//单位为ms
        }
        return factory;
    }
}