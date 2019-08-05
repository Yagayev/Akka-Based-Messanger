package example.akka.remote.shared;

import java.io.Serializable;

public class LoginMessage extends Message {
    public String username;
    public LoginMessage(String str){
        username = str;
    }

}
