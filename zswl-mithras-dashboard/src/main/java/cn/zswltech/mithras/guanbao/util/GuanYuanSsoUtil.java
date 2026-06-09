package cn.zswltech.mithras.guanbao.util;

import org.apache.commons.codec.binary.Base64;

import javax.crypto.Cipher;
import java.io.ByteArrayOutputStream;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;


public class GuanYuanSsoUtil {
    public static final String CHARSET = "UTF-8";
    public static final String RSA_ALGORITHM = "RSA";
    public static final int KEY_SIZE = 1024;
//    private static final String PRIVATE_KEY = "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAJxWG//i6L8B/W3Woq0oqH4SDV16SmsueZGASVvdy9uKlK+Nw4Hm0AWr5c2/66N6J9HQZ9B3jmkw4HNLTJco/CWtSvuMCtCtZIQeviUzXJw5RZFCNe+uAh78VrPZkY/SBUvkRYpFCJGc5Bo8BuUVl8js48MGK837T7YedYxmzKKnAgMBAAECgYB2NjpXw6j//S43Jqu9SVgWc+n9O+IXp32vApT1zQE/kUsab5Rcnj2/fBp4+FziMRT49p/5Jt3dNmMRaueQCSKW4Bbf5tIBHq1NNeRbbRQKg8THCIjSj9i0v+zYWBvgFDJJaSQ7FUePvlv+XcWgJUbwOvzOvKkxmQ7G/1b6cYetWQJBAMwPChYwFY38alnups5DQD9tmTA9g6712AMTgjqDB8iPNwTB8IWTvbHvEUnOsVXjdS/l/iN6hadkg35sL3Cf3dUCQQDEIV1CqPpMj24JA5SkfQSsAvIgsbWfnY/mBXQvZ0LhMMmCdg80PYKpnTRUzUrshiQBR7dfwtxraWME+DVCKnCLAkAJKqPJW1+cgTpFz/KuUVcRglrfT+Qad/C8MqQm/LvcxpIyJIQpe/mJhLPCHQbaVN1uyWYhMd/PJTTC+RkodgYBAkASRS1j3Uvbv+y3KD87h23c/DVD+Gn74XibK9zj4ZGoWpwa7BaEfkpitINAdxNIr0JBYEXjxMo1s06HEvwbGsr7AkEAiZC9vq20f3RAej0wHiYVPj2MDfP+xIl6jLp3ZXtDgRsMJttUWpANqBS5Pq9MxJa8RVCMDY8PmjPM+WHqVIvFdw==";
//    private static final String PUBLIC_KEY = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCcVhv/4ui/Af1t1qKtKKh+Eg1dekprLnmRgElb3cvbipSvjcOB5tAFq+XNv+ujeifR0GfQd45pMOBzS0yXKPwlrUr7jArQrWSEHr4lM1ycOUWRQjXvrgIe/Faz2ZGP0gVL5EWKRQiRnOQaPAblFZfI7OPDBivN+0+2HnWMZsyipwIDAQAB";

//    public static String getPrivateKey() {
//        return PRIVATE_KEY;
//    }
//
//    public static String getPublicKey() {
//        return PUBLIC_KEY;
//    }

    public static Map<String, String> createKeys(){
        //为RSA算法创建一个KeyPairGenerator对象
        KeyPairGenerator kpg;
        try{
            kpg = KeyPairGenerator.getInstance(RSA_ALGORITHM);
        }catch(NoSuchAlgorithmException e){
            throw new IllegalArgumentException("No such algorithm-->[" + RSA_ALGORITHM + "]");
        }

        //初始化KeyPairGenerator对象,密钥长度
        kpg.initialize(KEY_SIZE);
        //生成密匙对
        KeyPair keyPair = kpg.generateKeyPair();
        //得到公钥
        Key publicKey = keyPair.getPublic();
        String publicKeyStr = Base64.encodeBase64String(publicKey.getEncoded());
        //得到私钥
        Key privateKey = keyPair.getPrivate();
        String privateKeyStr = Base64.encodeBase64String(privateKey.getEncoded());
        Map<String, String> keyPairMap = new HashMap<String, String>();
        keyPairMap.put("publicKey", publicKeyStr);
        keyPairMap.put("privateKey", privateKeyStr);

        return keyPairMap;
    }

    public static RSAPublicKey getPublicKey(String publicKey) throws NoSuchAlgorithmException, InvalidKeySpecException {
        //通过X509编码的Key指令获得公钥对象
        KeyFactory keyFactory = KeyFactory.getInstance(RSA_ALGORITHM);
        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(Base64.decodeBase64(publicKey));
        RSAPublicKey key = (RSAPublicKey) keyFactory.generatePublic(x509KeySpec);
        return key;
    }


    public static RSAPrivateKey getPrivateKey(String privateKey) throws NoSuchAlgorithmException, InvalidKeySpecException {
        //通过PKCS#8编码的Key指令获得私钥对象
        KeyFactory keyFactory = KeyFactory.getInstance(RSA_ALGORITHM);
        PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(Base64.decodeBase64(privateKey));
        RSAPrivateKey key = (RSAPrivateKey) keyFactory.generatePrivate(pkcs8KeySpec);
        return key;
    }

    public static String privateEncrypt(String data, RSAPrivateKey privateKey){
        try{
            Cipher cipher = Cipher.getInstance(RSA_ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, privateKey);
            return Base64.encodeBase64String(rsaSplitCodec(cipher, Cipher.ENCRYPT_MODE, data.getBytes(CHARSET), privateKey.getModulus().bitLength()));
        }catch(Exception e){
            throw new RuntimeException("加密字符串[" + data + "]时遇到异常", e);
        }
    }


    public static String publicDecrypt(String data, RSAPublicKey publicKey){
        try{
            Cipher cipher = Cipher.getInstance(RSA_ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, publicKey);
            return new String(rsaSplitCodec(cipher, Cipher.DECRYPT_MODE, Base64.decodeBase64(data), publicKey.getModulus().bitLength()), CHARSET);
        }catch(Exception e){
            throw new RuntimeException("解密字符串[" + data + "]时遇到异常", e);
        }
    }

    private static byte[] rsaSplitCodec(Cipher cipher, int opmode, byte[] datas, int keySize){
        int maxBlock = 0;
        if(opmode == Cipher.DECRYPT_MODE){
            maxBlock = keySize / 8;
        }else{
            maxBlock = keySize / 8 - 11;
        }
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int offSet = 0;
        byte[] buff;
        int i = 0;
        try{
            while(datas.length > offSet){
                if(datas.length-offSet > maxBlock){
                    buff = cipher.doFinal(datas, offSet, maxBlock);
                }else{
                    buff = cipher.doFinal(datas, offSet, datas.length-offSet);
                }
                out.write(buff, 0, buff.length);
                i++;
                offSet = i * maxBlock;
            }
        } catch(Exception e){
            e.getMessage();
        }
        byte[] resultDatas = out.toByteArray();
        try {
            out.close();
        } catch(Exception e){
            e.getMessage();
        }
        return resultDatas;
    }
    public static String toHexString(String s)
    {
        String str="";
        for (int i=0;i<s.length();i++)
        {
            int ch = (int)s.charAt(i);
            str += Integer.toHexString(ch);
        }
        return str;
    }
    public static void main (String[] args) throws Exception {
//        Map<String, String> keyMap = createKeys();
        String  publicKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCcVhv/4ui/Af1t1qKtKKh+Eg1dekprLnmRgElb3cvbipSvjcOB5tAFq+XNv+ujeifR0GfQd45pMOBzS0yXKPwlrUr7jArQrWSEHr4lM1ycOUWRQjXvrgIe/Faz2ZGP0gVL5EWKRQiRnOQaPAblFZfI7OPDBivN+0+2HnWMZsyipwIDAQAB";
        String  privateKey = "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAJxWG//i6L8B/W3Woq0oqH4SDV16SmsueZGASVvdy9uKlK+Nw4Hm0AWr5c2/66N6J9HQZ9B3jmkw4HNLTJco/CWtSvuMCtCtZIQeviUzXJw5RZFCNe+uAh78VrPZkY/SBUvkRYpFCJGc5Bo8BuUVl8js48MGK837T7YedYxmzKKnAgMBAAECgYB2NjpXw6j//S43Jqu9SVgWc+n9O+IXp32vApT1zQE/kUsab5Rcnj2/fBp4+FziMRT49p/5Jt3dNmMRaueQCSKW4Bbf5tIBHq1NNeRbbRQKg8THCIjSj9i0v+zYWBvgFDJJaSQ7FUePvlv+XcWgJUbwOvzOvKkxmQ7G/1b6cYetWQJBAMwPChYwFY38alnups5DQD9tmTA9g6712AMTgjqDB8iPNwTB8IWTvbHvEUnOsVXjdS/l/iN6hadkg35sL3Cf3dUCQQDEIV1CqPpMj24JA5SkfQSsAvIgsbWfnY/mBXQvZ0LhMMmCdg80PYKpnTRUzUrshiQBR7dfwtxraWME+DVCKnCLAkAJKqPJW1+cgTpFz/KuUVcRglrfT+Qad/C8MqQm/LvcxpIyJIQpe/mJhLPCHQbaVN1uyWYhMd/PJTTC+RkodgYBAkASRS1j3Uvbv+y3KD87h23c/DVD+Gn74XibK9zj4ZGoWpwa7BaEfkpitINAdxNIr0JBYEXjxMo1s06HEvwbGsr7AkEAiZC9vq20f3RAej0wHiYVPj2MDfP+xIl6jLp3ZXtDgRsMJttUWpANqBS5Pq9MxJa8RVCMDY8PmjPM+WHqVIvFdw==";
        System.out.println("公钥: \n\r" + publicKey);
        System.out.println("私钥： \n\r" + privateKey);

        System.out.println("私钥加密——公钥解密");
        String str = "{\"domainId\":\"guanbi\",\"externalUserId\":\"zulin\"}";
        System.out.println("\r明文：\r\n" + str);
        String encodedData = privateEncrypt(str, getPrivateKey(privateKey));
        System.out.println("密文：\r\n" + toHexString(encodedData));
        String decodedData = publicDecrypt(encodedData, getPublicKey(publicKey));
        System.out.println("解密后文字: \r\n" + decodedData);
    }
}
