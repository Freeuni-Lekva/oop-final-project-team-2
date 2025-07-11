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
                "    profile_picture VARCHAR(500),\n" +
                "    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP\n" +
                ");";

        executeSQL(dataSource, createUsersTableSQL);
    }


    /**
     * Creates the user_movie_preferences table in the test database.
     * Table structure matches the real production MySQL database.
     */
    public static void createUserMoviePreferencesTable(BasicDataSource dataSource) throws SQLException {
        String createPreferencesTableSQL =
                "CREATE TABLE IF NOT EXISTS user_movie_preferences (\n" +
                        "    user_id INT NOT NULL,\n" +
                        "    username VARCHAR(100) NOT NULL,\n" +
                        "    movie_id INT NOT NULL,\n" +
                        "    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,\n" +
                        "    PRIMARY KEY (user_id, movie_id),\n" +
                        "    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE\n" +
                        ");";

        executeSQL(dataSource, createPreferencesTableSQL);
    }

    /**
     * Creates the movie_reviews table in the test database.
     * Table structure matches the real production MySQL database.
     */
    public static void createMovieReviewsTable(BasicDataSource dataSource) throws SQLException {
        String createMovieReviewsTableSQL =
                "CREATE TABLE IF NOT EXISTS movie_reviews (" +
                        "    id INT PRIMARY KEY AUTO_INCREMENT, " +
                        "    user_id INT NOT NULL, " +
                        "    movie_id INT NOT NULL, " +
                        "    review_text TEXT NOT NULL, " +
                        "    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                        "    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE" +
                        ");";

        executeSQL(dataSource, createMovieReviewsTableSQL);
    }

    /**
     * Creates the friendships table in the test database.
     * Table structure matches the real production MySQL database.
     */
    public static void createFriendshipsTable(BasicDataSource dataSource) throws SQLException {
        String createFriendshipsTableSQL =
                "CREATE TABLE IF NOT EXISTS friendships (" +
                        "    id INT PRIMARY KEY AUTO_INCREMENT, " +
                        "    user1_id INT NOT NULL, " +
                        "    user2_id INT NOT NULL, " +
                        "    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                        "    FOREIGN KEY (user1_id) REFERENCES users(id) ON DELETE CASCADE, " +
                        "    FOREIGN KEY (user2_id) REFERENCES users(id) ON DELETE CASCADE, " +
                        "    UNIQUE (user1_id, user2_id)" +
                        ");";

        executeSQL(dataSource, createFriendshipsTableSQL);
    }

    /**
     * Creates the friend_requests table in the test database.
     * Table structure matches the real production MySQL database.
     */
    public static void createFriendRequestsTable(BasicDataSource dataSource) throws SQLException {
        String createFriendRequestsTableSQL =
                "CREATE TABLE IF NOT EXISTS friend_requests (" +
                        "    id INT PRIMARY KEY AUTO_INCREMENT, " +
                        "    sender_id INT NOT NULL, " +
                        "    receiver_id INT NOT NULL, " +
                        "    status VARCHAR(20) DEFAULT 'pending', " +
                        "    request_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                        "    FOREIGN KEY (sender_id) REFERENCES users(id) ON DELETE CASCADE, " +
                        "    FOREIGN KEY (receiver_id) REFERENCES users(id) ON DELETE CASCADE" +
                        ");";

        executeSQL(dataSource, createFriendRequestsTableSQL);
    }

    /**
     * Creates the user_favorites table in the test database.
     * Table structure matches the real production MySQL database.
     */
    public static void createUserFavoritesTable(BasicDataSource dataSource) throws SQLException {
        String createUserFavoritesTableSQL =
                "CREATE TABLE IF NOT EXISTS user_favorites (" +
                        "    id INT PRIMARY KEY AUTO_INCREMENT, " +
                        "    user_id INT NOT NULL, " +
                        "    movie_id INT NOT NULL, " +
                        "    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                        "    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE, " +
                        "    UNIQUE (user_id, movie_id)" +
                        ");";

        executeSQL(dataSource, createUserFavoritesTableSQL);
    }

    /**
     * Creates the user_watchlist table in the test database.
     * Table structure matches the real production MySQL database.
     */
    public static void createUserWatchlistTable(BasicDataSource dataSource) throws SQLException {
        String createUserWatchlistTableSQL =
                "CREATE TABLE IF NOT EXISTS user_watchlist (" +
                        "    id INT PRIMARY KEY AUTO_INCREMENT, " +
                        "    user_id INT NOT NULL, " +
                        "    movie_id INT NOT NULL, " +
                        "    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                        "    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE, " +
                        "    UNIQUE (user_id, movie_id)" +
                        ");";

        executeSQL(dataSource, createUserWatchlistTableSQL);
    }

    /**
     * Creates the user_lists table in the test database.
     * Table structure matches the real production MySQL database.
     */
    public static void createUserListsTable(BasicDataSource dataSource) throws SQLException {
        String createUserListsTableSQL =
                "CREATE TABLE IF NOT EXISTS user_lists (" +
                        "    id INT PRIMARY KEY AUTO_INCREMENT, " +
                        "    user_id INT NOT NULL, " +
                        "    name VARCHAR(255) NOT NULL, " +
                        "    description TEXT, " +
                        "    is_public BOOLEAN DEFAULT TRUE, " +
                        "    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                        "    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE" +
                        ");";

        executeSQL(dataSource, createUserListsTableSQL);
    }

    /**
     * Creates the user_list_items table in the test database.
     * Table structure matches the real production MySQL database.
     */
    public static void createUserListItemsTable(BasicDataSource dataSource) throws SQLException {
        String createUserListItemsTableSQL =
                "CREATE TABLE IF NOT EXISTS user_list_items (" +
                        "    id INT PRIMARY KEY AUTO_INCREMENT, " +
                        "    list_id INT NOT NULL, " +
                        "    movie_id INT NOT NULL, " +
                        "    added_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                        "    FOREIGN KEY (list_id) REFERENCES user_lists(id) ON DELETE CASCADE, " +
                        "    UNIQUE (list_id, movie_id)" +
                        ");";

        executeSQL(dataSource, createUserListItemsTableSQL);
    }

    /**
     * Creates the friends_activity_view in the test database.
     * View structure matches the real production MySQL database.
     */
    public static void createFriendsActivityView(BasicDataSource dataSource) throws SQLException {
        String createViewSQL =
                "CREATE OR REPLACE VIEW friends_activity_view AS " +
                "SELECT " +
                "    'added_to_favorites' as activity_type, " +
                "    u.id as user_id, " +
                "    u.username, " +
                "    u.profile_picture, " +
                "    f.movie_id, " +
                "    NULL as list_id, " +
                "    NULL as list_name, " +
                "    f.created_at as activity_time, " +
                "    NULL as additional_info " +
                "FROM user_favorites f " +
                "JOIN users u ON f.user_id = u.id " +
                "UNION ALL " +
                "SELECT " +
                "    'added_to_watchlist' as activity_type, " +
                "    u.id as user_id, " +
                "    u.username, " +
                "    u.profile_picture, " +
                "    w.movie_id, " +
                "    NULL as list_id, " +
                "    NULL as list_name, " +
                "    w.created_at as activity_time, " +
                "    NULL as additional_info " +
                "FROM user_watchlist w " +
                "JOIN users u ON w.user_id = u.id " +
                "UNION ALL " +
                "SELECT " +
                "    'reviewed' as activity_type, " +
                "    u.id as user_id, " +
                "    u.username, " +
                "    u.profile_picture, " +
                "    r.movie_id, " +
                "    NULL as list_id, " +
                "    NULL as list_name, " +
                "    r.created_at as activity_time, " +
                "    SUBSTRING(r.review_text, 1, 100) as additional_info " +
                "FROM movie_reviews r " +
                "JOIN users u ON r.user_id = u.id " +
                "UNION ALL " +
                "SELECT " +
                "    'created_list' as activity_type, " +
                "    u.id as user_id, " +
                "    u.username, " +
                "    u.profile_picture, " +
                "    NULL as movie_id, " +
                "    l.id as list_id, " +
                "    l.name as list_name, " +
                "    l.created_at as activity_time, " +
                "    l.description as additional_info " +
                "FROM user_lists l " +
                "JOIN users u ON l.user_id = u.id " +
                "UNION ALL " +
                "SELECT " +
                "    'added_to_list' as activity_type, " +
                "    u.id as user_id, " +
                "    u.username, " +
                "    u.profile_picture, " +
                "    li.movie_id, " +
                "    l.id as list_id, " +
                "    l.name as list_name, " +
                "    li.added_at as activity_time, " +
                "    NULL as additional_info " +
                "FROM user_list_items li " +
                "JOIN user_lists l ON li.list_id = l.id " +
                "JOIN users u ON l.user_id = u.id";

        executeSQL(dataSource, createViewSQL);
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
     * Drops all tables and views from the test database.
     */
    public static void dropAllTables(BasicDataSource dataSource) throws SQLException {
        try {
            executeSQL(dataSource, "DROP VIEW IF EXISTS friends_activity_view");
            executeSQL(dataSource, "DROP TABLE IF EXISTS user_list_items");
            executeSQL(dataSource, "DROP TABLE IF EXISTS user_lists");
            executeSQL(dataSource, "DROP TABLE IF EXISTS user_watchlist");
            executeSQL(dataSource, "DROP TABLE IF EXISTS user_favorites");
            executeSQL(dataSource, "DROP TABLE IF EXISTS friend_requests");
            executeSQL(dataSource, "DROP TABLE IF EXISTS friendships");
            executeSQL(dataSource, "DROP TABLE IF EXISTS movie_reviews");
            executeSQL(dataSource, "DROP TABLE IF EXISTS user_movie_preferences");
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


