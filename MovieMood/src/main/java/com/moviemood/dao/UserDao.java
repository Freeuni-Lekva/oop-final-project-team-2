package com.moviemood.dao;

import org.apache.commons.dbcp2.BasicDataSource;

import com.moviemood.bean.User;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * This is DAO for the User entity.
 * Handles all database operations related to users, including:
 * - Retrieving a user by email
 * - Inserting a new user
 * This class assumes that the "users" table exists in the connected MySQL database.
 */

public class UserDao {
    private final BasicDataSource dataSource;

    public UserDao(BasicDataSource dataSource) { this.dataSource = dataSource; }

    /**
     * Retrieves a user from the database by email.
     * takes email as a parameter.
     * returns User if exists or null if does not exist.
     */
    public User getUserByEmail(String email) {

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM users WHERE email = ?")) {
            statement.setString(1, email);
            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                int id = rs.getInt("id");
                String username = rs.getString("username");
                String passwordHash = rs.getString("password_hash");
                String remember_token = rs.getString("remember_token");
                return new User(id, username, email, passwordHash, remember_token);
            } else {
                return null;
            }

        } catch (SQLException e) {
            throw new RuntimeException("Failed to make connection to database", e);
        }
    }


    /**
     * Inserts a user in the database.
     * takes username, email and password (hashed) as parameters
     */

    public void insertUser(String username, String email, String passwordHash) {
        if (getUserByEmail(email) != null) {
            throw new IllegalArgumentException("User with this email is already registered");
        }


        String query = "INSERT INTO users (username, email, password_hash, remember_token) VALUES (?, ?, ?, ?)";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, username);
            statement.setString(2, email);
            statement.setString(3, passwordHash);
            statement.setNull(4, java.sql.Types.VARCHAR); // No token

            statement.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Failed to make connection to database", e);
        }


    }


}
