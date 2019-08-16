package messenger.akka.shared;

import java.io.Serializable;

public class GroupCreationMessage implements Serializable {
    public String sender;
    public String groupName;
    public GroupCreationMessage(String group, String fromName){
        this.sender = fromName;
        this.groupName = group;
    }

}
