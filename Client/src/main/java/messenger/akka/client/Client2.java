package example.akka.remote.client;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import com.typesafe.config.ConfigFactory;
import example.akka.remote.shared.*;

import java.io.IOException;
import java.util.Arrays;
import java.util.Scanner;

public class Client2 {
    public static void main(String[] args) {
        // Creating environment
//        ActorSystem system = ActorSystem.create("AkkaRemoteClient", ConfigFactory.load());
        String port;
        if(args.length > 0) {
            port = args[0];
        }
        else{
            port = "2553";
        }
        com.typesafe.config.Config conf = ConfigFactory.parseString("akka.actor {\n" +
                "    provider = \"akka.remote.RemoteActorRefProvider\"\n" +
                "  }\n" +
                "akka.remote.netty.tcp {\n" +
                "      hostname = 127.0.0.1" + "\n" +
                "      port = " + 2222 +"\n" +
                "  }");

        com.typesafe.config.Config newconf = ConfigFactory.load(conf);
        ActorSystem system = ActorSystem.create("AkkaRemoteClient", newconf);
        // Client actor
        ActorRef client = system.actorOf(Props.create(MessengerClient.class));

        Scanner scanner = new Scanner(System.in);

        while(true){
            String str = scanner.nextLine();
            client.tell(str, ActorRef.noSender());

        }

    }
}
