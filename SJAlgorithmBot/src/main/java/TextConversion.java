import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class TextConversion {
    public static String base64ToUtf8(String str){
        str=str.replaceAll("\n","");
        return new String(Base64.getDecoder().decode(str),StandardCharsets.UTF_8);
    }

    public static String utf8ToBase64(String str){
        byte[] message = str.getBytes(StandardCharsets.UTF_8);
        String result=Base64.getEncoder().encodeToString(message);
        return result;
    }
}
