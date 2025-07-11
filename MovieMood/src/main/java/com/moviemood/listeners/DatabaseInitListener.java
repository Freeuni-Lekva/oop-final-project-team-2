package com.moviemood.listeners;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import com.moviemood.config.Config;
import com.moviemood.dao.FriendRequestDao;
import com.moviemood.dao.FriendshipDao;
import com.moviemood.dao.MovieReviewsDao;
import com.moviemood.dao.UserDao;
import com.moviemood.dao.UserFavoritesDao;
import com.moviemood.dao.UserListDao;
import com.moviemood.dao.UserWatchlistDao;
import org.apache.commons.dbcp2.BasicDataSource;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

@WebListener
public class DatabaseInitListener implements ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        try {
            BasicDataSource dataSource = new BasicDataSource();
            dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
            dataSource.setUrl(Config.get("jdbc.url"));
            dataSource.setUsername(Config.get("jdbc.username"));
            dataSource.setPassword(Config.get("jdbc.password"));

            setUpDatabase(dataSource);

            UserDao userDao = new UserDao(dataSource);
            FriendRequestDao friendRequestDAO = new FriendRequestDao(dataSource);
            FriendshipDao friendshipDAO = new FriendshipDao(dataSource);
            UserWatchlistDao watchlistDao = new UserWatchlistDao(dataSource);
            MovieReviewsDao reviewsDao = new MovieReviewsDao(dataSource);
            UserFavoritesDao favoritesDao = new UserFavoritesDao(dataSource);
            UserListDao listsDao = new UserListDao(dataSource);
            
            servletContextEvent.getServletContext().setAttribute("userDao", userDao);
            servletContextEvent.getServletContext().setAttribute("friendRequestDao", friendRequestDAO);
            servletContextEvent.getServletContext().setAttribute("friendshipDao", friendshipDAO);
            servletContextEvent.getServletContext().setAttribute("watchlistDao", watchlistDao);
            servletContextEvent.getServletContext().setAttribute("reviewsDao", reviewsDao);
            servletContextEvent.getServletContext().setAttribute("favoritesDao", favoritesDao);
            servletContextEvent.getServletContext().setAttribute("listsDao", listsDao);
            servletContextEvent.getServletContext().setAttribute("dataSource", dataSource);
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize database", e);
        }

    }

    // create "users" table
    private void createUserTable(Statement statement) throws SQLException {
        statement.executeUpdate("\n" +
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
                ");");
    }

    // create "user_movie_preferences" table
    private void createUserMoviePreferencesTable(Statement statement) throws SQLException {
        statement.executeUpdate("\n" +
                "CREATE TABLE IF NOT EXISTS user_movie_preferences (\n" +
                "    user_id INT NOT NULL,\n" +
                "    username VARCHAR(100) NOT NULL,\n" +
                "    movie_id INT NOT NULL,\n" +
                "    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,\n" +
                "    PRIMARY KEY (user_id, movie_id),\n" +
                "    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE\n" +
                ");");
    }

    // create friend_requests table
    private void createFriendRequestTable(Statement statement) throws SQLException {
        statement.executeUpdate(
                "CREATE TABLE IF NOT EXISTS friend_requests (" +
                        "id INT PRIMARY KEY AUTO_INCREMENT, " +
                        "sender_id INT NOT NULL, " +
                        "receiver_id INT NOT NULL, " +
                        "status ENUM('pending', 'accepted', 'rejected', 'cancelled') NOT NULL DEFAULT 'pending', " +
                        "request_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                        "FOREIGN KEY (sender_id) REFERENCES users(id)  ON DELETE CASCADE, " +
                        "FOREIGN KEY (receiver_id) REFERENCES users(id)  ON DELETE CASCADE" +
                        ");"
        );
    }

    // create friendships table
    private void createFriendshipsTable(Statement statement) throws SQLException {
        statement.executeUpdate(
                " CREATE TABLE IF NOT EXISTS friendships (" +
                        " user1_id INT NOT NULL, " +
                        " user2_id INT NOT NULL, " +
                        " creation_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                        " FOREIGN KEY (user1_id) REFERENCES users(id) ON DELETE CASCADE, " +
                        " FOREIGN KEY (user2_id) REFERENCES users(id) ON DELETE CASCADE, " +
                        " PRIMARY KEY (user1_id, user2_id), " +
                        " CHECK (user1_id < user2_id)" +
                        ");"
        );
    }

    // creating userWatchListTable
    private void createUserWatchlistTable(Statement statement) throws SQLException {
        statement.executeUpdate(
                "CREATE TABLE IF NOT EXISTS user_watchlist (" +
                        "    user_id INT NOT NULL," +
                        "    movie_id INT NOT NULL," +
                        "    added_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                        "    PRIMARY KEY (user_id, movie_id)," +
                        "    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE" +
                        ");"
        );
    }

    //create movie_reviews table
    private void CreateMovieReviewsTable(Statement statement) throws SQLException {
        statement.executeUpdate(
                "CREATE TABLE IF NOT EXISTS movie_reviews (" +
                        "    id INT PRIMARY KEY AUTO_INCREMENT, " +
                        "    user_id INT NOT NULL, " +
                        "    movie_id INT NOT NULL, " +  // TMDB movie ID
                        "    review_text TEXT NOT NULL, " +
                        "    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                        "    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE" +
                        ");"
        );
    }


    //create movie_ratings table
    private void createMovieRatingsTable(Statement statement) throws SQLException {
        statement.executeUpdate(
                "CREATE TABLE IF NOT EXISTS movie_ratings (" +
                        "movie_id INT NOT NULL, " +
                        "user_id INT NOT NULL, " +
                        "score_value DOUBLE NOT NULL, " +
                        "score_date DATE, " +
                        "PRIMARY KEY (user_id, movie_id), " +
                        "FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE" +
                        ");"
        );
    }

    // create user_favorites table
    private void createUserFavoritesTable(Statement statement) throws SQLException {
        statement.executeUpdate(
                "CREATE TABLE IF NOT EXISTS user_favorites (" +
                        "    user_id INT NOT NULL," +
                        "    movie_id INT NOT NULL," +
                        "    added_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                        "    PRIMARY KEY (user_id, movie_id)," +
                        "    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE" +
                        ");"
        );
    }

    // create user_lists table (for custom movie lists)
    private void createUserListsTable(Statement statement) throws SQLException {
        statement.executeUpdate(
                "CREATE TABLE IF NOT EXISTS user_lists (" +
                        "    id INT PRIMARY KEY AUTO_INCREMENT," +
                        "    user_id INT NOT NULL," +
                        "    name VARCHAR(255) NOT NULL," +
                        "    description TEXT," +
                        "    is_public BOOLEAN DEFAULT TRUE," +
                        "    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                        "    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP," +
                        "    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE" +
                        ");"
        );
    }

    // create user_list_items table (movies in custom lists)
    private void createUserListItemsTable(Statement statement) throws SQLException {
        statement.executeUpdate(
                "CREATE TABLE IF NOT EXISTS user_list_items (" +
                        "    list_id INT NOT NULL," +
                        "    movie_id INT NOT NULL," +
                        "    added_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                        "    PRIMARY KEY (list_id, movie_id)," +
                        "    FOREIGN KEY (list_id) REFERENCES user_lists(id) ON DELETE CASCADE" +
                        ");"
        );
    }


    private void setUpDatabase(BasicDataSource dataSource) {
        try (Connection connection = dataSource.getConnection();
            Statement statement = connection.createStatement()) {

            createUserTable(statement);
            // Add profile_picture column if it doesn't exist (for existing databases)
            addProfilePictureColumnIfNotExists(statement);
            createUserMoviePreferencesTable(statement);
            createFriendRequestTable(statement);
            createFriendshipsTable(statement);
            createUserWatchlistTable(statement);
            CreateMovieReviewsTable(statement);
            createMovieRatingsTable(statement);
            createUserFavoritesTable(statement);
            createUserListsTable(statement);
            createUserListItemsTable(statement);

        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize database", e);
        }
    }

    // Add profile_picture column to existing users table if it doesn't exist
    private void addProfilePictureColumnIfNotExists(Statement statement) throws SQLException {
        try {
            // Try to add the column - this will fail silently if column already exists
            statement.executeUpdate("ALTER TABLE users ADD COLUMN profile_picture VARCHAR(500)");
        } catch (SQLException e) {
            // Column likely already exists, which is fine
            // Only re-throw if it's not a "column already exists" error
            if (!e.getMessage().toLowerCase().contains("duplicate column") && 
                !e.getMessage().toLowerCase().contains("already exists")) {
                throw e;
            }
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        BasicDataSource dataSource = (BasicDataSource) servletContextEvent.getServletContext().getAttribute("dataSource");
        try {
            dataSource.close();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to close datasource", e);
        }
    }
}
