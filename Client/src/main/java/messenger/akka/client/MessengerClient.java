package example.akka.remote.client;

import akka.actor.ActorSelection;
import akka.actor.Cancellable;
import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import example.akka.remote.shared.*;

import java.io.*;
import java.util.*;


public class MessengerClient extends UntypedActor {
    boolean loggedIn;
    private String username; //starts uninstantiated, only gets value once login issuccessfull
    private LoggingAdapter log;
    private Map<String, Queue<MessageToSend>> outgoingMessages;
    // Getting the other actor
    private ActorSelection addressBook;
    private ActorSelection groupSystem;

    public MessengerClient(){
        loggedIn = false;
        log = Logging.getLogger(getContext().system(), this);
        addressBook = getContext().actorSelection("akka.tcp://MessengerServer@127.0.0.1:2552/user/AddressBook");
        groupSystem = getContext().actorSelection("akka.tcp://MessengerServer@127.0.0.1:2552/user/GroupSystem");
        outgoingMessages = new HashMap<String, Queue<MessageToSend>>();
    }


    @Override
    public void onReceive(Object message) throws Exception {
        if(message instanceof String){
            parseStr((String)message);
        }
        if(message instanceof LoginMessage){
            if(loggedIn){
                Logger.log("info", "Already logged in!");
                return;
            }
            addressBook.tell(message, getSelf());
        }
        else if(message instanceof NotificationMessage){
            Logger.log("info", ((NotificationMessage) message).note);
        }
        else if(message instanceof SuccessfulLoginMessage){
            loggedIn = true;
            username = ((SuccessfulLoginMessage) message).username;
            Logger.log("info", ((SuccessfulLoginMessage) message).username+" has connected successfully!");
        }
        else if(message instanceof UserMessage){
            UserMessage um = (UserMessage) message;
//            System.out.println("["+getTime()+"][user]["+um.senderName+"] "+um.messageContent);
            Logger.log("user", um.senderName, um.messageContent);

        }
        else if(message instanceof MessageToSend){
            MessageToSend um = (MessageToSend) message;
            Queue<MessageToSend> box = outgoingMessages.get(um.target);
            if(box == null){
                box = new LinkedList<MessageToSend>();
                outgoingMessages.put(um.target, box);
            }
            box.add(um);
            addressBook.tell(new AddressQueryMessage(um.target), getSelf());
        }
        else if(message instanceof UserAddressMessage){
            UserAddressMessage uam = (UserAddressMessage) message;
            Queue<MessageToSend> box = outgoingMessages.get(uam.username);
            while(!box.isEmpty()){
                MessageToSend msg = box.poll();
                if (msg instanceof UserMessageToSend){
                    UserMessageToSend textMsg = (UserMessageToSend) msg;
                    uam.adress.tell(new UserMessage(this.username, textMsg.message), getSelf());
                }
                else if(msg instanceof FileToSend){
                    FileToSend fileMsg = (FileToSend) msg;
                    File file = new File(fileMsg.path);
                    uam.adress.tell(new FileMessage(this.username, file), getSelf());
                }

            }
        }
        else if(message instanceof FileMessage){
            FileMessage fm = (FileMessage) message;
            File newFile = new File(fm.file.getName());
            InputStream is = null;
            OutputStream os = null;
            try {
                is = new FileInputStream(fm.file);
                os = new FileOutputStream(newFile);
                byte[] buffer = new byte[1024];
                int length;
                while ((length = is.read(buffer)) > 0) {
                    os.write(buffer, 0, length);
                }
                Logger.log("user", fm.sender, "File received: " + newFile.getPath());
//                System.out.println("["+getTime()+"][user]["+fm.sender+"] File received: "+newFile.getPath());
            }
            catch (IOException e){
                System.out.println("failed to recieve file");
                System.out.println(e.getMessage());
                System.out.println(e.getCause());
                e.printStackTrace();
            }
            finally {
                is.close();
                os.close();
            }


        }
//        else if(message instanceof  GroupToCreateMessage){
//            GroupToCreateMessage groupMsg = (GroupToCreateMessage) message;
//            groupSystem.tell(new GroupCreationMessage(groupMsg.groupName, username), getSelf());
//        }
//        else if(message instanceof GroupUserToAddMessage){
//            GroupUserToAddMessage userToAddMsg = (GroupUserToAddMessage) message;
//            groupSystem.tell(new GroupInviteUser(username, userToAddMsg.groupName, userToAddMsg.user), getSelf());
//        }

        //the old implementation that couldn't work asynchronycally
//        else if(message instanceof GroupInvitationMessage){
//            GroupInvitationMessage invitation = (GroupInvitationMessage) message;
//            System.out.println("You have been invited to "+ invitation.groupName +", Accept? y/n");
//            char c = '0';
//            while(c != 'y'&& c != 'Y'&&c != 'n'&&c != 'N'){
//                c = (char) System.in.read();
//            }
//            if(c=='y'||c=='Y'){
//                getSender().tell(
//                        new GroupInviteAcceptMessage(username),
//                        getSelf()
//                );
//            }
//        }
        else if(message instanceof GroupInvitationMessage){
            GroupInvitationMessage invitation = (GroupInvitationMessage) message;
            System.out.println("You have been invited to "+ invitation.groupName +
                    ", to accept type in '/group accept " + invitation.groupName + "'");

        }
        else if (message instanceof GroupTextMessage){
            GroupTextMessage gtm = (GroupTextMessage) message;
            Logger.log(gtm.groupName, gtm.sender, gtm.msg);
        }
        else if (message instanceof  GroupFileMessage){
            GroupFileMessage fm = (GroupFileMessage) message;
            File newFile = new File(fm.file.getName());
            InputStream is = null;
            OutputStream os = null;
            try {
                is = new FileInputStream(fm.file);
                os = new FileOutputStream(newFile);
                byte[] buffer = new byte[1024];
                int length;
                while ((length = is.read(buffer)) > 0) {
                    os.write(buffer, 0, length);
                }
                Logger.log(fm.groupName, fm.sender, "File received: " + newFile.getPath());
            }
            catch (IOException e){
                System.out.println("failed to recieve file");
                System.out.println(e.getMessage());
                System.out.println(e.getCause());
                e.printStackTrace();
            }
            finally {
                is.close();
                os.close();
            }

        }
        else if (message instanceof DisconnectMessage){
            if(((DisconnectMessage) message).username.equals(username)){
                username = null;
                loggedIn = false;
            }


        }


//        if (message.equals("DoCalcs")) {
//
//            log.info("Got a calc job, send it to the remote calculator");
//            selection.tell(new Messages.Sum(1, 2), getSelf());
//
//        } else if (message instanceof Messages.Result) {
//            Messages.Result result = (Messages.Result) message;
//            log.info("Got result back from calculator: {}", result.getResult());
//        }


    }

    void parseStr(String str){
        String[] words = str.split(" ");
        if (words.length < 2){
            illegalInput();
            return;
        }
        //if user is not logged in, the only allowed action is connect
        if(!loggedIn
            && (words.length!=3
                || !words[0].equals("/user")
                || !words[1].equals("connect"))){
            System.out.println("You are not signed in!");
            return;
        }

        if(words[0].equals("/user")){
            if(words[1].equals("text")){
                if (words.length < 4){
                    illegalInput();
                    return;
                }
                String target = words[2];
                String[] msgWords = Arrays.copyOfRange(words, 3, words.length);
                String msg = String.join(" ", msgWords);
                commitUserMessage(new UserMessageToSend(target, msg));
                return;
            }
            else if(words[1].equals("file")){
                if (words.length != 4){
                    illegalInput();
                    return;
                }
                commitUserMessage(new FileToSend(words[2], words[3]));
                return;

            }
            else if(words[1].equals("connect")){
                if (words.length < 3){
                    illegalInput();
                    return;
                }
                String[] msgWords = Arrays.copyOfRange(words, 2, words.length);
                String msg = String.join(" ", msgWords);

                if(loggedIn){
                    log.info("Already logged in!");
                    return;
                }
                addressBook.tell(new LoginMessage(msg), getSelf());
                return;
            }
            else if(words[1].equals("disconnect")&&words.length==2){

                groupSystem.tell(new DisconnectMessage(username), getSelf());
                return;
            }
        }
        else if(words[0].equals("/group")){
            if(words[1].equals("create")){
                if (words.length < 3){
                    illegalInput();
                    return;
                }
                String[] msgWords = Arrays.copyOfRange(words, 2, words.length);
                String msg = String.join(" ", msgWords);
//                GroupToCreateMessage groupMsg = new GroupToCreateMessage(msg);
                groupSystem.tell(new GroupCreationMessage(msg, username), getSelf());

                return;
            }
            if(words[1].equals("user")&&words.length==5&&words[2].equals("invite")){
//                if (words.length < 4){
//                    return null;
//                }
//                String[] msgWords = Arrays.copyOfRange(words, 3, words.length);
//                String userToInvite = String.join(" ", msgWords);

                groupSystem.tell(new GroupInviteUser(username, words[3], words[4]), getSelf());
                // so far I didn't limit neither username nor group name to not
                // have spaces, but it seems here it can't be done
                // without ambiguity

                return;
            }
            else if(words[1].equals("send")&&words.length>=5){
                if(words[2].equals("text")){
                    String group = words[3];
                    String[] msgWords = Arrays.copyOfRange(words, 4, words.length);
                    String msg = String.join(" ", msgWords);
                    groupSystem.tell(new GroupTextMessage(username, group, msg), getSelf());
                    return;
                }
                else if(words[2].equals("file")&&words.length==5){
                    String group = words[3];
                    File file = new File(words[4]);
                    groupSystem.tell(new GroupFileMessage(username,group, file), getSelf());
                }
            }
            else if (words[1].equals("coadmins")&&words.length==5){
                if(words[2].equals("add")){
                    groupSystem.tell(new MakeAdminMessage(username, words[3], words[4]), getSelf());
                    return;
                }
                else if(words[2].equals("remove")){
                    groupSystem.tell(new RemoveAdminMessage(username, words[3], words[4]), getSelf());
                    return;
                }

            }
            else if (words[1].equals("user")&&words.length==6&&words[2].equals("mute")){
                long time;
                try{
                    time = Long.parseLong(words[5]);
                }
                catch(NumberFormatException e){
                    illegalInput();
                    return;
                }
                groupSystem.tell(new GroupMuteMessage(username, words[3], words[4], time), getSelf());
                return;
            }
            else if (words[1].equals("user")&&words.length==5&&words[2].equals("unmute")) {
                groupSystem.tell(new GroupUnmuteMessage(username, words[3], words[4]), getSelf());
                return;
            }
            else if (words[1].equals("user")&&words.length==5&&words[2].equals("remove")) {
                groupSystem.tell(new GroupRemoveUser(username, words[3], words[4]), getSelf());
                return;
            }
            else if (words[1].equals("leave")&&words.length == 3) {
                groupSystem.tell(new GroupLeaveMessage(username, words[2]), getSelf());
                return;
            }
            else if(words[1].equals("accept") && words.length == 3){
                groupSystem.tell(
                        new GroupInviteAcceptMessage(username, words[2]),
                        getSelf()
                );
                return;
            }
        }
        illegalInput();
    }

    private void commitUserMessage(MessageToSend um){
        Queue<MessageToSend> box = outgoingMessages.get(um.target);
        if(box == null){
            box = new LinkedList<MessageToSend>();
            outgoingMessages.put(um.target, box);
        }
        box.add(um);
        addressBook.tell(new AddressQueryMessage(um.target), getSelf());
    }

    private void illegalInput(){
        System.out.println("Illigal input!");
    }


}

