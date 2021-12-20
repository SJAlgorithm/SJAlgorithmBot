public class SJAlgorithmBot {

    public static void main(String[] args){
        String token=args[0];
        GitAPI gitAPI = new GitAPI(token);
        gitAPI.readContents("SJAlgorithm","SJAlgorithmBot","README.md");
        gitAPI.updateContents("SJAlgorithm","SJAlgorithmBot","README.md");
    }
}
