package messenger.akka.shared;

import java.io.Serializable;

public class AddressQueryMessage implements Serializable {
    public String username;
    public AddressQueryMessage(String str){
        username = str;
    }
}
