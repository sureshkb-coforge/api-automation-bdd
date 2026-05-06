// File: src/main/java/exceptions/APIException.java
package exceptions;

/**
 * Exception for API client errors.
 */
public class APIException extends APIAutomationException {

    private int statusCode;
    private String responseBody;

    public APIException(String message) {
        super("API_ERROR", message);
    }

    public APIException(String message, Throwable cause) {
        super("API_ERROR", message, cause);
    }

    public APIException(int statusCode, String responseBody) {
        super("API_ERROR", "API request failed with status: " + statusCode);
        this.statusCode = statusCode;
        this.responseBody = responseBody;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getResponseBody() {
        return responseBody;
    }
}
