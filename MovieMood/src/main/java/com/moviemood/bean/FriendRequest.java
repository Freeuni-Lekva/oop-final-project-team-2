package com.moviemood.bean;

import java.io.Serializable;
import java.time.LocalDateTime;

public class FriendRequest implements Serializable {
    private static final long serialVersionUID = 1L;
    private int requestId;
    private int senderId;
    private int receiverId;
    private String status;
    private LocalDateTime requestTime;
    private String senderUsername;
    private String receiverUsername;

    public FriendRequest() {

    }

    public FriendRequest(int requestId, int senderId, int receiverId, String status, LocalDateTime requestTime) {
        this.requestId = requestId;
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.status = status;
        this.requestTime = requestTime;
    }

    public int getRequestId() {
        return requestId;
    }

    public void setRequestId(int requestId) {
        this.requestId = requestId;
    }

    public int getSenderId() {
        return senderId;
    }

    public void setSenderId(int senderId) {
        this.senderId = senderId;
    }

    public int getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(int receiverId) {
        this.receiverId = receiverId;
    }

    public String getSenderUsername() {
        return senderUsername;
    }

    public void setSenderUsername(String senderUsername) {
        this.senderUsername = senderUsername;
    }

    public String getReceiverUsername() {
        return receiverUsername;
    }

    public void setReceiverUsername(String receiverUsername) {
        this.receiverUsername = receiverUsername;
    }


    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getRequestTime() {
        return requestTime;
    }

    public void setRequestTime(LocalDateTime requestTime) {
        this.requestTime = requestTime;
    }

    @Override
    public String toString() {
        return "FriendRequest{" + "id=" + requestId + ", senderId=" + senderId + ", status=" + status + ", requestTime=" + requestTime + '}';
    }




}
