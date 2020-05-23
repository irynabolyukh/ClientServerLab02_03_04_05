package org.clientserver.classes;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
//
//import lombok.Builder;
//import lombok.Data;
//
//@Data
//@Builder(toBuilder = true)
//public class DeEncriptor {
//    private static String deencode(String string) {
//        char cipherKey = 'X';
//        String result = "";
//        int length = string.length();
//        for (int i = 0; i < length; i++)
//            result = result + (char) (string.charAt(i) ^ cipherKey);
//        return result;
//    }
//    public static String encode(final String string) {
//        return deencode(string);
//    }
//    public static String decode(final String string) {
//        return deencode(string);
//    }
//}


public class DeEncriptor {

    private static final String KEY = "FHDSFHSDJHFJDSXD";
    private static final String IV = "SDFDFCVDSXADQSDA";

    public static byte[] encode(final byte[] messageToEncode) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        final SecretKeySpec secretKeySpec = new SecretKeySpec(KEY.getBytes(), "AES");
        final IvParameterSpec ivParameterSpec = new IvParameterSpec(IV.getBytes());
        final Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivParameterSpec);
        byte[] encodedBytes = cipher.doFinal(messageToEncode);
        return encodedBytes;
    }

    public static byte[] decode(final byte[] messageToDecode) throws InvalidAlgorithmParameterException, InvalidKeyException, NoSuchPaddingException, NoSuchAlgorithmException, BadPaddingException, IllegalBlockSizeException {
        final SecretKeySpec secretKeySpec = new SecretKeySpec(KEY.getBytes(), "AES");
        final IvParameterSpec ivParameterSpec = new IvParameterSpec(IV.getBytes());
        final Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, ivParameterSpec);
        final byte[]  decodeText = cipher.doFinal(messageToDecode);
        return decodeText;
    }
}