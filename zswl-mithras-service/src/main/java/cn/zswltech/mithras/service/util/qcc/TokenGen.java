package cn.zswltech.mithras.service.util.qcc;

import cn.zswltech.mithras.service.others.MithrasException;
import com.alibaba.fastjson.JSON;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * 企查查token生成
 */
public class TokenGen {
    public static final String ROLE_ADMIN = "admin";
    public static final String ROLE_STAFF = "staff";

    public static String generateToken(String loginName, String AES_KEY, String AES_IV) throws Exception {
        return generateToken(loginName, null, null, null, AES_KEY, AES_IV);
    }

    public static String generateToken(String loginName, String role, String name, String email, String AES_KEY, String AES_IV) throws Exception {
        if(StringUtils.isBlank(loginName)) {
            throw new MithrasException("用户名或ID不能为空");
        }
        Map<String, String> result = new HashMap<String, String>();
        result.put("loginName", loginName);
        if(StringUtils.isBlank(name)) {
            result.put("name", loginName);
        } else {
            result.put("name", name);
        }
        if(StringUtils.isNotBlank(role)) {
            if(!(ROLE_ADMIN.equals(role) || ROLE_STAFF.equals(role))) {
                throw new MithrasException("角色不正确");
            }
        } else {
            role = ROLE_STAFF;
        }
        result.put("role", role);
        result.put("email", email == null ? "" : email);
        result.put("timespan", String.valueOf(System.currentTimeMillis()/1000));

        String content = JSON.toJSONString(result);
        return AesUtil.encrypt(content, AES_KEY, AES_IV);
    }
}
