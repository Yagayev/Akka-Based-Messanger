package example.akka.remote.shared;

import akka.actor.ActorRef;

import java.io.Serializable;

public class UserMessage implements Serializable {
    public String senderName;
    public String messageContent;

    public UserMessage(String senderStr, String msg){
        senderName = senderStr;
        messageContent = msg;
    }


}
