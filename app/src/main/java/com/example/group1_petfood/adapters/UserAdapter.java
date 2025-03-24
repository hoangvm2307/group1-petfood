package com.example.group1_petfood.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.group1_petfood.R;
import com.example.group1_petfood.models.User;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {
    private List<User> userList;
    private Context context;
    private OnUserActionListener listener;

    public enum UserAction {
        VIEW, EDIT, DELETE
    }

    public interface OnUserActionListener {
        void onUserAction(User user, UserAction action);
    }

    public UserAdapter(List<User> userList, Context context, OnUserActionListener listener) {
        this.userList = userList;
        this.context = context;
        this.listener = listener;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_user, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User user = userList.get(position);

        // Hiển thị thông tin người dùng
        holder.nameTextView.setText(user.getFullName());
        holder.usernameTextView.setText("@" + user.getUsername());
        holder.emailTextView.setText(user.getEmail());

        // Hiển thị vai trò với màu sắc tương ứng
        String roleText = user.getRole().getRoleName();
        holder.roleTextView.setText(roleText);

        switch (user.getRole()) {
            case ADMIN:
                holder.roleTextView.setTextColor(context.getResources().getColor(android.R.color.holo_red_dark));
                break;
            case STAFF:
                holder.roleTextView.setTextColor(context.getResources().getColor(android.R.color.holo_green_dark));
                break;
            default:
                holder.roleTextView.setTextColor(context.getResources().getColor(android.R.color.darker_gray));
                break;
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onUserAction(user, UserAction.VIEW);
            }
        });

        holder.btnEdit.setOnClickListener(v -> {
            if (listener != null) {
                listener.onUserAction(user, UserAction.EDIT);
            }
        });

        holder.btnDelete.setOnClickListener(v -> {
            if (listener != null) {
                listener.onUserAction(user, UserAction.DELETE);
            }
        });
    }

    @Override
    public int getItemCount() {
        return userList != null ? userList.size() : 0;
    }

    public void updateList(List<User> newList) {
        userList = newList;
        notifyDataSetChanged();
    }

    static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView, usernameTextView, emailTextView, roleTextView;
        ImageButton btnEdit, btnDelete;

        UserViewHolder(View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.userFullName);
            usernameTextView = itemView.findViewById(R.id.userUsername);
            emailTextView = itemView.findViewById(R.id.userEmail);
            roleTextView = itemView.findViewById(R.id.userRole);
            btnEdit = itemView.findViewById(R.id.btnEditUser);
            btnDelete = itemView.findViewById(R.id.btnDeleteUser);
        }
    }
}