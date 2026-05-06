// File: src/main/java/exceptions/APIAutomationException.java
package exceptions;

/**
 * Base exception for API automation framework.
 */
public class APIAutomationException extends RuntimeException {

    private String errorCode;
    private Object context;

    public APIAutomationException(String message) {
        super(message);
        this.errorCode = "UNKNOWN";
    }

    public APIAutomationException(String message, Throwable cause) {
        super(message, cause);
        this.errorCode = "UNKNOWN";
    }

    public APIAutomationException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public APIAutomationException(String errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public Object getContext() {
        return context;
    }

    public void setContext(Object context) {
        this.context = context;
    }
}
