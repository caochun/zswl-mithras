package cn.zswltech.mithras.others;

import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.jasypt.encryption.pbe.config.EnvironmentStringPBEConfig;
import org.jasypt.salt.NoOpIVGenerator;

/**
 * @author yibin
 */
public class JasyptUtil {

    private static final String PWD = "Zswl@2022#$^ds";

    public static void main(String[] args) {

//        testDecrypt("iYHcvNtc4A3y791f3eltNfukkrybuyT3");
//        testEncryption("minio");
//        testEncryption("vG7Yd3idhtiWzwd3d");
        testDecrypt("iYHcvNtc4A3y791f3eltNfukkrybuyT3");
        testDecrypt("NVxdhoMAmsW0R498zTGKRZXAHruMzrWs");
        testDecrypt("5McxkzAkAlp/TYB1amdTfXb68RhAKIuK");


//        String p = AccountUtil.genPwd("Zswl@1130$", "b32bb040da584897ade6edda1260c261");
//        System.out.println(p);
    }

    public static void testDecrypt(String content) {
        StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
        EnvironmentStringPBEConfig config = new EnvironmentStringPBEConfig();
        config.setAlgorithm("PBEWithMD5AndDES");
        config.setPassword(PWD);
        config.setIvGenerator(new NoOpIVGenerator());
        encryptor.setConfig(config);
        String decryptCtn = encryptor.decrypt(content);
        System.out.println("decrypt content:" + decryptCtn);
    }

    public static void testEncryption(String content) {
        StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
        EnvironmentStringPBEConfig config = new EnvironmentStringPBEConfig();
        config.setAlgorithm("PBEWithMD5AndDES");
        config.setPassword(PWD);
        config.setIvGenerator(new NoOpIVGenerator());
        encryptor.setConfig(config);
        String encryptCtn = encryptor.encrypt(content);
        System.out.println("encrypt content:" + encryptCtn);
    }
}
