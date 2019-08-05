package example.akka.remote.client;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import com.typesafe.config.ConfigFactory;
import example.akka.remote.shared.*;

import java.io.IOException;
import java.util.Arrays;
import java.util.Scanner;

public class Client {
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
                "      port = " + 2553 +"\n" +
                "  }");

        com.typesafe.config.Config newconf = ConfigFactory.load(conf);
        ActorSystem system = ActorSystem.create("AkkaRemoteClient", newconf);
        // Client actor
        ActorRef client = system.actorOf(Props.create(MessengerClient.class));

        Scanner scanner = new Scanner(System.in);

        while(true){
            String str = scanner.nextLine();
            client.tell(str, ActorRef.noSender());

//            Message msg = parseStr(str);
//            if(msg == null){
//                System.out.println("Illigal command!");
//            }
//            else{
//                client.tell(msg, ActorRef.noSender());
//            }
        }

    }

    static Message parseStr(String str){
        String[] words = str.split(" ");
        if (words.length < 2){
            return null;
        }
        if(words[0].equals("/user")){
            if(words[1].equals("text")){
                if (words.length < 4){
                    return null;
                }
                String target = words[2];
                String[] msgWords = Arrays.copyOfRange(words, 3, words.length);
                String msg = String.join(" ", msgWords);
                return new UserMessageToSend(target, msg);
            }
            else if(words[1].equals("file")){
                if (words.length != 4){
                    return null;
                }
                return new FileToSend(words[2], words[3]);

            }
            else if(words[1].equals("connect")){
                if (words.length < 3){
                    return null;
                }
                String[] msgWords = Arrays.copyOfRange(words, 2, words.length);
                String msg = String.join(" ", msgWords);
                return new LoginMessage(msg);
            }
            else if(words[1].equals("disconnect")){
                System.out.println("TODO disconnect");
            }
        }
        else if(words[0].equals("/group")){
            if(words[1].equals("create")){
                if (words.length < 3){
                    return null;
                }
                String[] msgWords = Arrays.copyOfRange(words, 2, words.length);
                String msg = String.join(" ", msgWords);
                return new GroupToCreateMessage(msg);
            }
            else if(words[1].equals("user")&&words.length==5&&words[2].equals("invite")){
//                if (words.length < 4){
//                    return null;
//                }
//                String[] msgWords = Arrays.copyOfRange(words, 3, words.length);
//                String userToInvite = String.join(" ", msgWords);
                return new GroupUserToAddMessage(words[3],words[4]);
                // so far I didn't limit neither username nor group name to not
                // have spaces, but it seems here it can't be done
                // without ambiguity

            }
            else if (words[1].equals("coadmins")&&words.length==5){
                if(words[2].equals("add")){

                }
                else if(words[2].equals("remove")){

                }

            }
            else {
                System.out.println("TODO groups");
            }
        }
        return null;
    }
}
