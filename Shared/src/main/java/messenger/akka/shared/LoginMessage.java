package messenger.akka.shared;

public class LoginMessage extends Message {
    public String username;
    public LoginMessage(String str){
        username = str;
    }

}
