package utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import exceptions.DataException;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

public final class JsonUtil {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    private JsonUtil() {
    }

    /**
     * Read JSON file as string
     */
    public static String readJsonFile(String filePath) {
        try {
            LoggerUtil.info("Reading JSON file: " + filePath);
            if (!Files.exists(Paths.get(filePath))) {
                throw new DataException(filePath, "JSON file not found: " + filePath);
            }
            return new String(Files.readAllBytes(Paths.get(filePath)), StandardCharsets.UTF_8);
        } catch (DataException e) {
            throw e;
        } catch (Exception e) {
            LoggerUtil.error("Failed to read JSON file: " + filePath, e);
            throw new DataException(filePath, "Unable to read JSON file: " + filePath, e);
        }
    }

    /**
     * Parse JSON string to JsonNode
     */
    public static JsonNode parseJson(String jsonString) {
        try {
            return objectMapper.readTree(jsonString);
        } catch (Exception e) {
            LoggerUtil.error("Failed to parse JSON", e);
            throw new DataException("Invalid JSON format", e);
        }
    }

    /**
     * Pretty print JSON
     */
    public static String prettyPrintJson(String jsonString) {
        try {
            JsonNode jsonNode = parseJson(jsonString);
            return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(jsonNode);
        } catch (Exception e) {
            LoggerUtil.error("Failed to pretty print JSON", e);
            return jsonString;
        }
    }

    /**
     * Extract value from JSON using path
     */
    public static String extractValue(String jsonString, String jsonPath) {
        try {
            JsonNode node = parseJson(jsonString);
            String[] pathElements = jsonPath.split("\\.");

            for (String element : pathElements) {
                node = node.get(element);
                if (node == null) {
                   // LoggerUtil.warn("Path not found in JSON: " + jsonPath, e);
                    return null;
                }
            }

            return node.asText();
        } catch (Exception e) {
            LoggerUtil.error("Failed to extract value from JSON path: " + jsonPath, e);
            throw new DataException(jsonPath, "Failed to extract JSON value", e);
        }
    }

    /**
     * Validate JSON structure against expected keys
     */
    public static boolean validateJsonStructure(String jsonString, String[] requiredKeys) {
        try {
            JsonNode node = parseJson(jsonString);
            for (String key : requiredKeys) {
                if (!node.has(key)) {
                 //   LoggerUtil.warn("Required JSON key missing: " + key, e);
                    return false;
                }
            }
            return true;
        } catch (Exception e) {
            LoggerUtil.error("Failed to validate JSON structure", e);
            return false;
        }
    }

    /**
     * Convert object to JSON string
     */
    public static String toJson(Object object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (Exception e) {
            LoggerUtil.error("Failed to convert object to JSON", e);
            throw new DataException("Failed to serialize object to JSON", e);
        }
    }
}
