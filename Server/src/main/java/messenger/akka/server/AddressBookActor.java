package messenger.akka.server;
import akka.actor.ActorRef;
import akka.actor.UntypedActor;
import messenger.akka.shared.*;

import java.util.HashMap;

public class AddressBookActor extends UntypedActor {
    HashMap<String, ActorRef> addresses;
    public AddressBookActor(HashMap<String, ActorRef> addresses){
        this.addresses =addresses;
    }
    public void onReceive(Object msg) throws Exception {
        if(msg instanceof LoginMessage){
            String username = ((LoginMessage) msg).username;
            if(addresses.containsKey(username)){
                getSender().tell(new NotificationMessage(username + " is in use!"), getSelf());
            }
            else{
                addresses.put(username, getSender());
                getSender().tell(new SuccessfulLoginMessage(username), getSelf());
            }
        }
        else if(msg instanceof AddressQueryMessage){
            AddressQueryMessage aqm = (AddressQueryMessage) msg;
            ActorRef target = addresses.get(aqm.username);
            if(target == null){
                getSender().tell(new NotificationMessage(aqm.username+" does not exist!"), getSelf());
            }
            else{
                getSender().tell(new UserAddressMessage(aqm.username, target), getSelf());
            }
        }

    }
}
