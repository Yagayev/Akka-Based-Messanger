package example.akka.remote.shared;

import java.io.Serializable;

public class GroupInviteAcceptMessage extends GroupMessage {
    public GroupInviteAcceptMessage(String userName, String groupName) {
        super(userName, groupName);
    }
}
