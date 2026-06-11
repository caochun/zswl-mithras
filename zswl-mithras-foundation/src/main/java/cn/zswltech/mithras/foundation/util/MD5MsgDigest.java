package cn.zswltech.mithras.foundation.util;
import java.nio.charset.Charset;
import java.security.MessageDigest;

/**
 * @author frank
 * @date 2025/4/22
 * @description
 */
public class MD5MsgDigest {

    public static String digest(String rawString) {
         return digest(rawString, "utf-8");
         }

     public static String digest(String rawString, String charset) {
     Charset cs = Charset.forName(charset);
     try {
         return compute(rawString, cs);
         } catch (Exception e) {
         return "";
         }
     }
     private static String compute(String inStr, Charset charset)
        throws Exception {
     MessageDigest md5 = MessageDigest.getInstance("MD5");
     byte[] md5Bytes = md5.digest(inStr.getBytes(charset));
     return toHexString(md5Bytes);
     }

    public static String toHexString(byte[] bytes) {
         StringBuffer hexValue = new StringBuffer();
         for (int i = 0; i < bytes.length; i++) {
             int val = ((int) bytes[i]) & 0xff;
             if (val < 16) {
                 hexValue.append("0");
                 }
             hexValue.append(Integer.toHexString(val));
             }
         return hexValue.toString();
         }

    /**
     * 通用MD5处理工具类
     * @param text
     * @return
     */
    public static String MD5Bit32(String text) throws Exception {
        StringBuffer sb = new StringBuffer();

        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update(text.getBytes("UTF-8"));
        byte[] b = md.digest();
        int i;
        for (int offset = 0; offset < b.length; offset++) {
            i = b[offset];
            if (i < 0) {
                i += 256;
            }
            if (i < 16) {
                sb.append("0");
            }
            sb.append(Integer.toHexString(i));
        }

        return sb.toString().toUpperCase();
    }
}
