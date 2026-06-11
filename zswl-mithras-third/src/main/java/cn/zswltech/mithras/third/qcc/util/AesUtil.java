package cn.zswltech.mithras.third.qcc.util;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.Key;

/**
 * 对称加密算法AES的实现
 * 采用了偏移向量iv
 */
public class AesUtil {

    private static final String ALGORITHM = "AES";
    private static final String IV_ALGORITHM = "AES/CBC/PKCS5Padding";
    private static final String DEFAULT_CHARSET = "UTF-8";

    /**
     */
    public static SecretKeySpec getSecretKeySpec(String secretKeyStr) {
        byte[] secretKey = Base64.decodeBase64(secretKeyStr);
        return new SecretKeySpec(secretKey, ALGORITHM);
    }

    /**
     * 加密
     */
    public static String encrypt(String content, String secretKey, String ivParameterSpec) throws Exception {
        if (content == null) {
            content = "";
        }
        Key key = getSecretKeySpec(secretKey);
        Cipher cipher = Cipher.getInstance(IV_ALGORITHM);
        IvParameterSpec iv = new IvParameterSpec(ivParameterSpec.getBytes());
        cipher.init(Cipher.ENCRYPT_MODE, key, iv);
        byte[] result = cipher.doFinal(content.getBytes(DEFAULT_CHARSET));
        return Base64.encodeBase64String(result);
    }


    /**
     * 解密
     */
    public static String decrypt(String content, String secretKey, String ivParameterSpec) throws Exception {
        if (StringUtils.isNotBlank(content)) {
            Key key = getSecretKeySpec(secretKey);
            Cipher cipher = Cipher.getInstance(IV_ALGORITHM);
            IvParameterSpec iv = new IvParameterSpec(ivParameterSpec.getBytes());
            cipher.init(Cipher.DECRYPT_MODE, key, iv);
            byte[] result = cipher.doFinal(Base64.decodeBase64(content));
            return new String(result, DEFAULT_CHARSET);
        }
        return "";
    }
}
