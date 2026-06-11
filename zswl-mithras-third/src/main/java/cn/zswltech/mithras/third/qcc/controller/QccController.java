package cn.zswltech.mithras.third.qcc.controller;

import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.api.qcc.QccApi;
import cn.zswltech.mithras.third.qcc.util.TokenGen;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * 企查查
 **/
@RestController
public class QccController implements QccApi {

    @Value("${qcc.aes-key}")
    private String AES_KEY;
    @Value("${qcc.aes-iv}")
    private String AES_IV;
    @Value("${qcc.login-name}")
    private String loginName;
    @Value("${qcc.company-key}")
    private String companyKey;

    @Override
    public R<Map<String, String>> generateToken() throws Exception {
        String token = TokenGen.generateToken(loginName, AES_KEY, AES_IV);
        HashMap<String, String> result = new HashMap<>();
        result.put("token", token);
        result.put("companyKey", companyKey);
        return R.ok(result);
    }
}
