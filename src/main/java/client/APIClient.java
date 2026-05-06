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
            RestAssured.baseURI = ConfigLoader.get("baseURL");

            Response response = RestAssured
                    .given()
                    .header("x-api-key", TokenManager.getApiKey())
                    .when()
                    .get(endpoint)
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
}
