package runners;

import base.BaseTest;
import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;
import org.testng.annotations.DataProvider;
import utils.LoggerUtil;

@CucumberOptions(
        features = "src/test/resources/features",
        glue = "stepdefinitions",
        tags = "@VancityAPIAutomation",
        plugin = {
                "pretty",
                "summary",
                "html:reports/cucumber-report.html",
                "json:reports/cucumber-report.json",
                "com.aventstack.extentreports.cucumber.adapter.ExtentCucumberAdapter:"
        },
        monochrome = true
)
public class TestNGRunner extends AbstractTestNGCucumberTests {

    @Override
    @DataProvider(parallel = false)
    public Object[][] scenarios() {
        try {
            return super.scenarios();
        } catch (Exception e) {
            LoggerUtil.error("Failed to load Cucumber scenarios", e);
            throw new RuntimeException("Scenario loading failed", e);
        }
    }
}
