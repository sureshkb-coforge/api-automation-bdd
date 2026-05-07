package utils;

import com.aventstack.extentreports.cucumber.adapter.ExtentCucumberAdapter;
import io.restassured.response.Response;

import java.util.List;
import java.util.Map;

/**
 * Manages data-driven scenario execution.
 * Orchestrates Excel data loading and scenario iteration.
 */
public final class DataDrivenScenarioManager {

    private DataDrivenScenarioManager() {
    }

    /**
     * Load test data from Excel and initialize context
     * Excel file name and sheet name should match scenario name/test case name
     * @param scenarioName Name of scenario or test case
     * @return true if data loaded successfully, false otherwise
     */
    public static boolean loadTestDataForScenario(String scenarioName) {
        LoggerUtil.info("Loading test data for scenario: " + scenarioName);

        try {
            // Get base path from config
            String excelBasePath = "src/test/resources/testdata/"+ scenarioName +".xlsx";

            // Use scenario name as sheet name
            String sheetName = scenarioName;

            LoggerUtil.info("Loading Excel data | File: " + excelBasePath + " | Sheet: " + sheetName);

            // Get all rows from Excel
            List<Map<String, String>> allRows = ExcelUtil.getAllRowsAsMap(excelBasePath, sheetName);

            if (allRows == null || allRows.isEmpty()) {
 //               LoggerUtil.warn("No test data found for scenario: " + scenarioName);
                ExtentCucumberAdapter.addTestStepLog("Warning: No test data found in Excel sheet: " + sheetName);
                return false;
            }

            // Initialize DataDrivenContext
            DataDrivenContext.initialize(excelBasePath, sheetName, allRows);

            LoggerUtil.info("Test data loaded successfully. Total rows: " + allRows.size());
            ExtentCucumberAdapter.addTestStepLog("Test data loaded | Sheet: " + sheetName + " | Total rows: " + allRows.size());

            return true;

        } catch (Exception e) {
            LoggerUtil.error("Failed to load test data for scenario: " + scenarioName, e);
            ExtentCucumberAdapter.addTestStepLog("Error loading test data: " + e.getMessage());
            throw e;
        }
    }

    /**
     * Check if more rows are available for iteration
     * @return true if next row exists, false otherwise
     */
    public static boolean hasMoreRows() {
        int currentRow = DataDrivenContext.getCurrentRowIndex();
        int totalRows = DataDrivenContext.getTotalRows();
        boolean hasMore = currentRow < totalRows;

        LoggerUtil.debug("Checking for more rows | Current: " + currentRow + " | Total: " + totalRows + " | HasMore: " + hasMore);
        return hasMore;
    }

    /**
     * Move to next row and prepare data
     * @return true if successfully moved to next row, false if no more rows
     */
    public static boolean moveToNextTestRow() {
        boolean moved = DataDrivenContext.moveToNextRow();

        if (moved) {
            int currentRowIndex = DataDrivenContext.getCurrentRowIndex();
            Map<String, String> rowData = DataDrivenContext.getAllCurrentRowData();

            LoggerUtil.info("Moved to row #" + currentRowIndex);
            ExtentCucumberAdapter.addTestStepLog("Processing row #" + currentRowIndex + " | Data: " + rowData);

            // Store row data in GlobalContext for use in step definitions
            GlobalContext.set("currentRowData", rowData);
            GlobalContext.set("currentRowIndex", currentRowIndex);
        } else {
            LoggerUtil.info("No more rows to process");
        }

        return moved;
    }

    /**
     * Get current test row data
     * @return Map containing column name -> value pairs
     */
    public static Map<String, String> getCurrentTestData() {
        return DataDrivenContext.getCurrentRowData();
    }

    /**
     * Get value from current row by column name
     * @param columnName Column name in Excel
     * @return Column value as String
     */
    public static String getTestDataValue(String columnName) {
        return DataDrivenContext.getColumnValue(columnName);
    }

    /**
     * Validate API response against Excel expected values
     * @param response API Response
     * @param statusCodeColumn Excel column name for expected status code
     * @return true if all validations pass
     */
    public static boolean validateResponseAgainstExcel(Response response, String statusCodeColumn) {
        LoggerUtil.info("Validating response against Excel data");

        try {
            boolean statusMatch = ExcelResponseValidator.validateStatusCodeFromExcel(response, statusCodeColumn);

            if (!statusMatch) {
                LoggerUtil.error("Status code validation failed");
                return false;
            }

            return true;

        } catch (Exception e) {
            LoggerUtil.error("Response validation against Excel failed", e);
            throw e;
        }
    }

    /**
     * Clean up after scenario execution
     */
    public static void cleanup() {
        DataDrivenContext.reset();
        LoggerUtil.info("DataDrivenScenarioManager cleanup completed");
    }

    /**
     * Get scenario execution summary
     * @return Summary string with row count and current progress
     */
    public static String getExecutionSummary() {
        int currentRow = DataDrivenContext.getCurrentRowIndex();
        int totalRows = DataDrivenContext.getTotalRows();
        String sheet = DataDrivenContext.getCurrentSheet();
        String file = DataDrivenContext.getCurrentFile();

        return String.format("Scenario: %s | Sheet: %s | Progress: %d/%d rows",
                file, sheet, currentRow, totalRows);
    }
}