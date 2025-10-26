package common;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LoggerUtil {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static void log(String message) {
        System.out.println("[" + LocalDateTime.now().format(FORMATTER) + "] " + message);
    }

    public static void error(String message, Exception e) {
        System.err.println("[" + LocalDateTime.now().format(FORMATTER) + "] ERROR: " + message);
        if (e != null) e.printStackTrace(System.err);
    }
}
