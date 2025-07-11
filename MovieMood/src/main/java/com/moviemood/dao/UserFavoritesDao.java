package com.moviemood.dao;

import org.apache.commons.dbcp2.BasicDataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UserFavoritesDao {

    private final BasicDataSource dataSource;

    public UserFavoritesDao(BasicDataSource dataSource) {
        this.dataSource = dataSource;
    }

    /**
     * Get all movie IDs in a user's favorites
     * @param userId The user ID
     * @return List of movie IDs in the user's favorites
     */
    public List<Integer> getUserFavorites(int userId) {
        List<Integer> movieIds = new ArrayList<>();
        String sql = "SELECT movie_id FROM user_favorites WHERE user_id = ? ORDER BY added_date DESC";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, userId);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    movieIds.add(resultSet.getInt("movie_id"));
                }
            }

        } catch (SQLException e) {
            System.err.println("Error retrieving user favorites for user ID: " + userId);
            e.printStackTrace();
            throw new RuntimeException("Failed to retrieve user favorites", e);
        }

        return movieIds;
    }

    /**
     * Add a movie to user's favorites
     * @param userId The user ID
     * @param movieId The movie ID to add
     * @return true if successfully added, false if already exists or error occurred
     */
    public boolean addMovieToFavorites(int userId, int movieId) {
        String sql = "INSERT INTO user_favorites (user_id, movie_id) VALUES (?, ?)";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, userId);
            statement.setInt(2, movieId);

            int rowsAffected = statement.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            // Check if it's a duplicate key error (movie already in favorites)
            if (e.getErrorCode() == 1062 || e.getMessage().contains("Duplicate entry")) {
                System.out.println("Movie " + movieId + " is already in user " + userId + "'s favorites");
                return false;
            }

            System.err.println("Error adding movie to favorites. User ID: " + userId + ", Movie ID: " + movieId);
            e.printStackTrace();
            throw new RuntimeException("Failed to add movie to favorites", e);
        }
    }

    /**
     * Remove a movie from user's favorites
     * @param userId The user ID
     * @param movieId The movie ID to remove
     * @return true if successfully removed, false if not found or error occurred
     */
    public boolean removeMovieFromFavorites(int userId, int movieId) {
        String sql = "DELETE FROM user_favorites WHERE user_id = ? AND movie_id = ?";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, userId);
            statement.setInt(2, movieId);

            int rowsAffected = statement.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("Error removing movie from favorites. User ID: " + userId + ", Movie ID: " + movieId);
            e.printStackTrace();
            throw new RuntimeException("Failed to remove movie from favorites", e);
        }
    }

    /**
     * Check if a movie is in user's favorites
     * @param userId The user ID
     * @param movieId The movie ID to check
     * @return true if movie is in favorites, false otherwise
     */
    public boolean isMovieInFavorites(int userId, int movieId) {
        String sql = "SELECT 1 FROM user_favorites WHERE user_id = ? AND movie_id = ?";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, userId);
            statement.setInt(2, movieId);

            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next();
            }

        } catch (SQLException e) {
            System.err.println("Error checking if movie is in favorites. User ID: " + userId + ", Movie ID: " + movieId);
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Get the count of movies in user's favorites
     * @param userId The user ID
     * @return Number of movies in the favorites
     */
    public int getFavoritesCount(int userId) {
        String sql = "SELECT COUNT(*) FROM user_favorites WHERE user_id = ?";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, userId);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt(1);
                }
            }

        } catch (SQLException e) {
            System.err.println("Error getting favorites count for user ID: " + userId);
            e.printStackTrace();
        }

        return 0;
    }


} 