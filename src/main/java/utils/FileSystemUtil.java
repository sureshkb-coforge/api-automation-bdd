// File: src/main/java/utils/FileSystemUtil.java
package utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public final class FileSystemUtil {

    private FileSystemUtil() {
    }

    /**
     * Ensures a directory exists; creates it if it doesn't.
     */
    public static void ensureDirectoryExists(String dirPath) {
        try {
            Path path = Paths.get(dirPath);
            if (!Files.exists(path)) {
                Files.createDirectories(path);
                LoggerUtil.info("Directory created: " + dirPath);
            }
        } catch (IOException e) {
            LoggerUtil.error("Failed to create directory: " + dirPath, e);
            throw new RuntimeException("Directory creation failed: " + dirPath, e);
        }
    }

    /**
     * Deletes a directory and all its contents.
     */
    public static void deleteDirectory(String dirPath) {
        try {
            Path path = Paths.get(dirPath);
            if (Files.exists(path)) {
                Files.walk(path)
                        .sorted((p1, p2) -> p2.compareTo(p1))
                        .forEach(p -> {
                            try {
                                Files.delete(p);
                            } catch (IOException e) {
                                LoggerUtil.error("Failed to delete: " + p, e);
                            }
                        });
                LoggerUtil.info("Directory deleted: " + dirPath);
            }
        } catch (IOException e) {
            LoggerUtil.error("Failed to delete directory: " + dirPath, e);
        }
    }

    /**
     * Checks if a file exists.
     */
    public static boolean fileExists(String filePath) {
        return Files.exists(Paths.get(filePath));
    }

    /**
     * Gets the absolute path of a file/directory.
     */
    public static String getAbsolutePath(String path) {
        return Paths.get(path).toAbsolutePath().toString();
    }
}
