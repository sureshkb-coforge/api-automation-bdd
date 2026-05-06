package reports;

import com.aventstack.extentreports.ExtentTest;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class ExtentTestManager {

    private static final Map<Long, ExtentTest> TEST_MAP = new ConcurrentHashMap<>();

    private ExtentTestManager() {
    }

    public static synchronized ExtentTest startTest(String testName) {
        ExtentTest test = ExtentManager.getExtentReports().createTest(testName);
        TEST_MAP.put(Thread.currentThread().getId(), test);
        return test;
    }

    public static ExtentTest getTest() {
        return TEST_MAP.get(Thread.currentThread().getId());
    }

    public static void unload() {
        TEST_MAP.remove(Thread.currentThread().getId());
    }
}
