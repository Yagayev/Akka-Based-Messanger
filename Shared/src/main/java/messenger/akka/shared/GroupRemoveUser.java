package example.akka.remote.shared;

public class GroupRemoveUser  extends GroupMessage {
    public String userToRemove;
    public GroupRemoveUser(String fromName, String group, String userToRemove){
        super(fromName, group);
        this.userToRemove =  userToRemove;
    }
}