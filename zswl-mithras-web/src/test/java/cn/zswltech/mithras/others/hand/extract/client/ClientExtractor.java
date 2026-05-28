package cn.zswltech.mithras.others.hand.extract.client;

import cn.hutool.core.io.IoUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.io.FileOutputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 客户信息
 *
 * @author wangchuanhao
 * @date 2022/8/18 12:23 PM
 */
@Slf4j
public class ClientExtractor {

    private static final String OUTPUT_JSON_FILE_PATH = "/Users/wang/Desktop/客户模块数据整理json_线上_20220818.json";

    @SneakyThrows
    public static void extractList() {
        OkHttpClient client = new OkHttpClient().newBuilder()
                .connectTimeout(600L, TimeUnit.SECONDS)
                .readTimeout(600L, TimeUnit.SECONDS)
                .writeTimeout(600L, TimeUnit.SECONDS)
                .build();
        MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded; charset=UTF-8");
        RequestBody body = RequestBody.create(mediaType, "_request_data=%7B%22parameter%22%3A%7B%22randomString%22%3A%220.8678202229462741Thu%20Aug%2018%202022%2012%3A23%3A52%20GMT%2B0800%20(%E4%B8%AD%E5%9B%BD%E6%A0%87%E5%87%86%E6%97%B6%E9%97%B4)%22%7D%7D");
        Request request = new Request.Builder()
                .url("http://10.100.222.10/core/layout/common/source?sqlId=com.hand.hls.prj.mapper.CusHlsBpMasterMapper.queryNew&layout_code=PRJ307&tab_code=G_QUERY_RESULT&bp_seq=&pagesize=10000&pageNum=1&_fetchall=true&_autocount=true")
                .method("POST", body)
                .addHeader("Accept", "*/*")
                .addHeader("Accept-Language", "zh-CN,zh;q=0.9")
                .addHeader("Connection", "keep-alive")
                .addHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8")
                .addHeader("Cookie", "vw=1440; vh=625; SESSIONID_HAP=9bf65e87-8bb2-4314-a600-97145c52ae23; TARGETURL=modules/PRJ/PRJ_BP/PRJ307/hls_bp_master_query_entrance.lview%3Ffunction_group_id%3D10226%26function_code%3DPRJ307; userId=10001; app_theme=hap; app_subject=HAP; EL-ADMIN-TOEKN=eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ7XCJhbGxvY2F0aW9uSWRcIjoxNDEsXCJ1c2VybmFtZVwiOlwiYWRtaW5cIn0iLCJleHAiOjE2NjA3OTYwODMsImlhdCI6MTY2MDcyNDA4M30.Z9LQ4VKGQ85zFq7ztXH6xifFMA1RcuoNQiFWHCEXVjb--zDfhoB4Itq39WlaoCDxnL5ItNKMTnP07jspZVjdnw; sidebarStatus=0")
                .addHeader("Origin", "http://10.100.222.10")
                .addHeader("Referer", "http://10.100.222.10/core/modules/PRJ/PRJ_BP/PRJ307/hls_bp_master_query_entrance.lview?function_group_id=10226&function_code=PRJ307")
                .addHeader("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/104.0.0.0 Safari/537.36")
                .addHeader("X-CSRF-TOKEN", "e22aa9b8-e4ac-4fc2-ae9d-bcfa43bbc06a")
                .addHeader("X-Requested-With", "XMLHttpRequest")
                .addHeader("springFlag", "Y")
                .build();
        Response response = client.newCall(request).execute();
        JSONObject resObj = JSONObject.parseObject(response.body().string());
        log.info("数据:{}", resObj.toJSONString());
        JSONArray dataArray = resObj.getJSONObject("result").getJSONArray("record");
        List<InitDataModel> initDataModelList = new ArrayList<>();
        for (int i = 0; i < dataArray.size(); i++) {
            JSONObject dataObj = dataArray.getJSONObject(i);
            InitDataModel initDataModel = InitDataModel.builder()
                    .clientName(dataObj.getString("bp_name"))
                    .bpClass(dataObj.getString("bp_class"))
                    .createUserName(dataObj.getString("owner_user_id_n"))
                    .build();
            if ("ORG".equals(initDataModel.getBpClass())) {
                JSONObject detailObj = extractCorp(dataObj.getString("bp_id"));
                initDataModel.setSocialCreditCode(detailObj.getString("social_credit_code"));
            } else if ("NP".equals(initDataModel.getBpClass())) {
                JSONObject detailObj = extractNormal(dataObj.getString("bp_id"));
                initDataModel.setIdCardNo(detailObj.getString("id_card_no"));
                initDataModel.setIdType(detailObj.getString("id_type"));
            }
            initDataModelList.add(initDataModel);
        }
        log.info("处理数据条数:{}", initDataModelList.size());
        IoUtil.write(new FileOutputStream(OUTPUT_JSON_FILE_PATH), true, JSON.toJSONString(initDataModelList).getBytes(StandardCharsets.UTF_8));
    }

    @SneakyThrows
    private static JSONObject extractCorp(String bpId) {
        OkHttpClient client = new OkHttpClient().newBuilder()
                .connectTimeout(600L, TimeUnit.SECONDS)
                .readTimeout(600L, TimeUnit.SECONDS)
                .writeTimeout(600L, TimeUnit.SECONDS)
                .build();
        MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded; charset=UTF-8");
        RequestBody body = RequestBody.create(mediaType, "_request_data=%7B%22parameter%22%3A%7B%22bp_id%22%3A%22" + bpId + "%22%2C%22randomString%22%3A%220.5146057423700023Thu%20Aug%2018%202022%2012%3A50%3A07%20GMT%2B0800%20(%E4%B8%AD%E5%9B%BD%E6%A0%87%E5%87%86%E6%97%B6%E9%97%B4)%22%7D%7D");
        Request request = new Request.Builder()
                .url("http://10.100.222.10/core/layout/common/source?sqlId=com.hand.hls.prj.mapper.CusHlsBpMasterMapper.queryDetails&layout_code=PRJ306F1&tab_code=F_BILLING&bp_seq=&pagesize=10&pagenum=1&pageSize=10&pageNum=1&_fetchall=false&_autocount=true")
                .method("POST", body)
                .addHeader("Accept", "*/*")
                .addHeader("Accept-Language", "zh-CN,zh;q=0.9")
                .addHeader("Connection", "keep-alive")
                .addHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8")
                .addHeader("Cookie", "vw=1440; vh=625; SESSIONID_HAP=9bf65e87-8bb2-4314-a600-97145c52ae23; TARGETURL=modules/FND/FND_MODEL/FND113/hls_doc_layout_n.lview%3Ffunction_group_id%3D10199%26function_code%3DFND113; userId=10001; app_theme=hap; app_subject=HAP; EL-ADMIN-TOEKN=eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ7XCJhbGxvY2F0aW9uSWRcIjoxNDEsXCJ1c2VybmFtZVwiOlwiYWRtaW5cIn0iLCJleHAiOjE2NjA3OTYwODMsImlhdCI6MTY2MDcyNDA4M30.Z9LQ4VKGQ85zFq7ztXH6xifFMA1RcuoNQiFWHCEXVjb--zDfhoB4Itq39WlaoCDxnL5ItNKMTnP07jspZVjdnw; sidebarStatus=0")
                .addHeader("Origin", "http://10.100.222.10")
                .addHeader("Referer", "http://10.100.222.10/core/modules/PRJ/PRJ_BP/PRJ307/hls_bp_master_query_entrance.lview?function_group_id=10226&function_code=PRJ307")
                .addHeader("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/104.0.0.0 Safari/537.36")
                .addHeader("X-CSRF-TOKEN", "e22aa9b8-e4ac-4fc2-ae9d-bcfa43bbc06a")
                .addHeader("X-Requested-With", "XMLHttpRequest")
                .addHeader("springFlag", "Y")
                .build();
        Response response = client.newCall(request).execute();
        return JSONObject.parseObject(response.body().string()).getJSONObject("result").getJSONArray("record").getJSONObject(0);
    }

    @SneakyThrows
    private static JSONObject extractNormal(String bpId) {
        OkHttpClient client = new OkHttpClient().newBuilder()
                .connectTimeout(600L, TimeUnit.SECONDS)
                .readTimeout(600L, TimeUnit.SECONDS)
                .writeTimeout(600L, TimeUnit.SECONDS)
                .build();
        MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded; charset=UTF-8");
        RequestBody body = RequestBody.create(mediaType, "_request_data=%7B%22parameter%22%3A%7B%22bp_id%22%3A%22" + bpId + "%22%2C%22randomString%22%3A%220.9993632637678782Thu%20Aug%2018%202022%2012%3A31%3A37%20GMT%2B0800%20(%E4%B8%AD%E5%9B%BD%E6%A0%87%E5%87%86%E6%97%B6%E9%97%B4)%22%7D%7D");
        Request request = new Request.Builder()
                .url("http://10.100.222.10/core/layout/common/source?sqlId=com.hand.hls.prj.mapper.CusHlsBpMasterMapper.queryForNp&layout_code=PRJ306F2&tab_code=F_SPOUSE&bp_seq=&pagesize=10&pagenum=1&pageSize=10&pageNum=1&_fetchall=false&_autocount=true")
                .method("POST", body)
                .addHeader("Accept", "*/*")
                .addHeader("Accept-Language", "zh-CN,zh;q=0.9")
                .addHeader("Connection", "keep-alive")
                .addHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8")
                .addHeader("Cookie", "vw=1440; vh=625; SESSIONID_HAP=9bf65e87-8bb2-4314-a600-97145c52ae23; TARGETURL=modules/FND/FND_MODEL/FND113/hls_doc_layout_n.lview%3Ffunction_group_id%3D10199%26function_code%3DFND113; userId=10001; app_theme=hap; app_subject=HAP; EL-ADMIN-TOEKN=eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ7XCJhbGxvY2F0aW9uSWRcIjoxNDEsXCJ1c2VybmFtZVwiOlwiYWRtaW5cIn0iLCJleHAiOjE2NjA3OTYwODMsImlhdCI6MTY2MDcyNDA4M30.Z9LQ4VKGQ85zFq7ztXH6xifFMA1RcuoNQiFWHCEXVjb--zDfhoB4Itq39WlaoCDxnL5ItNKMTnP07jspZVjdnw; sidebarStatus=0")
                .addHeader("Origin", "http://10.100.222.10")
                .addHeader("Referer", "http://10.100.222.10/core/modules/PRJ/PRJ_BP/PRJ307/hls_bp_master_query_entrance.lview?function_group_id=10226&function_code=PRJ307")
                .addHeader("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/104.0.0.0 Safari/537.36")
                .addHeader("X-CSRF-TOKEN", "e22aa9b8-e4ac-4fc2-ae9d-bcfa43bbc06a")
                .addHeader("X-Requested-With", "XMLHttpRequest")
                .addHeader("springFlag", "Y")
                .build();
        Response response = client.newCall(request).execute();
        return JSONObject.parseObject(response.body().string()).getJSONObject("result").getJSONArray("record").getJSONObject(0);
    }

    public static void main(String[] args) {
        extractFileName();
    }

    @SneakyThrows
    public static void extractFileName() {
        OkHttpClient client = new OkHttpClient().newBuilder()
                .connectTimeout(600L, TimeUnit.SECONDS)
                .readTimeout(600L, TimeUnit.SECONDS)
                .writeTimeout(600L, TimeUnit.SECONDS)
                .build();
        MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded; charset=UTF-8");
        RequestBody body = RequestBody.create(mediaType, "_request_data=%7B%22parameter%22%3A%7B%22randomString%22%3A%220.8678202229462741Thu%20Aug%2018%202022%2012%3A23%3A52%20GMT%2B0800%20(%E4%B8%AD%E5%9B%BD%E6%A0%87%E5%87%86%E6%97%B6%E9%97%B4)%22%7D%7D");
        Request request = new Request.Builder()
                .url("http://10.100.222.10/core/layout/common/source?sqlId=com.hand.hls.prj.mapper.CusHlsBpMasterMapper.queryNew&layout_code=PRJ307&tab_code=G_QUERY_RESULT&bp_seq=&pagesize=10000&pagenum=1&pageSize=10000&pageNum=1&_fetchall=true&_autocount=true")
                .method("POST", body)
                .addHeader("Accept", "*/*")
                .addHeader("Accept-Language", "zh-CN,zh;q=0.9")
                .addHeader("Connection", "keep-alive")
                .addHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8")
                .addHeader("Cookie", "SESSIONID_HAP=ef6d623f-e006-41db-837a-27b38066ddae; vh=572; vw=1440; TARGETURL=modules/PRJ/PRJ_BP/PRJ307/hls_bp_master_query_entrance.lview%3Ffunction_group_id%3D10226%26function_code%3DPRJ307; userId=10001; app_theme=hap; app_subject=HAP")
                .addHeader("Origin", "http://10.100.222.10")
                .addHeader("Referer", "http://10.100.222.10/core/modules/PRJ/PRJ_BP/PRJ307/hls_bp_master_query_entrance.lview?function_group_id=10226&function_code=PRJ307")
                .addHeader("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/104.0.0.0 Safari/537.36")
                .addHeader("X-CSRF-TOKEN", "3b2524ae-aa0d-4287-a065-5572d2e30c1b")
                .addHeader("X-Requested-With", "XMLHttpRequest")
                .addHeader("springFlag", "Y")
                .build();
        Response response = client.newCall(request).execute();
        JSONObject resObj = JSONObject.parseObject(response.body().string());
        log.info("数据:{}", resObj.toJSONString());
        JSONArray dataArray = resObj.getJSONObject("result").getJSONArray("record");
        // 客户名称 -> 资料类别 -> {资料名称,资料id}
        Map<String, Map<String, JSONArray>> fileNameMap = new HashMap<>();
        for (int i = 0; i < dataArray.size(); i++) {
            JSONObject dataObj = dataArray.getJSONObject(i);
            String clientName = dataObj.getString("bp_name");
            Map<String, JSONArray> fileMap = fileNameMap.computeIfAbsent(clientName, k -> new HashMap<>());

            // 获取资料清单
            JSONObject d = new JSONObject();
            d.put("parameter", dataObj);
            String aBodyData = "_request_data=" + URLEncoder.encode(d.toJSONString(), "utf-8");
            log.info("请求数据:{}", aBodyData);
            RequestBody aBody = RequestBody.create(mediaType, aBodyData);
            Request aReq = new Request.Builder()
                    .url("http://10.100.222.10/core/layout/common/source?sqlId=com.hand.hls.prj.mapper.HlsCusBpMasterAttachmentMapper.queryList&layout_code=PRJ306F1&tab_code=G_DOCUMENT_LIST&bp_seq=&pagesize=10000&pagenum=1&pageSize=10000&pageNum=1&_fetchall=true&_autocount=true")
                    .method("POST", aBody)
                    .addHeader("Accept", "*/*")
                    .addHeader("Accept-Language", "zh-CN,zh;q=0.9")
                    .addHeader("Connection", "keep-alive")
                    .addHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8")
                    .addHeader("Cookie", "SESSIONID_HAP=ef6d623f-e006-41db-837a-27b38066ddae; vh=572; vw=1440; TARGETURL=modules/PRJ/PRJ_BP/PRJ307/hls_bp_master_query_entrance.lview%3Ffunction_group_id%3D10226%26function_code%3DPRJ307; userId=10001; app_theme=hap; app_subject=HAP")
                    .addHeader("Origin", "http://10.100.222.10")
                    .addHeader("Referer", "http://10.100.222.10/core/modules/PRJ/PRJ_BP/PRJ307/hls_bp_master_query_entrance.lview?function_group_id=10226&function_code=PRJ307")
                    .addHeader("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/104.0.0.0 Safari/537.36")
                    .addHeader("X-CSRF-TOKEN", "3b2524ae-aa0d-4287-a065-5572d2e30c1b")
                    .addHeader("X-Requested-With", "XMLHttpRequest")
                    .addHeader("springFlag", "Y")
                    .build();
            Response aRes = client.newCall(aReq).execute();
            JSONArray aResArray = JSONArray.parseObject(aRes.body().string()).getJSONObject("result").getJSONArray("record");
            for (int j = 0; j < aResArray.size(); j++) {
                JSONObject aData = aResArray.getJSONObject(j);
                if (StringUtils.isBlank(aData.getString("file_names"))) {
                    continue;
                }
                JSONArray aFileArray = fileMap.computeIfAbsent(aData.getString("document_name"), k -> new JSONArray());
                aFileArray.addAll(extractFileDetail(aData.getString("bp_attachment_id")));
            }

            // 获取附件
            Request bReq = new Request.Builder()
                    .url("http://10.100.222.10/core/layout/common/source?sqlId=com.hand.hls.prj.mapper.CusBpMasterAttachmentMapper.query&layout_code=PRJ306F1&tab_code=MASTER_ATTACHMENT&bp_seq=&pagesize=10000&pagenum=1&pageSize=10000&pageNum=1&_fetchall=true&_autocount=true")
                    .method("POST", aBody)
                    .addHeader("Accept", "*/*")
                    .addHeader("Accept-Language", "zh-CN,zh;q=0.9")
                    .addHeader("Connection", "keep-alive")
                    .addHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8")
                    .addHeader("Cookie", "SESSIONID_HAP=ef6d623f-e006-41db-837a-27b38066ddae; vh=572; vw=1440; TARGETURL=modules/PRJ/PRJ_BP/PRJ307/hls_bp_master_query_entrance.lview%3Ffunction_group_id%3D10226%26function_code%3DPRJ307; userId=10001; app_theme=hap; app_subject=HAP")
                    .addHeader("Origin", "http://10.100.222.10")
                    .addHeader("Referer", "http://10.100.222.10/core/modules/PRJ/PRJ_BP/PRJ307/hls_bp_master_query_entrance.lview?function_group_id=10226&function_code=PRJ307")
                    .addHeader("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/104.0.0.0 Safari/537.36")
                    .addHeader("X-CSRF-TOKEN", "3b2524ae-aa0d-4287-a065-5572d2e30c1b")
                    .addHeader("X-Requested-With", "XMLHttpRequest")
                    .addHeader("springFlag", "Y")
                    .build();
            Response bRes = client.newCall(bReq).execute();
            JSONArray bResArray = JSONArray.parseObject(bRes.body().string()).getJSONObject("result").getJSONArray("record");
            for (int j = 0; j < bResArray.size(); j++) {
                JSONObject aData = bResArray.getJSONObject(j);
                if (StringUtils.isBlank(aData.getString("file_names"))) {
                    continue;
                }
                JSONArray aFileArray = fileMap.computeIfAbsent(aData.getString("archive_category_n"), k -> new JSONArray());
                aFileArray.addAll(extractFileDetail(aData.getString("bp_attachment_id")));
            }
        }
        IoUtil.write(new FileOutputStream("/Users/wang/Desktop/客户文件整理_线上_20221124_2.json"), true, JSON.toJSONString(fileNameMap).getBytes(StandardCharsets.UTF_8));
    }

    @SneakyThrows
    private static JSONArray extractFileDetail(String headerId) {
        OkHttpClient client = new OkHttpClient().newBuilder()
                .connectTimeout(600L, TimeUnit.SECONDS)
                .readTimeout(600L, TimeUnit.SECONDS)
                .writeTimeout(600L, TimeUnit.SECONDS)
                .build();
        MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded; charset=UTF-8");
        RequestBody bBodyData = RequestBody.create(mediaType, "_request_data=%7B%22parameter%22%3A%7B%22randomString%22%3A%220.45566619325580415Fri%20Aug%2019%202022%2016%3A19%3A43%20GMT%2B0800%20(%E4%B8%AD%E5%9B%BD%E6%A0%87%E5%87%86%E6%97%B6%E9%97%B4)%22%7D%7D");
        Request bReq = new Request.Builder()
                .url("http://10.100.222.10/core/fnd/attachment/query?headerId=" + headerId + "&tableName=hls_bp_master_attachment&pagesize=10&pagenum=1&pageSize=10&pageNum=1&_fetchall=true&_autocount=false")
                .method("POST", bBodyData)
                .addHeader("Accept", "*/*")
                .addHeader("Accept-Language", "zh-CN,zh;q=0.9")
                .addHeader("Connection", "keep-alive")
                .addHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8")
                .addHeader("Cookie", "SESSIONID_HAP=ef6d623f-e006-41db-837a-27b38066ddae; vh=572; vw=1440; TARGETURL=modules/PRJ/PRJ_BP/PRJ307/hls_bp_master_query_entrance.lview%3Ffunction_group_id%3D10226%26function_code%3DPRJ307; userId=10001; app_theme=hap; app_subject=HAP")
                .addHeader("Origin", "http://10.100.222.10")
                .addHeader("Referer", "http://10.100.222.10/core/modules/PRJ/PRJ_BP/PRJ307/hls_bp_master_query_entrance.lview?function_group_id=10226&function_code=PRJ307")
                .addHeader("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/104.0.0.0 Safari/537.36")
                .addHeader("X-CSRF-TOKEN", "3b2524ae-aa0d-4287-a065-5572d2e30c1b")
                .addHeader("X-Requested-With", "XMLHttpRequest")
                .addHeader("springFlag", "Y")
                .build();
        Response bRes = client.newCall(bReq).execute();
        JSONArray bResArray = JSONArray.parseObject(bRes.body().string()).getJSONObject("result").getJSONArray("record");
        return bResArray;
    }

    /**
     * 初始化数据模型
     */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class InitDataModel {

        private String clientName;

        /**
         * NP、ORG
         */
        private String bpClass;

        /**
         * 法人有该字段 统一社会信用代码
         */
        private String socialCreditCode;

        /**
         * 自然人有该字段 证件号码
         */
        private String idCardNo;

        /**
         * 证件类型 ID_CARD居民身份证及其他以公民身份证号为标识的证件
         */
        private String idType;

        private String createUserName;

    }

}
