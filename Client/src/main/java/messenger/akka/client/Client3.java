package messenger.akka.client;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import com.typesafe.config.ConfigFactory;
import java.util.Scanner;

//exactly the same as Client, created to be able to multiple instances of Client from the IDE.
public class Client3 {
    public static void main(String[] args) {
        // overriding the port with either a user input, or a different port for every client "instance"
        // (i.e. Client, Client2, Client3).
        String port;
        if(args.length > 0) {
            port = args[0];
        }
        else{
            port = "2555";
        }
        com.typesafe.config.Config conf = ConfigFactory.parseString("akka.actor {\n" +
                "    provider = \"akka.remote.RemoteActorRefProvider\"\n" +
                "  }\n" +
                "akka.remote.netty.tcp {\n" +
                "      hostname = 127.0.0.1" + "\n" +
                "      port = " + port +"\n" +
                "  }");

        com.typesafe.config.Config newconf = ConfigFactory.load(conf);

        //starting the system with the overriden port
        ActorSystem system = ActorSystem.create("AkkaRemoteClient", newconf);

        ActorRef client = system.actorOf(Props.create(MessengerClient.class));

        Scanner scanner = new Scanner(System.in);

        while(true){
            String str = scanner.nextLine();
            client.tell(str, ActorRef.noSender());
        }

    }
}
