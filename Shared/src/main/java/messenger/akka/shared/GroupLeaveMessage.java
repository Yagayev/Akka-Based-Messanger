package example.akka.remote.shared;

public class GroupLeaveMessage extends GroupMessage {

    public GroupLeaveMessage(String fromName, String group){
        super(fromName, group);
    }
}
