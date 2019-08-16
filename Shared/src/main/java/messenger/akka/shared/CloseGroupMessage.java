package messenger.akka.shared;

import java.io.Serializable;

public class CloseGroupMessage implements Serializable {
    public String groupName;
    public CloseGroupMessage(String groupName){
        this.groupName = groupName;
    }
}
