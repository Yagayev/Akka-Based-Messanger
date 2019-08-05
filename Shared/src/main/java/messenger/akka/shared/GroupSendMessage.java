package example.akka.remote.shared;

public abstract class GroupSendMessage extends GroupMessage{
    public GroupSendMessage(String fromName, String group){
        super (fromName, group);
    }
}
