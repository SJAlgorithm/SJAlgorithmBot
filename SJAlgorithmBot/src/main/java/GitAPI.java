import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class GitAPI {
    private String sha,content,owner;
    private String token;

    public GitAPI(String token){
        this.token=token;
    }

    public int callGetApi(String strUrl){
        String result="";
        try {
            URL url=new URL(strUrl);
            BufferedReader bf;
            bf=new BufferedReader(new InputStreamReader(url.openStream(),"UTF-8"));
            result=bf.readLine();

            JSONParser jsonParser=new JSONParser();
            JSONObject jsonObject=(JSONObject) jsonParser.parse(result);

            setSha((String) jsonObject.get("sha"));
            setContent((String)jsonObject.get("content"));

            return 1;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public int callPutApi(String strUrl,String owner){
       try {
           URL url=new URL(strUrl);
           HttpURLConnection con = (HttpURLConnection) url.openConnection();
           con.setRequestMethod("PUT");
           con.setRequestProperty("Authorization","token "+token);
           con.setRequestProperty("Content-type","application/json");
           con.setDoOutput(true);
           con.setDoInput(true);

           JSONObject params=new JSONObject();
           params.put("message","update");
           params.put("sha",getSha());
           params.put("owner",owner);
           params.put("content",getContent());

           OutputStreamWriter wr=new OutputStreamWriter(con.getOutputStream(),"UTF-8");
           wr.write(params.toString());
           wr.flush();

           int responseCode=con.getResponseCode();
           if(responseCode==HttpURLConnection.HTTP_OK){
                return 1;
           }
           else{
               return 0;
           }
       } catch (ProtocolException e) {
           e.printStackTrace();
       } catch (MalformedURLException e) {
           e.printStackTrace();
       } catch (IOException e) {
           e.printStackTrace();
       }
       return 0;
    }

    public void readContents(String owner,String repo,String path){
        if(callGetApi("https://api.github.com/repos/"+owner+"/"+repo+"/contents/"+path)==1){
            System.out.println("Read success!");
        }
        else{
            System.out.println("Read fail!");
        }
    }

    public void updateContents(String owner,String repo,String path){
        //temp generate 1000~23853
        int rNum= (int) (Math.random()*(23853-1000+1)+1000);
        String InputString="<br>* A : [BOJ_"+rNum+"](https://www.acmicpc.net/problem/"+rNum+") **Name**";
        byte[] message = InputString.getBytes(StandardCharsets.UTF_8);
        String encoded=Base64.getEncoder().encodeToString(message);
        setContent(getContent()+encoded);

        if(callPutApi("https://api.github.com/repos/"+owner+"/"+repo+"/contents/"+path,owner)==1){
            System.out.println("Update Success!");
        }
        else{
            System.out.println("Update Fail!");
        }
    }

    public String getSha() {
        return sha;
    }

    private void setSha(String sha) {
        this.sha = sha;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}