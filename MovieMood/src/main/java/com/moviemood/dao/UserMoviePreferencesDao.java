package com.moviemood.dao;


import org.apache.commons.dbcp2.BasicDataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UserMoviePreferencesDao {

    private BasicDataSource dataSource;

    public UserMoviePreferencesDao(BasicDataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void saveUserPreference(int userId, String username, int movieId) throws SQLException {
        String sql = "MERGE INTO user_movie_preferences (user_id, username, movie_id) VALUES (?, ?, ?)";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            stmt.setString(2, username);
            stmt.setInt(3, movieId);
            stmt.executeUpdate();
        }
    }

    public List<Integer> getUserPreferences(int userId) throws SQLException {
        List<Integer> movieIds = new ArrayList<>();
        String sql = "SELECT movie_id FROM user_movie_preferences WHERE user_id = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                movieIds.add(rs.getInt("movie_id"));
            }
        }
        return movieIds;
    }

    public void deleteUserPreference(int userId, int movieId) throws SQLException {
        String sql = "DELETE FROM user_movie_preferences WHERE user_id = ? AND movie_id = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            stmt.setInt(2, movieId);
            stmt.executeUpdate();
        }
    }


}