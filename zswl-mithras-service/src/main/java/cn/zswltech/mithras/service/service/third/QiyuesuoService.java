package cn.zswltech.mithras.service.service.third;

import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.http.Method;
import cn.zswltech.mithras.service.annotation.QiyuesuoApiLog;
import cn.zswltech.mithras.service.config.QiyuesuoConfig;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.service.third.model.qiyuesuo.*;
import com.alibaba.fastjson.JSONObject;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.qiyuesuo.v3sdk.model.contract.request.ContractCreatebycategoryRequest;
import net.qiyuesuo.v3sdk.model.contract.request.ContractDetailRequest;
import net.qiyuesuo.v3sdk.model.contract.request.ContractRecallRequest;
import net.qiyuesuo.v3sdk.model.document.request.DocumentDownloadRequest;
import net.qiyuesuo.v3sdk.model.seal.request.SealListRequest;
import net.qiyuesuo.v3sdk.model.v2auth.request.V2AuthCompanysignsilentUrlRequest;
import net.qiyuesuo.v3sdk.model.v2contract.request.V2ContractSignbycompanyRequest;
import net.qiyuesuo.v3sdk.model.v2contract.request.V2ContractSignbylegalpersonRequest;
import okhttp3.*;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.APPLICATION_OCTET_STREAM_VALUE;

/**
 * @author bigbear
 * @date 2024/11/20 16:52
 * @description 契约锁调用接口
 */
@Slf4j
@Service
public class QiyuesuoService {

    @Resource
    private QiyuesuoConfig qiyuesuoConfig;
    @Resource
    private RedisTemplate<String, String> redisTemplate;

    private Request.Builder baseRequestBuilder() {
        return new Request.Builder()
                .addHeader(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON_VALUE)
                .addHeader("x-qys-accesstoken", qiyuesuoConfig.getHeaders().getAccessToken())
                .addHeader("x-qys-signature", qiyuesuoConfig.getHeaders().getSignature())
                .addHeader("x-qys-timestamp", qiyuesuoConfig.getHeaders().getTimestamp().toString());
    }

    /**
     * 公司印章静默签署v2
     *
     * @param req 请求参数
     * @return SilentSealSignV2Response
     * @throws IOException io异常
     */
    @QiyuesuoApiLog
    public SilentSealSignV2Response silentSealSignV2(V2ContractSignbycompanyRequest req) throws IOException {
        log.info("公司印章静默签署v2请求参数：{}", req);
        String token = generateToken(URIEnum.SILENT_SEAL_SIGN_V2);
        OkHttpClient client = new OkHttpClient().newBuilder()
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .connectTimeout(30, TimeUnit.SECONDS)
                .build();
        String requestUrl = qiyuesuoConfig.getServer() + URIEnum.SILENT_SEAL_SIGN_V2.path;
        RequestBody requestBody = RequestBody.create(JSONObject.toJSONString(req), MediaType.parse(APPLICATION_JSON_VALUE));
        Request request = baseRequestBuilder()
                .url(requestUrl)
                .addHeader(HttpHeaders.AUTHORIZATION, token)
                .method(URIEnum.SILENT_SEAL_SIGN_V2.method, requestBody)
                .build();
        Response response = client.newCall(request).execute();
        log.info("公司印章静默签署v2接口返回：{}", response);
        if (!response.isSuccessful()) {
            throw new MithrasException("公司印章静默签署v2接口调用失败，" + response.message());
        }
        Assert.notNull(response.body(), "公司印章静默签署v2请求失败！");
        try {
            return JSONObject.parseObject(response.body().string(), SilentSealSignV2Response.class);
        } catch (IOException e) {
            log.error("公司印章静默签署v2接口返回不合法，请检查，接口数据：{}", response.body().string(), e);
            throw new RuntimeException(e);
        }
    }

    /**
     * 下载合同文档
     *
     * @param req 请求参数
     * @return DownloadContractResponse
     * @throws IOException io异常
     */
    @QiyuesuoApiLog
    public DownloadContractResponse downloadContract(DocumentDownloadRequest req) throws IOException {
        log.info("下载合同文档请求参数：{}", req);
        String token = generateToken(URIEnum.DOWNLOAD_CONTRACT_DOCUMENT);
        OkHttpClient client = new OkHttpClient().newBuilder()
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .connectTimeout(30, TimeUnit.SECONDS)
                .build();
        String requestUrl = qiyuesuoConfig.getServer() + URIEnum.DOWNLOAD_CONTRACT_DOCUMENT.path;
        HttpUrl httpUrl = Objects.requireNonNull(HttpUrl.parse(requestUrl)).newBuilder()
                .addEncodedQueryParameter("documentId", String.valueOf(req.getDocumentId()))
                .build();
        Request request = baseRequestBuilder()
                .url(httpUrl)
                .addHeader(HttpHeaders.AUTHORIZATION, token)
                .method(URIEnum.DOWNLOAD_CONTRACT_DOCUMENT.method, null)
                .build();
        Response response = client.newCall(request).execute();
        log.info("下载合同文档接口返回：{}", response);
        if (!response.isSuccessful()) {
            log.error("下载合同文档接口调用失败，原因是：[{}]", response.message());
            throw new MithrasException("下载合同文档接口调用失败，" + response.message());
        }
        Assert.notNull(response.body(), "下载合同文档请求失败！");
        // 将数据流转化为文件
        DownloadContractResponse downloadContractResponse = new DownloadContractResponse();
        MockMultipartFile file = new MockMultipartFile("file", response.body().byteStream());
        downloadContractResponse.setFile(file);
        return downloadContractResponse;
    }

    /**
     * 创建合同
     *
     * @param req 请求参数
     * @return CreateContractResponse
     * @throws IOException io异常
     */
    @QiyuesuoApiLog
    public CreateContractResponse createContract(ContractCreatebycategoryRequest req) throws IOException {
        log.info("创建合同请求参数：{}", req);
        String token = generateToken(URIEnum.CREATE_CONTRACT);
        OkHttpClient client = new OkHttpClient().newBuilder()
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .connectTimeout(30, TimeUnit.SECONDS)
                .build();
        String requestUrl = qiyuesuoConfig.getServer() + URIEnum.CREATE_CONTRACT.path;
        RequestBody requestBody = RequestBody.create(JSONObject.toJSONString(req), MediaType.parse(APPLICATION_JSON_VALUE));
        Request request = baseRequestBuilder()
                .url(requestUrl)
                .addHeader(HttpHeaders.AUTHORIZATION, token)
                .method(URIEnum.CREATE_CONTRACT.method, requestBody)
                .build();
        Response response = client.newCall(request).execute();
        log.info("创建合同接口返回：{}", response);
        if (!response.isSuccessful()) {
            throw new MithrasException("创建合同接口调用失败，" + response.message());
        }
        Assert.notNull(response.body(), "创建合同请求失败！");
        try {
            CreateContractResponse createContractResponse = JSONObject.parseObject(response.body().string(), CreateContractResponse.class);
            if (createContractResponse.getContractId() == null) {
                throw MithrasException.newException("创建合同接口调用失败，原因是：" + createContractResponse.getMessage());
            }
            return createContractResponse;
        } catch (IOException e) {
            log.error("创建合同接口返回不合法，请检查，接口数据：{}", response.body().string(), e);
            throw new RuntimeException(e);
        }
    }

    /**
     * 查询合同详情
     *
     * @param req 请求参数 四个参数传合同ID即可
     * @return ContractDetailResponse
     * @throws IOException io异常
     */
    @QiyuesuoApiLog
    public ContractDetailResponse contractDetail(ContractDetailRequest req) throws IOException {
        log.info("查询合同详情请求参数：{}", req);
        String token = generateToken(URIEnum.CONTRACT_DETAIL);
        OkHttpClient client = new OkHttpClient().newBuilder()
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .connectTimeout(30, TimeUnit.SECONDS)
                .build();
        String requestUrl = qiyuesuoConfig.getServer() + URIEnum.CONTRACT_DETAIL.path;
        HttpUrl httpUrl = Objects.requireNonNull(HttpUrl.parse(requestUrl)).newBuilder()
                .addQueryParameter("contractId", String.valueOf(req.getContractId()))
                .build();
        Request request = baseRequestBuilder()
                .url(httpUrl)
                .addHeader(HttpHeaders.AUTHORIZATION, token)
                .method(URIEnum.CONTRACT_DETAIL.method, null)
                .build();
        Response response = client.newCall(request).execute();
        log.info("查询合同详情接口返回：{}", response);
        if (!response.isSuccessful()) {
            throw new MithrasException("查询合同详情接口调用失败，" + response.message());
        }
        Assert.notNull(response.body(), "查询合同详情请求失败！");
        try {
            return JSONObject.parseObject(response.body().string(), ContractDetailResponse.class);
        } catch (IOException e) {
            log.error("查询合同详情接口返回不合法，请检查，接口数据：{}", response.body().string(), e);
            throw new RuntimeException(e);
        }
    }

    /**
     * 上传本地签署文件创建待签署的文档
     *
     * @param req 请求参数
     * @return UploadLocalFileResponse
     * @throws IOException io异常
     */
    @QiyuesuoApiLog
    public UploadLocalFileResponse uploadLocalFile(UploadLocalFileRequest req) throws IOException {
        log.info("上传本地签署文件创建待签署的文档请求参数：{}", req);
        String token = generateToken(URIEnum.UPLOAD_FILE_CREATE_SIGN_DOCUMENT);
        OkHttpClient client = new OkHttpClient().newBuilder()
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .connectTimeout(30, TimeUnit.SECONDS)
                .build();
        String requestUrl = qiyuesuoConfig.getServer() + URIEnum.UPLOAD_FILE_CREATE_SIGN_DOCUMENT.path;
        HttpUrl.Builder urlBuilder = Objects.requireNonNull(HttpUrl.parse(requestUrl)).newBuilder();
        MultipartBody multipartBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file", req.getFile().getOriginalFilename(),
                        RequestBody.create(req.getFile().getBytes(), MediaType.parse(APPLICATION_OCTET_STREAM_VALUE)))
                .addFormDataPart("title", req.getTitle())
                .addFormDataPart("fileType", req.getFileType().getValue())
                .build();
        Request request = baseRequestBuilder()
                .url(urlBuilder.build())
                .addHeader(HttpHeaders.AUTHORIZATION, token)
                .method(URIEnum.UPLOAD_FILE_CREATE_SIGN_DOCUMENT.method, multipartBody)
                .build();
        Response response = client.newCall(request).execute();
        log.info("上传本地签署文件创建待签署的文档接口返回：{}", response);
        if (!response.isSuccessful()) {
            throw new MithrasException("上传本地签署文件创建待签署的文档接口调用失败，" + response.message());
        }
        Assert.notNull(response.body(), "上传本地签署文件创建待签署的文档请求失败！");
        try {
            return JSONObject.parseObject(response.body().string(), UploadLocalFileResponse.class);
        } catch (IOException e) {
            log.error("上传本地签署文件创建待签署的文档接口返回不合法，请检查，接口数据：{}", response.body().string(), e);
            throw new RuntimeException(e);
        }
    }

    /**
     * 获取印章列表
     *
     * @param req 请求参数
     * @return GetUnitSealListResponse
     * @throws IOException io异常
     */
    @QiyuesuoApiLog
    public GetUnitSealListResponse getUnitSealList(SealListRequest req) throws IOException {
        log.info("获取印章列表请求参数：{}", req);
        String token = generateToken(URIEnum.GET_UNIT_SEAL_LIST);
        log.info("token:{}", token);
        OkHttpClient client = new OkHttpClient().newBuilder()
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .connectTimeout(30, TimeUnit.SECONDS)
                .build();
        String requestUrl = qiyuesuoConfig.getServer() + URIEnum.GET_UNIT_SEAL_LIST.path;
        HttpUrl.Builder urlBuilder = Objects.requireNonNull(HttpUrl.parse(requestUrl)).newBuilder();
        FormBody formBody = new FormBody.Builder()
                .add("name", req.getName())
                .add("sealCategoryName", req.getSealCategoryName())
                .add("sealAttribute", req.getSealAttribute())
                .build();
        Request request = baseRequestBuilder()
                .url(urlBuilder.build())
                .addHeader(HttpHeaders.AUTHORIZATION, token)
                .method(URIEnum.GET_UNIT_SEAL_LIST.method, formBody)
                .build();
        Response response = client.newCall(request).execute();
        log.info("获取印章列表接口返回：{}", response);
        if (!response.isSuccessful()) {
            throw new MithrasException("获取印章列表接口调用失败，" + response.message());
        }
        Assert.notNull(response.body(), "获取印章列表请求失败");
        String data = response.body().string();
        log.info("response:{}", JSONObject.toJSON(data).toString());
        try {
            return JSONObject.parseObject(data, GetUnitSealListResponse.class);
        } catch (Exception e) {
            log.error("印章列表接口返回不合法，请检查，接口数据：{}", data, e);
            throw new RuntimeException(e);
        }
    }

    /**
     * 法定代表人静默签署V2
     *
     * @param req 请求参数
     * @throws IOException io异常
     */
    @QiyuesuoApiLog
    public void legalPersonSealSignV2(V2ContractSignbylegalpersonRequest req) throws IOException {
        log.info("法定代表人静默签署V2请求参数：{}", req);
        String token = generateToken(URIEnum.LEGAL_PERSON_SEAL_SIGN_V2);
        OkHttpClient client = new OkHttpClient().newBuilder()
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .connectTimeout(30, TimeUnit.SECONDS)
                .build();
        String requestUrl = qiyuesuoConfig.getServer() + URIEnum.LEGAL_PERSON_SEAL_SIGN_V2.path;
        HttpUrl.Builder urlBuilder = Objects.requireNonNull(HttpUrl.parse(requestUrl)).newBuilder();
        RequestBody requestBody = RequestBody.create(JSONObject.toJSONString(req), MediaType.parse(APPLICATION_JSON_VALUE));
        Request request = baseRequestBuilder()
                .url(urlBuilder.build())
                .addHeader(HttpHeaders.AUTHORIZATION, token)
                .method(URIEnum.LEGAL_PERSON_SEAL_SIGN_V2.method, requestBody)
                .build();
        try {
            Response response = client.newCall(request).execute();
            log.info("legalPersonSealSignV2接口返回：{}", response);
            if (!response.isSuccessful()) {
                throw new MithrasException("legalPersonSealSignV2接口调用失败，" + response.message());
            }
        } catch (IOException e) {
            log.error("请求失败", e);
            throw e;
        }
    }

    /**
     * 企业印章静默签署授权链接
     *
     * @param req 请求参数
     * @return String url
     * @throws IOException io异常
     */
    @QiyuesuoApiLog
    public V2AuthCompanySignSilentUrlResponse authCompanySignSilentUrl(V2AuthCompanysignsilentUrlRequest req) throws IOException {
        log.info("企业印章静默签署授权链接请求参数：{}", req);
        String token = generateToken(URIEnum.SILENT_SEAL_SIGN_AUTH_URL);
        OkHttpClient client = new OkHttpClient().newBuilder()
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .connectTimeout(30, TimeUnit.SECONDS)
                .build();
        String requestUrl = qiyuesuoConfig.getServer() + URIEnum.SILENT_SEAL_SIGN_AUTH_URL.path;
        HttpUrl.Builder urlBuilder = Objects.requireNonNull(HttpUrl.parse(requestUrl)).newBuilder();
        RequestBody requestBody = RequestBody.create(JSONObject.toJSONString(req), MediaType.parse(APPLICATION_JSON_VALUE));
        Request request = baseRequestBuilder()
                .url(urlBuilder.build())
                .addHeader(HttpHeaders.AUTHORIZATION, token)
                .method(URIEnum.SILENT_SEAL_SIGN_AUTH_URL.method, requestBody)
                .build();
        try {
            Response response = client.newCall(request).execute();
            log.info("企业印章静默签署授权链接接口返回：{}", response);
            if (!response.isSuccessful()) {
                throw new MithrasException("企业印章静默签署授权链接接口调用失败，" + response.message());
            }
            Assert.notNull(response.body(), "企业印章静默签署授权链接请求失败");
            String data = response.body().string();
            return JSONObject.parseObject(data, V2AuthCompanySignSilentUrlResponse.class);
        } catch (Exception e) {
            log.error("企业印章静默签署授权链接调用失败", e);
            throw e;
        }
    }

    /**
     * 撤回合同
     *
     * @param req 请求参数
     * @throws IOException io异常
     */
    @QiyuesuoApiLog
    public void withdrawContract(ContractRecallRequest req) throws IOException {
        log.info("撤回合同请求参数：{}", req);
        String token = generateToken(URIEnum.WITHDRAW_CONTRACT);
        OkHttpClient client = new OkHttpClient().newBuilder()
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .connectTimeout(30, TimeUnit.SECONDS)
                .build();
        String requestUrl = qiyuesuoConfig.getServer() + URIEnum.WITHDRAW_CONTRACT.path;
        RequestBody requestBody = RequestBody.create(JSONObject.toJSONString(req), MediaType.parse(APPLICATION_JSON_VALUE));
        Request request = baseRequestBuilder()
                .url(requestUrl)
                .addHeader(HttpHeaders.AUTHORIZATION, token)
                .method(URIEnum.WITHDRAW_CONTRACT.method, requestBody)
                .build();
        try {
            Response response = client.newCall(request).execute();
            log.info("撤回合同接口返回：{}", response);
            if (!response.isSuccessful()) {
                throw new MithrasException("撤回合同接口调用失败，" + response.message());
            }
        } catch (Exception e) {
            log.error("撤回合同接口调用失败", e);
            throw e;
        }
    }

    /**
     * 访问契约锁的接口，获取token
     *
     * @param uriEnum 请求路径
     * @return token
     */
    @QiyuesuoApiLog
    private String generateToken(URIEnum uriEnum) {
        String token;
        // 首先尝试从redis中获取token
        token = redisTemplate.opsForValue().get(uriEnum.path);
        if (CharSequenceUtil.isNotBlank(token)) {
            log.info("获取到redis中token:{}", token);
            return token;
        }
        String requestUrl = qiyuesuoConfig.getServer() + uriEnum.path + "/oauth2/token";
        log.info("契约锁requestUrl:{}", requestUrl);

        OkHttpClient client = new OkHttpClient().newBuilder()
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .connectTimeout(30, TimeUnit.SECONDS)
                .build();
        MediaType mediaType = MediaType.parse(APPLICATION_JSON_VALUE);
        Map<String, String> requestJo = new HashMap<>();
        requestJo.put("client_id", qiyuesuoConfig.getClientId());
        requestJo.put("client_secret", qiyuesuoConfig.getClientSecret());
        requestJo.put("grant_type", "client_credentials");

        RequestBody body = RequestBody.create(JSONObject.toJSONString(requestJo), mediaType);
        Request request = new Request.Builder()
                .url(requestUrl)
                .method(Method.POST.name(), body)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful() && response.body() != null) {
                String responseBody = response.body().string();
                JSONObject responseJo = JSONObject.parseObject(responseBody);
                token = responseJo.getString("access_token");
            } else {
                log.error("契约锁获取token失败，响应结果:【{}】", response);
                throw MithrasException.newException(response.message());
            }
        } catch (IOException ex) {
            log.error("契约锁获取token失败", ex);
            throw MithrasException.newException(ex.getMessage());
        }
        String fullToken = "Bearer " + token;
        // 将token缓存到redis中
        redisTemplate.opsForValue().set(uriEnum.path, token, 15, TimeUnit.MINUTES);
        return fullToken;
    }

    @Getter
    @AllArgsConstructor
    public enum URIEnum {
        GET_SEAL_DETAIL_VIEW_URL(Method.POST.name(), "/jttj/contract_lock/seal/viewurl", "获取印章详情页面"),
        GET_UNIT_SEAL_LIST(Method.POST.name(), "/jttj/contract_lock/seal/list", "获取单位印章列表"),
        INITIATE_CONTRACT_CANCEL(Method.POST.name(), "/jttj/contract_lock/contract/initiate/cancel", "发起合同作废"),
        CONTRACT_SIGN_URL_V3(Method.POST.name(), "/jttj/contract_lock/contract/signurl/v3", "合同签署页面v3"),
        WITHDRAW_CONTRACT(Method.POST.name(), "/jttj/contract_lock/contract/recall", "撤回合同"),
        DOWNLOAD_CONTRACT_DOCUMENT(Method.GET.name(), "/jttj/contract_lock/document/download", "下载合同文档"),
        SILENT_SEAL_SIGN_AUTH_URL(Method.POST.name(), "/jttj/contract_lock/v2/auth/companysignsilent/url", "企业印章静默签署授权链接"),
        SILENT_SEAL_SIGN_V2(Method.POST.name(), "/jttj/contract_lock/v2/contract/signbycompany", "公司印章静默签署v2"),
        UPLOAD_FILE_CREATE_SIGN_DOCUMENT(Method.POST.name(), "/jttj/contract_lock/v2/document/createbyfile", "上传本地文件创建待签署文档"),
        CREATE_CONTRACT(Method.POST.name(), "/jttj/contract_lock/contract/createbycategory", "创建合同"),
        CONTRACT_DETAIL(Method.GET.name(), "/jttj/contract_lock/contract/detail", "查询合同详情信息"),
        LEGAL_PERSON_SEAL_SIGN_V2(Method.POST.name(), "/jttj/contract_lock/v2/contract/signbylegalperson", " 法定代表人静默签署V2"),
        ;

        private final String method;
        private final String path;
        private final String display;
    }
}
