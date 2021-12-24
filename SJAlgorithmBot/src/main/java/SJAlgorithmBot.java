import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

public class SJAlgorithmBot {
    public static JSONObject callAPI(String strUrl, String method, Map<String,String> header,Map<String,String> body) throws MalformedURLException {
        try {
            URL url = new URL(strUrl);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod(method);

            if(header!=null) {
               header.entrySet().stream().forEach(e->con.setRequestProperty(e.getKey(),e.getValue()));
            }
            con.setDoOutput(true);
            con.setDoInput(true);

            if(body!=null) {
                JSONObject params = new JSONObject();
                OutputStreamWriter wr = new OutputStreamWriter(con.getOutputStream(), "UTF-8");

                wr.write(params.toString());
                wr.flush();
            }
            int responseCode=con.getResponseCode();
            if(responseCode==HttpURLConnection.HTTP_OK){
                BufferedReader br=new BufferedReader(new InputStreamReader(con.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String line = "";
                while ((line = br.readLine()) != null) {
                    sb.append(line);
                }
                JSONObject responseJson = new JSONObject();
                JSONParser jsonParser=new JSONParser();
                responseJson=(JSONObject) jsonParser.parse(sb.toString());

                return responseJson;
            }
            else{
                System.out.println("Error Code : "+responseCode);
            }
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String base64ToUtf8(String str){
        byte[] message= Base64.getDecoder().decode(str.trim());
        String result=new String(message, StandardCharsets.UTF_8);
        result.replace("\n","<br>");
        return result;
    }

    public static String utf8ToBase64(String str){
        str.replace("\n","<br>");
        byte[] message = str.getBytes(StandardCharsets.UTF_8);
        String result=Base64.getEncoder().encodeToString(message);
        return result;
    }

    public static void main(String[] args) throws MalformedURLException {
        String token=args[0];
        JSONObject readmeJson=callAPI("https://api.github.com/repos/Hyeon-Uk/Hyeon-Uk/readme","GET",null,null);
        System.out.println("My Readme:\n"+base64ToUtf8((String)readmeJson.get("content")));
    }
}
