package reports;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import utils.LoggerUtil;

public final class ExtentManager {

    private static ExtentReports extentReports;

    private ExtentManager() {
    }

    public static synchronized void initReports() {
        if (extentReports == null) {
            try {
                ExtentSparkReporter sparkReporter = new ExtentSparkReporter("reports/manual-extent-report.html");
                sparkReporter.config().setReportName("API Automation Manual Test Report");
                sparkReporter.config().setDocumentTitle("API Automation");
                extentReports = new ExtentReports();
                extentReports.attachReporter(sparkReporter);
                LoggerUtil.info("Extent reports initialized successfully");
            } catch (Exception e) {
                LoggerUtil.error("Failed to initialize Extent reports", e);
                throw new RuntimeException("Unable to initialize Extent reports", e);
            }
        }
    }

    public static synchronized ExtentReports getExtentReports() {
        try {
            if (extentReports == null) {
                initReports();
            }
            return extentReports;
        } catch (Exception e) {
            LoggerUtil.error("Failed to get Extent reports", e);
            throw new RuntimeException("Unable to retrieve Extent reports", e);
        }
    }

    public static synchronized void flushReports() {
        if (extentReports != null) {
            try {
                extentReports.flush();
                LoggerUtil.info("Extent reports flushed successfully");
            } catch (Exception e) {
                LoggerUtil.error("Failed to flush Extent reports", e);
                throw new RuntimeException("Unable to flush Extent reports", e);
            }
        }
    }
}
