package utils;

import com.aventstack.extentreports.cucumber.adapter.ExtentCucumberAdapter;
import exceptions.DatabaseException;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Database utility for executing queries and managing database operations.
 * Supports SELECT, INSERT, UPDATE, DELETE operations.
 */
public final class DatabaseUtil {

    private DatabaseUtil() {
    }

    /**
     * Execute a SELECT query and return single row as Map
     * @param query SQL SELECT query
     * @return Map with column names as keys and values as values
     */
    public static Map<String, String> executeQueryAndGetSingleRow(String query) {
        LoggerUtil.info("Executing database query: " + query);
        ExtentCucumberAdapter.addTestStepLog("Executing DB Query: " + query);

        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;

        try {
            connection = DatabaseConnectionManager.getInstance().getConnection();
            statement = connection.createStatement();
            resultSet = statement.executeQuery(query);

            Map<String, String> rowData = new HashMap<>();

            if (resultSet.next()) {
                int columnCount = resultSet.getMetaData().getColumnCount();
                for (int i = 1; i <= columnCount; i++) {
                    String columnName = resultSet.getMetaData().getColumnName(i);
                    String columnValue = resultSet.getString(columnName);
                    rowData.put(columnName, columnValue);
                }
                LoggerUtil.info("Query executed successfully. Retrieved 1 row with " + rowData.size() + " columns");
                ExtentCucumberAdapter.addTestStepLog("Database Query Result: " + rowData);
                return rowData;
            } else {
                LoggerUtil.info("Query returned no results");
                ExtentCucumberAdapter.addTestStepLog("Database Query Result: No records found");
                return new HashMap<>();
            }

        } catch (SQLException e) {
            LoggerUtil.error("Database query execution failed: " + query, e);
            ExtentCucumberAdapter.addTestStepLog("Database Query Error: " + e.getMessage());
            throw new DatabaseException("Failed to execute query", query, e);
        } finally {
            closeResources(connection, statement, resultSet);
        }
    }

    /**
     * Execute a SELECT query and return all rows as List of Maps
     * @param query SQL SELECT query
     * @return List of Maps, each Map representing a row
     */
    public static List<Map<String, String>> executeQueryAndGetAllRows(String query) {
        LoggerUtil.info("Executing database query: " + query);
        ExtentCucumberAdapter.addTestStepLog("Executing DB Query: " + query);

        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;

        try {
            connection = DatabaseConnectionManager.getInstance().getConnection();
            statement = connection.createStatement();
            resultSet = statement.executeQuery(query);

            List<Map<String, String>> allRows = new ArrayList<>();
            int columnCount = resultSet.getMetaData().getColumnCount();

            while (resultSet.next()) {
                Map<String, String> rowData = new HashMap<>();
                for (int i = 1; i <= columnCount; i++) {
                    String columnName = resultSet.getMetaData().getColumnName(i);
                    String columnValue = resultSet.getString(columnName);
                    rowData.put(columnName, columnValue);
                }
                allRows.add(rowData);
            }

            LoggerUtil.info("Query executed successfully. Retrieved " + allRows.size() + " rows");
            ExtentCucumberAdapter.addTestStepLog("Database Query Result: Retrieved " + allRows.size() + " rows");
            return allRows;

        } catch (SQLException e) {
            LoggerUtil.error("Database query execution failed: " + query, e);
            ExtentCucumberAdapter.addTestStepLog("Database Query Error: " + e.getMessage());
            throw new DatabaseException("Failed to execute query", query, e);
        } finally {
            closeResources(connection, statement, resultSet);
        }
    }

    /**
     * Execute a SELECT query and return a single column value as String
     * @param query SQL SELECT query
     * @param columnName Name of the column to extract
     * @return Column value as String
     */
    public static String executeQueryAndGetSingleValue(String query, String columnName) {
        LoggerUtil.info("Executing database query for single value: " + query);

        Map<String, String> result = executeQueryAndGetSingleRow(query);
        String value = result.get(columnName);

        if (value != null) {
            LoggerUtil.info("Retrieved value from column '" + columnName + "': " + value);
            return value;
        } else {
            LoggerUtil.info("Column '" + columnName + "' not found in result");
            throw new DatabaseException("Column not found in query result: " + columnName);
        }
    }

    /**
     * Execute INSERT query
     * @param query INSERT SQL query
     * @return Number of rows affected
     */
    public static int executeInsert(String query) {
        LoggerUtil.info("Executing INSERT query: " + query);
        ExtentCucumberAdapter.addTestStepLog("Executing DB INSERT: " + query);

        Connection connection = null;
        Statement statement = null;

        try {
            connection = DatabaseConnectionManager.getInstance().getConnection();
            statement = connection.createStatement();
            int rowsAffected = statement.executeUpdate(query);

            LoggerUtil.info("INSERT query executed successfully. Rows affected: " + rowsAffected);
            ExtentCucumberAdapter.addTestStepLog("INSERT operation successful. Rows affected: " + rowsAffected);
            return rowsAffected;

        } catch (SQLException e) {
            LoggerUtil.error("INSERT query execution failed: " + query, e);
            ExtentCucumberAdapter.addTestStepLog("INSERT Error: " + e.getMessage());
            throw new DatabaseException("Failed to execute INSERT query", query, e);
        } finally {
            closeResources(connection, statement, null);
        }
    }

    /**
     * Execute UPDATE query
     * @param query UPDATE SQL query
     * @return Number of rows affected
     */
    public static int executeUpdate(String query) {
        LoggerUtil.info("Executing UPDATE query: " + query);
        ExtentCucumberAdapter.addTestStepLog("Executing DB UPDATE: " + query);

        Connection connection = null;
        Statement statement = null;

        try {
            connection = DatabaseConnectionManager.getInstance().getConnection();
            statement = connection.createStatement();
            int rowsAffected = statement.executeUpdate(query);

            LoggerUtil.info("UPDATE query executed successfully. Rows affected: " + rowsAffected);
            ExtentCucumberAdapter.addTestStepLog("UPDATE operation successful. Rows affected: " + rowsAffected);
            return rowsAffected;

        } catch (SQLException e) {
            LoggerUtil.error("UPDATE query execution failed: " + query, e);
            ExtentCucumberAdapter.addTestStepLog("UPDATE Error: " + e.getMessage());
            throw new DatabaseException("Failed to execute UPDATE query", query, e);
        } finally {
            closeResources(connection, statement, null);
        }
    }

    /**
     * Execute DELETE query
     * @param query DELETE SQL query
     * @return Number of rows affected
     */
    public static int executeDelete(String query) {
        LoggerUtil.info("Executing DELETE query: " + query);
        ExtentCucumberAdapter.addTestStepLog("Executing DB DELETE: " + query);

        Connection connection = null;
        Statement statement = null;

        try {
            connection = DatabaseConnectionManager.getInstance().getConnection();
            statement = connection.createStatement();
            int rowsAffected = statement.executeUpdate(query);

            LoggerUtil.info("DELETE query executed successfully. Rows affected: " + rowsAffected);
            ExtentCucumberAdapter.addTestStepLog("DELETE operation successful. Rows affected: " + rowsAffected);
            return rowsAffected;

        } catch (SQLException e) {
            LoggerUtil.error("DELETE query execution failed: " + query, e);
            ExtentCucumberAdapter.addTestStepLog("DELETE Error: " + e.getMessage());
            throw new DatabaseException("Failed to execute DELETE query", query, e);
        } finally {
            closeResources(connection, statement, null);
        }
    }

    /**
     * Close all database resources
     */
    private static void closeResources(Connection connection, Statement statement, ResultSet resultSet) {
        try {
            if (resultSet != null && !resultSet.isClosed()) {
                resultSet.close();
            }
            if (statement != null && !statement.isClosed()) {
                statement.close();
            }
            if (connection != null && !connection.isClosed()) {
                DatabaseConnectionManager.getInstance().closeConnection(connection);
            }
        } catch (SQLException e) {
            LoggerUtil.warn("Error closing database resources", e);
        }
    }
}