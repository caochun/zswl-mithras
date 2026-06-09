package cn.zswltech.mithras.service.util.ding;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import cn.zswltech.mithras.service.util.ding.body.DingBody;
import lombok.extern.slf4j.Slf4j;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * @author junke
 */
@Slf4j
public class DingUtil {

    static boolean ding(DingObj dingObj) {
        String webHook = dingObj.getWebHook();
        if (StrUtil.isNotBlank(dingObj.getToken())) {
            try {
                webHook = addSign(dingObj.getWebHook(), dingObj.getToken());
            } catch (Exception e) {
                log.error("add sign to webHook failed.", e);
                return false;
            }
        }
        try {
            dingPost(webHook, dingObj.getBody());
        } catch (Exception e) {
            log.error("http ding post failed.", e);
            return false;
        }
        return true;
    }

    private static String addSign(String webHook, String token) throws Exception {
        Long timestamp = System.currentTimeMillis();
        String stringToSign = timestamp + "\n" + token;
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(new SecretKeySpec(token.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
        byte[] signData = mac.doFinal(stringToSign.getBytes(StandardCharsets.UTF_8));
        String sign = URLEncoder.encode(Base64.encode(signData), "UTF-8");
        webHook += "&sign=" + sign;
        webHook += "&timestamp=" + timestamp;
        return webHook;
    }


    private static void dingPost(String webhook, DingBody body) throws Exception {
        //创建连接
        URL url = new URL(webhook);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setDoOutput(true);
        connection.setDoInput(true);
        connection.setRequestMethod("POST");
        connection.setUseCaches(false);
        connection.setInstanceFollowRedirects(true);
        connection.setRequestProperty("Content-Type", "application/json");
        connection.connect();
        //POST请求
        OutputStream out = connection.getOutputStream();
        out.write(JSONUtil.toJsonStr(body).getBytes(StandardCharsets.UTF_8));
        out.flush();
        out.close();
        //读取响应
        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String lines;
        StringBuilder sb = new StringBuilder();
        while ((lines = reader.readLine()) != null) {
            lines = new String(lines.getBytes(), StandardCharsets.UTF_8);
            sb.append(lines);
        }
        DingResp resp = JSONUtil.toBean(sb.toString(), DingResp.class);
        if (!Objects.equals(resp.getErrCode(), 0)) {
            log.error("ding http post error. resp:{}", resp);
        }
        reader.close();
        // 断开连接
        connection.disconnect();
    }
}
