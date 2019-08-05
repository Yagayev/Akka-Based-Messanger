package example.akka.remote.shared;

import java.io.Serializable;

public abstract class MessageToSend extends Message {
    public String target;
    public MessageToSend(String str){
        target = str;
    }
}
