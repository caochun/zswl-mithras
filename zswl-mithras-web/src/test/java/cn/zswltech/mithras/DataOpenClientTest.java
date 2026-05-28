package cn.zswltech.mithras;

import cn.hutool.crypto.SmUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import cn.zswltech.lib.crypto.client.CryptoRequest;
import cn.zswltech.lib.crypto.client.CryptoResponse;
import cn.zswltech.lib.crypto.client.CryptoService;
import cn.zswltech.lib.crypto.crypto.SM3;
import cn.zswltech.lib.crypto.crypto.SaltService;
import cn.zswltech.mithras.others.service.ApplicationTest;
import lombok.Data;
import org.junit.Test;

import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

/**
 * @author dingqi
 * @date 2024/12/3
 * @description
 */
public class DataOpenClientTest extends ApplicationTest {
    @Resource
    private SM3 sm3;

    @Test
    public void testReq() {
//        String url = "http://10.42.200.70/api/open/service/page/contractList";
        String url = "http://10.42.200.70/api/open/service/userJobList";
        String accountId = "1000733";
        String privateKey = "MFkwEwYHKoZIzj0CAQYIKoZIzj0DAQcDQgAEFYNlyhk3T9K5lv9xrx4tUBW7Y3o5Rw84e/vrrisaJtwkGIBTqPhMvKchpkxK4yZd1OOVNZpcd6kasAFZvCAo1w==";
        long timestamp = System.currentTimeMillis();
        // 签名字符串
        String s = accountId + timestamp + privateKey;
        CryptoResponse res = sm3.encrypt(new CryptoRequest(s.getBytes(StandardCharsets.UTF_8)));
        String sign = new String(Base64.getEncoder().encode(res.getResult()),StandardCharsets.UTF_8);
        System.out.println(sign);
        // 请求参数
//        ContractListReq myReq = new ContractListReq();
//        myReq.setPage(1);
//        myReq.setPageSize(5);
//        myReq.setContractCode("浙商租【2024】转让字第(ZR-0001)号");
//        Map<String, Object> parameters = new HashMap<>();
//        parameters.put("params", myReq);
        UserJobListReq myReq = new UserJobListReq();
        myReq.setUserId(50L);
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("params", myReq);
        // 发送请求
        String httpRes = HttpUtil.createPost(url)
                .header("Auth-Subject", accountId)
                .header("Auth-Time", String.valueOf(timestamp))
                .header("Auth-Sign", sign)
                .header("Content-Type", "application/json")
                .body(JSONUtil.toJsonStr(parameters))
                .execute()
                .body();
        System.out.println(httpRes);
    }

    @Data
    private static class ContractListReq {
        private int page;
        private int pageSize;
        private String contractCode;
        private String projName;
    }

    @Data
    private static class UserJobListReq {
        private Long userId;
    }
}
