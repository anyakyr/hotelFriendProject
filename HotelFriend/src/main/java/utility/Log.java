package utility;
import org.apache.log4j.Logger;

public class Log {

    private static Logger Log = Logger.getLogger(utility.Log.class.getName());

    public static <T extends Object> void info(T message) {
        Log.info(String.valueOf(message));
    }
    public static <T extends Object> void warn(T message) {
        Log.warn(String.valueOf(message));
    }
    public static <T extends Object> void error(T message) {
        Log.error(String.valueOf(message));
    }

}


