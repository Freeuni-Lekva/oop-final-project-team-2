package com.moviemood.dao;

import com.moviemood.bean.Friendship;
import com.moviemood.bean.User;
import com.moviemood.bean.FriendSuggestion;
import org.apache.commons.dbcp2.BasicDataSource;

import java.sql.*;
import java.util.*;

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




    public List<User> getFriendsByUserId(int userId) {
        List<User> friends = new ArrayList<>();
        String query = "SELECT u.id, u.username, u.email, u.password_hash, u.remember_token, u.profile_picture " +
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
                    friend.setProfilePicture(rs.getString("profile_picture"));
                    friends.add(friend);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return friends;
    }

    public List<FriendSuggestion> getFriendSuggestions(int userId, int limit) {
        Map<Integer, Integer> mutualFriendCounts = new HashMap<>();
        
        List<User> currentUserFriends = getFriendsByUserId(userId);
        Set<Integer> currentFriendIds = new HashSet<>();
        for (User friend : currentUserFriends) {
            currentFriendIds.add(friend.getId());
        }
        
        for (User friend : currentUserFriends) {
            List<User> friendsOfFriend = getFriendsByUserId(friend.getId());
            for (User friendOfFriend : friendsOfFriend) {
                int suggestionId = friendOfFriend.getId();
                
                if (suggestionId == userId || currentFriendIds.contains(suggestionId)) {
                    continue;
                }
                
                Integer currentCount = mutualFriendCounts.get(suggestionId);
                if (currentCount == null) {
                    mutualFriendCounts.put(suggestionId, 1);
                } else {
                    mutualFriendCounts.put(suggestionId, currentCount + 1);
                }
            }
        }
        
        Set<Integer> excludedIds = getExcludedUserIds(userId);
        
        List<Map.Entry<Integer, Integer>> sortedEntries = new ArrayList<>();
        for (Map.Entry<Integer, Integer> entry : mutualFriendCounts.entrySet()) {
            if (!excludedIds.contains(entry.getKey())) {
                sortedEntries.add(entry);
            }
        }
        
        sortedEntries.sort(new Comparator<Map.Entry<Integer, Integer>>() {
            @Override
            public int compare(Map.Entry<Integer, Integer> e1, Map.Entry<Integer, Integer> e2) {
                return e2.getValue().compareTo(e1.getValue());
            }
        });
        
        List<FriendSuggestion> suggestions = new ArrayList<>();
        String getUserQuery = "SELECT id, username, profile_picture FROM users WHERE id = ?";
        
        int count = 0;
        for (Map.Entry<Integer, Integer> entry : sortedEntries) {
            if (count >= limit) break;
            
            try (Connection conn = dataSource.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(getUserQuery)) {
                
                stmt.setInt(1, entry.getKey());
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        User user = new User(
                                rs.getInt("id"),
                                rs.getString("username"),
                                null, null, null
                        );
                        user.setProfilePicture(rs.getString("profile_picture"));
                        FriendSuggestion suggestion = new FriendSuggestion(user, entry.getValue());
                        suggestions.add(suggestion);
                        count++;
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        
        return suggestions;
    }
    
    public Set<Integer> getExcludedUserIds(int userId) {
        Set<Integer> excludedIds = new HashSet<>();
        String query = 
            "SELECT receiver_id FROM friend_requests WHERE sender_id = ? AND status = 'pending' " +
            "UNION " +
            "SELECT sender_id FROM friend_requests WHERE receiver_id = ? AND status = 'pending'";
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, userId);
            stmt.setInt(2, userId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    excludedIds.add(rs.getInt(1));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return excludedIds;
    }
}


