package messenger.akka.shared;

import java.io.Serializable;

public class DisconnectMessage implements Serializable {
    public String username;
    public DisconnectMessage(String username){
        this.username = username;
    }
}
