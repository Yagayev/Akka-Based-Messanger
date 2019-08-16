package messenger.akka.shared;

import java.io.Serializable;

public abstract class GroupMessage implements Serializable {
    public String sender;
    public String groupName;
    public GroupMessage(String fromName, String group){
        this.sender = fromName;
        this.groupName = group;
    }

}
