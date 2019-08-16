package messenger.akka.shared;

public class GroupMuteMessage  extends GroupMessage {
    public String userToMute;
    public long timeInMils;
    public GroupMuteMessage(String fromName, String group, String userToMute, long timeInMils){
        super(fromName, group);
        this.userToMute =  userToMute;
        this.timeInMils = timeInMils;
    }
}