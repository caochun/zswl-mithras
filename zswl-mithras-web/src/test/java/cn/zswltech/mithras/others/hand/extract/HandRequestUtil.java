package cn.zswltech.mithras.others.hand.extract;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import java.net.URLEncoder;
import java.util.concurrent.TimeUnit;

/**
 * 汉得提取数据http工具类
 *
 * @author wangchuanhao
 * @date 2022/9/22 10:27 AM
 */
@Slf4j
public class HandRequestUtil {

    // 每次先界面拿一次数据更新下
    private static final String X_CSRF_TOKEN = "e60c00eb-0fa8-42c0-80b0-28f3b13a7716";
    private static final String SESSIONID_HAP = "04c829f9-5151-47e1-81de-1a8c0f6995a0";
    private static final OkHttpClient client = new OkHttpClient().newBuilder()
            .connectTimeout(600L, TimeUnit.SECONDS)
            .readTimeout(600L, TimeUnit.SECONDS)
            .writeTimeout(600L, TimeUnit.SECONDS)

            .build();

    @SneakyThrows
    public static String doPost(String url, JSONObject bodyData, String referer, String targetUrl) {
        MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded; charset=UTF-8");

        JSONObject nestedBodyData = new JSONObject();
        nestedBodyData.put("parameter", bodyData);
        String nestedBodyDataString = "_request_data=" + URLEncoder.encode(nestedBodyData.toJSONString(), "utf-8");
        RequestBody body = RequestBody.create(mediaType, nestedBodyDataString);

        Request request = new Request.Builder()
                .url(url)
                .method("POST", body)
                .addHeader("Accept", "*/*")
                .addHeader("Accept-Language", "zh-CN,zh;q=0.9")
                .addHeader("Connection", "keep-alive")
                .addHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8")
                .addHeader("Cookie", String.format("vw=1440; vh=625; SESSIONID_HAP=%s; TARGETURL=%s; userId=10001; app_theme=hap; app_subject=HAP; EL-ADMIN-TOEKN=eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ7XCJhbGxvY2F0aW9uSWRcIjoxNDEsXCJ1c2VybmFtZVwiOlwiYWRtaW5cIn0iLCJleHAiOjE2NjA3OTYwODMsImlhdCI6MTY2MDcyNDA4M30.Z9LQ4VKGQ85zFq7ztXH6xifFMA1RcuoNQiFWHCEXVjb--zDfhoB4Itq39WlaoCDxnL5ItNKMTnP07jspZVjdnw; sidebarStatus=0", SESSIONID_HAP, targetUrl))
                .addHeader("Origin", "http://10.100.222.10")
                .addHeader("Referer", referer)
                .addHeader("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/104.0.0.0 Safari/537.36")
                .addHeader("X-CSRF-TOKEN", X_CSRF_TOKEN)
                .addHeader("X-Requested-With", "XMLHttpRequest")
                .addHeader("springFlag", "Y")
                .build();
        Response response = client.newCall(request).execute();
        String result = response.body().string();
        response.close();
        return result;
    }

    public static JSONArray extractCommonArray(String resData) {
        JSONObject jsonObject = JSONObject.parseObject(resData);
        if (!Boolean.TRUE.equals(jsonObject.getBoolean("success"))) {
            log.error("http请求汉得失败:{}", resData);
        }
        return jsonObject.getJSONObject("result").getJSONArray("record");
    }

    public static void main(String[] args) {
        log.info(doPost("http://10.100.222.10/core/layout/common/source?sqlId=com.hand.hls.prj.mapper.CusHlsBpMasterMapper.queryNew&layout_code=PRJ307&tab_code=G_QUERY_RESULT&bp_seq=&pagesize=10000&pageNum=1&_fetchall=true&_autocount=true",
                JSONObject.parseObject("{\"parameter\":{\"randomString\":\"0.8678202229462741Thu Aug 18 2022 12:23:52 GMT+0800 (中国标准时间)\"}}"),
                        "http://10.100.222.10/core/modules/PRJ/PRJ_BP/PRJ307/hls_bp_master_query_entrance.lview?function_group_id=10226&function_code=PRJ307",
                        "modules/PRJ/PRJ_BP/PRJ307/hls_bp_master_query_entrance.lview%3Ffunction_group_id%3D10226%26function_code%3DPRJ307"
                        ));
    }

}
