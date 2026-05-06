package utils;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import exceptions.DatabaseException;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Manages database connections using HikariCP connection pooling.
 * Singleton pattern ensures one pool per application lifecycle.
 */
public final class DatabaseConnectionManager {

    private static volatile DatabaseConnectionManager instance;
    private static final Object LOCK = new Object();
    private HikariDataSource dataSource;
    private DatabaseConfig databaseConfig;

    private DatabaseConnectionManager() {
    }

    /**
     * Singleton pattern - get instance of DatabaseConnectionManager
     */
    public static DatabaseConnectionManager getInstance() {
        if (instance == null) {
            synchronized (LOCK) {
                if (instance == null) {
                    instance = new DatabaseConnectionManager();
                }
            }
        }
        return instance;
    }

    /**
     * Initialize connection pool with database config
     */
    public void initialize(DatabaseConfig config) {
        synchronized (LOCK) {
            if (dataSource != null && !dataSource.isClosed()) {
              //  LoggerUtil.warn("Connection pool already initialized. Skipping re-initialization.", e);
                return;
            }

            try {
                this.databaseConfig = config;

                // Validate configuration
                if (config.getDbUrl() == null || config.getDbUrl().isEmpty()) {
                    throw new DatabaseException("Database URL is not configured. Please add 'db.url' to config properties");
                }

                LoggerUtil.info("Initializing database connection pool");
                LoggerUtil.debug("Database URL: " + config.getDbUrl());

                // Configure HikariCP
                HikariConfig hikariConfig = new HikariConfig();
                hikariConfig.setJdbcUrl(config.getDbUrl());
                hikariConfig.setUsername(config.getDbUsername());
                hikariConfig.setPassword(config.getDbPassword());
                hikariConfig.setMaximumPoolSize(config.getMaxPoolSize());
                hikariConfig.setMinimumIdle(config.getMinPoolSize());
                hikariConfig.setConnectionTimeout(config.getConnectionTimeout());
                hikariConfig.setIdleTimeout(600000); // 10 minutes
                hikariConfig.setMaxLifetime(1800000); // 30 minutes
                hikariConfig.setAutoCommit(true);
                hikariConfig.setPoolName("VancityAPITestPool");
                hikariConfig.setLeakDetectionThreshold(60000); // 1 minute

                // Create data source
                dataSource = new HikariDataSource(hikariConfig);

                LoggerUtil.info("Database connection pool initialized successfully");
                LoggerUtil.info("Pool size - Min: " + config.getMinPoolSize() + ", Max: " + config.getMaxPoolSize());

            } catch (Exception e) {
                LoggerUtil.error("Failed to initialize database connection pool", e);
                throw new DatabaseException("Database initialization failed", e);
            }
        }
    }

    /**
     * Initialize with default config from properties file
     */
    public void initializeFromConfig() {
        try {
            DatabaseConfig config = DatabaseConfig.loadFromConfig();
            initialize(config);
        } catch (DatabaseException e) {
            throw e;
        } catch (Exception e) {
            LoggerUtil.error("Failed to load database configuration", e);
            throw new DatabaseException("Failed to load database configuration from properties", e);
        }
    }

    /**
     * Get a connection from the pool
     */
    public Connection getConnection() {
        try {
            if (dataSource == null || dataSource.isClosed()) {
                throw new DatabaseException("Connection pool is not initialized. Call initialize() first.");
            }

            Connection connection = dataSource.getConnection();
            LoggerUtil.debug("Connection obtained from pool. Active connections: " + dataSource.getHikariPoolMXBean().getActiveConnections());
            return connection;

        } catch (SQLException e) {
            LoggerUtil.error("Failed to obtain database connection", e);
            throw new DatabaseException("Unable to get database connection", e);
        }
    }

    /**
     * Close a connection (returns it to pool)
     */
    public void closeConnection(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
                LoggerUtil.debug("Connection returned to pool");
            } catch (SQLException e) {
                LoggerUtil.warn("Error closing connection", e);
            }
        }
    }

    /**
     * Close all connections and shutdown pool
     */
    public void closePool() {
        synchronized (LOCK) {
            if (dataSource != null && !dataSource.isClosed()) {
                try {
                    dataSource.close();
                    LoggerUtil.info("Database connection pool closed successfully");
                } catch (Exception e) {
                    LoggerUtil.error("Error closing connection pool", e);
                }
            }
        }
    }

    /**
     * Get pool statistics
     */
    public void printPoolStats() {
        if (dataSource != null && !dataSource.isClosed()) {
            LoggerUtil.info("===== Database Connection Pool Statistics =====");
            LoggerUtil.info("Active Connections: " + dataSource.getHikariPoolMXBean().getActiveConnections());
            LoggerUtil.info("Idle Connections: " + dataSource.getHikariPoolMXBean().getIdleConnections());
            LoggerUtil.info("Total Connections: " + dataSource.getHikariPoolMXBean().getTotalConnections());
            LoggerUtil.info("Threads Waiting: " + dataSource.getHikariPoolMXBean().getThreadsAwaitingConnection());
            LoggerUtil.info("===============================================");
        }
    }

    /**
     * Check if connection pool is initialized
     */
    public boolean isInitialized() {
        return dataSource != null && !dataSource.isClosed();
    }
}