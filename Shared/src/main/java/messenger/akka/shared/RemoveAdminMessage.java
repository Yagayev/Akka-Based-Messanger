package messenger.akka.shared;

public class RemoveAdminMessage  extends GroupMessage {
    public String userToDemote;
    public RemoveAdminMessage(String fromName, String group, String userToDemote){
        super(fromName, group);
        this.userToDemote =  userToDemote;
    }
}