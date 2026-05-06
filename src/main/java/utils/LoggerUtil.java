package utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.SQLException;

public final class LoggerUtil {

    private static final Logger LOGGER = LogManager.getLogger(LoggerUtil.class);

    private LoggerUtil() {
    }

    public static void info(String message) {
        LOGGER.info(message);
    }

    public static void warn(String message, SQLException e) {
        LOGGER.warn(message);
    }

    public static void error(String message) {
        LOGGER.error(message);
    }

    public static void error(String message, Throwable throwable) {
        LOGGER.error(message, throwable);
    }

    public static void debug(String message) {
        LOGGER.debug(message);
    }
}
