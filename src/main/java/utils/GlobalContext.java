// Enhanced: src/main/java/utils/GlobalContext.java
package utils;

import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

/**
 * Global context for storing and retrieving runtime data across the entire test project.
 * Thread-safe for concurrent access.
 */
public final class GlobalContext {

    private static final ConcurrentHashMap<String, Object> context = new ConcurrentHashMap<>();

    private GlobalContext() {}

    /**
     * Stores a value in the global context.
     */
    public static void set(String key, Object value) {
        if (key == null || key.trim().isEmpty()) {
            throw new IllegalArgumentException("Context key cannot be null or empty");
        }
        context.put(key, value);
        LoggerUtil.debug("Stored in GlobalContext: " + key + " = " + value);
    }

    /**
     * Retrieves a value from the global context.
     */
    public static Object get(String key) {
        if (key == null) {
            return null;
        }
        Object value = context.get(key);
        LoggerUtil.debug("Retrieved from GlobalContext: " + key + " = " + value);
        return value;
    }

    /**
     * Retrieves a value as a String.
     */
    public static String getString(String key) {
        Object value = get(key);
        return value instanceof String ? (String) value : null;
    }

    /**
     * Retrieves a value as an Integer.
     */
    public static Integer getInt(String key) {
        Object value = get(key);
        return value instanceof Integer ? (Integer) value : null;
    }

    /**
     * Retrieves a value as a Boolean.
     */
    public static Boolean getBoolean(String key) {
        Object value = get(key);
        return value instanceof Boolean ? (Boolean) value : null;
    }

    /**
     * Checks if a key exists.
     */
    public static boolean containsKey(String key) {
        return context.containsKey(key);
    }

    /**
     * Removes a key-value pair.
     */
    public static void remove(String key) {
        context.remove(key);
        LoggerUtil.debug("Removed from GlobalContext: " + key);
    }

    /**
     * Clears all data.
     */
    public static void clear() {
        context.clear();
        LoggerUtil.info("GlobalContext cleared");
    }

    /**
     * Gets the size of the context.
     */
    public static int size() {
        return context.size();
    }

    /**
     * Gets all keys in context.
     */
    public static java.util.Set<String> getAllKeys() {
        return context.keySet();
    }

    /**
     * Gets all context as Map copy.
     */
    public static Map<String, Object> getAll() {
        return new ConcurrentHashMap<>(context);
    }

    /**
     * Dumps all context data to logs.
     */
    public static void dumpContext() {
        LoggerUtil.info("======= GlobalContext Dump =======");
        context.forEach((key, value) -> LoggerUtil.info(key + " => " + value));
        LoggerUtil.info("===================================");
    }
}
