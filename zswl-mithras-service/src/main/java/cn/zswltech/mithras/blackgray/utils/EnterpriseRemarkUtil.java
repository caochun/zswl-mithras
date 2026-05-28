package cn.zswltech.mithras.blackgray.utils;


import cn.zswltech.mithras.blackgray.enums.BusinessType;
import org.junit.Assert;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2023/12/15 11:29
 */
public class EnterpriseRemarkUtil {

    /**
     * 生成企业标识
     * @param enterpriseName 企业名称
     * @param unifiedSocialCreditCode 统一社会信用代码
     * @param businessType {@link BusinessType} 业务类型
     * @return 企业标识
     */
    public static String getEnterpriseRemark(String enterpriseName, String unifiedSocialCreditCode, BusinessType businessType){
        Assert.assertNotNull("缺少参数enterpriseName",enterpriseName);
        Assert.assertNotNull("缺少参数unifiedSocialCreditCode",unifiedSocialCreditCode);
        Assert.assertNotNull("缺少参数businessType",businessType);
        String info = String.join("-", enterpriseName, unifiedSocialCreditCode, businessType.name());
        return Md5Util.encrypt(info);
    }
}
