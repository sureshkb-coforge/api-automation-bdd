// File: src/main/java/utils/ResponseValidator.java
package utils;

import io.restassured.response.Response;
import static org.testng.Assert.*;

/**
 * Utility for validating API responses.
 */
public final class ResponseValidator {

    private ResponseValidator() {}

    /**
     * Validate response status code
     */
    public static void validateStatusCode(Response response, int expectedCode) {
        LoggerUtil.info("Validating status code. Expected: " + expectedCode + ", Actual: " + response.getStatusCode());
        assertEquals(response.getStatusCode(), expectedCode,
                "Status code mismatch. Expected: " + expectedCode + ", Got: " + response.getStatusCode());
    }

    /**
     * Validate status code is 2xx
     */
    public static void validateSuccessStatus(Response response) {
        int statusCode = response.getStatusCode();
        assertTrue(statusCode >= 200 && statusCode < 300,
                "Expected success status code (2xx), but got: " + statusCode);
        LoggerUtil.info("Response status is successful: " + statusCode);
    }

    /**
     * Validate JSON path value
     */
    public static void validateJsonPathValue(Response response, String jsonPath, Object expectedValue) {
        Object actualValue = response.jsonPath().get(jsonPath);
        assertEquals(actualValue, expectedValue,
                "JSON path value mismatch for '" + jsonPath + "'. Expected: " + expectedValue + ", Got: " + actualValue);
        LoggerUtil.info("JSON path validation passed for: " + jsonPath);
    }

    /**
     * Validate JSON path exists
     */
    public static void validateJsonPathExists(Response response, String jsonPath) {
        Object value = response.jsonPath().get(jsonPath);
        assertNotNull(value, "JSON path '" + jsonPath + "' not found in response");
        LoggerUtil.info("JSON path exists: " + jsonPath);
    }

    /**
     * Validate response contains field
     */
    public static void validateFieldExists(Response response, String fieldName) {
        String responseBody = response.asString();
        assertTrue(responseBody.contains(fieldName),
                "Response does not contain field: " + fieldName);
        LoggerUtil.info("Field found in response: " + fieldName);
    }

    /**
     * Validate response time
     */
    public static void validateResponseTime(Response response, long maxTimeMs) {
        long responseTime = response.getTime();
        assertTrue(responseTime <= maxTimeMs,
                "Response time exceeded threshold. Expected <= " + maxTimeMs + "ms, Got: " + responseTime + "ms");
        LoggerUtil.info("Response time validation passed: " + responseTime + "ms");
    }

    /**
     * Validate response contains specific text
     */
    public static void validateResponseContains(Response response, String expectedText) {
        String responseBody = response.asString();
        assertTrue(responseBody.contains(expectedText),
                "Response does not contain expected text: " + expectedText);
        LoggerUtil.info("Response contains expected text");
    }

    /**
     * Validate response is not empty
     */
    public static void validateResponseNotEmpty(Response response) {
        String responseBody = response.asString();
        assertFalse(responseBody.isEmpty(), "Response body is empty");
        LoggerUtil.info("Response is not empty");
    }
}
