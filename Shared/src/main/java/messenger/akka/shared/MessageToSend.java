package messenger.akka.shared;

public abstract class MessageToSend extends Message {
    public String target;
    public MessageToSend(String str){
        target = str;
    }
}
