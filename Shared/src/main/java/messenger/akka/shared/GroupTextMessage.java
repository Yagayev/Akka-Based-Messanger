package example.akka.remote.shared;

public class GroupTextMessage extends GroupSendMessage {
    public String msg;
    public GroupTextMessage(String fromName, String group, String msg){
        super (fromName, group);
        this.msg = msg;
    }
}