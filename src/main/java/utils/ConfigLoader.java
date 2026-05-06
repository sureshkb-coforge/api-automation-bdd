package utils;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

public final class ConfigLoader {

    private static final Properties PROPERTIES = new Properties();
    private static final Properties DEFAULT_PROPERTIES = new Properties();
    private static boolean loaded = false;
    private static String currentEnvironment = "QA"; // Default environment
    private static final Object LOCK = new Object();

    private ConfigLoader() {
    }

    /**
     * Initialize environment from system property or default to QA
     */
    public static void initializeEnvironment() {
        String envFromSystem = System.getProperty("env");
        if (envFromSystem != null && !envFromSystem.trim().isEmpty()) {
            currentEnvironment = envFromSystem.trim();
        }
        LoggerUtil.info("Environment set to: " + currentEnvironment);
    }

    /**
     * Get current environment
     */
    public static String getCurrentEnvironment() {
        return currentEnvironment;
    }

    /**
     * Set environment explicitly (useful for tests)
     */
    public static void setEnvironment(String env) {
        if (env != null && !env.trim().isEmpty()) {
            currentEnvironment = env.trim();
            // Reload properties for new environment
            reload();
        }
    }

    /**
     * Load properties from environment-specific config file with fallback to default
     */
    public static synchronized void loadProperties() {
        if (loaded) {
            LoggerUtil.debug("Properties already loaded for environment: " + currentEnvironment);
            return;
        }

        synchronized (LOCK) {
            if (loaded) {
                return;
            }

            try {
                // Initialize environment from system property
                initializeEnvironment();

                // Load default properties first
                loadDefaultProperties();

                // Load environment-specific properties
                loadEnvironmentSpecificProperties();

                loaded = true;
                LoggerUtil.info("Configuration loaded successfully for environment: " + currentEnvironment);
                LoggerUtil.info("Total properties: " + PROPERTIES.size());
                LoggerUtil.debug("Loaded properties keys: " + PROPERTIES.keySet());

            } catch (Exception e) {
                LoggerUtil.error("Failed to load configuration properties", e);
                throw new RuntimeException("Unable to load configuration properties", e);
            }
        }
    }

    /**
     * Load default properties as fallback
     */
    private static void loadDefaultProperties() {
        String defaultConfigPath = "config/config.properties";
        try {
            if (Files.exists(Paths.get(defaultConfigPath))) {
                try (InputStream inputStream = Files.newInputStream(Paths.get(defaultConfigPath))) {
                    DEFAULT_PROPERTIES.load(inputStream);
                    PROPERTIES.putAll(DEFAULT_PROPERTIES);
                    LoggerUtil.info("Default configuration loaded from: " + defaultConfigPath);
                }
            }
        } catch (Exception e) {
           // LoggerUtil.warn("Default configuration file not found: " + defaultConfigPath, e);
        }
    }

    /**
     * Load environment-specific properties
     */
    private static void loadEnvironmentSpecificProperties() {
        String envConfigPath = "config/config-" + currentEnvironment + ".properties";
        try {
            if (Files.exists(Paths.get(envConfigPath))) {
                Properties envProperties = new Properties();
                try (InputStream inputStream = Files.newInputStream(Paths.get(envConfigPath))) {
                    envProperties.load(inputStream);
                    // Override default properties with environment-specific ones
                    PROPERTIES.putAll(envProperties);
                    LoggerUtil.info("Environment-specific configuration loaded from: " + envConfigPath);
                }
            } else {
               // LoggerUtil.warn("Environment-specific configuration not found: " + envConfigPath, e);
                LoggerUtil.info("Using default configuration only");
            }
        } catch (Exception e) {
            LoggerUtil.error("Failed to load environment-specific configuration: " + envConfigPath, e);
            LoggerUtil.info("Continuing with default configuration");
        }
    }

    /**
     * Get property value
     */
    public static String get(String key) {
        if (!loaded) {
            loadProperties();
        }

        String value = PROPERTIES.getProperty(key);
//        if (value == null) {
//            LoggerUtil.warn("Configuration key not found: " + key, e);
//        }

        return value;
    }

    /**
     * Get property with default value
     */
    public static String get(String key, String defaultValue) {
        if (!loaded) {
            loadProperties();
        }

        String value = PROPERTIES.getProperty(key, defaultValue);
        if (value.equals(defaultValue)) {
            LoggerUtil.debug("Using default value for key: " + key + " = " + defaultValue);
        }

        return value;
    }

    /**
     * Get integer property
     */
    public static int getInt(String key) {
        try {
            String value = get(key);
            if (value != null) {
                return Integer.parseInt(value);
            }
          //  LoggerUtil.warn("Config key not found for int conversion: " + key, e);
            return 0;
        } catch (NumberFormatException e) {
            LoggerUtil.error("Invalid integer value for key: " + key, e);
            return 0;
        }
    }

    /**
     * Get boolean property
     */
    public static boolean getBoolean(String key) {
        return Boolean.parseBoolean(get(key));
    }

    /**
     * Reload properties (useful for environment switching)
     */
    public static synchronized void reload() {
        loaded = false;
        PROPERTIES.clear();
        DEFAULT_PROPERTIES.clear();
        loadProperties();
        LoggerUtil.info("Configuration reloaded for environment: " + currentEnvironment);
    }

    /**
     * Check if key exists
     */
    public static boolean containsKey(String key) {
        if (!loaded) {
            loadProperties();
        }
        return PROPERTIES.containsKey(key);
    }

    /**
     * Get all properties
     */
    public static Properties getAllProperties() {
        if (!loaded) {
            loadProperties();
        }
        return new Properties(PROPERTIES);
    }
}
