package stepdefinitions;

import com.aventstack.extentreports.cucumber.adapter.ExtentCucumberAdapter;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import utils.DataDrivenContext;
import utils.DataDrivenScenarioManager;
import utils.LoggerUtil;

/**
 * Hooks for data-driven scenario execution.
 * Initializes and cleans up data context for each scenario.
 */
public class DataDrivenHooks {

    /**
     * Before each scenario - initialize data-driven context if needed
     */
    @Before
    public void beforeScenario(Scenario scenario) {
        LoggerUtil.info("=== Starting Scenario: " + scenario.getName() + " ===");
        ExtentCucumberAdapter.addTestStepLog("Scenario: " + scenario.getName());
    }

    /**
     * After each scenario - cleanup data-driven context
     */
    @After
    public void afterScenario(Scenario scenario) {
        try {
            String status = scenario.isFailed() ? "FAILED" : "PASSED";
            LoggerUtil.info("=== Scenario: " + scenario.getName() + " Status: " + status + " ===");

            // Print execution summary
            String summary = DataDrivenScenarioManager.getExecutionSummary();
            LoggerUtil.info("Execution Summary: " + summary);

            // Cleanup
            DataDrivenScenarioManager.cleanup();

        } catch (Exception e) {
           // LoggerUtil.warn("Error during scenario cleanup", e);
        }
    }
}