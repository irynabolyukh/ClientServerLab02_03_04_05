package org.clientserver.classes;
import lombok.Builder;
import lombok.Data;

@Data
@Builder(toBuilder = true)
public class Cipher {
    private static String deencode(String string) {
        char cipherKey = 'X';
        String result = "";
        int length = string.length();
        for (int i = 0; i < length; i++)
            result = result + (char) (string.charAt(i) ^ cipherKey);
        return result;
    }
    public static String encode(final String string) {
        return deencode(string);
    }
    public static String decode(final String string) {
        return deencode(string);
    }
}
