package messenger.akka.shared;

public abstract class GroupSendMessage extends GroupMessage{
    public GroupSendMessage(String fromName, String group){
        super (fromName, group);
    }
}
