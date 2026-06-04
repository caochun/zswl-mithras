package cn.zswltech.mithras.service.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

public class Sha256Util {

    public static String generateSecret(String clientId) throws NoSuchAlgorithmException {
        // 生成16字节随机盐值
        byte[] salt = new byte[16];
        new SecureRandom().nextBytes(salt);

        // 将client_id和盐值拼接
        String input = clientId + Base64.getEncoder().encodeToString(salt);

        // 使用SHA-256生成密钥
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(input.getBytes());

        // 返回十六进制格式的密钥
        StringBuilder hexString = new StringBuilder();
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }

        return hexString.toString();
    }

    public static void main(String[] args) {
        try {
            String clientId = "hly-finance-prod-001";
            String secret = generateSecret(clientId);
            System.out.println("Client ID: " + clientId);
            System.out.println("Generated Secret: " + secret);
        } catch (NoSuchAlgorithmException e) {
            System.err.println("SHA-256算法不可用: " + e.getMessage());
        }
    }
}
