package stepdefinitions;

import client.APIClient;
import com.aventstack.extentreports.cucumber.adapter.ExtentCucumberAdapter;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.response.Response;
import utils.*;

import java.util.Map;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

public class VancityStepDef {

    private final APIClient apiClient = new APIClient();
    private Response response;
    private String requestPayload;
    private int rowIndex;
    private String sheetName;



//    @Given("Perform get operation for individual user having endpoint {string}")
//    public void perform_get_operation_for_individual_user_having_endpoint(String endpoint) {
//        LoggerUtil.info("GET request started ");
//        try {
//            String get_single_user_ep = ConfigLoader.get(endpoint);
//            ExtentCucumberAdapter.addTestStepLog("Calling GET endpoint: " + get_single_user_ep);
//            response = apiClient.get(get_single_user_ep);
//            ExtentCucumberAdapter.addTestStepLog("Response body captured after executing the Get API " + response.asPrettyString());
//
//            // Store response data globally (example: store user ID if present)
//            if (response.getStatusCode() == 200) {
//                String userId = response.jsonPath().getString("data.id");
//                if (userId != null) {
//                    GlobalContext.set("userId", userId);
//                }
//                // Store other values as needed, e.g., first name
//                String firstName = response.jsonPath().getString("data.first_name");
//                if (firstName != null) {
//                    GlobalContext.set("userFirstName", firstName);
//                }
//            }
//
//            // how to retrive the store values.. use below wherfe ever required.
//            // String storedId = GlobalContext.getString("userId");
//            // String storedfirstName = GlobalContext.getString("userFirstName");
//
//    /*
//    To reset the global context between test suites (if running multiple suites), add to BaseTest.java's beforeSuite or afterSuite:
//
//        GlobalContext.clear();  // Clear any leftover data from previous runs
//
//    Suppose you have a scenario that creates a user (POST), stores the ID, then uses it in a later GET or DELETE.
//    In the POST step: GlobalContext.set("newUserId", response.jsonPath().getString("id"));
//    In a later step: String id = GlobalContext.getString("newUserId"); then use it in the endpoint.
//     */
//        } catch (Exception e) {
//            LoggerUtil.error("Failed to perform GET operation for endpoint: " + endpoint, e);
//            throw new RuntimeException("GET operation failed", e);
//        }
//    }




    // Enhanced step definition example showing best practices
    @Given("Perform get operation for individual user having endpoint {string}")
    public void perform_get_operation_for_individual_user_having_endpoint(String endpoint) {
        LoggerUtil.info("Starting GET request for endpoint: " + endpoint);
        try {
            String getEndpoint = ConfigLoader.get(endpoint);
            ExtentCucumberAdapter.addTestStepLog("Calling GET endpoint: " + getEndpoint);

            response = apiClient.get(getEndpoint);
            ResponseValidator.validateSuccessStatus(response);

            // Store response for later use
            ResponseContext.storeResponse("lastResponse", response);

            // Extract and store important data
            if (response.getStatusCode() == 200) {
                ResponseContext.storeExtractedData("userId", "data.id", response);
                ResponseContext.storeExtractedData("userFirstName", "data.first_name", response);
            }

            ExtentCucumberAdapter.addTestStepLog("Response received successfully");

        } catch (Exception e) {
            LoggerUtil.error("Failed to perform GET operation", e);
            ExtentCucumberAdapter.addTestStepLog("Error: " + e.getMessage());
            throw e;
        }
    }



    @Then("I should receive status code {int}")
    public void i_should_receive_status_code(int expectedStatusCode) {
        LoggerUtil.info("Validating the response code ");
        try {
            ExtentCucumberAdapter.addTestStepLog("Validating response status code");
            assertEquals(response.getStatusCode(), expectedStatusCode);
        } catch (Exception e) {
            LoggerUtil.error("Failed to validate status code", e);
            throw e;
        }
    }

    @Then("the response should contain user first name {string}")
    public void the_response_should_contain_user_first_name(String expectedFirstName) {
        LoggerUtil.info("Validating a specific field value");
        try {
            String actualName = response.jsonPath().getString("data.first_name");
            ExtentCucumberAdapter.addTestStepLog("Actual first name: " + actualName);
            assertEquals(actualName, expectedFirstName);
        } catch (Exception e) {
            LoggerUtil.error("Failed to extract or validate user first name", e);
            throw new RuntimeException("Validation failed for user first name", e);
        }
    }

    @Then("Verify the field {string} having count {int}")
    public void verifyTheFieldTotal_pagesHavingCount(String key, int exp_count) {
        LoggerUtil.info("Validating a specific field value");
        try {
            String actual_count = response.jsonPath().getString(key);
            ExtentCucumberAdapter.addTestStepLog("Count captured from response " + actual_count);
            assertEquals(Integer.parseInt(actual_count), exp_count);
        } catch (Exception e) {
            LoggerUtil.error("Failed to verify field " + key, e);
            throw new RuntimeException("Field verification failed for " + key, e);
        }
    }

    @Then("verify the filed {string} whose value is displayed as {int}")
    public void verifyTheFiledTotalWhoseValueIsDisplayedAs(String key, int exp_count) {
        LoggerUtil.info("Validating a specific field value");
        try {
            String actual_count = response.jsonPath().getString(key);
            ExtentCucumberAdapter.addTestStepLog("Count captured from response " + actual_count);
            assertEquals(Integer.parseInt(actual_count), exp_count);
        } catch (Exception e) {
            LoggerUtil.error("Failed to verify field " + key, e);
            throw new RuntimeException("Field verification failed for " + key, e);
        }
    }

    @Given("Run the API to Perform delete operation for individual user having end point {string}")
    public void runTheAPIToPerformDeleteOperationForIndividualUserHavingEndPointDeleteUserEndpoint(String endpoint)  {
        LoggerUtil.info("running DELETE API");
        try {
            String delete_user_ep = ConfigLoader.get(endpoint);
            ExtentCucumberAdapter.addTestStepLog("Calling DELETE endpoint: " + delete_user_ep);
            response = apiClient.delete(delete_user_ep);
        } catch (Exception e) {
            LoggerUtil.error("Failed to perform DELETE operation for endpoint: " + endpoint, e);
            throw new RuntimeException("DELETE operation failed", e);
        }
    }

    @And("I send POST request to update user using endpoint {string}")
    public void iSendPOSTRequestToUpdateUserUsingEndpointUserRegisterEndpoint(String endpoint) {
        LoggerUtil.info("running POST API");
        try {
            String ep = ConfigLoader.get(endpoint);
            ExtentCucumberAdapter.addTestStepLog("Calling PUT endpoint: " + ep);
            response = apiClient.post(ep, requestPayload);
            ExtentCucumberAdapter.addTestStepLog("Response body captured after executing the Post API " + response.asPrettyString());
        } catch (Exception e) {
            LoggerUtil.error("Failed to send POST request to endpoint: " + endpoint, e);
            throw new RuntimeException("POST request failed", e);
        }
    }

    @Given("I read test data from excel row {int}")
    public void iReadTestDataFromExcelRow(int excelRow) {
        try {
            this.rowIndex = excelRow;
            ExtentCucumberAdapter.addTestStepLog("Reading dynamic data from Excel row: " + excelRow);
        } catch (Exception e) {
            LoggerUtil.error("Failed to read test data from Excel row: " + excelRow, e);
            throw new RuntimeException("Excel data read failed", e);
        }
    }

    @When("I build request body from {string}")
    public void iBuildRequestBodyFrom(String payloadFileName) {
        LoggerUtil.info("here we are creating the request body from excel data and json file");
        try {
            String excelPath = ConfigLoader.get("excelPath");
            String sheetName = ConfigLoader.get("defaultSheet");
            Map<String, String> data = ExcelUtil.getRowDataAsMap(excelPath, sheetName, rowIndex);
            String template = JsonUtil.readJsonFile("src/test/resources/payloads/" + payloadFileName);
            requestPayload = DynamicDataEngine.applyDynamicValues(template, data);
            ExtentCucumberAdapter.addTestStepLog("Generated payload: " + requestPayload);
            assertNotNull(requestPayload);
        } catch (Exception e) {
            LoggerUtil.error("Failed to build request body from " + payloadFileName, e);
            throw new RuntimeException("Request body build failed", e);
        }
    }

    @Then("I should be able to see {string}")
    public void i_should_be_able_to_see(String key) {
        LoggerUtil.info("validating the field value");
        try {
            String actual_value = response.jsonPath().getString(key);
            ExtentCucumberAdapter.addTestStepLog("Expected value captured from response is : " + actual_value);
        } catch (Exception e) {
            LoggerUtil.error("Failed to extract field " + key + " from response", e);
            throw new RuntimeException("Field extraction failed for " + key, e);
        }
    }


}
