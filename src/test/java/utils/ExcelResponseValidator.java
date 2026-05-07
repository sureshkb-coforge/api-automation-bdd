package utils;

import com.aventstack.extentreports.cucumber.adapter.ExtentCucumberAdapter;
import io.restassured.response.Response;

import java.util.Map;

/**
 * Validates API responses against expected values from Excel.
 * Supports multiple field validations and status code verification.
 */
public final class ExcelResponseValidator {

    private ExcelResponseValidator() {
    }

    /**
     * Validate API response status code against Excel expected value
     * @param response REST API Response
     * @param expectedStatusCodeColumn Column name in Excel containing expected status code
     * @return true if status code matches, false otherwise
     */
    public static boolean validateStatusCodeFromExcel(Response response, String expectedStatusCodeColumn) {
        LoggerUtil.info("Validating status code from Excel column: " + expectedStatusCodeColumn);

        try {
            // Get expected status code from current row data
            String expectedStatusStr = DataDrivenContext.getColumnValue(expectedStatusCodeColumn);

            if (expectedStatusStr == null || expectedStatusStr.isEmpty()) {
              //  LoggerUtil.warn("Expected status code not found in Excel column: " + expectedStatusCodeColumn);
                ExtentCucumberAdapter.addTestStepLog("Warning: Expected status code column is empty");
                return false;
            }

            int expectedStatus = Integer.parseInt(expectedStatusStr);
            int actualStatus = response.getStatusCode();

            boolean isMatch = actualStatus == expectedStatus;

            if (isMatch) {
                LoggerUtil.info("✓ Status Code Match | Expected: " + expectedStatus + " | Actual: " + actualStatus);
                ExtentCucumberAdapter.addTestStepLog("✓ Status Code MATCH | Expected: " + expectedStatus + " | Actual: " + actualStatus);
            } else {
                LoggerUtil.error("✗ Status Code Mismatch | Expected: " + expectedStatus + " | Actual: " + actualStatus);
                ExtentCucumberAdapter.addTestStepLog("✗ Status Code MISMATCH | Expected: " + expectedStatus + " | Actual: " + actualStatus);
            }

            return isMatch;

        } catch (NumberFormatException e) {
            LoggerUtil.error("Invalid status code format in Excel", e);
            ExtentCucumberAdapter.addTestStepLog("Error: Invalid status code format - " + e.getMessage());
            throw new RuntimeException("Invalid status code format in Excel column: " + expectedStatusCodeColumn, e);
        } catch (Exception e) {
            LoggerUtil.error("Failed to validate status code from Excel", e);
            throw e;
        }
    }

    /**
     * Validate single response field against Excel expected value
     * @param response REST API Response
     * @param jsonPath JsonPath to the field in response (e.g., "data.id")
     * @param excelColumnName Column name in Excel containing expected value
     * @return true if field value matches Excel expected value, false otherwise
     */
    public static boolean validateResponseFieldFromExcel(Response response, String jsonPath, String excelColumnName) {
        LoggerUtil.info("Validating response field | JsonPath: " + jsonPath + " | Excel Column: " + excelColumnName);

        try {
            // Get actual value from API response
            Object actualValue = response.jsonPath().get(jsonPath);

            // Get expected value from Excel
            String expectedValue = DataDrivenContext.getColumnValue(excelColumnName);

            if (expectedValue == null) {
              //  LoggerUtil.warn("Expected value not found in Excel column: " + excelColumnName);
                return false;
            }

            boolean isMatch = actualValue != null && actualValue.toString().equals(expectedValue);

            if (isMatch) {
                LoggerUtil.info("✓ Field Match | JsonPath: " + jsonPath + " | Expected: " + expectedValue + " | Actual: " + actualValue);
                ExtentCucumberAdapter.addTestStepLog("✓ Field MATCH | " + jsonPath + " = " + actualValue);
            } else {
           //     LoggerUtil.warn("✗ Field Mismatch | JsonPath: " + jsonPath + " | Expected: " + expectedValue + " | Actual: " + actualValue);
                ExtentCucumberAdapter.addTestStepLog("✗ Field MISMATCH | " + jsonPath + " | Expected: " + expectedValue + " | Actual: " + actualValue);
            }

            return isMatch;

        } catch (Exception e) {
            LoggerUtil.error("Failed to validate response field from Excel", e);
            throw e;
        }
    }

    /**
     * Validate multiple response fields against Excel expected values
     * @param response REST API Response
     * @param fieldMappings Map with key=jsonPath, value=excelColumnName
     * @return Map with validation results for each field
     */
    public static Map<String, Boolean> validateMultipleFieldsFromExcel(Response response, Map<String, String> fieldMappings) {
        LoggerUtil.info("Validating multiple response fields (" + fieldMappings.size() + " fields)");

        Map<String, Boolean> validationResults = new java.util.HashMap<>();

        try {
            for (Map.Entry<String, String> mapping : fieldMappings.entrySet()) {
                String jsonPath = mapping.getKey();
                String excelColumn = mapping.getValue();

                try {
                    boolean isValid = validateResponseFieldFromExcel(response, jsonPath, excelColumn);
                    validationResults.put(jsonPath, isValid);
                } catch (Exception e) {
                    LoggerUtil.error("Failed to validate field: " + jsonPath, e);
                    validationResults.put(jsonPath, false);
                }
            }

            // Log summary
            long matchCount = validationResults.values().stream().filter(v -> v).count();
            LoggerUtil.info("Field Validation Summary: " + matchCount + "/" + validationResults.size() + " fields matched");
            ExtentCucumberAdapter.addTestStepLog("Field Validation Summary: " + matchCount + "/" + validationResults.size() + " MATCHED");

            return validationResults;

        } catch (Exception e) {
            LoggerUtil.error("Failed to validate multiple fields from Excel", e);
            throw e;
        }
    }

    /**
     * Validate response field value contains expected substring from Excel
     * @param response REST API Response
     * @param jsonPath JsonPath to the field in response
     * @param excelColumnName Column name in Excel containing expected substring
     * @return true if field contains the substring, false otherwise
     */
    public static boolean validateResponseFieldContainsFromExcel(Response response, String jsonPath, String excelColumnName) {
        LoggerUtil.info("Validating response field contains substring | JsonPath: " + jsonPath);

        try {
            Object actualValue = response.jsonPath().get(jsonPath);
            String expectedSubstring = DataDrivenContext.getColumnValue(excelColumnName);

            if (expectedSubstring == null || actualValue == null) {
            //    LoggerUtil.warn("Expected substring or actual value is null");
                return false;
            }

            boolean isMatch = actualValue.toString().contains(expectedSubstring);

            if (isMatch) {
                LoggerUtil.info("✓ Field Contains Match | JsonPath: " + jsonPath + " contains '" + expectedSubstring + "'");
                ExtentCucumberAdapter.addTestStepLog("✓ Field CONTAINS '" + expectedSubstring + "'");
            } else {
               // LoggerUtil.warn("✗ Field Does Not Contain | JsonPath: " + jsonPath + " does not contain '" + expectedSubstring + "'");
                ExtentCucumberAdapter.addTestStepLog("✗ Field DOES NOT CONTAIN '" + expectedSubstring + "'");
            }

            return isMatch;

        } catch (Exception e) {
            LoggerUtil.error("Failed to validate field contains from Excel", e);
            throw e;
        }
    }

    /**
     * Validate response field as integer range from Excel
     * @param response REST API Response
     * @param jsonPath JsonPath to the field in response
     * @param minColumnName Column name in Excel containing minimum value
     * @param maxColumnName Column name in Excel containing maximum value
     * @return true if field value is within range, false otherwise
     */
    public static boolean validateResponseFieldRangeFromExcel(Response response, String jsonPath, String minColumnName, String maxColumnName) {
        LoggerUtil.info("Validating response field range | JsonPath: " + jsonPath);

        try {
            Object actualValue = response.jsonPath().get(jsonPath);
            String minStr = DataDrivenContext.getColumnValue(minColumnName);
            String maxStr = DataDrivenContext.getColumnValue(maxColumnName);

            if (actualValue == null || minStr == null || maxStr == null) {
              //  LoggerUtil.warn("Actual value or range values are null");
                return false;
            }

            int actual = Integer.parseInt(actualValue.toString());
            int min = Integer.parseInt(minStr);
            int max = Integer.parseInt(maxStr);

            boolean isInRange = actual >= min && actual <= max;

            if (isInRange) {
                LoggerUtil.info("✓ Field Range Match | JsonPath: " + jsonPath + " (" + actual + ") is between " + min + " and " + max);
                ExtentCucumberAdapter.addTestStepLog("✓ Field RANGE OK | " + actual + " is between " + min + "-" + max);
            } else {
            //    LoggerUtil.warn("✗ Field Range Mismatch | JsonPath: " + jsonPath + " (" + actual + ") is NOT between " + min + " and " + max);
                ExtentCucumberAdapter.addTestStepLog("✗ Field RANGE ERROR | " + actual + " NOT between " + min + "-" + max);
            }

            return isInRange;

        } catch (NumberFormatException e) {
            LoggerUtil.error("Invalid number format for range validation", e);
            throw new RuntimeException("Invalid number format for range validation", e);
        } catch (Exception e) {
            LoggerUtil.error("Failed to validate field range from Excel", e);
            throw e;
        }
    }
}