// File: src/main/java/utils/RetryUtil.java
package utils;

import java.util.function.Supplier;

/**
 * Retry utility for handling transient failures.
 */
public final class RetryUtil {

    private RetryUtil() {
    }

    /**
     * Retry a given operation with exponential backoff.
     */
    public static <T> T executeWithRetry(String operationName, Supplier<T> operation,
                                         int maxAttempts, long initialDelayMs) {
        int attempt = 0;
        long delay = initialDelayMs;
        Exception lastException = null;

        while (attempt < maxAttempts) {
            try {
                attempt++;
                LoggerUtil.info("Executing " + operationName + " (Attempt " + attempt + "/" + maxAttempts + ")");
                return operation.get();
            } catch (Exception e) {
                lastException = e;
             //   LoggerUtil.warn("Attempt " + attempt + " failed: " + e.getMessage(), e);

                if (attempt < maxAttempts) {
                    try {
                        LoggerUtil.info("Retrying after " + delay + "ms...");
                        Thread.sleep(delay);
                        delay = Math.min(delay * 2, 30000); // Cap at 30 seconds
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        LoggerUtil.error("Retry interrupted", ie);
                        throw new RuntimeException("Operation interrupted", ie);
                    }
                }
            }
        }

        LoggerUtil.error("Operation " + operationName + " failed after " + maxAttempts + " attempts");
        throw new RuntimeException("Operation failed after " + maxAttempts + " attempts", lastException);
    }

    /**
     * Simple retry with fixed delay.
     */
    public static <T> T executeWithRetry(String operationName, Supplier<T> operation,
                                         int maxAttempts) {
        return executeWithRetry(operationName, operation, maxAttempts, 1000);
    }
}
