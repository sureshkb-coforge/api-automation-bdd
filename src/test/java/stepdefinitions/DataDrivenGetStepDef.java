package stepdefinitions;

import client.APIClient;
import com.aventstack.extentreports.cucumber.adapter.ExtentCucumberAdapter;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.response.Response;
import utils.*;

import java.util.HashMap;
import java.util.Map;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

/**
 * Step definitions for data-driven GET API testing.
 * Scenarios execute for each row in Excel.
 * Excel file name and sheet name should match scenario name.
 */
public class DataDrivenGetStepDef {

    private final APIClient apiClient = new APIClient();
    private Response response;
    private Map<String, String> currentTestData;

    /**
     * Load test data from Excel for current scenario
     * Excel sheet name should match scenario name
     * Excel columns: Status_Code, Response_Field_[FieldName], etc.
     */
    @Given("I load test data for scenario {string}")
    public void loadTestDataForScenario(String scenarioName) {
        LoggerUtil.info("Loading test data for scenario: " + scenarioName);
        try {
            boolean dataLoaded = DataDrivenScenarioManager.loadTestDataForScenario(scenarioName);

            ExtentCucumberAdapter.addTestStepLog("Scenario: " + scenarioName + " | Data loaded: " + dataLoaded);

            assertTrue(dataLoaded, "Failed to load test data for scenario: " + scenarioName);

        } catch (Exception e) {
            LoggerUtil.error("Failed to load test data", e);
            throw e;
        }
    }

    /**
     * Execute GET API for each row in Excel
     * This step is called for each data row
     */
    @When("I execute GET request for each test data row using endpoint {string}")
    public void executeGETForEachRow(String endpointKey) {
        LoggerUtil.info("Executing GET request for each test data row");
        try {
            // Check if there are more rows to process
            if (!DataDrivenScenarioManager.hasMoreRows()) {
              //  LoggerUtil.warn("No more test data rows to process");
                ExtentCucumberAdapter.addTestStepLog("No more test data rows");
                return;
            }

            // Move to next row
            boolean moved = DataDrivenScenarioManager.moveToNextTestRow();

            if (!moved) {
            //    LoggerUtil.warn("Failed to move to next test row");
                return;
            }

            // Get current test data
            currentTestData = DataDrivenScenarioManager.getCurrentTestData();
            LoggerUtil.info("Current test data: " + currentTestData);

            // Get endpoint from config
            String endpoint = ConfigLoader.get(endpointKey);

            // If endpoint has placeholders from Excel, replace them
            String resolvedEndpoint = replacePlaceholders(endpoint, currentTestData);

            LoggerUtil.info("Calling GET endpoint: " + resolvedEndpoint);
            ExtentCucumberAdapter.addTestStepLog("Executing GET: " + resolvedEndpoint);

            // Execute GET request
            response = apiClient.get(resolvedEndpoint);

            // Store response for later validation
            ResponseContext.storeResponse("lastResponse", response);

            LoggerUtil.info("GET request completed. Status: " + response.getStatusCode());
            ExtentCucumberAdapter.addTestStepLog("Response Status: " + response.getStatusCode());

        } catch (Exception e) {
            LoggerUtil.error("Failed to execute GET request", e);
            ExtentCucumberAdapter.addTestStepLog("Error: " + e.getMessage());
            throw e;
        }
    }

    /**
     * Validate status code from Excel
     * Excel should have column "Status_Code" with expected status code
     */
    @Then("I verify the status code matches Excel expected value")
    public void verifyStatusCodeFromExcel() {
        LoggerUtil.info("Verifying status code matches Excel");
        try {
            boolean matches = ExcelResponseValidator.validateStatusCodeFromExcel(response, "Status_Code");

            assertTrue(matches, "Status code validation failed for current row");

        } catch (Exception e) {
            LoggerUtil.error("Status code validation failed", e);
            throw e;
        }
    }

    /**
     * Validate single response field from Excel
     * Excel should have column with header like "Response_Field_FieldName"
     */
    @Then("I verify the response field {string} matches Excel column {string}")
    public void verifyResponseFieldFromExcel(String jsonPath, String excelColumnName) {
        LoggerUtil.info("Verifying response field: " + jsonPath + " against Excel column: " + excelColumnName);
        try {
            boolean matches = ExcelResponseValidator.validateResponseFieldFromExcel(response, jsonPath, excelColumnName);

            assertTrue(matches, "Response field validation failed | JsonPath: " + jsonPath + " | Excel Column: " + excelColumnName);

        } catch (Exception e) {
            LoggerUtil.error("Response field validation failed", e);
            throw e;
        }
    }

    /**
     * Validate response contains substring from Excel
     */
    @Then("I verify the response field {string} contains Excel column value {string}")
    public void verifyResponseFieldContainsFromExcel(String jsonPath, String excelColumnName) {
        LoggerUtil.info("Verifying response field contains value from Excel");
        try {
            boolean matches = ExcelResponseValidator.validateResponseFieldContainsFromExcel(response, jsonPath, excelColumnName);

            assertTrue(matches, "Response field contains validation failed | JsonPath: " + jsonPath);

        } catch (Exception e) {
            LoggerUtil.error("Response field contains validation failed", e);
            throw e;
        }
    }

    /**
     * Validate multiple response fields from Excel
     * Excel columns should be named like: Field_1_JsonPath, Field_1_ExcelColumn, Field_2_JsonPath, Field_2_ExcelColumn, etc.
     */
    @Then("I verify all response fields match Excel expected values")
    public void verifyMultipleFieldsFromExcel() {
        LoggerUtil.info("Verifying multiple response fields match Excel");
        try {
            // Build field mappings from current test data
            Map<String, String> fieldMappings = new HashMap<>();

            for (Map.Entry<String, String> entry : currentTestData.entrySet()) {
                String columnName = entry.getKey();

                // Look for columns like "Response_Field_[fieldName]"
                if (columnName.startsWith("Response_Field_")) {
                    String fieldName = columnName.replace("Response_Field_", "");
                    String jsonPath = entry.getValue();

                    fieldMappings.put(jsonPath, columnName);
                }
            }

            if (fieldMappings.isEmpty()) {
            //    LoggerUtil.warn("No response fields found for validation");
                ExtentCucumberAdapter.addTestStepLog("Warning: No response fields configured");
                return;
            }

            Map<String, Boolean> results = ExcelResponseValidator.validateMultipleFieldsFromExcel(response, fieldMappings);

            // Check if all validations passed
            boolean allPassed = results.values().stream().allMatch(v -> v);
            assertTrue(allPassed, "Some response field validations failed");

        } catch (Exception e) {
            LoggerUtil.error("Multiple field validation failed", e);
            throw e;
        }
    }

    /**
     * Validate response field is within range from Excel
     * Excel columns: min_value, max_value
     */
    @Then("I verify response field {string} is within Excel range columns {string} and {string}")
    public void verifyResponseFieldRangeFromExcel(String jsonPath, String minColumnName, String maxColumnName) {
        LoggerUtil.info("Verifying response field within range from Excel");
        try {
            boolean matches = ExcelResponseValidator.validateResponseFieldRangeFromExcel(response, jsonPath, minColumnName, maxColumnName);

            assertTrue(matches, "Response field range validation failed | JsonPath: " + jsonPath);

        } catch (Exception e) {
            LoggerUtil.error("Response field range validation failed", e);
            throw e;
        }
    }

    /**
     * Repeat scenario for each test data row
     */
    @And("I repeat the scenario for the next test data row")
    public void repeatForNextRow() {
        LoggerUtil.info("Checking for next test data row");
        try {
            if (DataDrivenScenarioManager.hasMoreRows()) {
                LoggerUtil.info("More rows available, scenario will be repeated");
                ExtentCucumberAdapter.addTestStepLog("Next row available - Scenario will repeat");
            } else {
                LoggerUtil.info("No more rows - Scenario execution complete");
                ExtentCucumberAdapter.addTestStepLog("All rows processed - Scenario complete");
            }
        } catch (Exception e) {
            LoggerUtil.error("Error checking for next row", e);
            throw e;
        }
    }

    /**
     * Get current test data value
     */
    @Then("I can access test data value {string}")
    public void accessTestDataValue(String columnName) {
        LoggerUtil.info("Accessing test data value for column: " + columnName);
        try {
            String value = DataDrivenScenarioManager.getTestDataValue(columnName);

            LoggerUtil.info("Test data value for '" + columnName + "': " + value);
            ExtentCucumberAdapter.addTestStepLog("Test Data[" + columnName + "]: " + value);

        } catch (Exception e) {
            LoggerUtil.error("Failed to access test data value", e);
            throw e;
        }
    }

    /**
     * Helper method to replace placeholders in endpoint
     */
    private String replacePlaceholders(String template, Map<String, String> data) {
        String result = template;
        for (Map.Entry<String, String> entry : data.entrySet()) {
            result = result.replace("{{" + entry.getKey() + "}}", entry.getValue());
        }
        return result;
    }
}