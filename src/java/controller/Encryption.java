package controller;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

public class Encryption {

    public static String encrypt(String strToEncrypt, String secret, String algorithm) {
        try {
            SecretKeySpec secretKey = new SecretKeySpec(secret.getBytes(), algorithm);
            Cipher cipher = Cipher.getInstance(algorithm);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);

            byte[] encrypted = cipher.doFinal(strToEncrypt.getBytes());
            return Base64.getEncoder().encodeToString(encrypted);

        } catch (Exception e) {
            throw new RuntimeException("Error while encrypting: " + e.getMessage());
        }
    }

    public static String decrypt(String strToDecrypt, String secret, String algorithm) {
        try {
            SecretKeySpec secretKey = new SecretKeySpec(secret.getBytes(), algorithm);
            Cipher cipher = Cipher.getInstance(algorithm);
            cipher.init(Cipher.DECRYPT_MODE, secretKey);

            byte[] decoded = Base64.getDecoder().decode(strToDecrypt);
            return new String(cipher.doFinal(decoded));

        } catch (Exception e) {
            throw new RuntimeException("Error while decrypting: " + e.getMessage());
        }
    }
}