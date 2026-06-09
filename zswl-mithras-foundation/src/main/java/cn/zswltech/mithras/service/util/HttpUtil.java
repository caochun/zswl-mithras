package cn.zswltech.mithras.service.util;

import cn.zswltech.mithras.service.enums.ContentTypeEnum;
import cn.zswltech.mithras.service.others.SpringContextHolder;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.client.SystemDefaultCredentialsProvider;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.SSLContext;
import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * http调用工具类
 *
 * @author wang
 * @date 2022/4/11 7:10 PM
 */
public class HttpUtil {

    private static final Logger infoLogger = LoggerFactory.getLogger(HttpUtil.class);

    private static class LazyHttpsClientHolder {

        private static final CloseableHttpClient INSTANCE;
        private static final CloseableHttpClient OCR_INSTANCE;
        private static final CloseableHttpClient SHORT_INSTANCE;

        static {

            SSLContext sslContext = SSLContexts.createSystemDefault();

            SSLConnectionSocketFactory sslCSF = new SSLConnectionSocketFactory(
                    sslContext,
                    NoopHostnameVerifier.INSTANCE);
            PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
            // 设置连接池最大连接数
            cm.setMaxTotal(1000);
            // 设置路由的默认最大连接数（不设置默认值是2，设置过小不能支持大并发）
            cm.setDefaultMaxPerRoute(1000);

            RequestConfig requestConfig = RequestConfig.custom()
                    // 建立连接的超时时间
                    .setConnectTimeout(10000)
                    // 读取数据的超时时间
                    .setSocketTimeout(30000)
                    // 从连接池获取请求的超时时间
                    .setConnectionRequestTimeout(3000)
                    .build();

            RequestConfig shortRequestConfig = RequestConfig.custom()
                    // 建立连接的超时时间
                    .setConnectTimeout(1000)
                    // 读取数据的超时时间
                    .setSocketTimeout(3000)
                    // 从连接池获取请求的超时时间
                    .setConnectionRequestTimeout(3000)
                    .build();

            /**
             * 阿里云ocr识别服务慢的一笔
             */
            RequestConfig ocrRequestConfig = RequestConfig.custom()
                    // 建立连接的超时时间
                    .setConnectTimeout(60000)
                    // 读取数据的超时时间
                    .setSocketTimeout(60000)
                    // 从连接池获取请求的超时时间
                    .setConnectionRequestTimeout(500)
                    .build();

            // 创建过滤器
            RequestInterceptorFilter requestInterceptorFilter = new RequestInterceptorFilter();
            ResponseInterceptorFilter responseInterceptorFilter = new ResponseInterceptorFilter();

            HttpClientBuilder instanceBuilder = HttpClients.custom()
                    .setConnectionManager(cm)
                    .evictExpiredConnections()
                    .addInterceptorFirst(requestInterceptorFilter)
                    .addInterceptorLast(responseInterceptorFilter)
                    .evictIdleConnections(5L, TimeUnit.SECONDS)
                    .setDefaultRequestConfig(requestConfig)
                    .setSSLSocketFactory(sslCSF)
                    // 重试次数设为0次（默认3次）
                    .setRetryHandler(new DefaultHttpRequestRetryHandler(0, false));

            HttpClientBuilder shortInstanceBuilder = HttpClients.custom()
                    .setConnectionManager(cm)
                    .evictExpiredConnections()
                    // 不打印请求体 因为是 base64
                    //.addInterceptorFirst(requestInterceptorFilter)
                    .addInterceptorLast(responseInterceptorFilter)
                    .evictIdleConnections(5L, TimeUnit.SECONDS)
                    .setDefaultRequestConfig(shortRequestConfig)
                    .setSSLSocketFactory(sslCSF)
                    // 重试次数设为0次（默认3次）
                    .setRetryHandler(new DefaultHttpRequestRetryHandler(0, false));

            HttpClientBuilder ocrInstanceBuilder = HttpClients.custom()
                    .setConnectionManager(cm)
                    .evictExpiredConnections()
                    // 不打印请求体 因为是 base64
                    //.addInterceptorFirst(requestInterceptorFilter)
                    .addInterceptorLast(responseInterceptorFilter)
                    .evictIdleConnections(5L, TimeUnit.SECONDS)
                    .setDefaultRequestConfig(ocrRequestConfig)
                    .setSSLSocketFactory(sslCSF)
                    // 重试次数设为0次（默认3次）
                    .setRetryHandler(new DefaultHttpRequestRetryHandler(0, false));

            String httpProxyUrl = SpringContextHolder.getProperty("mithras.http.proxy.host");
            Integer httpProxyPort = SpringContextHolder.getProperty("mithras.http.proxy.port", Integer.class);
            if (StringUtils.isNotBlank(httpProxyUrl) && Objects.nonNull(httpProxyPort)) {
                // 内网往外网访问需要配置代理
                CredentialsProvider credentialsProvider = new SystemDefaultCredentialsProvider();
                credentialsProvider.setCredentials(//指定需要身份验证的站点，即代理地址，全部用AuthScope.ANY
                        AuthScope.ANY,
                        //用户名、密码
                        new UsernamePasswordCredentials("", ""));
                HttpHost proxy = new HttpHost(httpProxyUrl, httpProxyPort);
                // proxy、credentialsProvider 内网调用外网接口需要设置代理
                instanceBuilder.setProxy(proxy)
                        .setDefaultCredentialsProvider(credentialsProvider);
                ocrInstanceBuilder.setProxy(proxy).
                        setDefaultCredentialsProvider(credentialsProvider);
                shortInstanceBuilder.setProxy(proxy).
                        setDefaultCredentialsProvider(credentialsProvider);
            }
            INSTANCE = instanceBuilder.build();
            OCR_INSTANCE = ocrInstanceBuilder.build();
            SHORT_INSTANCE = shortInstanceBuilder.build();
        }
    }

    public static CloseableHttpClient getHttpsClientInstance() {
        return LazyHttpsClientHolder.INSTANCE;
    }

    public static CloseableHttpClient getOcrHttpsClientInstance() {
        return LazyHttpsClientHolder.OCR_INSTANCE;
    }

    public static CloseableHttpClient getShortHttpsClientInstance() {
        return LazyHttpsClientHolder.SHORT_INSTANCE;
    }

    /**
     * 发送GET 请求
     *
     * @param url 请求地址
     * @return 响应
     */
    public static String httpGetRequest(String url) throws IOException {
        return httpGetRequest(url, null);
    }

    /**
     * 发送GET 请求
     *
     * @param url    请求地址
     * @return 响应
     */
    public static String httpGetRequest(String url, Map<String, String> header) throws IOException {
        return httpGetRequest(HttpUtil.getHttpsClientInstance(), url, header);
    }

    /**
     * 发送GET 请求
     *
     * @param url    请求地址
     * @return 响应
     */
    public static String httpGetRequest(CloseableHttpClient httpClient, String url) throws IOException {
        return httpGetRequest(httpClient, url, null);
    }

    /**
     * 发送GET 请求
     *
     * @param httpClient    client
     * @param url    请求地址
     * @param header header
     * @return 响应
     */
    public static String httpGetRequest(CloseableHttpClient httpClient, String url, Map<String, String> header) throws IOException {
        CloseableHttpResponse response = null;
        long startTime = System.currentTimeMillis();
        try {

            HttpGet httpGet = new HttpGet(url);
            if (!CollectionUtils.isEmpty(header)) {
                for (String s : header.keySet()) {
                    httpGet.setHeader(s, header.get(s));
                }
            }
            response = httpClient.execute(httpGet);
            HttpEntity entity = response.getEntity();

            long endTime = System.currentTimeMillis();

            infoLogger.info("httpClient get execute success,url:{} , time:{}ms", url, endTime - startTime);
            return EntityUtils.toString(entity, "UTF-8");

        } catch (Exception throwable) {
            infoLogger.warn("failed to do http get request", throwable);
            long endTime = System.currentTimeMillis();

            infoLogger.info("httpClient get execute throw exception,url:{}, time:{}ms", url, endTime - startTime);
            throw new IOException(throwable);
        } finally {
            responseClose(response, "get");
        }
    }

    /**
     * 发送POST 请求
     *
     * @param url     请求地址
     * @param content 请求内容
     * @return 响应
     */
    public static String httpPostJsonRequest(String url, String content) throws IOException {
        return post(url, content, ContentTypeEnum.json);
    }

    /**
     * 发送POST 请求
     *
     * @param url     请求地址
     * @param content 请求内容
     * @return 响应
     */
    public static String httpPostJsonRequest(String url, String content,Map<String,String> headers) throws IOException {
        return post(url, content, ContentTypeEnum.json,headers);
    }

    public static byte[] httpPostJsonRequestReturnByte(String url, String content) throws IOException {
        return postReturnByte(url, content, ContentTypeEnum.json);
    }

    /**
     * 发送POST 请求
     *
     * @param url         请求地址
     * @return 响应
     */
    public static String httpPostFormRequest(String url, Map<String, Object> paramMap, Map<String, String> header) throws IOException {
        return doHttpPostFormRequest(HttpUtil.getHttpsClientInstance(), url, paramMap, header);
    }

    public static String ocrRequest(String url, Map<String, Object> paramMap) throws IOException {
        return doHttpPostFormRequest(HttpUtil.getOcrHttpsClientInstance(), url, paramMap, new HashMap<>());
    }

    public static String shortPostRequest(String url, String content, Map<String, String> header) throws IOException {
        return shortPost(url, content, ContentTypeEnum.json, header);
    }

    public static String doHttpPostFormRequest(CloseableHttpClient httpClient, String url, Map<String, Object> paramMap, Map<String, String> header) throws IOException {
        CloseableHttpResponse response = null;
        long startTime = System.currentTimeMillis();
        try {

            HttpPost httpPost = new HttpPost(url);
            if (!CollectionUtils.isEmpty(header)) {
                for (String s : header.keySet()) {
                    httpPost.setHeader(s, header.get(s));
                }
            }
            List<NameValuePair> nvps = new ArrayList<>();
            for (String key : paramMap.keySet()) {
                String value = Optional.ofNullable(paramMap.get(key)).map(String::valueOf).orElse(null);

                NameValuePair nameValuePair = new BasicNameValuePair(key, value);
                nvps.add(nameValuePair);
            }
            httpPost.addHeader("Content-Type", "application/x-www-form-urlencoded"); // 添加请求头
            httpPost.setEntity(new UrlEncodedFormEntity(nvps, "UTF-8"));
            response = httpClient.execute(httpPost);

            long endTime = System.currentTimeMillis();

            infoLogger.info("httpClient post execute success,url:{} , time:{}", url, endTime - startTime);
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {

                HttpEntity entity = response.getEntity();
                return EntityUtils.toString(entity, "UTF-8");

            } else {
                return null;
            }

        } catch (Exception throwable) {

            long endTime = System.currentTimeMillis();
            infoLogger.info("httpClient post execute throw exception,url:{}, time:{}ms", url, endTime - startTime);
            infoLogger.warn("failed to do http post request", throwable);
            throw new IOException(throwable);
        } finally {
            responseClose(response, "post");
        }
    }

    /**
     * 发送POST 请求
     *
     * @param url         请求地址
     * @param content     请求内容
     * @param contentType 编码类型
     * @return 响应
     */
    public static String post(String url, String content, ContentTypeEnum contentType,Map<String,String> headers) throws IOException {
        return doPost(HttpUtil.getHttpsClientInstance(), url, content, contentType, headers);
    }

    public static String shortPost(String url, String content, ContentTypeEnum contentType,Map<String,String> headers) throws IOException {
        return doPost(HttpUtil.getShortHttpsClientInstance(), url, content, contentType, headers);
    }

    private static String doPost(CloseableHttpClient httpClient, String url, String content, ContentTypeEnum contentType, Map<String, String> headers) throws IOException {
        CloseableHttpResponse response = null;
        long startTime = System.currentTimeMillis();
        try {

            HttpPost httpPost = new HttpPost(url);
            StringEntity s = new StringEntity(content, "UTF-8");
            s.setContentEncoding("UTF-8");
            s.setContentType(contentType.getValue());
            httpPost.setEntity(s);
            if (!CollectionUtils.isEmpty(headers)) {
                for (String header : headers.keySet()) {
                    httpPost.setHeader(header, headers.get(header));
                }
            }
            response = httpClient.execute(httpPost);

            long endTime = System.currentTimeMillis();

            infoLogger.info("httpClient post execute success,url:{} , time:{}", url, endTime - startTime);
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {

                HttpEntity entity = response.getEntity();
                return EntityUtils.toString(entity, "UTF-8");

            } else {
                infoLogger.warn("httpClient post execute fail,url:{} , response:{}", url, JSONObject.toJSONString(response));
                return null;
            }

        } catch (Exception throwable) {

            long endTime = System.currentTimeMillis();
            infoLogger.info("httpClient post execute throw exception,url:{}, time:{}ms", url, endTime - startTime);
            infoLogger.warn("failed to do http post request", throwable);
            throw new IOException(throwable);
        } finally {
            responseClose(response, "post");
        }
    }


    /**
     * 发送POST 请求
     *
     * @param url         请求地址
     * @param content     请求内容
     * @param contentType 编码类型
     * @return 响应
     */
    public static String post(String url, String content, ContentTypeEnum contentType) throws IOException {
        CloseableHttpClient httpClient = HttpUtil.getHttpsClientInstance();
        CloseableHttpResponse response = null;
        long startTime = System.currentTimeMillis();
        try {

            HttpPost httpPost = new HttpPost(url);
            StringEntity s = new StringEntity(content, "UTF-8");
            s.setContentEncoding("UTF-8");
            s.setContentType(contentType.getValue());
            httpPost.setEntity(s);

            response = httpClient.execute(httpPost);

            long endTime = System.currentTimeMillis();

            infoLogger.info("httpClient post execute success,url:{} , time:{}", url, endTime - startTime);
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {

                HttpEntity entity = response.getEntity();
                return EntityUtils.toString(entity, "UTF-8");

            } else {
                return null;
            }

        } catch (Exception throwable) {

            long endTime = System.currentTimeMillis();
            infoLogger.info("httpClient post execute throw exception,url:{}, time:{}ms", url, endTime - startTime);
            infoLogger.warn("failed to do http post request", throwable);
            throw new IOException(throwable);
        } finally {
            responseClose(response, "post");
        }
    }

    private static void responseClose(CloseableHttpResponse response, String requestMethod) {
        if (response != null) {
            try {
                response.close();
            } catch (IOException e) {
                infoLogger.error("httpClient close fail,requestMethod:{},e:{}", requestMethod, e);
            }
        }
    }

    /**
     * 将map转换为请求字符串
     * <p>
     * data=xxx&msg_type=xxx
     * </p>
     *
     * @param params
     * @param charset
     * @return
     * @throws IOException
     */
    public static String buildQuery(Map<String, Object> params, String charset) throws IOException {
        if (params == null || params.isEmpty()) {
            return null;
        }

        // 设置参数字符集
        if (charset == null) {
            charset = "utf-8";
        }

        StringBuffer data = new StringBuffer();
        boolean flag = false;

        for (Map.Entry<String, Object> entry : params.entrySet()) {
            if (entry.getValue() == null) {
                continue;
            }

            if (flag) {
                data.append("&");
            } else {
                flag = true;
            }

            data.append(entry.getKey())
                    .append("=")
                    .append(URLEncoder.encode(entry.getValue().toString(), charset));
        }
        return data.toString();

    }

    private static final RestTemplate REST_TEMPLATE = buildRestTemplate();

    private static RestTemplate buildRestTemplate() {
        // 新建RestTemplate，使用默认的HttpMessageConverter
        RestTemplate restTemplate = new RestTemplate();
        // 使用HttpClient作为底层实现
        HttpComponentsClientHttpRequestFactory httpComponentsClientHttpRequestFactory =
                new HttpComponentsClientHttpRequestFactory(getHttpsClientInstance());
        restTemplate.setRequestFactory(httpComponentsClientHttpRequestFactory);
        // 设置响应错误处理器
        restTemplate.setErrorHandler(new DefaultResponseErrorHandler() {
            @Override
            public void handleError(ClientHttpResponse response) {
                // no op
            }
        });
        return restTemplate;
    }

    /**
     * 执行一次http请求
     *
     * @param requestEntity 请求对象，封装了请求方法、url、请求头、请求体
     * @param responseType  响应体类型
     * @param <I>           响应体类型参数
     * @param <O>           请求体类型参数
     * @return 响应对象，封装了响应码、响应头、响应体
     */
    public static <I, O> ResponseEntity<I> exchange(RequestEntity<O> requestEntity, Class<I> responseType) {
        return REST_TEMPLATE.exchange(requestEntity, responseType);
    }

    public static byte[] postReturnByte(String url, String content, ContentTypeEnum contentType) throws IOException {

        byte[] buffer = null;

        CloseableHttpClient httpClient = HttpUtil.getHttpsClientInstance();
        CloseableHttpResponse response = null;
        long startTime = System.currentTimeMillis();
        try {

            HttpPost httpPost = new HttpPost(url);
            StringEntity s = new StringEntity(content, "UTF-8");
            s.setContentEncoding("UTF-8");
            s.setContentType(contentType.getValue());
            httpPost.setEntity(s);

            response = httpClient.execute(httpPost);

            long endTime = System.currentTimeMillis();

            infoLogger.info("httpClient post execute success,url:{} , time:{}", url, endTime - startTime);
            //请求成功
            if (HttpStatus.SC_OK == response.getStatusLine().getStatusCode()) {
                //5.取得请求内容
                HttpEntity entity = response.getEntity();
                if (entity != null) {
                    InputStream input = entity.getContent();

                    ByteArrayOutputStream bos = new ByteArrayOutputStream(1000);
                    byte[] b = new byte[1000];
                    int n;
                    while ((n = input.read(b)) != -1) {
                        bos.write(b, 0, n);
                    }
                    input.close();
                    bos.close();
                    buffer = bos.toByteArray();
                }
            }
        } catch (Exception throwable) {

            long endTime = System.currentTimeMillis();
            infoLogger.info("httpClient post execute throw exception,url:{}, time:{}ms", url, endTime - startTime);
            infoLogger.warn("failed to do http post request", throwable);
            throw new IOException(throwable);
        } finally {
            responseClose(response, "post");
        }
        return buffer;
    }

    /**
     * 向指定 URL 发送POST方法的请求
     *
     * @param url 发送请求的 URL
     * @param param 请求参数，请求参数应该是 name1=value1&name2=value2 的形式。
     * @return 所代表远程资源的响应结果
     */
    public static String sendPost(String url, String param)
    {
        PrintWriter out = null;
        BufferedReader in = null;
        StringBuilder result = new StringBuilder();
        try
        {
            String urlNameString = url;
            infoLogger.info("sendPost - {}", urlNameString +" param - {}"+param);
            URL realUrl = new URL(urlNameString);
            URLConnection conn = realUrl.openConnection();
            conn.setRequestProperty("accept", "*/*");
            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            conn.setRequestProperty("Accept-Charset", "utf-8");
            conn.setRequestProperty("contentType", "utf-8");
            conn.setDoOutput(true);
            conn.setDoInput(true);
            out = new PrintWriter(conn.getOutputStream());
            out.print(param);
            out.flush();
            in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"));
            String line;
            while ((line = in.readLine()) != null)
            {
                result.append(line);
            }
            infoLogger.info("recv - {}", result);
        }
        catch (ConnectException e)
        {
            infoLogger.error("调用HttpUtils.sendPost ConnectException, url=" + url + ",param=" + param, e);
        }
        catch (SocketTimeoutException e)
        {
            infoLogger.error("调用HttpUtils.sendPost SocketTimeoutException, url=" + url + ",param=" + param, e);
        }
        catch (IOException e)
        {
            infoLogger.error("调用HttpUtils.sendPost IOException, url=" + url + ",param=" + param, e);
        }
        catch (Exception e)
        {
            infoLogger.error("调用HttpsUtil.sendPost Exception, url=" + url + ",param=" + param, e);
        }
        finally
        {
            try
            {
                if (out != null)
                {
                    out.close();
                }
                if (in != null)
                {
                    in.close();
                }
            }
            catch (IOException ex)
            {
                infoLogger.error("调用in.close Exception, url=" + url + ",param=" + param, ex);
            }
        }
        return result.toString();
    }

/*    public static void main(String[] args) {
        String url = "http://zhfk.zjzsfh.com/gungnirApi/user/getAuthCode";
        String param = "account=zszl&password=uQKLgQ02u9EWoxVHokXwn%2Fjaz8fwO2hn97vXou%2FAk5pzXRKrIccWKUCJZ6LfsQd79ghGupIdzW%2FQ%2FqFjxPqr1g7JSaHZG5vsMvx4LN8gwMHcczYNkNNNVVlo1GJM%2B6gbzZ2M85K6%2FMerdGxLIhUZRNewuN8BwIS4GkZRTIkn0Rw%3D";
        String responseResult=HttpUtil.sendPost(url,param);
        System.out.println(responseResult);
    }*/

}
