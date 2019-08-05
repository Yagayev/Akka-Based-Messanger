package example.akka.remote.shared;

public class FileToSend extends MessageToSend{
    public String path;
    public FileToSend(String targetUsername, String filename){
        super(targetUsername);
        path = filename;
    }
}
