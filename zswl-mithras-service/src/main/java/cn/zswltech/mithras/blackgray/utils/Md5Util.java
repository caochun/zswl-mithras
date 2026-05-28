package cn.zswltech.mithras.blackgray.utils;

import cn.hutool.crypto.SecureUtil;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2023/12/11 16:43
 */
public class Md5Util {


    private static final String DEFAULT_SALT = "zswltech";
    /**
     * 计算摘要-加盐
     * @param str 待加密字符串
     * @param salt 盐
     * @return 摘要
     */
    public static String encrypt(String str, String salt) {
        return SecureUtil.md5(str + salt);
    }

    public static String encrypt(String str) {
        return encrypt(str, DEFAULT_SALT);
    }
}
