// Enhanced: src/main/java/client/APIClient.java
package client;

import base.TestConfig;
import exceptions.APIException;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import utils.ConfigLoader;
import utils.LoggerUtil;
import utils.RequestResponseLogger;
import utils.RetryUtil;
import utils.DataDrivenContext;
import java.util.Map;

public class APIClient {

    private final RequestSpecification requestSpecification;
    private static final int MAX_RETRIES = 3;
    private static final long INITIAL_DELAY_MS = 1000;

    public APIClient() {
        try {
            this.requestSpecification = TestConfig.getDefaultRequestSpec();
            LoggerUtil.info("API Client initialized successfully");
        } catch (Exception e) {
            LoggerUtil.error("Failed to initialize API client", e);
            throw new APIException("Failed to initialize API client", e);
        }
    }

    /**
     * Send GET request with retry mechanism
     */
    public Response get(String endpoint) {
        return get(endpoint, MAX_RETRIES);
    }

    /**
     * Send GET request with custom retry count
     */
    public Response get(String endpoint, int maxRetries) {
        try {
            return RetryUtil.executeWithRetry(
                    "GET " + endpoint,
                    () -> executeGet(endpoint),
                    maxRetries,
                    INITIAL_DELAY_MS
            );
        } catch (Exception e) {
            LoggerUtil.error("GET request failed for endpoint: " + endpoint, e);
            throw new APIException("GET request failed for endpoint: " + endpoint, e);
        }
    }

    private Response executeGet(String endpoint) {
        try {
            LoggerUtil.info("Executing GET request to: " + endpoint);

            // NEW: Resolve placeholders in endpoint using Excel data from DataDrivenContext
            String resolvedEndpoint = resolveEndpointPlaceholders(endpoint);
            LoggerUtil.info("Resolved endpoint with Excel data: " + resolvedEndpoint);

            RestAssured.baseURI = ConfigLoader.get("baseURL");

            Response response = RestAssured
                    .given()
                    .header("x-api-key", TokenManager.getApiKey())
                    .when()
                    .get(resolvedEndpoint)  // Use resolved endpoint
                    .then()
                    .extract()
                    .response();

            RequestResponseLogger.logResponse(response);

            if (response.getStatusCode() >= 400) {
                throw new APIException(response.getStatusCode(), response.asString());
            }

            return response;
        } catch (APIException e) {
            throw e;
        } catch (Exception e) {
            LoggerUtil.error("Error executing GET request", e);
            throw new APIException("GET request execution failed", e);
        }
    }

    /**
     * Send POST request with retry mechanism
     */
    public Response post(String endpoint, Object payload) {
        return post(endpoint, payload, MAX_RETRIES);
    }

    /**
     * Send POST request with custom retry count
     */
    public Response post(String endpoint, Object payload, int maxRetries) {
        try {
            return RetryUtil.executeWithRetry(
                    "POST " + endpoint,
                    () -> executePost(endpoint, payload),
                    maxRetries,
                    INITIAL_DELAY_MS
            );
        } catch (Exception e) {
            LoggerUtil.error("POST request failed for endpoint: " + endpoint, e);
            throw new APIException("POST request failed for endpoint: " + endpoint, e);
        }
    }

    private Response executePost(String endpoint, Object payload) {
        try {
            LoggerUtil.info("Executing POST request to: " + endpoint);
            LoggerUtil.info("Payload: " + payload);
            RestAssured.baseURI = ConfigLoader.get("baseURL");

            Response response = RestAssured
                    .given()
                    .header("x-api-key", TokenManager.getApiKey())
                    .header("Content-Type", "application/json")
                    .body(payload)
                    .when()
                    .post(endpoint)
                    .then()
                    .extract()
                    .response();

            RequestResponseLogger.logResponse(response);

            if (response.getStatusCode() >= 400) {
                throw new APIException(response.getStatusCode(), response.asString());
            }

            return response;
        } catch (APIException e) {
            throw e;
        } catch (Exception e) {
            LoggerUtil.error("Error executing POST request", e);
            throw new APIException("POST request execution failed", e);
        }
    }

    /**
     * Send DELETE request
     */
    public Response delete(String endpoint) {
        try {
            LoggerUtil.info("Executing DELETE request to: " + endpoint);
            RestAssured.baseURI = ConfigLoader.get("baseURL");

            Response response = RestAssured
                    .given()
                    .header("x-api-key", TokenManager.getApiKey())
                    .when()
                    .delete(endpoint)
                    .then()
                    .extract()
                    .response();

            RequestResponseLogger.logResponse(response);

            if (response.getStatusCode() >= 400) {
                throw new APIException(response.getStatusCode(), response.asString());
            }

            return response;
        } catch (APIException e) {
            throw e;
        } catch (Exception e) {
            LoggerUtil.error("Error executing DELETE request", e);
            throw new APIException("DELETE request failed for endpoint: " + endpoint, e);
        }
    }

    /**
     * Send PUT request
     */
    public Response put(String endpoint, Object payload) {
        try {
            LoggerUtil.info("Executing PUT request to: " + endpoint);
            RestAssured.baseURI = ConfigLoader.get("baseURL");

            Response response = RestAssured
                    .given()
                    .header("x-api-key", TokenManager.getApiKey())
                    .header("Content-Type", "application/json")
                    .body(payload)
                    .when()
                    .put(endpoint)
                    .then()
                    .extract()
                    .response();

            RequestResponseLogger.logResponse(response);

            if (response.getStatusCode() >= 400) {
                throw new APIException(response.getStatusCode(), response.asString());
            }

            return response;
        } catch (APIException e) {
            throw e;
        } catch (Exception e) {
            LoggerUtil.error("Error executing PUT request", e);
            throw new APIException("PUT request failed for endpoint: " + endpoint, e);
        }
    }

    /**
     * Send PATCH request
     */
    public Response patch(String endpoint, Object payload) {
        try {
            LoggerUtil.info("Executing PATCH request to: " + endpoint);
            RestAssured.baseURI = ConfigLoader.get("baseURL");

            Response response = RestAssured
                    .given()
                        .header("x-api-key", TokenManager.getApiKey())
                        .header("Content-Type", "application/json")
                        .body(payload)
                    .when()
                        .patch(endpoint)
                    .then()
                        .extract()
                        .response();

            RequestResponseLogger.logResponse(response);

            if (response.getStatusCode() >= 400) {
                throw new APIException(response.getStatusCode(), response.asString());
            }

            return response;
        } catch (APIException e) {
            throw e;
        } catch (Exception e) {
            LoggerUtil.error("Error executing PATCH request", e);
            throw new APIException("PATCH request failed for endpoint: " + endpoint, e);
        }
    }

    /**
     * Resolves placeholders in the endpoint string using values from DataDrivenContext (Excel data).
     * Placeholders should be in the format {{columnName}}, e.g., {{userId}}.
     * @param endpoint The original endpoint string
     * @return Endpoint with placeholders replaced by Excel values
     */
    private String resolveEndpointPlaceholders(String endpoint) {
        String resolved = endpoint;
        try {
            // Check if DataDrivenContext has data (i.e., we're in a data-driven scenario)
            if (DataDrivenContext.getCurrentRowIndex() > 0) {  // Ensure data is loaded
                Map<String, String> currentRowData = DataDrivenContext.getAllCurrentRowData();
                if (currentRowData != null && !currentRowData.isEmpty()) {
                    for (Map.Entry<String, String> entry : currentRowData.entrySet()) {
                        String placeholder = "{{" + entry.getKey() + "}}";
                        String value = entry.getValue();
                        if (value != null && resolved.contains(placeholder)) {
                            resolved = resolved.replace(placeholder, value);
                            LoggerUtil.debug("Replaced placeholder " + placeholder + " with value: " + value);
                        }
                    }
                } else {
                 //   LoggerUtil.warn("No Excel row data available for endpoint resolution");
                }
            } else {
                LoggerUtil.debug("Not in a data-driven context; using endpoint as-is");
            }
        } catch (Exception e) {
           // LoggerUtil.warn("Failed to resolve endpoint placeholders: " + e.getMessage());
            // Continue with original endpoint if resolution fails
        }
        return resolved;
    }

}
