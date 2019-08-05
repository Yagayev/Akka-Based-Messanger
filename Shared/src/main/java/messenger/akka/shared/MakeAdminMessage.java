package example.akka.remote.shared;

public class MakeAdminMessage extends GroupMessage {
    public String userToPromote;
    public MakeAdminMessage(String fromName, String group, String userToPromote){
        super(fromName, group);
        this.userToPromote =  userToPromote;
    }
}
