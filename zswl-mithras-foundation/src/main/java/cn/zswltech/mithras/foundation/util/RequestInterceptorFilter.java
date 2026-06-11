package cn.zswltech.mithras.foundation.util;

import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.RequestLine;
import org.apache.http.client.methods.HttpRequestWrapper;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class RequestInterceptorFilter implements HttpRequestInterceptor {

    private static final Logger providerLogger = LoggerFactory.getLogger("PROVIDER");

    @Override
    public void process(HttpRequest request, HttpContext context) throws HttpException, IOException {
        if (request instanceof HttpRequestWrapper) {
            request = ((HttpRequestWrapper) request).getOriginal();
        }
        RequestLine requestLine = request.getRequestLine();
        String uri = requestLine.getUri();
        String method = requestLine.getMethod();
        String body = null;
        if (request instanceof HttpEntityEnclosingRequest) {
            HttpEntityEnclosingRequest entityEnclosingRequest = (HttpEntityEnclosingRequest) request;
            HttpEntity entity = entityEnclosingRequest.getEntity();
            // 可重复读
            BufferedHttpEntity bufferedHttpEntity = new BufferedHttpEntity(entity);
            entityEnclosingRequest.setEntity(bufferedHttpEntity);
            body = EntityUtils.toString(bufferedHttpEntity, StandardCharsets.UTF_8);
        }
        providerLogger.info("请求uri:{},请求method:{},请求体:{}", uri, method, body);
    }
}
