package io.github.technocrats.capstone;

import org.junit.Test;

import java.util.Arrays;

import javax.crypto.SecretKey;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class SymmetricEncryptionTest {

    @Test
    public void createAESKey() throws Exception {
        SecretKey key = SymmetricEncryptionUtils.generateKeySpec("password");
        assertNotNull(key);
        System.out.println(Arrays.toString(key.getEncoded()));
    }

    @Test
    public void testAESCryptoRoutine() throws Exception {
        String password = "password";
        String encPassword = SymmetricEncryptionUtils.encryptPassword(password);
        assertNotNull(password);
        String decryptedPassword = SymmetricEncryptionUtils.decryptPassword(encPassword, password);
        assertEquals(password, decryptedPassword);
    }
}
