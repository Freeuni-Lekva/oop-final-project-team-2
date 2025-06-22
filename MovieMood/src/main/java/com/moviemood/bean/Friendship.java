package com.moviemood.bean;

import java.io.Serializable;
import java.sql.Timestamp;
import java.time.LocalDateTime;

public class Friendship implements Serializable {
    private static final long serialVersionUID = 1L;
    private int user1Id;
    private int user2Id;
    private LocalDateTime creation_time;

    public Friendship(int user1Id, int user2Id, LocalDateTime creation_time) {
        this.user1Id = user1Id;
        this.user2Id = user2Id;
        this.creation_time = creation_time;
    }

    public int getUser1Id() {
        return user1Id;
    }

    public void setUser1Id(int user1Id) {
        this.user1Id = user1Id;
    }

    public int getUser2Id() {
        return user2Id;
    }

    public void setUser2Id(int user2Id) {
        this.user2Id = user2Id;
    }

    public LocalDateTime getCreation_time() {
        return creation_time;
    }

    public void setCreation_time(Timestamp creation_time) {
        this.creation_time = creation_time.toLocalDateTime();
    }

    @Override
    public String toString() {
        return "Friendship{" + "user1_id=" + user1Id +  ", user2_id=" + user2Id + ", creation_time=" + creation_time + "}";
    }
}

