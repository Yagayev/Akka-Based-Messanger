package messenger.akka.server;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.Scheduler;
import akka.actor.UntypedActor;
import messenger.akka.shared.*;

import java.util.HashMap;
import java.util.stream.Collectors;

public class GroupSystemActor extends UntypedActor {
    HashMap<String, ActorRef> groups = new HashMap<String, ActorRef>();
    HashMap<String, ActorRef> addresses;
    Scheduler scheduler;

    public GroupSystemActor(HashMap<String, ActorRef> addresses, Scheduler scheduler){

        this.addresses =addresses;
        this.scheduler = scheduler;
    }
    public void onReceive(Object msg) throws Exception {
        if(msg instanceof GroupCreationMessage){
            GroupCreationMessage groupCreation = (GroupCreationMessage) msg;
            if(groups.containsKey(groupCreation.groupName)){
                getSender().tell(new NotificationMessage(groupCreation.groupName+"  already exists!"), getSelf());
            }
            else{
                ActorRef newGroup = getContext().actorOf(Props.create(GroupActor.class, groupCreation.groupName, groupCreation.sender, addresses, scheduler));
                groups.put(groupCreation.groupName, newGroup);
                getSender().tell(new NotificationMessage(groupCreation.groupName+" created successfully!"), getSelf());
            }
        }
        else if(msg instanceof GroupMessage){
            GroupMessage groupMsg = (GroupMessage) msg;
            ActorRef group = groups.get(groupMsg.groupName);
            if(group == null){
                getSender().tell(
                        new NotificationMessage(groupMsg.groupName+" doesn't exist!"),
                        getSelf());
            }
            else{
                group.tell(groupMsg, getSender());
            }
        }
        else if(msg instanceof CloseGroupMessage){
            CloseGroupMessage closeGroup = (CloseGroupMessage) msg;
            ActorRef groupRef = groups.get(closeGroup.groupName);
            if(groupRef == null){
                return;
            }
            groups.remove(closeGroup.groupName);
            groupRef.tell(akka.actor.PoisonPill.getInstance(), getSelf());
        }
        else if(msg instanceof DisconnectMessage){
            DisconnectMessage discMsg = (DisconnectMessage) msg;
            //sign out of all groups
            groups.keySet()
                    .stream()
                    .peek(groupname -> {
                        groups.get(groupname)
                                .tell(
                                        new GroupLeaveMessage(discMsg.username, groupname),
                                        ActorRef.noSender()
                                );
                    })
                    .collect(Collectors.toList());
            addresses.remove(discMsg.username);
            getSender().tell(msg, getSelf());
        }
    }
}
