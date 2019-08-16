package messenger.akka.shared;

import akka.actor.ActorRef;
import java.io.Serializable;

public class UserAddressMessage implements Serializable {
    public String username;
    public ActorRef adress;
    public UserAddressMessage(String str, ActorRef ref){
        username = str;
        adress = ref;
    }
}
