package com.moviemood.bean;

/**
 * Represents a friend suggestion with the suggested user and number of mutual friends
 */
public class FriendSuggestion {
    private User user;
    private int mutualFriendCount;

    public FriendSuggestion(User user, int mutualFriendCount) {
        this.user = user;
        this.mutualFriendCount = mutualFriendCount;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public int getMutualFriendCount() {
        return mutualFriendCount;
    }

    public void setMutualFriendCount(int mutualFriendCount) {
        this.mutualFriendCount = mutualFriendCount;
    }
} 