package messenger.akka.shared;

public class GroupInviteAcceptMessage extends GroupMessage {
    public GroupInviteAcceptMessage(String userName, String groupName) {
        super(userName, groupName);
    }
}
