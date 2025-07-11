package com.moviemood.dao;

import com.moviemood.bean.FriendActivity;
import org.apache.commons.dbcp2.BasicDataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class FriendActivityDao {
    private final BasicDataSource dataSource;

    public FriendActivityDao(BasicDataSource dataSource) {
        this.dataSource = dataSource;
    }

    /**
     * Get recent activities from all friends of a user using the friends_activity_view
     * @param userId The user whose friends' activities to retrieve
     * @param limit Maximum number of activities to return
     * @return List of friend activities
     */
    public List<FriendActivity> getFriendActivities(int userId, int limit) {
        List<FriendActivity> activities = new ArrayList<>();
        
        String query = 
            "SELECT fav.* FROM friends_activity_view fav " +
            "JOIN friendships f ON (" +
            "    (f.user1_id = ? AND f.user2_id = fav.user_id) OR " +
            "    (f.user2_id = ? AND f.user1_id = fav.user_id)" +
            ") " +
            "WHERE fav.user_id != ? " +
            "ORDER BY fav.activity_time DESC " +
            "LIMIT ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, userId); 
            stmt.setInt(2, userId); 
            stmt.setInt(3, userId); 
            stmt.setInt(4, limit);  
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    FriendActivity activity = new FriendActivity();
                    activity.setActivityType(rs.getString("activity_type"));
                    activity.setUserId(rs.getInt("user_id"));
                    activity.setUsername(rs.getString("username"));
                    activity.setProfilePicture(rs.getString("profile_picture"));
                    activity.setMovieId(rs.getInt("movie_id"));
                    activity.setListId(rs.getInt("list_id"));
                    activity.setListName(rs.getString("list_name"));
                    activity.setTimestamp(rs.getTimestamp("activity_time").toLocalDateTime());
                    activity.setAdditionalInfo(rs.getString("additional_info"));
                    
                    activities.add(activity);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to get friend activities", e);
        }
        
        return activities;
    }

} 