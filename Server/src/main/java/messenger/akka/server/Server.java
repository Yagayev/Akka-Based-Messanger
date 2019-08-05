package example.akka.remote.server;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.actor.Scheduler;
import com.typesafe.config.ConfigFactory;

import java.util.HashMap;

public class Server {

    public static void main(String... args) {
        // Creating environment
//        ActorSystem system = ActorSystem.create("AkkaRemoteServer", ConfigFactory.load());
        ActorSystem system = ActorSystem.create("MessengerServer", ConfigFactory.load());
        // Create an actor
        HashMap<String, ActorRef> addresses = new HashMap<String, ActorRef>();
        Scheduler scheduler = system.scheduler();
        system.actorOf(Props.create(AddressBookActor.class, addresses), "AddressBook");
        system.actorOf(Props.create(GroupSystemActor.class, addresses, scheduler), "GroupSystem");

    }
}
