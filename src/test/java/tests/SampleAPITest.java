package tests;

import base.BaseTest;
import client.APIClient;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import reports.ExtentTestManager;
import utils.ConfigLoader;
import utils.LoggerUtil;

public class SampleAPITest extends BaseTest {

    private APIClient apiClient;

    @BeforeMethod(alwaysRun = true)
    public void setUpTest() {
        try {
            apiClient = new APIClient();
        } catch (Exception e) {
            LoggerUtil.error("Failed to set up API client", e);
            throw new RuntimeException("Test setup failed", e);
        }
    }

    @AfterMethod(alwaysRun = true)
    public void tearDownTest() {
        try {
            ExtentTestManager.unload();
        } catch (Exception e) {
            LoggerUtil.error("Failed to unload Extent test", e);
            // Non-critical, so log but don't throw
        }
    }

    @Test(description = "Validate GET /api/users/2 returns 200")
    public void validateGetUserApi() {
        try {
            ExtentTestManager.startTest("validateGetUserApi")
                    .info("Executing TestNG sample API test");

            Response response = apiClient.get(ConfigLoader.get("getUserEndpoint"));
            ExtentTestManager.getTest().info("Response body: " + response.asPrettyString());

            Assert.assertEquals(response.getStatusCode(), 200);
            Assert.assertEquals(response.jsonPath().getString("data.first_name"), "Janet");
            ExtentTestManager.getTest().pass("Validated user details successfully");
        } catch (Exception e) {
            LoggerUtil.error("Test validateGetUserApi failed", e);
            ExtentTestManager.getTest().fail("Test failed: " + e.getMessage());
            throw e;
        }
    }
}
