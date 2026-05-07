package base;

import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeSuite;
import reports.ExtentManager;
import utils.*;

import java.sql.SQLException;

public class BaseTest {

    @BeforeSuite(alwaysRun = true)
    public void beforeSuite() {

        FileSystemUtil.ensureDirectoryExists("logs");

        try {
            LoggerUtil.info("========================================");
            LoggerUtil.info("Starting API automation suite execution");
            LoggerUtil.info("========================================");

            // Initialize environment first
            ConfigLoader.initializeEnvironment();
            String environment = ConfigLoader.getCurrentEnvironment();
            LoggerUtil.info("Using environment: " + environment);

            // Ensure required directories exist
            ensureRequiredDirectories();

            // Load configurations for the environment
            ConfigLoader.loadProperties();
            LoggerUtil.info("Configuration properties loaded successfully for environment: " + environment);

            // Initialize reports
            ExtentManager.initReports();
            LoggerUtil.info("Extent reports initialized");

            // Clear any leftover context from previous runs
            GlobalContext.clear();
            LoggerUtil.info("Global context cleared for fresh start");

        } catch (Exception e) {
            LoggerUtil.error("Failed to initialize test suite", e);
            throw new RuntimeException("Suite initialization failed", e);
        }
    }

    @AfterSuite(alwaysRun = true)
    public void afterSuite() {
        try {
            LoggerUtil.info("========================================");
            LoggerUtil.info("Finishing API automation suite");
            LoggerUtil.info("========================================");

            // Dump global context for debugging
            GlobalContext.dumpContext();

            // Flush reports
            ExtentManager.flushReports();
            LoggerUtil.info("Extent reports flushed successfully");

        } catch (Exception e) {
            LoggerUtil.error("Failed to finalize test suite", e);
            throw new RuntimeException("Suite finalization failed", e);
        }
    }

//    @BeforeClass
//    public void setupDatabase() {
//        try {
//            LoggerUtil.info("Setting up database connection pool for test suite");
//            DatabaseConnectionManager.getInstance().initializeFromConfig();
//        } catch (Exception e) {
//            LoggerUtil.warn("Database initialization failed - tests without DB will continue", (SQLException) e);
//        }
//    }
//
//    @AfterClass
//    public void teardownDatabase() {
//        try {
//            LoggerUtil.info("Tearing down database connection pool");
//            DatabaseConnectionManager.getInstance().closePool();
//        } catch (Exception e) {
//            LoggerUtil.warn("Error closing database pool", (SQLException) e);
//        }
//    }

    /**
     * Ensure all required directories exist
     */
    private void ensureRequiredDirectories() {
        try {
            FileSystemUtil.ensureDirectoryExists("logs");
            FileSystemUtil.ensureDirectoryExists("reports");
            FileSystemUtil.ensureDirectoryExists("config");
            LoggerUtil.info("All required directories verified");
        } catch (Exception e) {
            LoggerUtil.error("Failed to ensure required directories", e);
            throw e;
        }
    }
}
