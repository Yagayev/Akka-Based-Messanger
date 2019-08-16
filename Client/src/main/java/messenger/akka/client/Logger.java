package messenger.akka.client;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Logger {
    // a basic logger, that seemed like the easiest solution to get the exact output as was requested in the assignment
    // all args will be printed as:
    // [<time>][arg0][arg1]...[argN-1]argN
    // defaults to printing "[<time>]" if no args were given
    public static void log(String ... args){
        if(args.length == 0){
            System.out.println("["+getTime()+"]");
        }
        else{
            int i = 0;
            String str = "";
            while (i < args.length-1){
                str += "["+args[i]+"]";
                i++;
            }
            System.out.println("["+getTime()+"]" + str + " " + args[args.length - 1]);
        }
    }
    private static String getTime(){
        Date date = new Date();
        String strDateFormat = "dd/mm/yyyy hh:mm:ss";
        DateFormat dateFormat = new SimpleDateFormat(strDateFormat);
        String formattedDate= dateFormat.format(date);
        return formattedDate;
    }
}
