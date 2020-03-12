package io.github.technocrats.capstone;

import android.util.Base64;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class SymmetricEncryptionUtils {

    private static final String AES = "AES";

    public static String encryptPassword(String password) throws Exception{
        SecretKeySpec secretKeySpec = generateKeySpec(password);
        Cipher cipher = Cipher.getInstance(AES);
        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);

        byte[] encrypt = cipher.doFinal(password.getBytes());
        return Base64.encodeToString(encrypt, Base64.DEFAULT);
    }

    public static String decryptPassword(String output, String inputPassword) throws Exception {
        SecretKeySpec secretKeySpec = generateKeySpec(inputPassword);
        Cipher cipher = Cipher.getInstance(AES);
        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);
        byte[] decodedValue = Base64.decode(output, Base64.DEFAULT);
        byte[] decValue = cipher.doFinal(decodedValue);
        return new String(decValue);
    }

    public static SecretKeySpec generateKeySpec(String password) throws Exception{
        final MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");

        byte[] bytes = password.getBytes(StandardCharsets.UTF_8);
        messageDigest.update(bytes,0,bytes.length);

        byte[] key = messageDigest.digest();

        return new SecretKeySpec(key, "AES");
    }
}
