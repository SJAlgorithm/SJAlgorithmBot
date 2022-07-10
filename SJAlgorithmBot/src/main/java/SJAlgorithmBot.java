import org.json.simple.JSONObject;

import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class SJAlgorithmBot {
    static final String sjUrl="https://api.github.com/repos/SJAlgorithm/SJAlgorithm/contents/README.md";
    static final String tonyUrl="https://api.github.com/repos/tony9402/baekjoon/contents/picked.md";
    static final String sjBotUrl="https://api.github.com/repos/SJAlgorithm/SJAlgorithmBot/contents/README.md";
    static final int instLength=22;//머릿말 길이
    static final API api=API.getInstance();

    //문제 추첨
    public static Vector<Problem> generate(String oldContents,int count) throws IOException {
        //이미 해결한 문제들 체크
        Map<Integer,Boolean> cleared=new HashMap<>();
        String[] old=oldContents.split("\n");
        Arrays.stream(old).forEach(e->{
            Pattern pattern = Pattern.compile("\\/([0-9]+)\\)");
            Matcher matcher=pattern.matcher(e);
            while(matcher.find()){
                cleared.put(Integer.parseInt(matcher.group(1)),true);
            }
        });


        //tony9402님의 추천 알고리즘 문제들이 있는 md파일 읽어오기
        JSONObject tonyJson=api.callAPI(tonyUrl,"GET",null,null);
        String content=TextConversion.base64ToUtf8((String)tonyJson.get("content"));
        String[] contents=content.split("\n");

        //선정되지않은 문제번호와 제목 정규식으로 추출
        Vector<Problem> problems=new Vector<>();
        Arrays.stream(contents).forEach(e->{
            if(e.contains("<img")){
                Pattern pattern = Pattern.compile("\\[(.*?)\\]");
                Matcher matcher=pattern.matcher(e);
                String[] temp=new String[2];
                int index=0;
                while(matcher.find()){
                    temp[index++]=matcher.group(1);
                }
                int pNum=Integer.parseInt(temp[0]);
                String pTitle=temp[1];
                if(cleared.get(pNum)==null){
                    problems.add(new Problem(pNum,pTitle));
                }
            }
        });


        //count개수만큼 랜덤 추출
        Random random=new Random();
        int maxSize=problems.size();
        Vector<Problem> ret=new Vector<>();
        for(int i=0;i<count;i++){
            int rNum=random.nextInt(maxSize);
            ret.add(problems.get(rNum));
        }

        //신규 문제 반환
        return ret;
    }

    //새로운 문제들을 뽑아서 포멧으로 변환
    public static String getNewContents(String oldContents,int count) throws IOException {
        Vector<Problem> problems=generate(oldContents,count);//새로 추첨된 문제
        //포멧 생성
        Calendar calendar=Calendar.getInstance();
        String contents="*****************************************************************************\n\n";
        contents+=String.format("### %d/%d/%d~",
                calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH)+1,
                calendar.get(Calendar.DAY_OF_MONTH));
        calendar.setTime(calendar.getTime());
        calendar.add(Calendar.DATE,6);
        contents+=String.format("%d/%d/%d\n",
                calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH)+1,
                calendar.get(Calendar.DAY_OF_MONTH));
        for(int i=0;i<problems.size();i++){
            Problem p=problems.get(i);
            contents+=String.format("* %c : [BOJ_"+p.getpNum()+"](https://www.acmicpc.net/problem/%d) **%s**\n",'A'+i,p.getpNum(),p.getpTitle());
        }
        return oldContents.substring(0,instLength)+"\n"+contents+oldContents.substring(instLength);
    }

    public static void main(String[] args) throws IOException {
        String token=args[0];

        //API를 이용해서 SJAlgorithm의 README.md 읽어옴
        Map<String,String> h=new HashMap<>();
        h.put("Authorization","token "+token);
        JSONObject myReadmeJson=api.callAPI(sjUrl,"GET",h,null);
        String contents=TextConversion.base64ToUtf8((String)myReadmeJson.get("content"));
        String newContents=getNewContents(contents,4);

        //set Header and Body
        Map<String,String> header=new HashMap<>();
        Map<String,String> body=new HashMap<>();

        header.put("Authorization","token "+token);
        body.put("message","update");
        body.put("sha",(String)myReadmeJson.get("sha"));
        body.put("content",TextConversion.utf8ToBase64(newContents));
        body.put("owner","SJAlgorithm");

        JSONObject updateResponse=api.callAPI(sjUrl,"PUT",header,body);
    }
}
