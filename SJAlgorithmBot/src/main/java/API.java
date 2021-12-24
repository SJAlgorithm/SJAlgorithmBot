import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.Map;

public class API {
    public static JSONObject callAPI(String strUrl, String method, Map<String,String> header, Map<String,String> body) throws MalformedURLException {
        URL url;
        HttpURLConnection con;
        try {
            url = new URL(strUrl);
            con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod(method);

            if(header!=null) {
                header.entrySet().stream().forEach(e->con.setRequestProperty(e.getKey(),e.getValue()));
            }
            con.setDoOutput(true);
            con.setDoInput(true);

            if(body!=null) {
                JSONObject params = new JSONObject();
                body.entrySet().forEach(e->{
                    params.put(e.getKey(),e.getValue());
                });
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

                con.disconnect();
                return responseJson;
            }
            else{
                con.disconnect();
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
}
