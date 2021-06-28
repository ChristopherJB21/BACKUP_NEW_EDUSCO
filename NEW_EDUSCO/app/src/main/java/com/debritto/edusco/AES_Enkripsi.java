package com.debritto.edusco;

import android.util.Base64;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class AES_Enkripsi {

    // Metode untuk decrypt AES
    public static String decrypt(String input, String Key) throws Exception {
        try {
            byte[] keyBytes = Arrays.copyOf(Key.getBytes(StandardCharsets.US_ASCII), 16);
            SecretKey key = new SecretKeySpec(keyBytes, "AES");
            Cipher decipher = Cipher.getInstance("AES");
            decipher.init(Cipher.DECRYPT_MODE, key);

            byte[] cleartext = input.getBytes();
            byte[] decode_Base64 = Base64.decode(cleartext, Base64.DEFAULT);
            byte[] ciphertextBytes = decipher.doFinal(decode_Base64);
            return new String(ciphertextBytes);

        } catch (Exception e) {
            e.getMessage();
        }
        return null;
    }

    // Metode untuk encrypt AES
    public static String encrypt(String input, String Key) throws Exception {
        byte[] keyBytes = Arrays.copyOf(Key.getBytes(StandardCharsets.US_ASCII), 16);

        SecretKey key = new SecretKeySpec(keyBytes, "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, key);

        byte[] cleartext = input.getBytes(StandardCharsets.UTF_8);
        byte[] ciphertextBytes = cipher.doFinal(cleartext);

        return Base64.encodeToString((ciphertextBytes), Base64.DEFAULT);
    }
}
