package messenger.akka.shared;

public class GroupInviteUser extends GroupMessage {
    public String userToInvite;
    public GroupInviteUser(String fromName, String group, String userToInvite){
        super(fromName, group);
        this.userToInvite =  userToInvite;
    }
}
