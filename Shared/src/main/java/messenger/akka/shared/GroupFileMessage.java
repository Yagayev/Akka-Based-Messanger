package messenger.akka.shared;

import java.io.File;

public class GroupFileMessage extends GroupSendMessage {
    public File file;
    public GroupFileMessage(String fromName, String group, File file){
        super (fromName, group);
        this.file = file;
    }
}
