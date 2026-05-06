package utils;

/**
 * Database configuration holder - loads from config properties file.
 */
public final class DatabaseConfig {

    private String dbDriver;
    private String dbUrl;
    private String dbUsername;
    private String dbPassword;
    private int connectionTimeout;
    private int maxPoolSize;
    private int minPoolSize;

    private DatabaseConfig(Builder builder) {
        this.dbDriver = builder.dbDriver;
        this.dbUrl = builder.dbUrl;
        this.dbUsername = builder.dbUsername;
        this.dbPassword = builder.dbPassword;
        this.connectionTimeout = builder.connectionTimeout;
        this.maxPoolSize = builder.maxPoolSize;
        this.minPoolSize = builder.minPoolSize;
    }

    // Getters
    public String getDbDriver() {
        return dbDriver;
    }

    public String getDbUrl() {
        return dbUrl;
    }

    public String getDbUsername() {
        return dbUsername;
    }

    public String getDbPassword() {
        return dbPassword;
    }

    public int getConnectionTimeout() {
        return connectionTimeout;
    }

    public int getMaxPoolSize() {
        return maxPoolSize;
    }

    public int getMinPoolSize() {
        return minPoolSize;
    }

    /**
     * Builder pattern for DatabaseConfig
     */
    public static class Builder {
        private String dbDriver;
        private String dbUrl;
        private String dbUsername;
        private String dbPassword;
        private int connectionTimeout = 30000; // Default 30 seconds
        private int maxPoolSize = 10;
        private int minPoolSize = 5;

        public Builder dbDriver(String dbDriver) {
            this.dbDriver = dbDriver;
            return this;
        }

        public Builder dbUrl(String dbUrl) {
            this.dbUrl = dbUrl;
            return this;
        }

        public Builder dbUsername(String dbUsername) {
            this.dbUsername = dbUsername;
            return this;
        }

        public Builder dbPassword(String dbPassword) {
            this.dbPassword = dbPassword;
            return this;
        }

        public Builder connectionTimeout(int connectionTimeout) {
            this.connectionTimeout = connectionTimeout;
            return this;
        }

        public Builder maxPoolSize(int maxPoolSize) {
            this.maxPoolSize = maxPoolSize;
            return this;
        }

        public Builder minPoolSize(int minPoolSize) {
            this.minPoolSize = minPoolSize;
            return this;
        }

        public DatabaseConfig build() {
            if (dbUrl == null || dbUsername == null || dbPassword == null) {
                throw new IllegalArgumentException("Database URL, username, and password are required");
            }
            return new DatabaseConfig(this);
        }
    }

    /**
     * Load database config from properties file
     */
    public static DatabaseConfig loadFromConfig() {
        LoggerUtil.info("Loading database configuration from config properties");

        String dbDriver = ConfigLoader.get("db.driver", "com.mysql.cj.jdbc.Driver");
        String dbUrl = ConfigLoader.get("db.url");
        String dbUsername = ConfigLoader.get("db.username");
        String dbPassword = ConfigLoader.get("db.password");
        int connectionTimeout = ConfigLoader.getInt("db.connection.timeout");
        int maxPoolSize = ConfigLoader.getInt("db.pool.maxSize");
        int minPoolSize = ConfigLoader.getInt("db.pool.minSize");

        if (connectionTimeout == 0) connectionTimeout = 30000;
        if (maxPoolSize == 0) maxPoolSize = 10;
        if (minPoolSize == 0) minPoolSize = 5;

        return new DatabaseConfig.Builder()
                .dbDriver(dbDriver)
                .dbUrl(dbUrl)
                .dbUsername(dbUsername)
                .dbPassword(dbPassword)
                .connectionTimeout(connectionTimeout)
                .maxPoolSize(maxPoolSize)
                .minPoolSize(minPoolSize)
                .build();
    }
}