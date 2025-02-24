package com.example.group1_petfood.models;

public class ChatMessage {
    private int id;
    private int userId;
    private String message;
    private boolean isFromStore;
    private boolean readStatus;
    private String createdAt;

    // Constructor
    public ChatMessage() {}

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public boolean isFromStore() { return isFromStore; }
    public void setFromStore(boolean fromStore) { isFromStore = fromStore; }

    public boolean isReadStatus() { return readStatus; }
    public void setReadStatus(boolean readStatus) { this.readStatus = readStatus; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
}
