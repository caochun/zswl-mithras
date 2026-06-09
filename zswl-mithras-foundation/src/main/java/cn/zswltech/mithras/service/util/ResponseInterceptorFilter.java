package cn.zswltech.mithras.service.util;

import org.apache.http.HttpEntity;
import org.apache.http.HttpException;
import org.apache.http.HttpResponse;
import org.apache.http.HttpResponseInterceptor;
import org.apache.http.StatusLine;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class ResponseInterceptorFilter implements HttpResponseInterceptor {

    private static final Logger providerLogger = LoggerFactory.getLogger("PROVIDER");

    @Override
    public void process(HttpResponse response, HttpContext context) throws HttpException, IOException {
        HttpEntity entity = response.getEntity();
        BufferedHttpEntity bufferedHttpEntity = new BufferedHttpEntity(entity);
        response.setEntity(bufferedHttpEntity);
        String body = EntityUtils.toString(bufferedHttpEntity, StandardCharsets.UTF_8);
        StatusLine statusLine = response.getStatusLine();
        providerLogger.info("响应entity:{},响应statusCode:{}", body, statusLine.getStatusCode());
    }
}
