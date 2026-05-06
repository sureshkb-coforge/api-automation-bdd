// File: src/main/java/exceptions/ConfigException.java
package exceptions;

/**
 * Exception for configuration loading errors.
 */
public class ConfigException extends APIAutomationException {

    private String configKey;

    public ConfigException(String message) {
        super("CONFIG_ERROR", message);
    }

    public ConfigException(String configKey, String message) {
        super("CONFIG_ERROR", message);
        this.configKey = configKey;
    }

    public String getConfigKey() {
        return configKey;
    }
}
