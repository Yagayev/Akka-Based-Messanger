package messenger.akka.server;

import akka.actor.ActorRef;
import akka.actor.Cancellable;
import akka.actor.Scheduler;
import akka.actor.UntypedActor;
import messenger.akka.shared.*;
import scala.concurrent.duration.FiniteDuration;

import java.util.HashMap;
import java.util.Vector;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.Collections;

public class GroupActor extends UntypedActor {
    String groupName;
    String adminName;
    Vector<String> coAdmins;
    Vector<String> users;
    Vector<String> pendingInvites;
    HashMap<String, MutedUser> muted;
    HashMap<String, ActorRef> addresses;
    Scheduler scheduler;
    public GroupActor(String groupName, String adminName, HashMap<String, ActorRef> addresses, Scheduler scheduler){
        this.groupName = groupName;
        this.adminName = adminName;
        this.addresses = addresses;
        this.scheduler = scheduler;
        coAdmins = new Vector<String>();
        users = new Vector<String>();
        muted = new HashMap<String, MutedUser>();
        pendingInvites = new Vector<String>();
        users.add(adminName);
    }

    public void onReceive(Object msg) throws Exception {
        if(msg instanceof GroupLeaveMessage){
            GroupLeaveMessage leave = (GroupLeaveMessage) msg;
            if(!users.contains(leave.sender)){
                return;
            }
            if(adminName.equals(leave.sender)){
                closeGroup();
            }
            broadcast(new NotificationMessage(leave.sender + " has left " + groupName));
            removeUser(leave.sender);
        }
        else if(msg instanceof GroupInviteUser){
            GroupInviteUser inviteMsg = (GroupInviteUser) msg;
            if(!isAtLeastCoAdmin(inviteMsg.sender)) {
                getSender().tell(new NotificationMessage("You are neither an admin nor a co-admin of "
                        + groupName + "!"), getSelf());
                return;
            }
            if(!addresses.containsKey(inviteMsg.userToInvite)){
                getSender().tell(new NotificationMessage(inviteMsg.userToInvite + " does not exist!"), getSelf());
                return;
            }
            if(users.contains(inviteMsg.userToInvite)){
                getSender().tell(new NotificationMessage(inviteMsg.userToInvite+" is already in "+groupName), getSelf());
                return;
            }
            if(!pendingInvites.contains(inviteMsg.userToInvite)) {
                pendingInvites.add(inviteMsg.userToInvite);
            }

            addresses.get(inviteMsg.userToInvite)
                    .tell(new GroupInvitationMessage(groupName, inviteMsg.sender), getSelf());
        }
        else if(msg instanceof GroupInviteAcceptMessage){
            GroupInviteAcceptMessage acceptence = (GroupInviteAcceptMessage) msg;
            if(!pendingInvites.contains(acceptence.sender)){
                getSender().tell(new NotificationMessage("You weren't invited to "+groupName), getSelf());
                return;
            }
            pendingInvites.removeAll(Collections.singleton(acceptence.sender));
            users.add(acceptence.sender);
            getSender().tell(new NotificationMessage("Welcome to "+groupName), getSelf());
        }
        else if(msg instanceof GroupSendMessage){
            GroupSendMessage sendMsg = (GroupSendMessage) msg;
            if(!users.contains(sendMsg.sender)){
                getSender().tell(new NotificationMessage("You are not a member of "+groupName), getSelf());
                return;
            }
            if(muted.containsKey(sendMsg.sender)){
                getSender().tell(new NotificationMessage("You are muted"), getSelf());
                return;
            }
            else broadcast(sendMsg);

        }
        else if (msg instanceof MakeAdminMessage){
            MakeAdminMessage promotion = (MakeAdminMessage) msg;
            if(!isAtLeastCoAdmin(promotion.sender)) {
                getSender().tell(new NotificationMessage("You are neither an admin nor a co-admin of "
                        + groupName + "!"), getSelf());
                return;
            }
            if(!addresses.containsKey(promotion.userToPromote)){
                getSender().tell(new NotificationMessage(promotion.userToPromote + " does not exist!"), getSelf());
                return;
            }
            if(!users.contains(promotion.userToPromote)){
                getSender().tell(new NotificationMessage(promotion.userToPromote+" is not in "+groupName), getSelf());
                return;
            }
            if(isAtLeastCoAdmin(promotion.userToPromote)){
                getSender().tell(new NotificationMessage(promotion.userToPromote+" is already an admin or co-admin of "+groupName), getSelf());
                return;
            }
            coAdmins.add(promotion.userToPromote);
            addresses.get(promotion.userToPromote).tell(
                    new NotificationMessage("You have been promoted to co-admin in "+groupName+"!"),
                    getSelf());
        }
        else if (msg instanceof RemoveAdminMessage){
            RemoveAdminMessage demotion = (RemoveAdminMessage) msg;
            if(!isAtLeastCoAdmin(demotion.sender)) {
                getSender().tell(new NotificationMessage("You are neither an admin nor a co-admin of "
                        + groupName + "!"), getSelf());
                return;
            }
            if(!addresses.containsKey(demotion.userToDemote)){
                getSender().tell(new NotificationMessage(demotion.userToDemote + " does not exist!"), getSelf());
                return;
            }
            if(!users.contains(demotion.userToDemote)){
                getSender().tell(new NotificationMessage(demotion.userToDemote+" is not in "+groupName), getSelf());
                return;
            }
            if(!coAdmins.contains(demotion.userToDemote)){
                getSender().tell(new NotificationMessage(demotion.userToDemote+" is not a co-admin of "+groupName), getSelf());
                return;
            }
            coAdmins.remove(demotion.userToDemote);
            addresses.get(demotion.userToDemote).tell(
                    new NotificationMessage("You have been demoted to user in "+groupName+"!"),
                    getSelf());
        }
        else if(msg instanceof GroupMuteMessage){
            GroupMuteMessage muteMsg = (GroupMuteMessage) msg;
            if(!isAtLeastCoAdmin(muteMsg.sender)) {
                getSender().tell(new NotificationMessage("You are neither an admin nor a co-admin of "
                        + groupName + "!"), getSelf());
                return;
            }
            if(!addresses.containsKey(muteMsg.userToMute)){
                getSender().tell(new NotificationMessage(muteMsg.userToMute + " does not exist!"), getSelf());
                return;
            }
            if(!users.contains(muteMsg.userToMute)){
                getSender().tell(new NotificationMessage(muteMsg.userToMute+" is not in "+groupName), getSelf());
                return;
            }
            if(muted.containsKey(muteMsg.userToMute)){
                getSender().tell(new NotificationMessage(muteMsg.userToMute+" is already muted in "+groupName), getSelf());
                return;
            }
            ActorRef self = getSelf();
            Cancellable muting = scheduler
                    .scheduleOnce(
                            new FiniteDuration(muteMsg.timeInMils, TimeUnit.MILLISECONDS),
                            self,
                            new ScheduledUnmute(muteMsg.userToMute),
                            getContext().dispatcher(),
                            self);
            muted.put(muteMsg.userToMute,
                    new MutedUser(muteMsg.userToMute, muteMsg.timeInMils, muting));
            addresses.get(muteMsg.userToMute)
                    .tell(new NotificationMessage(
                            "you have been muted in "+groupName+" for "+String.valueOf(muteMsg.timeInMils)),
                            getSelf());
        }
        else if (msg instanceof ScheduledUnmute){
            ScheduledUnmute unmuteMsg = (ScheduledUnmute) msg;
            if(!muted.containsKey(unmuteMsg.username)){
                return;
            }
            muted.remove(unmuteMsg.username);
            addresses.get(unmuteMsg.username)
                    .tell(new NotificationMessage(
                                    "you have been unmuted in "+groupName),
                            getSelf());
        }
        else if (msg instanceof GroupUnmuteMessage){
            GroupUnmuteMessage unmuteMsg = (GroupUnmuteMessage) msg;

            if(!isAtLeastCoAdmin(unmuteMsg.sender)) {
                getSender().tell(new NotificationMessage("You are neither an admin nor a co-admin of "
                        + groupName + "!"), getSelf());
                return;
            }
            if(!addresses.containsKey(unmuteMsg.userToUnmute)){
                getSender().tell(new NotificationMessage(unmuteMsg.userToUnmute + " does not exist!"), getSelf());
                return;
            }
            if(!users.contains(unmuteMsg.userToUnmute)){
                getSender().tell(new NotificationMessage(unmuteMsg.userToUnmute+" is not in "+groupName), getSelf());
                return;
            }
            if(!muted.containsKey(unmuteMsg.userToUnmute)){
                getSender().tell(new NotificationMessage(unmuteMsg.userToUnmute+" is not muted in "+groupName), getSelf());
                return;
            }

            muted.remove(unmuteMsg.userToUnmute);
            addresses.get(unmuteMsg.userToUnmute)
                    .tell(new NotificationMessage(
                                    "you have been unmuted in " + groupName + " by " + unmuteMsg.sender),
                            getSelf());
        }
        if(msg instanceof GroupRemoveUser){
            GroupRemoveUser removal = (GroupRemoveUser) msg;
            if(!isAtLeastCoAdmin(removal.sender)) {
                getSender().tell(new NotificationMessage("You are neither an admin nor a co-admin of "
                        + groupName + "!"), getSelf());
                return;
            }
            if(!addresses.containsKey(removal.userToRemove)){
                getSender().tell(new NotificationMessage(removal.userToRemove + " does not exist!"), getSelf());
                return;
            }
            if(!users.contains(removal.userToRemove)){
                getSender().tell(new NotificationMessage(removal.userToRemove+" is not in "+groupName), getSelf());
                return;
            }
            removeUser(removal.userToRemove);
            addresses.get(removal.userToRemove).tell(
                    new NotificationMessage("You have been removed from "+groupName),
                    getSelf());
        }


    }

    private void removeUser(String userToRemove) {
        if(users.contains(userToRemove)){
            users.remove(userToRemove);
            muted.remove(userToRemove);
            coAdmins.remove(userToRemove);
        }
        else{
            getSender().tell(new NotificationMessage(userToRemove+" is not in "+groupName+"!"), getSelf());
        }
    }

    private void closeGroup(){
        broadcast(new NotificationMessage(groupName + " admin has closed " + groupName));
        ActorRef groupManager = getContext().parent();
        groupManager.tell(new CloseGroupMessage(groupName), getSelf());
    }
    private void sendMsg(Object msg, String name){
        addresses.get(name).tell(msg, getSelf());
    }

    private void broadcast(Object msg){
        users.stream()
                .map(username -> addresses.get(username))
                .peek(actorRef -> actorRef.tell(msg, getSelf()))
                .collect(Collectors.toList());
    }

    private boolean isAtLeastCoAdmin(String str){
        if(adminName.equals(str)){
            return true;
        }
        if(coAdmins.contains(str)){
            return true;
        }
        return false;
    }

    private class MutedUser{
        String username;
        long duration;
        Cancellable unmuting;
        MutedUser(String username, long duration, Cancellable unmuting){
            this.username =username;
            this.duration = duration;
            this.unmuting = unmuting;
        }
    }

    private class ScheduledUnmute{
        String username;
        ScheduledUnmute(String str){
            username = str;
        }
    }
}
