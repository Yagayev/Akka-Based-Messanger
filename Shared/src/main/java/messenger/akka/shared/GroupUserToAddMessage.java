package messenger.akka.shared;
//TODO DELETE THIS
public class GroupUserToAddMessage extends Message {
    public String user;
    public String groupName;
    public GroupUserToAddMessage(String user, String groupName){
        this.user = user;
        this.groupName = groupName;
    }
}
