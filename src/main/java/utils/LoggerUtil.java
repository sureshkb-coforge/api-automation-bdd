package utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class LoggerUtil {

    private static final Logger LOGGER = LogManager.getLogger(LoggerUtil.class);

    private LoggerUtil() {
        // Utility class
    }

    // Prevent log forging (CRLF injection)
    private static String sanitize(String message) {
        if (message == null) {
            return null;
        }
        return message.replaceAll("[\n\r\t]", "_");
    }

    public static void info(String message) {
        LOGGER.info(sanitize(message));
    }

    public static void debug(String message) {
        LOGGER.debug(sanitize(message));
    }

    public static void warn(String message, Exception e) {
        LOGGER.warn(sanitize(message), e);
    }

    public static void error(String message) {
        LOGGER.error(sanitize(message));
    }

    public static void error(String message, Throwable throwable) {
        LOGGER.error(sanitize(message), throwable);
    }
}