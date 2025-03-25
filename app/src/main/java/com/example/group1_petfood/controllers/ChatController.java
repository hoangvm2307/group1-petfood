package com.example.group1_petfood.controllers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;

import com.example.group1_petfood.database.DatabaseHelper;
import com.example.group1_petfood.models.ChatMessage;
import com.example.group1_petfood.models.User;
import com.example.group1_petfood.models.UserRole;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class ChatController {
    private DatabaseHelper dbHelper;
    private int userId;
    private boolean isAdmin;
    private ChatListener chatListener;

    // Interface to notify the UI about new messages
    public interface ChatListener {
        void onMessageReceived(ChatMessage message);
    }

    public ChatController(Context context, int userId, boolean isAdmin, ChatListener listener) {
        this.dbHelper = new DatabaseHelper(context);
        this.userId = userId;
        this.isAdmin = isAdmin;
        this.chatListener = listener;
    }

    // Retrieve all chat messages for the current user (for customers)
    public List<ChatMessage> getAllChatMessages() {
        List<ChatMessage> messages = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM chat_messages WHERE user_id = ? ORDER BY created_at ASC",
                new String[]{String.valueOf(userId)});

        if (cursor.moveToFirst()) {
            do {
                ChatMessage message = new ChatMessage();
                message.setId(cursor.getInt(cursor.getColumnIndexOrThrow("id")));
                message.setUserId(cursor.getInt(cursor.getColumnIndexOrThrow("user_id")));
                message.setMessage(cursor.getString(cursor.getColumnIndexOrThrow("message")));
                message.setFromStore(cursor.getInt(cursor.getColumnIndexOrThrow("is_from_store")) == 1);
                message.setReadStatus(cursor.getInt(cursor.getColumnIndexOrThrow("read_status")) == 1);
                message.setCreatedAt(cursor.getString(cursor.getColumnIndexOrThrow("created_at")));
                messages.add(message);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return messages;
    }

    // Retrieve all chat messages for all users (for admins)
/*    public List<ChatMessage> getAllMessagesForAdmin() {
        List<ChatMessage> messages = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM chat_messages ORDER BY created_at ASC", null);

        if (cursor.moveToFirst()) {
            do {
                ChatMessage message = new ChatMessage();
                message.setId(cursor.getInt(cursor.getColumnIndexOrThrow("id")));
                message.setUserId(cursor.getInt(cursor.getColumnIndexOrThrow("user_id")));
                message.setMessage(cursor.getString(cursor.getColumnIndexOrThrow("message")));
                message.setFromStore(cursor.getInt(cursor.getColumnIndexOrThrow("is_from_store")) == 1);
                message.setReadStatus(cursor.getInt(cursor.getColumnIndexOrThrow("read_status")) == 1);
                message.setCreatedAt(cursor.getString(cursor.getColumnIndexOrThrow("created_at")));
                messages.add(message);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return messages;
    }*/

    // Retrieve all chat messages for a specific user (for admins to view a selected user's messages)
    public List<ChatMessage> getMessagesForUser(int selectedUserId) {
        List<ChatMessage> messages = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM chat_messages WHERE user_id = ? ORDER BY created_at ASC",
                new String[]{String.valueOf(selectedUserId)});

        if (cursor.moveToFirst()) {
            do {
                ChatMessage message = new ChatMessage();
                message.setId(cursor.getInt(cursor.getColumnIndexOrThrow("id")));
                message.setUserId(cursor.getInt(cursor.getColumnIndexOrThrow("user_id")));
                message.setMessage(cursor.getString(cursor.getColumnIndexOrThrow("message")));
                message.setFromStore(cursor.getInt(cursor.getColumnIndexOrThrow("is_from_store")) == 1);
                message.setReadStatus(cursor.getInt(cursor.getColumnIndexOrThrow("read_status")) == 1);
                message.setCreatedAt(cursor.getString(cursor.getColumnIndexOrThrow("created_at")));
                messages.add(message);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return messages;
    }

    // Retrieve a list of users who have sent messages (for admin user selection)
    public List<User> getUsersWithMessages() {
        List<User> users = new ArrayList<>();
        Set<Integer> userIds = new HashSet<>(); // To avoid duplicates
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        // First, get all unique user IDs from chat_messages
        Cursor cursor = db.rawQuery("SELECT DISTINCT user_id FROM chat_messages", null);
        if (cursor.moveToFirst()) {
            do {
                int userId = cursor.getInt(cursor.getColumnIndexOrThrow("user_id"));
                userIds.add(userId);
            } while (cursor.moveToNext());
        }
        cursor.close();

        // For each user ID, fetch user details from the users table
        for (int id : userIds) {
            Cursor userCursor = db.rawQuery("SELECT * FROM users WHERE id = ?",
                    new String[]{String.valueOf(id)});
            if (userCursor.moveToFirst()) {
                User user = new User();
                user.setId(userCursor.getInt(userCursor.getColumnIndexOrThrow("id")));
                user.setUsername(userCursor.getString(userCursor.getColumnIndexOrThrow("username")));
                user.setEmail(userCursor.getString(userCursor.getColumnIndexOrThrow("email")));
                user.setFullName(userCursor.getString(userCursor.getColumnIndexOrThrow("full_name")));
                user.setPhone(userCursor.getString(userCursor.getColumnIndexOrThrow("phone")));
                user.setAddress(userCursor.getString(userCursor.getColumnIndexOrThrow("address")));
                user.setRole(UserRole.valueOf(userCursor.getString(userCursor.getColumnIndexOrThrow("role"))));
                users.add(user);
            }
            userCursor.close();
        }
        db.close();
        return users;
    }

    // Add a new chat message
    public long addChatMessage(String messageText) {
        ChatMessage message = new ChatMessage();
        message.setUserId(userId);
        message.setMessage(messageText);
        message.setFromStore(isAdmin);
        message.setReadStatus(false);
        message.setCreatedAt(getCurrentTimestamp());

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("user_id", message.getUserId());
        values.put("message", message.getMessage());
        values.put("is_from_store", message.isFromStore() ? 1 : 0);
        values.put("read_status", message.isReadStatus() ? 1 : 0);
        values.put("created_at", message.getCreatedAt());

        long id = db.insert("chat_messages", null, values);
        db.close();

        if (id != -1) {
            message.setId((int) id);
            // Notify the listener about the new message
            if (chatListener != null) {
                chatListener.onMessageReceived(message);
            }
            // Simulate admin response if the sender is a user
            if (!isAdmin) {
                simulateAdminResponse();
            }
        }
        return id;
    }

    // Add a new chat message for a specific user (used by admins)
    public long addChatMessageForUser(int targetUserId, String messageText) {
        ChatMessage message = new ChatMessage();
        message.setUserId(targetUserId);
        message.setMessage(messageText);
        message.setFromStore(true); // Admin messages are always from the store
        message.setReadStatus(false);
        message.setCreatedAt(getCurrentTimestamp());

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("user_id", message.getUserId());
        values.put("message", message.getMessage());
        values.put("is_from_store", message.isFromStore() ? 1 : 0);
        values.put("read_status", message.isReadStatus() ? 1 : 0);
        values.put("created_at", message.getCreatedAt());

        long id = db.insert("chat_messages", null, values);
        db.close();

        if (id != -1) {
            message.setId((int) id);
            // Notify the listener about the new message
            if (chatListener != null) {
                chatListener.onMessageReceived(message);
            }
        }
        return id;
    }

    // Update a chat message (e.g., to mark it as read)
 /*   public int updateChatMessage(ChatMessage message) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("user_id", message.getUserId());
        values.put("message", message.getMessage());
        values.put("is_from_store", message.isFromStore() ? 1 : 0);
        values.put("read_status", message.isReadStatus() ? 1 : 0);
        values.put("created_at", message.getCreatedAt());

        int rowsAffected = db.update("chat_messages", values, "id = ?",
                new String[]{String.valueOf(message.getId())});
        db.close();
        return rowsAffected;
    }*/

    // Delete a chat message
/*
    public void deleteChatMessage(int messageId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete("chat_messages", "id = ?", new String[]{String.valueOf(messageId)});
        db.close();
    }
*/

    // Get the total number of chat messages for the user
/*
    public int getChatMessageCount() {
        List<ChatMessage> messages = getAllChatMessages();
        return messages.size();
    }
*/

    // Simulate an admin response
    private void simulateAdminResponse() {
        new Handler().postDelayed(() -> {
            ChatMessage adminMessage = new ChatMessage();
            adminMessage.setUserId(userId);
            adminMessage.setMessage("Thank you for your message! How can I assist you today?");
            adminMessage.setFromStore(true);
            adminMessage.setReadStatus(false);
            adminMessage.setCreatedAt(getCurrentTimestamp());

            SQLiteDatabase db = dbHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("user_id", adminMessage.getUserId());
            values.put("message", adminMessage.getMessage());
            values.put("is_from_store", adminMessage.isFromStore() ? 1 : 0);
            values.put("read_status", adminMessage.isReadStatus() ? 1 : 0);
            values.put("created_at", adminMessage.getCreatedAt());

            long id = db.insert("chat_messages", null, values);
            db.close();

            if (id != -1) {
                adminMessage.setId((int) id);
                // Notify the listener about the admin message
                if (chatListener != null) {
                    chatListener.onMessageReceived(adminMessage);
                }
            }
        }, 2000); // 2-second delay
    }

    // Get the current timestamp in the required format
    private String getCurrentTimestamp() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        return sdf.format(new Date());
    }

    // Clean up resources
    public void cleanup() {
        dbHelper.closeDB();
    }
}