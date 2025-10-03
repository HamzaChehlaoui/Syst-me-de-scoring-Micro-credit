package utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


public final class DatabaseConnection {
    private static volatile DatabaseConnection instance;
    private final String url;
    private final String user;
    private final String password;

    private DatabaseConnection() {
        this.url = System.getProperty("db.url", "jdbc:mysql://localhost:3306/microfinance?useSSL=false&serverTimezone=UTC");
        this.user = System.getProperty("db.user", "root");
        this.password = System.getProperty("db.password", "");
    }

    public static DatabaseConnection getInstance() {
        if (instance == null) {
            synchronized (DatabaseConnection.class) {
                if (instance == null) {
                    instance = new DatabaseConnection();
                }
            }
        }
        return instance;
    }

    /**
     * Get a new Connection. Caller is responsible to close it.
     */
    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }
}
