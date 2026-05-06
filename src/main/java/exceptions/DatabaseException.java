package exceptions;

/**
 * Exception for database-related errors.
 */
public class DatabaseException extends APIAutomationException {

    private String query;
    private String tableName;

    public DatabaseException(String message) {
        super("DATABASE_ERROR", message);
    }

    public DatabaseException(String message, String query) {
        super("DATABASE_ERROR", message);
        this.query = query;
    }

    public DatabaseException(String message, Throwable cause) {
        super("DATABASE_ERROR", message, cause);
    }

    public DatabaseException(String message, String query, Throwable cause) {
        super("DATABASE_ERROR", message, cause);
        this.query = query;
    }

    public String getQuery() {
        return query;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getTableName() {
        return tableName;
    }
}