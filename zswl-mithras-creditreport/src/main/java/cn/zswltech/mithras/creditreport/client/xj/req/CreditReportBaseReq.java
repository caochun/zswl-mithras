package cn.zswltech.mithras.creditreport.client.xj.req;

import lombok.Data;

/**
 * @ClassName CreditReportBaseReq
 * @Description TODO
 * @Author jackerhe
 * @Date 2025/11/21 09:07
 * @Version 1.0
 **/
@Data
public class CreditReportBaseReq {
    //认证标识查询用户密码MD5加码 (生成32位md5码)+ 查询用户（填写有查询权限的用户base64转码）;
    //Sign=MD5.bit32(password)+Base64.encode(account)
    private String signature;
}
