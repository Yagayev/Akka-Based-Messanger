package messenger.akka.shared;

public class GroupUnmuteMessage  extends GroupMessage  {
    public String userToUnmute;
    public GroupUnmuteMessage(String fromName, String group, String userToUnmute){
        super(fromName, group);
        this.userToUnmute =  userToUnmute;
    }

}