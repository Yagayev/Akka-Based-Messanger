package example.akka.remote.shared;

import java.io.File;
import java.io.Serializable;

public class FileMessage implements Serializable {
    public String sender;
    public File file;
    public FileMessage(String str, File f){
        sender = str;
        file = f;
    }
}
