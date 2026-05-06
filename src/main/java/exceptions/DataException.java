package exceptions;

/**
 * Exception for data parsing/extraction errors.
 */
public class DataException extends APIAutomationException {

    private String dataPath;

    public DataException(String message) {
        super("DATA_ERROR", message);
    }

    public DataException(String dataPath, String message) {
        super("DATA_ERROR", message);
        this.dataPath = dataPath;
    }

    /**
     * Constructor with cause for chaining exceptions
     */
    public DataException(String message, Throwable cause) {
        super("DATA_ERROR", message, cause);
    }

    /**
     * Constructor with dataPath and cause
     */
    public DataException(String dataPath, String message, Throwable cause) {
        super("DATA_ERROR", message, cause);
        this.dataPath = dataPath;
    }

    public String getDataPath() {
        return dataPath;
    }
}
