package com.moviemood.util;


import org.apache.commons.dbcp2.BasicDataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Utility class for setting up H2 in-memory database for testing.
 */
public class TestDatabaseHelper {

    /**
     * Creates a BasicDataSource configured for H2 in-memory database.
     */
    public static BasicDataSource createTestDataSource() {
        BasicDataSource dataSource = new BasicDataSource();

        dataSource.setUrl("jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1");
        dataSource.setUsername("sa");
        dataSource.setPassword("");
        dataSource.setDriverClassName("org.h2.Driver");
        dataSource.setInitialSize(1);
        dataSource.setMaxTotal(5);
        dataSource.setMaxIdle(2);

        return dataSource;
    }

    /**
     * Creates the users table in the test database.
     * Table structure matches the real production MySQL database.
     */
    public static void createUsersTable(BasicDataSource dataSource) throws SQLException {
        String createUsersTableSQL =
        "CREATE TABLE IF NOT EXISTS users (\n" +
                "    id INT PRIMARY KEY AUTO_INCREMENT,\n" +
                "    username VARCHAR(100) UNIQUE NOT NULL,\n" +
                "    email VARCHAR(255) UNIQUE NOT NULL,\n" +
                "    password_hash VARCHAR(255) NOT NULL,\n" +
                "    remember_token VARCHAR(255),\n" +
                "    is_verified BOOLEAN DEFAULT FALSE,\n" +
                "    verification_code VARCHAR(10),\n" +
                "    verification_code_expiry TIMESTAMP,\n" +
                "    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP\n" +
                ");";

        executeSQL(dataSource, createUsersTableSQL);
    }


    /**
     * Inserts sample test data into users table.
     */
    public static void insertSampleUsers(BasicDataSource dataSource) throws SQLException {
        String[] insertStatements = {
                "INSERT INTO users (username, email, password_hash, is_verified) VALUES ('lela', 'lela@gmail.com', 'hashedpassword123', true)",
                "INSERT INTO users (username, email, password_hash, is_verified) VALUES ('joni', 'joni@yahoo.com', 'hashedpassword456', false)",
                "INSERT INTO users (username, email, password_hash, is_verified, verification_code) VALUES ('unverified_user', 'unverified@gmail.com', 'hashedpassword789', false, '123456')"
        };

        for (String str : insertStatements) {
            executeSQL(dataSource, str);
        }
    }

    /**
     * Clears all data from users table.
     */
    public static void clearUsersTable(BasicDataSource dataSource) throws SQLException {
        executeSQL(dataSource, "DELETE FROM users");
        executeSQL(dataSource, "ALTER TABLE users ALTER COLUMN id RESTART WITH 1");
    }

    /**
     * Drops all tables from the test database.
     */
    public static void dropAllTables(BasicDataSource dataSource) throws SQLException {
        try {
            executeSQL(dataSource, "DROP TABLE IF EXISTS users");
        } catch (SQLException e) {
        }
    }

    /**
     * Executes a SQL statement on the test database.
     */
    private static void executeSQL(BasicDataSource dataSource, String sql) throws SQLException {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.executeUpdate();
        }
    }


    /**
     * Validates that the test database is properly configured.
     */
    public static void validateTestDatabase(BasicDataSource dataSource) throws SQLException {
        try (Connection conn = dataSource.getConnection()) {
            if (conn == null) {
                throw new SQLException("Failed to get connection to test database");
            }

            try (PreparedStatement stmt = conn.prepareStatement("SELECT 1")) {
                stmt.executeQuery();
            }
        }
    }
}