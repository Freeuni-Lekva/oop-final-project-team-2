package com.moviemood.dao;

import com.moviemood.bean.FriendRequest;
import org.apache.commons.dbcp2.BasicDataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class FriendRequestDAO {
    private final BasicDataSource dataSource;

    public FriendRequestDAO(BasicDataSource dataSource) {
        this.dataSource = dataSource;
    }


    /**
     * Inserts new row into friend_requests table with given senderId and reciverId
     *
     * @param senderId
     * @param receiverId
     */
    public void sendRequest(int senderId, int receiverId) {
        String query = "INSERT INTO friend_requests (sender_id, receiver_id) VALUES (?, ?)";
        try (Connection con = dataSource.getConnection();
            PreparedStatement stmt = con.prepareStatement(query)){

            stmt.setInt(1, senderId);
            stmt.setInt(2, receiverId);
            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Failed to make connection to database", e);
        }
    }


    /**
     * Updates status of the pending request, chosen by requestId
     *
     * @param requestId - request to change
     * @param status - new status
     */
    public void updateRequestStatus(int requestId, String status) {
        String query = "UPDATE friend_requests SET status = ? WHERE id = ?";
        try(Connection con = dataSource.getConnection();
            PreparedStatement stmt = con.prepareStatement(query)) {

            stmt.setString(1, status);
            stmt.setInt(2, requestId);
            stmt.executeUpdate();

        }catch(SQLException e) {
            throw new RuntimeException("Failed to make connection to database", e);
        }
    }

    /**
     * deletes pending friend request from the table
     *
     * @param requestId - request to delete
     */

    public void deleteRequest(int requestId) {
        String query = "DELETE FROM friend_requests WHERE id = ?";
        try(Connection con = dataSource.getConnection();
            PreparedStatement stmt = con.prepareStatement(query)) {

            stmt.setInt(1, requestId);
            stmt.executeUpdate();

        }catch(SQLException e) {
            throw new RuntimeException("Failed to make connection to database", e);
        }
    }

    /**
     * Returns ArrayList of incoming friend requests for the given user.
     *
     * @param userId
     * @return ArrayList of FriendRequest Objects
     */
    public List<FriendRequest> getIncomingRequests(int userId) {
        List<FriendRequest> requests = new ArrayList<>();
        String query = "SELECT * FROM friend_requests WHERE receiver_id = ?";
        try(Connection con = dataSource.getConnection();
            PreparedStatement stmt = con.prepareStatement(query)) {

            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            while(rs.next()) {
                requests.add(new FriendRequest(rs.getInt(1), rs.getInt(2), rs.getInt(3), rs.getString(4), rs.getTimestamp(5).toLocalDateTime()));
            }

        }catch(SQLException e) {
            throw new RuntimeException("Failed to make connection to database", e);
        }
        return requests;
    }


    /**
     * Returns ArrayList of sent friend requests for the given user
     *
     * @param userId
     * @return ArrayList of FriendRequest objects
     */
    public List<FriendRequest> getSentRequests(int userId) {
        List<FriendRequest> requests = new ArrayList<>();
        String query = "SELECT * FROM friend_requests WHERE sender_id = ?";
        try(Connection con = dataSource.getConnection();
            PreparedStatement stmt = con.prepareStatement(query)) {

            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            while(rs.next()) {
                requests.add(new FriendRequest(rs.getInt(1), rs.getInt(2), rs.getInt(3), rs.getString(4), rs.getTimestamp(5).toLocalDateTime()));
            }

        }catch(SQLException e) {
            throw new RuntimeException("Failed to make connection to database", e);
        }
        return requests;
    }


    /**
     * Checks whether request with given senderId and receiverId exists in the table
     *
     * @param senderId
     * @param receiverId
     * @return true if row exists, false otherwise
     */
    public boolean requestExists(int senderId, int receiverId) {
        String query = "SELECT * FROM friend_requests WHERE sender_id = ?  AND receiver_id = ?";
        try(Connection con = dataSource.getConnection();
            PreparedStatement stmt = con.prepareStatement(query)) {

            stmt.setInt(1, senderId);
            stmt.setInt(2, receiverId);
            ResultSet rs = stmt.executeQuery();
            return rs.next();

        }catch(SQLException e) {
            throw new RuntimeException("Failed to make connection to database", e);
        }
    }


    // might have to implement later
//    public FriendRequest getRequest(int requestId) {
//        return null;
//    }


}
