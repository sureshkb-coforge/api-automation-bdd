// File: src/main/java/utils/RequestResponseLogger.java
package utils;

import io.restassured.response.Response;

/**
 * Utility for comprehensive request/response logging.
 */
public final class RequestResponseLogger {

    private RequestResponseLogger() {
    }

    /**
     * Log complete request details.
     */
    public static void logRequest(String method, String endpoint, String headers, String body) {
        LoggerUtil.info("====== REQUEST ======");
        LoggerUtil.info("Method: " + method);
        LoggerUtil.info("Endpoint: " + endpoint);
        LoggerUtil.info("Headers: " + headers);
        LoggerUtil.info("Body: " + body);
        LoggerUtil.info("=====================");
    }

    /**
     * Log complete response details.
     */
    public static void logResponse(Response response) {
        try {
            LoggerUtil.info("====== RESPONSE ======");
            LoggerUtil.info("Status Code: " + response.getStatusCode());
            LoggerUtil.info("Status Line: " + response.getStatusLine());
            LoggerUtil.info("Headers: " + response.getHeaders());
            LoggerUtil.info("Response Time: " + response.getTime() + "ms");
            LoggerUtil.info("Body: " + response.asPrettyString());
            LoggerUtil.info("======================");
        } catch (Exception e) {
            LoggerUtil.error("Failed to log response", e);
        }
    }

    /**
     * Log response summary only.
     */
    public static void logResponseSummary(Response response) {
        LoggerUtil.info("Response Status: " + response.getStatusCode() +
                " | Response Time: " + response.getTime() + "ms");
    }
}
