package base;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.specification.RequestSpecification;
import utils.ConfigLoader;

public final class TestConfig {

    private TestConfig() {
    }

    public static RequestSpecification getDefaultRequestSpec() {
        RequestSpecBuilder builder = new RequestSpecBuilder()
                .setBaseUri(ConfigLoader.get("baseURL"));

        String apiKey = ConfigLoader.get("apiKey");
      //  if (apiKey != null && !apiKey.isBlank()) {
            builder.addHeader("x-api-key", apiKey);
        //}

        return builder.build();
    }

    public static void applyDefaultConfig() {
        RestAssured.requestSpecification = getDefaultRequestSpec();
    }
}
