package utils;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

public final class ConfigLoader {

    private static final Properties PROPERTIES = new Properties();
    private static final Properties DEFAULT_PROPERTIES = new Properties();
    private static volatile boolean loaded = false; // visibility fix
    private static String currentEnvironment = "QA"; // Default environment
    private static final Object LOCK = new Object();

    private ConfigLoader() {
        // Utility class
    }

    /**
     * Initialize environment from system property or default to QA
     */
    public static void initializeEnvironment() {
        String envFromSystem = System.getProperty("env");
        if (envFromSystem != null && !envFromSystem.trim().isEmpty()) {
            currentEnvironment = envFromSystem.trim();
        }
        LoggerUtil.info("Environment initialized");
    }

    public static String getCurrentEnvironment() {
        return currentEnvironment;
    }

    public static void setEnvironment(String env) {
        if (env != null && !env.trim().isEmpty()) {
            currentEnvironment = env.trim();
            reload();
        }
    }

    /**
     * Load properties from environment-specific config file with fallback to default
     */
    public static void loadProperties() {
        if (loaded) {
            LoggerUtil.debug("Configuration already loaded");
            return;
        }

        synchronized (LOCK) {
            if (loaded) {
                return;
            }

            try {
                initializeEnvironment();

                loadDefaultProperties();
                loadEnvironmentSpecificProperties();

                loaded = true;
                LoggerUtil.info("Configuration loaded successfully");

            } catch (Exception e) {
                LoggerUtil.error("Configuration loading failed", e);
                throw new RuntimeException("Configuration initialization failed");
            }
        }
    }

    private static void loadDefaultProperties() {
        String defaultConfigPath = "config/config.properties";
        try {
            if (Files.exists(Paths.get(defaultConfigPath))) {
                try (InputStream inputStream = Files.newInputStream(Paths.get(defaultConfigPath))) {
                    DEFAULT_PROPERTIES.load(inputStream);
                    PROPERTIES.putAll(DEFAULT_PROPERTIES);
                    LoggerUtil.debug("Default configuration loaded");
                }
            }
        } catch (Exception e) {
            LoggerUtil.debug("Default configuration not available");
        }
    }

    private static void loadEnvironmentSpecificProperties() {
        String envConfigPath = "config/config-" + currentEnvironment + ".properties";
        try {
            if (Files.exists(Paths.get(envConfigPath))) {
                Properties envProperties = new Properties();
                try (InputStream inputStream = Files.newInputStream(Paths.get(envConfigPath))) {
                    envProperties.load(inputStream);
                    PROPERTIES.putAll(envProperties);
                }
                LoggerUtil.debug("Environment configuration loaded");
            } else {
                LoggerUtil.debug("Using default configuration");
            }
        } catch (Exception e) {
            LoggerUtil.error("Failed to load environment configuration", e);
        }
    }

    public static String get(String key) {
        if (!loaded) {
            loadProperties();
        }
        return PROPERTIES.getProperty(key);
    }

    public static String get(String key, String defaultValue) {
        if (!loaded) {
            loadProperties();
        }

        String value = PROPERTIES.getProperty(key, defaultValue);
        if (defaultValue.equals(value)) {
            LoggerUtil.debug("Default value used for configuration key");
        }
        return value;
    }

    public static int getInt(String key) {
        try {
            String value = get(key);
            return value != null ? Integer.parseInt(value) : 0;
        } catch (NumberFormatException e) {
            LoggerUtil.error("Invalid integer configuration value", e);
            return 0;
        }
    }

    public static boolean getBoolean(String key) {
        return Boolean.parseBoolean(get(key));
    }

    public static void reload() {
        synchronized (LOCK) {
            loaded = false;
            PROPERTIES.clear();
            DEFAULT_PROPERTIES.clear();
            loadProperties();
            LoggerUtil.info("Configuration reloaded");
        }
    }

    public static boolean containsKey(String key) {
        if (!loaded) {
            loadProperties();
        }
        return PROPERTIES.containsKey(key);
    }

    public static Properties getAllProperties() {
        if (!loaded) {
            loadProperties();
        }
        return new Properties(PROPERTIES);
    }
}