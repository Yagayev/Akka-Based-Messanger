package messenger.akka.shared;

import java.io.Serializable;

public class GroupInvitationMessage implements Serializable {
    public String groupName;
    public String invitor;
    public GroupInvitationMessage(String GroupName,String invitor){
        this.groupName = GroupName;
        this. invitor = invitor;
    }
}
