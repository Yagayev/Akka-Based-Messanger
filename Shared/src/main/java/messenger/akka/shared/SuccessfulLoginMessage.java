package messenger.akka.shared;

import java.io.Serializable;

public class SuccessfulLoginMessage implements Serializable {
    public String username;
    public SuccessfulLoginMessage(String str){
        username = str;
    }
}
