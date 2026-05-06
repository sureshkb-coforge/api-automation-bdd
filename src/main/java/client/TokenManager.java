package client;

import utils.ConfigLoader;

public final class TokenManager {

    private TokenManager() {
    }

    /**
     * Get API key from configuration
     */
    public static String getApiKey() {
        String apiKey = ConfigLoader.get("apiKey");
        return apiKey == null ? "" : apiKey;
    }
}
