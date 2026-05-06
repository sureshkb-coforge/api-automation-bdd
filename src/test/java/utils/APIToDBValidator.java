package utils;

import com.aventstack.extentreports.cucumber.adapter.ExtentCucumberAdapter;
import io.restassured.response.Response;

import java.util.Map;

/**
 * Utility for comparing API response values with database query results.
 * Provides comprehensive comparison and validation methods.
 */
public final class APIToDBValidator {

    private APIToDBValidator() {
    }

    /**
     * Compare a single API response field with database value
     * @param apiResponse REST API Response object
     * @param apiJsonPath JsonPath to the field in API response (e.g., "data.id")
     * @param dbQuery SQL query to execute
     * @param dbColumnName Column name in database result
     * @return true if values match, false otherwise
     */
    public static boolean compareAPIResponseWithDBValue(Response apiResponse, String apiJsonPath,
                                                        String dbQuery, String dbColumnName) {
        LoggerUtil.info("Starting API to Database value comparison");
        LoggerUtil.info("API Path: " + apiJsonPath + " | DB Column: " + dbColumnName);

        try {
            // Extract value from API response
            String apiValue = apiResponse.jsonPath().getString(apiJsonPath);
            LoggerUtil.info("API Response Value: " + apiValue);
            ExtentCucumberAdapter.addTestStepLog("API Value (" + apiJsonPath + "): " + apiValue);

            // Query database
            String dbValue = DatabaseUtil.executeQueryAndGetSingleValue(dbQuery, dbColumnName);
            LoggerUtil.info("Database Value: " + dbValue);
            ExtentCucumberAdapter.addTestStepLog("DB Value (" + dbColumnName + "): " + dbValue);

            // Compare values
            boolean isMatch = apiValue != null && apiValue.equals(dbValue);

            if (isMatch) {
                LoggerUtil.info("✓ API and Database values MATCH: " + apiValue);
                ExtentCucumberAdapter.addTestStepLog("✓ API and Database values MATCH");
            } else {
                LoggerUtil.info("✗ API and Database values DO NOT MATCH. API: " + apiValue + " | DB: " + dbValue);
                ExtentCucumberAdapter.addTestStepLog("✗ Values DO NOT MATCH. API: " + apiValue + " | DB: " + dbValue);
            }

            return isMatch;

        } catch (Exception e) {
            LoggerUtil.error("Failed to compare API response with database value", e);
            ExtentCucumberAdapter.addTestStepLog("Comparison Error: " + e.getMessage());
            throw new RuntimeException("API to DB comparison failed", e);
        }
    }

    /**
     * Compare multiple fields from API response with database row
     * @param apiResponse REST API Response object
     * @param fieldMappings Map with key=apiJsonPath, value=dbColumnName
     * @param dbQuery SQL query that returns a single row
     * @return Map with comparison results for each field
     */
    public static Map<String, Boolean> compareAPIResponseWithDBRow(Response apiResponse,
                                                                   Map<String, String> fieldMappings,
                                                                   String dbQuery) {
        LoggerUtil.info("Starting multi-field API to Database comparison");
        LoggerUtil.info("Number of fields to compare: " + fieldMappings.size());

        Map<String, Boolean> comparisonResults = new java.util.HashMap<>();

        try {
            // Get database row
            Map<String, String> dbRow = DatabaseUtil.executeQueryAndGetSingleRow(dbQuery);
            ExtentCucumberAdapter.addTestStepLog("Database row retrieved with " + dbRow.size() + " columns");

            // Compare each field
            for (Map.Entry<String, String> mapping : fieldMappings.entrySet()) {
                String apiPath = mapping.getKey();
                String dbColumn = mapping.getValue();

                try {
                    String apiValue = apiResponse.jsonPath().getString(apiPath);
                    String dbValue = dbRow.get(dbColumn);

                    boolean isMatch = apiValue != null && apiValue.equals(dbValue);
                    comparisonResults.put(apiPath + " <-> " + dbColumn, isMatch);

                    String status = isMatch ? "✓ MATCH" : "✗ MISMATCH";
                    LoggerUtil.info(status + " | API[" + apiPath + "]: " + apiValue + " | DB[" + dbColumn + "]: " + dbValue);
                    ExtentCucumberAdapter.addTestStepLog(status + " | API: " + apiValue + " | DB: " + dbValue);

                } catch (Exception e) {
                    LoggerUtil.error("Error comparing field " + apiPath + " with " + dbColumn, e);
                    comparisonResults.put(apiPath + " <-> " + dbColumn, false);
                }
            }

            // Summary
            long matchCount = comparisonResults.values().stream().filter(v -> v).count();
            LoggerUtil.info("Comparison Summary: " + matchCount + "/" + comparisonResults.size() + " fields matched");
            ExtentCucumberAdapter.addTestStepLog("Summary: " + matchCount + "/" + comparisonResults.size() + " fields matched");

            return comparisonResults;

        } catch (Exception e) {
            LoggerUtil.error("Failed to compare API response with database row", e);
            ExtentCucumberAdapter.addTestStepLog("Comparison Error: " + e.getMessage());
            throw new RuntimeException("API to DB row comparison failed", e);
        }
    }

    /**
     * Verify that API response data exists in database
     * @param apiResponse REST API Response object
     * @param apiJsonPath JsonPath to the unique identifier in API response
     * @param dbQuery SQL query template (should contain placeholder for the value)
     * @return true if record found in database, false otherwise
     */
    public static boolean verifyAPIResponseDataExistsInDB(Response apiResponse, String apiJsonPath,
                                                          String dbQuery) {
        LoggerUtil.info("Verifying if API response data exists in database");

        try {
            String apiValue = apiResponse.jsonPath().getString(apiJsonPath);
            LoggerUtil.info("API Value to verify: " + apiValue);

            // Replace placeholder in query with actual value
            String finalQuery = dbQuery.replace("?", "'" + apiValue + "'");
            LoggerUtil.info("Executing query: " + finalQuery);

            Map<String, String> dbResult = DatabaseUtil.executeQueryAndGetSingleRow(finalQuery);

            boolean exists = !dbResult.isEmpty();

            if (exists) {
                LoggerUtil.info("✓ Record found in database");
                ExtentCucumberAdapter.addTestStepLog("✓ Record exists in database: " + dbResult);
            } else {
                LoggerUtil.info("✗ Record NOT found in database");
                ExtentCucumberAdapter.addTestStepLog("✗ Record does not exist in database");
            }

            return exists;

        } catch (Exception e) {
            LoggerUtil.error("Failed to verify API response data in database", e);
            ExtentCucumberAdapter.addTestStepLog("Verification Error: " + e.getMessage());
            throw new RuntimeException("Database verification failed", e);
        }
    }

    /**
     * Get count of records in database matching API criteria
     * @param dbQuery SQL COUNT query
     * @return Count as integer
     */
    public static int getRecordCountFromDB(String dbQuery) {
        LoggerUtil.info("Fetching record count from database");

        try {
            String countValue = DatabaseUtil.executeQueryAndGetSingleValue(dbQuery, "COUNT(*)");
            int count = Integer.parseInt(countValue);

            LoggerUtil.info("Record count from database: " + count);
            ExtentCucumberAdapter.addTestStepLog("Database Record Count: " + count);

            return count;

        } catch (Exception e) {
            LoggerUtil.error("Failed to get record count from database", e);
            throw new RuntimeException("Record count fetch failed", e);
        }
    }
}