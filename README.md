Java Client-Server based messenger using Akka

#### Made by
Meir Yagayev 

#### Description
This program was made according to the instruction in Assignment 2 - Akka_ The Actor Model.md
Used [this](https://github.com/MartinKanters/java-akka-remote-example.git) repository as a boilerplate

##### The actors

On the client side there is a single Actor:
###### Client Actor
Handles 2 kinds of messeges:
 * Strings from the user, that are parsed to an action
 * Messges coming from the servers, that are parsed, make the necessery changes if there are such, and printed
   according to it's type

The Strings that are supported as input are:
/user text <target> <message>
/user file <target> <sourcefilePath>
/user connect <username>
/user disconnect
/group create <groupname>
/group leave <groupname>
/group send text <groupname> <message>
/group send file <groupname> <sourcefilePath>
/group user invite <groupname> <targetusername>
/group user remove <groupname> <targetusername>
/group user mute <groupname> <targetusername> <timeinseconds>
/group user unmute <groupname> <targetusername>
/group coadmin add <groupname> <targetusername>
/group coadmin remove <groupname> <targetusername>

On the server side there are 2 main actors:
####### Address Book Actor
  * Add new users
  * Remove an existing user
  * Query for an adress of a user by it's username

###### Group System Actor
  * Create groups
  * Delete groups(as per the requirements, can only be done by the group Admin leaving the group)
  * Remove a user from all of it's group on disconnect
  * forward every message that is targeted to a group

Every Group created is a child actor of the Group System Actor, and in itself is a Group Actor.

###### Group Actor
  * Invite users (Admin or Co Admin only)
  * Leave
  * Mute users (Admin or Co Admin only)
  * Unmute users (Admin or Co Admin only)
  * Remove users (Admin or Co Admin only)
  * Promote users to Co Admin (Admin or Co Admin only)
  * Demote users from Co Admin to regular users (Admin or Co Admin only)
  * Broadcast a text messege to the entire group (non-muted users only)
  * Broadcast a text file to the entire group (non-muted users only)


#### The Messege Proccesses

###### connction
asserts it's not already connectd
sends a LoginMessage to the adגress book
adגress book:
asserts username not taken
adds saves the Sender Ref as the adress of username
sends confirmatio to client
client updates it's connected status and username

###### direct messege
messege is added to the a queue of messeges to Target user.
an adress query is sent to the adress book.
adressbook matches the username with the ones it saved, and returns the matching ActorRef.
ones a matching ActorRef is returned, all the pending meesages to that user are sent.
on recieve, the client prints/saves file accordingly.
The implementation was with a queue for outging messages to prevent the system from being blocked
while waiting for the AtcorRef. This way the client can send several messeges even if for some reason
they only get the addres later, and they will be sent as soon as possible, and without a delay.
I did use a while loop for emptying the queue in the order the messages were dispatched, to make
sure the order of the messages is maintained.
A file that was sent from path "a/b/c.txt" is saved as "c.txt" in the
root folder of the project.

###### new group
a message with the group name is sent from the client to the group system
if the group already exists, yhe group system tells the client
otherwise, the group system creates a new child actor, which is a group actor
the new group actor is saved in a dictionary, where the group name is the key
the sender becomes the admin of the new group
a confirmetion message is sent to the client

###### other group messages
the client sends the message with the user name and group name, to the group system.
the group system asserts that a group with such name exists, and forwards it
to the apropreate group actor.
the group actor asserts the user name has the premissions to do the action
attempted, and performs it(adds/removes the apropreate user from the 
apropreate data structure, broadcast the message, etc.).

in order to check those permisions, each group has the following data structures:
- 3 vector of usernames:
 - usernames, represents all users that are in the group
 - coadmins, represents all group co-admins
 - pending invites - represents all users that have been invited to the group and yet to aprove
- muted, a map with usernames as keys, that maps them to how the duration of their muting,
  and the scheduled unmuting message.

when a user is muted the group actor uses the server system's scheduler
to recive the unmute message once the time for the muting passes.
when a user is unmuted, the group actor canceles this scheduled unmute message
this is done to prevent a case where:
T+0 - user is muted for 10 time units, unmuate A is scheduled
T+1 - user is unmuted
T+2 - user is muted again for 10 time units, unmute B is scheduled
t+10 - A is recived, user is unmuted, despit only 8 time units have passed

whe a user is invited, a message is sent only to him, and he is added to the pending invitations.
once he accepts an acceptence message, the group asserts he was invited, and adds him as a user

on leave, if the user was the admin, a message that the group was closed is
broadcasted to all users, and the group actor sends a message to the group system
the group system removes the group from it's map, and sends the group a poison pill

