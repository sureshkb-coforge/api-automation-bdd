package utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class LoggerUtil {

    private static final Logger LOGGER = LogManager.getLogger(LoggerUtil.class);

    private LoggerUtil() {
        // Utility class
    }

    /**
     * Sanitize log messages to prevent CRLF injection and other control character attacks.
     * Replaces newlines, carriage returns, tabs, and all control characters with underscores.
     * @param message The message to sanitize
     * @return Sanitized message
     */
    private static String sanitize(String message) {
        if (message == null) {
            return null;
        }
        // Remove CRLF, tabs, and all control characters (ASCII 0-31) to prevent injection
        return message.replaceAll("[\\n\\r\\t\\x00-\\x1F]", "_");
    }

    /**
     * Log trace level message
     */
    public static void trace(String message) {
        LOGGER.trace(sanitize(message));
    }

    /**
     * Log debug level message
     */
    public static void debug(String message) {
        LOGGER.debug(sanitize(message));
    }

    /**
     * Log debug level message with exception
     */
    public static void debug(String message, Exception e) {
        if (e == null) {
            LOGGER.debug(sanitize(message));
        } else {
            LOGGER.debug(sanitize(message), e);
        }
    }

    /**
     * Log info level message
     */
    public static void info(String message) {
        LOGGER.info(sanitize(message));
    }

    /**
     * Log warn level message
     */
    public static void warn(String message) {
        LOGGER.warn(sanitize(message));
    }

    /**
     * Log warn level message with exception
     */
    public static void warn(String message, Exception e) {
        if (e == null) {
            LOGGER.warn(sanitize(message));
        } else {
            LOGGER.warn(sanitize(message), e);
        }
    }

    /**
     * Log error level message
     */
    public static void error(String message) {
        LOGGER.error(sanitize(message));
    }

    /**
     * Log error level message with throwable
     */
    public static void error(String message, Throwable throwable) {
        if (throwable == null) {
            LOGGER.error(sanitize(message));
        } else {
            LOGGER.error(sanitize(message), throwable);
        }
    }
}