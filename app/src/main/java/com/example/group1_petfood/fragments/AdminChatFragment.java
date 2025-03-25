package com.example.group1_petfood.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.group1_petfood.adapters.ChatAdapter;
import com.example.group1_petfood.adapters.UserChatAdapter;
import com.example.group1_petfood.controllers.ChatController;
import com.example.group1_petfood.databinding.FragmentAdminChatBinding;
import com.example.group1_petfood.models.ChatMessage;
import com.example.group1_petfood.models.User;

import java.util.List;

public class AdminChatFragment extends DialogFragment implements ChatController.ChatListener, UserChatAdapter.OnUserClickListener {

    private FragmentAdminChatBinding binding;
    private ChatAdapter chatAdapter;
    private UserChatAdapter userChatAdapter;
    private ChatController chatController;
    private int userId;
    private int selectedUserId; // Track the selected user for admin chat

    // SharedPreferences keys (matching LoginActivity)
    private static final String PREF_NAME = "user_pref";
    private static final String KEY_USER_ID = "user_id";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentAdminChatBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Add padding to account for status bar height
        int statusBarHeight = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            statusBarHeight = getResources().getDimensionPixelSize(resourceId);
        }
        binding.getRoot().setPadding(
                binding.getRoot().getPaddingLeft(),
                binding.getRoot().getPaddingTop() + statusBarHeight,
                binding.getRoot().getPaddingRight(),
                binding.getRoot().getPaddingBottom()
        );

        // Retrieve user data from SharedPreferences
        SharedPreferences sharedPreferences = getContext().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        userId = sharedPreferences.getInt(KEY_USER_ID, 1); // Default to 1 if not found

        // Initialize ChatController (isAdmin is true for admins)
        chatController = new ChatController(getContext(), userId, true, this);

        // Set up RecyclerView for chat messages
        chatAdapter = new ChatAdapter();
        binding.chatRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.chatRecyclerView.setAdapter(chatAdapter);

        // Set up RecyclerView for user selection
        userChatAdapter = new UserChatAdapter(this);
        binding.userRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.userRecyclerView.setAdapter(userChatAdapter);

        // Load list of users with messages
        List<User> users = chatController.getUsersWithMessages();
        userChatAdapter.setUserList(users);

        // Close button listener
        binding.closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chatController.cleanup();
                dismiss();
            }
        });

        // Send button listener
        binding.sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String messageText = binding.messageInput.getText().toString();
                if (!messageText.isEmpty()) {
                    // Admin sends a message to the selected user
                    chatController.addChatMessageForUser(selectedUserId, messageText);
                    binding.messageInput.setText("");
                }
            }
        });
    }

    private void loadMessages() {
        List<ChatMessage> messages = chatController.getMessagesForUser(selectedUserId);
        chatAdapter.clearMessages(); // Clear existing messages
        for (ChatMessage message : messages) {
            chatAdapter.addMessage(message);
        }
        if (!messages.isEmpty()) {
            binding.chatRecyclerView.scrollToPosition(chatAdapter.getItemCount() - 1);
        }
    }

    @Override
    public void onMessageReceived(ChatMessage message) {
        // Update the UI when a new message is received
        // Only add the message if it belongs to the currently viewed user
        if (message.getUserId() == selectedUserId) {
            chatAdapter.addMessage(message);
            binding.chatRecyclerView.scrollToPosition(chatAdapter.getItemCount() - 1);
        }
    }

    @Override
    public void onUserClick(User user) {
        // Admin selected a user to chat with
        selectedUserId = user.getId();
        binding.userRecyclerView.setVisibility(View.GONE);
        binding.chatContainer.setVisibility(View.VISIBLE);
        loadMessages();
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() != null && getDialog().getWindow() != null) {
            Window window = getDialog().getWindow();
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            window.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        chatController.cleanup();
        binding = null;
    }
}