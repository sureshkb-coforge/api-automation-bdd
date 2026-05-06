// File: src/main/java/utils/ResponseContext.java
package utils;

import io.restassured.response.Response;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Manages API responses and extracted data for use across scenarios.
 */
public final class ResponseContext {

    private static final ConcurrentHashMap<String, Response> responses = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<String, Object> extractedData = new ConcurrentHashMap<>();

    private ResponseContext() {}

    /**
     * Store response with a key
     */
    public static void storeResponse(String key, Response response) {
        responses.put(key, response);
        LoggerUtil.info("Response stored with key: " + key);
    }

    /**
     * Retrieve response by key
     */
    public static Response getResponse(String key) {
        return responses.get(key);
    }

    /**
     * Store extracted data with a key
     */
    public static void storeExtractedData(String key, String jsonPath, Response response) {
        try {
            String value = response.jsonPath().getString(jsonPath);
            extractedData.put(key, value);
            GlobalContext.set(key, value); // Also store in global context
            LoggerUtil.info("Extracted and stored data: " + key + " = " + value);
        } catch (Exception e) {
            LoggerUtil.error("Failed to extract data from response", e);
            throw new RuntimeException("Data extraction failed", e);
        }
    }

    /**
     * Get extracted data
     */
    public static Object getExtractedData(String key) {
        return extractedData.get(key);
    }

    /**
     * Get extracted data as String
     */
    public static String getExtractedDataAsString(String key) {
        Object value = extractedData.get(key);
        return value != null ? value.toString() : null;
    }

    /**
     * Clear all stored responses
     */
    public static void clearResponses() {
        responses.clear();
        LoggerUtil.info("All responses cleared");
    }

    /**
     * Clear all extracted data
     */
    public static void clearExtractedData() {
        extractedData.clear();
        LoggerUtil.info("All extracted data cleared");
    }

    /**
     * Clear all context
     */
    public static void clearAll() {
        clearResponses();
        clearExtractedData();
        LoggerUtil.info("All response context cleared");
    }

    /**
     * Dump all stored data
     */
    public static void dumpContext() {
        LoggerUtil.info("===== Response Context Dump =====");
        LoggerUtil.info("Stored Responses: " + responses.keySet());
        LoggerUtil.info("Extracted Data: " + extractedData);
        LoggerUtil.info("==================================");
    }
}
