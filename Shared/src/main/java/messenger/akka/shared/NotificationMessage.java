package messenger.akka.shared;

import java.io.Serializable;

public class NotificationMessage implements Serializable {
    public String note;
    public NotificationMessage(String str){
        note = str;
    }
}
