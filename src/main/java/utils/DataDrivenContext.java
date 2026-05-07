package utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Context for managing data-driven test execution.
 * Stores Excel data and current row information during scenario execution.
 */
public final class DataDrivenContext {

    private static final ThreadLocal<Integer> CURRENT_ROW = ThreadLocal.withInitial(() -> 0);
    private static final ThreadLocal<String> CURRENT_SHEET = ThreadLocal.withInitial(() -> "");
    private static final ThreadLocal<String> CURRENT_FILE = ThreadLocal.withInitial(() -> "");
    private static final ThreadLocal<List<Map<String, String>>> ALL_TEST_DATA = ThreadLocal.withInitial(ArrayList::new);
    private static final ThreadLocal<Map<String, String>> CURRENT_ROW_DATA = ThreadLocal.withInitial(HashMap::new);
    private static final ThreadLocal<Integer> TOTAL_ROWS = ThreadLocal.withInitial(() -> 0);

    private DataDrivenContext() {
    }

    /**
     * Initialize context with test data
     */
    public static void initialize(String excelFile, String sheetName, List<Map<String, String>> testData) {
        CURRENT_FILE.set(excelFile);
        CURRENT_SHEET.set(sheetName);
        ALL_TEST_DATA.set(new ArrayList<>(testData));
        TOTAL_ROWS.set(testData.size());
        CURRENT_ROW.set(0);
        LoggerUtil.info("DataDrivenContext initialized | File: " + excelFile + " | Sheet: " + sheetName + " | Total Rows: " + testData.size());
    }

    /**
     * Move to next row and set current row data
     */
    public static boolean moveToNextRow() {
        int currentRowIndex = CURRENT_ROW.get();
        int totalRows = TOTAL_ROWS.get();

        if (currentRowIndex < totalRows) {
            Map<String, String> rowData = ALL_TEST_DATA.get().get(currentRowIndex);
            CURRENT_ROW_DATA.set(new HashMap<>(rowData));
            CURRENT_ROW.set(currentRowIndex + 1);
            LoggerUtil.info("Moved to row " + (currentRowIndex + 1) + " of " + totalRows);
            return true;
        }
        return false;
    }

    /**
     * Get current row data as Map
     */
    public static Map<String, String> getCurrentRowData() {
        return new HashMap<>(CURRENT_ROW_DATA.get());
    }

    /**
     * Get value from current row by column name
     */
    public static String getColumnValue(String columnName) {
        String value = CURRENT_ROW_DATA.get().get(columnName);
        LoggerUtil.debug("Column '" + columnName + "' value: " + value);
        return value;
    }

    /**
     * Get current row index
     */
    public static int getCurrentRowIndex() {
        return CURRENT_ROW.get();
    }

    /**
     * Get total rows count
     */
    public static int getTotalRows() {
        return TOTAL_ROWS.get();
    }

    /**
     * Get current sheet name
     */
    public static String getCurrentSheet() {
        return CURRENT_SHEET.get();
    }

    /**
     * Get current file name
     */
    public static String getCurrentFile() {
        return CURRENT_FILE.get();
    }

    /**
     * Reset context
     */
    public static void reset() {
        CURRENT_ROW.remove();
        CURRENT_SHEET.remove();
        CURRENT_FILE.remove();
        ALL_TEST_DATA.remove();
        CURRENT_ROW_DATA.remove();
        TOTAL_ROWS.remove();
        LoggerUtil.info("DataDrivenContext reset");
    }

    /**
     * Check if column exists in current row
     */
    public static boolean hasColumn(String columnName) {
        return CURRENT_ROW_DATA.get().containsKey(columnName);
    }

    /**
     * Get all data for current row
     */
    public static Map<String, String> getAllCurrentRowData() {
        return new HashMap<>(CURRENT_ROW_DATA.get());
    }
}