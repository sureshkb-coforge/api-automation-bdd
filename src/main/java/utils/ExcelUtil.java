package utils;

import exceptions.DataException;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.Map;

public final class ExcelUtil {

    private ExcelUtil() {
    }

    /**
     * Get environment-specific Excel path
     */
    private static String getEnvironmentSpecificExcelPath(String basePath) {
        String environment = ConfigLoader.getCurrentEnvironment();

        // Try environment-specific path first
        String envSpecificPath = basePath.replace(".xlsx", "-" + environment + ".xlsx");
        if (new File(envSpecificPath).exists()) {
            LoggerUtil.info("Using environment-specific Excel: " + envSpecificPath);
            return envSpecificPath;
        }

        // Fall back to default path
        LoggerUtil.info("Using default Excel: " + basePath);
        return basePath;
    }

    /**
     * Read row data as map from Excel
     */
    public static Map<String, String> getRowDataAsMap(String excelPath, String sheetName, int rowIndex) {
        Map<String, String> data = new LinkedHashMap<>();
        DataFormatter formatter = new DataFormatter();

        // Get environment-specific path if available
        String actualExcelPath = getEnvironmentSpecificExcelPath(excelPath);

        try {
            // Validate file exists
            File excelFile = new File(actualExcelPath);
            if (!excelFile.exists()) {
                throw new DataException(actualExcelPath, "Excel file not found: " + actualExcelPath);
            }

            try (Workbook workbook = WorkbookFactory.create(excelFile)) {
                Sheet sheet = workbook.getSheet(sheetName);
                if (sheet == null) {
                    throw new DataException(sheetName, "Sheet not found in Excel: " + sheetName);
                }

                Row headerRow = sheet.getRow(0);
                Row valueRow = sheet.getRow(rowIndex);

                if (headerRow == null) {
                    throw new DataException(actualExcelPath, "Header row is missing in Excel sheet: " + sheetName);
                }

                if (valueRow == null) {
                    throw new DataException(actualExcelPath, "Value row " + rowIndex + " is missing in Excel sheet: " + sheetName);
                }

                int maxCells = Math.max(headerRow.getLastCellNum(), valueRow.getLastCellNum());
                for (int i = 0; i < maxCells; i++) {
                    String key = formatter.formatCellValue(headerRow.getCell(i));
                    String value = formatter.formatCellValue(valueRow.getCell(i));
                    if (!key.isEmpty()) {
                        data.put(key, value);
                    }
                }

                LoggerUtil.info("Successfully read " + data.size() + " data points from Excel row " + rowIndex);
            }
        } catch (DataException e) {
            throw e;
        } catch (Exception e) {
            LoggerUtil.error("Failed to read Excel file: " + actualExcelPath, e);
        }

        return data;
    }

    /**
     * Get all rows from a sheet
     */
    public static java.util.List<Map<String, String>> getAllRowsAsMap(String excelPath, String sheetName) {
        java.util.List<Map<String, String>> allRows = new java.util.ArrayList<>();
        DataFormatter formatter = new DataFormatter();

        // Get environment-specific path if available
        String actualExcelPath = getEnvironmentSpecificExcelPath(excelPath);

        try {
            File excelFile = new File(actualExcelPath);
            if (!excelFile.exists()) {
                throw new DataException(actualExcelPath, "Excel file not found: " + actualExcelPath);
            }

            try (Workbook workbook = WorkbookFactory.create(excelFile)) {
                Sheet sheet = workbook.getSheet(sheetName);
                if (sheet == null) {
                    throw new DataException(sheetName, "Sheet not found in Excel: " + sheetName);
                }

                Row headerRow = sheet.getRow(0);
                if (headerRow == null) {
                    throw new DataException(actualExcelPath, "Header row is missing");
                }

                for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                    Row row = sheet.getRow(i);
                    if (row == null) continue;

                    Map<String, String> rowData = new LinkedHashMap<>();
                    int maxCells = Math.max(headerRow.getLastCellNum(), row.getLastCellNum());
                    for (int j = 0; j < maxCells; j++) {
                        String key = formatter.formatCellValue(headerRow.getCell(j));
                        String value = formatter.formatCellValue(row.getCell(j));
                        if (!key.isEmpty()) {
                            rowData.put(key, value);
                        }
                    }
                    allRows.add(rowData);
                }

                LoggerUtil.info("Successfully read " + allRows.size() + " rows from Excel sheet: " + sheetName);
            }
        } catch (DataException e) {
            throw e;
        } catch (Exception e) {
            LoggerUtil.error("Failed to read Excel file: " + actualExcelPath, e);
        }

        return allRows;
    }
}
