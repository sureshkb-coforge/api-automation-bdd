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

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

/**
 * Step definitions for API testing with database validation.
 * Demonstrates API to Database comparison and verification.
 */
public class VancityDatabaseStepDef {

    private final APIClient apiClient = new APIClient();
    private Response response;
    private String currentDBQuery;
    private Map<String, String> fieldMappings;

    /**
     * Initialize database connection
     */
    @Given("I initialize the database connection")
    public void initializeDatabaseConnection() {
        LoggerUtil.info("Initializing database connection");
        try {
            ExtentCucumberAdapter.addTestStepLog("Initializing database connection");
            DatabaseConnectionManager.getInstance().initializeFromConfig();
            DatabaseConnectionManager.getInstance().printPoolStats();
        } catch (Exception e) {
            LoggerUtil.error("Failed to initialize database connection", e);
            throw e;
        }
    }

    /**
     * Execute API call and verify response status
     */
    @When("I call the API endpoint {string}")
    public void callAPIEndpoint(String endpoint) {
        LoggerUtil.info("Calling API endpoint: " + endpoint);
        try {
            String getEndpoint = ConfigLoader.get(endpoint);
            ExtentCucumberAdapter.addTestStepLog("Calling GET endpoint: " + getEndpoint);

            response = apiClient.get(getEndpoint);
            ResponseValidator.validateSuccessStatus(response);

            ExtentCucumberAdapter.addTestStepLog("API Response: " + response.asPrettyString());
        } catch (Exception e) {
            LoggerUtil.error("Failed to call API endpoint", e);
            throw e;
        }
    }

    /**
     * Compare single API field with database value
     */
    @Then("I verify that API response field {string} matches database query result column {string}")
    public void verifyAPIFieldMatchesDBColumn(String apiJsonPath, String dbColumnName) {
        LoggerUtil.info("Verifying API field matches DB column");
        try {
            if (currentDBQuery == null || currentDBQuery.isEmpty()) {
                throw new RuntimeException("Database query not set. Use 'I query database with' step first");
            }

            boolean isMatch = APIToDBValidator.compareAPIResponseWithDBValue(
                    response,
                    apiJsonPath,
                    currentDBQuery,
                    dbColumnName
            );

            ExtentCucumberAdapter.addTestStepLog("API-DB Comparison Result: " + (isMatch ? "MATCH" : "MISMATCH"));
            assertTrue(isMatch, "API field " + apiJsonPath + " does not match DB column " + dbColumnName);

        } catch (Exception e) {
            LoggerUtil.error("API to DB field comparison failed", e);
            throw e;
        }
    }

    /**
     * Set database query for later use
     */
    @And("I query database with {string}")
    public void setDatabaseQuery(String query) {
        LoggerUtil.info("Setting database query: " + query);
        try {
            this.currentDBQuery = query;
            ExtentCucumberAdapter.addTestStepLog("Database query set: " + query);
        } catch (Exception e) {
            LoggerUtil.error("Failed to set database query", e);
            throw e;
        }
    }

    /**
     * Verify API response data exists in database
     */
    @Then("I verify that the API response with field {string} exists in database using query {string}")
    public void verifyAPIResponseExistsInDB(String apiJsonPath, String dbQuery) {
        LoggerUtil.info("Verifying API response data exists in database");
        try {
            boolean exists = APIToDBValidator.verifyAPIResponseDataExistsInDB(response, apiJsonPath, dbQuery);
            ExtentCucumberAdapter.addTestStepLog("Database verification: " + (exists ? "Record found" : "Record not found"));
            assertTrue(exists, "API response data not found in database");

        } catch (Exception e) {
            LoggerUtil.error("Database verification failed", e);
            throw e;
        }
    }

    /**
     * Compare multiple API fields with database row
     */
    @Then("I verify the following API fields match database columns")
    public void verifyMultipleAPIFieldsMatchDBColumns(io.cucumber.datatable.DataTable dataTable) {
        LoggerUtil.info("Verifying multiple API fields match database columns");
        try {
            if (currentDBQuery == null || currentDBQuery.isEmpty()) {
                throw new RuntimeException("Database query not set. Use 'I query database with' step first");
            }

            // Convert datatable to field mappings
            Map<String, String> mappings = new HashMap<>();
            for (java.util.List<String> row : dataTable.asLists()) {
                if (row.size() == 2) {
                    mappings.put(row.get(0), row.get(1));
                }
            }

            Map<String, Boolean> results = APIToDBValidator.compareAPIResponseWithDBRow(response, mappings, currentDBQuery);

            // Assert all matches
            long matchCount = results.values().stream().filter(v -> v).count();
            ExtentCucumberAdapter.addTestStepLog("Multi-field comparison: " + matchCount + "/" + results.size() + " matched");

            assertEquals(matchCount, results.size(), "Not all API fields match database columns");

        } catch (Exception e) {
            LoggerUtil.error("Multi-field verification failed", e);
            throw e;
        }
    }

    /**
     * Get count from database
     */
    @Then("I verify the database record count for query {string} is {int}")
    public void verifyDatabaseRecordCount(String query, int expectedCount) {
        LoggerUtil.info("Verifying database record count");
        try {
            int actualCount = APIToDBValidator.getRecordCountFromDB(query);
            ExtentCucumberAdapter.addTestStepLog("Expected Count: " + expectedCount + " | Actual Count: " + actualCount);
            assertEquals(actualCount, expectedCount, "Database record count mismatch");

        } catch (Exception e) {
            LoggerUtil.error("Record count verification failed", e);
            throw e;
        }
    }

    /**
     * Cleanup - close database connection
     */
    @And("I close the database connection")
    public void closeDatabaseConnection() {
        LoggerUtil.info("Closing database connection");
        try {
            DatabaseConnectionManager.getInstance().closePool();
            ExtentCucumberAdapter.addTestStepLog("Database connection pool closed");
        } catch (Exception e) {
            LoggerUtil.error("Failed to close database connection", e);
            throw e;
        }
    }
}