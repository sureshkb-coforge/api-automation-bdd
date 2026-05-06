package utils;

import java.util.Map;

public final class DynamicDataEngine {

    private DynamicDataEngine() {
    }

    public static String applyDynamicValues(String template, Map<String, String> replacements) {
        String result = template;
        for (Map.Entry<String, String> entry : replacements.entrySet()) {
            result = result.replace("{{" + entry.getKey() + "}}", entry.getValue());
        }
        return result;
    }
}
