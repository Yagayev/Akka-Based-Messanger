package example.akka.remote.shared;
//TODO DELETE THIS
public class GroupToCreateMessage extends Message{
    public String groupName;
    public GroupToCreateMessage(String str){
        groupName = str;
    }
}
