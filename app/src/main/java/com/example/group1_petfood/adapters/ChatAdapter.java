package com.example.group1_petfood.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.group1_petfood.R;
import com.example.group1_petfood.models.ChatMessage;

import java.util.ArrayList;
import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder> {

    private List<ChatMessage> messages;

    public ChatAdapter() {
        messages = new ArrayList<>();
    }

    public void addMessage(ChatMessage message) {
        messages.add(message);
        notifyItemInserted(messages.size() - 1);
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_chat, parent, false);
        return new ChatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        holder.bind(messages.get(position));
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }
    public void clearMessages() {
        messages.clear();
        notifyDataSetChanged();
    }
    static class ChatViewHolder extends RecyclerView.ViewHolder {
        private LinearLayout userMessageContainer;
        private LinearLayout adminMessageContainer;
        private TextView userMessageText;
        private TextView userMessageTimestamp;
        private TextView adminMessageText;
        private TextView adminMessageTimestamp;

        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);
            userMessageContainer = itemView.findViewById(R.id.userMessageContainer);
            adminMessageContainer = itemView.findViewById(R.id.adminMessageContainer);
            userMessageText = itemView.findViewById(R.id.userMessageText);
            userMessageTimestamp = itemView.findViewById(R.id.userMessageTimestamp);
            adminMessageText = itemView.findViewById(R.id.adminMessageText);
            adminMessageTimestamp = itemView.findViewById(R.id.adminMessageTimestamp);
        }

        public void bind(ChatMessage message) {
            if (!message.isFromStore()) {
                // Show user message (right-aligned)
                userMessageContainer.setVisibility(View.VISIBLE);
                adminMessageContainer.setVisibility(View.GONE);
                userMessageText.setText(message.getMessage());
                userMessageTimestamp.setText(message.getCreatedAt());
            } else {
                // Show admin/store message (left-aligned)
                adminMessageContainer.setVisibility(View.VISIBLE);
                userMessageContainer.setVisibility(View.GONE);
                adminMessageText.setText(message.getMessage());
                adminMessageTimestamp.setText(message.getCreatedAt());
            }
        }
    }
}