package messenger.akka.shared;

public class UserMessageToSend extends MessageToSend {

    public String message;
    public UserMessageToSend(String targetUsername, String msg){
        super(targetUsername);
        message = msg;
    }
}
