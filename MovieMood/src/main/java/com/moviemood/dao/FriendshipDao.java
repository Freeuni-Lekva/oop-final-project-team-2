package com.moviemood.dao;

import com.moviemood.bean.Friendship;
import com.moviemood.bean.User;
import org.apache.commons.dbcp2.BasicDataSource;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FriendshipDao {

    private final BasicDataSource dataSource;

    public FriendshipDao(BasicDataSource dataSource) {
        this.dataSource = dataSource;
    }

    /**
     *  Creates a new friendship in a database between two users
     * @param user1 ID of the first user
     * @param user2 ID of the second user
     */
    public void createFriendship(int user1, int user2) {

        int user1_id = Math.min(user1,user2);
        int user2_id = Math.max(user1,user2);

        String query = "INSERT INTO friendships (user1_id, user2_id) VALUES (?, ?)";
        try(Connection con = dataSource.getConnection();
            PreparedStatement stmt = con.prepareStatement(query)) {

            stmt.setInt(1, user1_id);
            stmt.setInt(2, user2_id);
            stmt.executeUpdate();

        }catch(SQLException e) {
            throw new RuntimeException("Failed to make connection to database", e);
        }


    }


    /**
     * Removes a friendship between two users from the table
     *
     * @param user1 ID of the first user
     * @param user2 ID of the second user
     */
    public void deleteFriendship(int user1, int user2) {
        int user1_id = Math.min(user1,user2);
        int user2_id = Math.max(user1,user2);

        String query = "DELETE FROM friendships WHERE user1_id = ? AND user2_id = ?";
        try(Connection con =  dataSource.getConnection();
        PreparedStatement stmt = con.prepareStatement(query)) {
            stmt.setInt(1, user1_id);
            stmt.setInt(2, user2_id);
            stmt.executeUpdate();

        }catch(SQLException e) {
            throw new RuntimeException("Failed to make connection to database", e);
        }
    }


    /**
     * returns all Friendships present in the table for the given user
     *
     * @param userId ID of the user
     * @return ArrayList of Friendships for given user
     */
    public List<Friendship> getFriendshipsForUser(int userId) {
        List<Friendship> friendships = new ArrayList<>();
        String query = "SELECT * FROM friendships where user1_id = ? OR user2_id = ?";

        try(Connection con = dataSource.getConnection();
        PreparedStatement stmt = con.prepareStatement(query)) {

            stmt.setInt(1, userId);
            stmt.setInt(2, userId);
            ResultSet rs = stmt.executeQuery();
            while(rs.next()) {
                friendships.add(new Friendship(rs.getInt(1), rs.getInt(2), rs.getTimestamp(3).toLocalDateTime()));
            }

        }catch(SQLException e) {
            throw new RuntimeException("Failed to make connection to database", e);
        }
        return friendships;
    }


    /**
     * Checks whether given friendship exists in the table
     *
     * @param friendship
     * @return returns true if Friendship exists in the table
     */
    public boolean friendshipExists(Friendship friendship) {
        String query = "SELECT 1 FROM friendships WHERE user1_id = ? AND user2_id = ?";
        try(Connection con = dataSource.getConnection();
        PreparedStatement stmt = con.prepareStatement(query)) {

            stmt.setInt(1, friendship.getUser1Id());
            stmt.setInt(2, friendship.getUser2Id());
            ResultSet rs = stmt.executeQuery();
            return rs.next();

        }catch(SQLException e) {
            throw new RuntimeException("Failed to make connection to database", e);
        }
    }

    public List<User> getFriendsByUserId(int userId) {
        List<User> friends = new ArrayList<>();
        String query = "SELECT u.id, u.username, u.email, u.password_hash, u.remember_token " +
                "FROM friendships f " +
                "JOIN users u ON (" +
                "    (u.id = f.user1_id AND f.user2_id = ?) OR " +
                "    (u.id = f.user2_id AND f.user1_id = ?))";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, userId);
            stmt.setInt(2, userId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    User friend = new User(
                            rs.getInt("id"),
                            rs.getString("username"),
                            rs.getString("email"),
                            rs.getString("password_hash"),
                            rs.getString("remember_token")
                    );
                    friends.add(friend);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return friends;
    }
}


