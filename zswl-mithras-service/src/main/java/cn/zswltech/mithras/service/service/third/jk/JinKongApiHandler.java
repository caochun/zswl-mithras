package cn.zswltech.mithras.service.service.third.jk;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import cn.zswltech.mithras.service.config.JKConfig;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.repository.PlatformApiHandler;
import cn.zswltech.mithras.service.service.third.jk.req.JinKongBasicReq;
import cn.zswltech.mithras.service.service.third.jk.res.JinKongBasicRes;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.HmacAlgorithms;
import org.apache.commons.codec.digest.HmacUtils;
import org.apache.commons.codec.digest.Md5Crypt;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.*;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;

import javax.annotation.Resource;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.Base64;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

/**
 * @author dingqi
 * @date 2023/8/7
 * @description
 */
@Slf4j
public abstract class JinKongApiHandler<REQ extends JinKongBasicReq, RES extends JinKongBasicRes> implements PlatformApiHandler<REQ, RES> {
    private static final String APPKEY_HEADER = "X-AURORA-APPKEY";
    private static final String SIGN_HEADER = "X-AURORA-SIGN";
    private static final String TIMESTAMP_HEADER = "X-AURORA-TIMESTAMP";
    private static final String CONTENT_MD5 = "CONTENT-MD5";

    @Resource
    protected JKConfig jkConfig;

    @Override
    public RES execute(REQ reqData) {
        CloseableHttpClient httpClient;
        try {
            httpClient = HttpClientBuilder
                    .create()
                    .setSSLContext(getAllTrustedSslContext())
                    .setSSLHostnameVerifier(getAllTrustedVerifier())
                    .build();
        } catch (Exception e) {
            log.error("初始化Http客户端发生异常", e);
            throw new MithrasException("执行异常");
        }
        String url = jkConfig.getBaseUrl() + this.apiPath();
        String dataJson = Objects.isNull(reqData) ? null : JSONUtil.toJsonStr(reqData);
        // 确定请求对象
        HttpRequestBase request = this.ensureHttpRequest(url, dataJson);
        request.setHeader(HttpHeaders.CONTENT_TYPE, "application/json;charset=UTF-8");
        // 签名
        this.doSign(request, StrUtil.isBlank(dataJson) ? null : dataJson.getBytes());
        // 准备请求
        String responseStr;
        CloseableHttpResponse response = null;
        try {
            log.info("金控接口请求参数 url:{} req:{}", url, JSONUtil.toJsonStr(reqData));
            // 由客户端执行请求
            response = httpClient.execute(request);
            // 从响应模型中获取响应实体
            HttpEntity httpEntity = response.getEntity();
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            httpEntity.writeTo(outputStream);
            responseStr = outputStream.toString();
            log.info("金控接口返回参数:{}", responseStr);
        } catch (Exception e) {
            log.error("处理HTTP请求发生异常", e);
            throw new MithrasException("发生未知异常");
        } finally {
            // 释放资源
            try {
                httpClient.close();
            } catch (IOException e) {
                log.error("关闭httpClient异常", e);
            }
            if (Objects.nonNull(response)) {
                try {
                    response.close();
                } catch (IOException e) {
                    log.error("关闭httpResponse异常", e);
                }
            }
        }
        return this.response(responseStr);
    }

    protected abstract String apiPath();

    protected abstract HttpMethod httpMethod();

    private SSLContext getAllTrustedSslContext() throws NoSuchAlgorithmException, KeyManagementException {
        TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
            @Override
            public X509Certificate[] getAcceptedIssuers() {
                return new X509Certificate[0];
            }

            @Override
            public void checkClientTrusted(X509Certificate[] chain, String authType) {

            }

            @Override
            public void checkServerTrusted(X509Certificate[] chain, String authType) {

            }
        }};
        SSLContext context = SSLContext.getInstance("TLS");
        context.init(null, trustAllCerts, new SecureRandom());
        return context;
    }

    private static HostnameVerifier getAllTrustedVerifier() {
        return (hostname, session) -> true;
    }

    private void doSign(HttpRequestBase httpRequestBase, byte[] body) {
        String method = httpRequestBase.getMethod().toUpperCase();
        URIBuilder uriBuilder = new URIBuilder(httpRequestBase.getURI());
        String queryString = queryJoin(uriBuilder.getQueryParams());
        long timestamp = System.currentTimeMillis();
        // 构造待签字符串
        String stringToSign;
        String contentMd5 = null;
        if (httpRequestBase instanceof HttpPost || httpRequestBase instanceof HttpPut) {
            contentMd5 = null == body || body.length == 0 ? "" : Base64.getEncoder().encodeToString(Md5Crypt.md5Crypt(body,"$1$contentCrypt").getBytes());
            httpRequestBase.setHeader(CONTENT_MD5, contentMd5);
            stringToSign = method + "\n" + queryString + "\n" + timestamp +"\n" + contentMd5;
        } else {
            stringToSign = method + "\n" + queryString + "\n" + timestamp;
        }
        String signature = Base64.getEncoder().encodeToString(new HmacUtils(HmacAlgorithms.HMAC_SHA_256, jkConfig.getAccessKeySecret()).hmacHex(stringToSign).getBytes());
        log.info("金控接口签名结果[timestamp:{}, queryString:{}, contentMd5:{}, signature:{}]", timestamp, queryString, contentMd5, signature);
        httpRequestBase.setHeader(APPKEY_HEADER, jkConfig.getAccessKeyId());
        httpRequestBase.setHeader(SIGN_HEADER, signature);
        httpRequestBase.setHeader(TIMESTAMP_HEADER, String.valueOf(timestamp));
    }

    private static String queryJoin(List<NameValuePair> query) {
        if (null == query || query.isEmpty()) {
            return "";
        }
        String split = "";
        StringBuilder stringBuilder = new StringBuilder();
        query.sort(Comparator.comparing(NameValuePair::getName));
        for(NameValuePair entry : query) {
            stringBuilder.append(split).append(entry.getName());
            if (!Objects.isNull(entry.getValue())) {
                stringBuilder.append("=").append(entry.getValue()); split = "&";
            }
        }return stringBuilder.toString();
    }

    private HttpRequestBase ensureHttpRequest(String url, String body) {
        HttpMethod httpMethod = this.httpMethod();
        switch (httpMethod) {
            case GET: {
                return new HttpGet(url);
            }
            case POST: {
                HttpPost httpPost = new HttpPost(url);
                if (StrUtil.isNotBlank(body)) {
                    httpPost.setEntity(new StringEntity(body, StandardCharsets.UTF_8));
                }
                return httpPost;
            }
            case PUT: {
                HttpPut httpPut = new HttpPut(url);
                if (StrUtil.isNotBlank(body)) {
                    httpPut.setEntity(new StringEntity(body, StandardCharsets.UTF_8));
                }
                return httpPut;
            }
            case DELETE: {
                return new HttpDelete(url);
            }
            default: {
                throw new MithrasException("暂未支持的HTTP请求方式");
            }
        }
    }
}
