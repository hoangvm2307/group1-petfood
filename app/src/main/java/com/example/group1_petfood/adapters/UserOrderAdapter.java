package com.example.group1_petfood.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.group1_petfood.R;
import com.example.group1_petfood.models.Order;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class UserOrderAdapter extends RecyclerView.Adapter<UserOrderAdapter.OrderViewHolder> {

    private List<Order> orderList;
    private Context context;
    private OnOrderClickListener listener;

    public interface OnOrderClickListener {
        void onOrderClick(Order order);
    }

    public UserOrderAdapter(List<Order> orderList, Context context, OnOrderClickListener listener) {
        this.orderList = orderList;
        this.context = context;
        this.listener = listener;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_user_order, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Order order = orderList.get(position);

        // Format order ID
        holder.tvOrderId.setText(String.format("Đơn hàng #%d", order.getId()));

        // Format date
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            Date date = inputFormat.parse(order.getCreatedAt());
            if (date != null) {
                holder.tvOrderDate.setText(outputFormat.format(date));
            } else {
                holder.tvOrderDate.setText(order.getCreatedAt());
            }
        } catch (ParseException e) {
            holder.tvOrderDate.setText(order.getCreatedAt());
        }

        // Format total amount
        holder.tvOrderTotal.setText(String.format(Locale.getDefault(), "%,.0f₫", order.getTotalAmount()));

        // Set status with appropriate color
        holder.tvOrderStatus.setText(order.getStatus());
        switch (order.getStatus().toLowerCase()) {
            case "đã giao":
            case "hoàn thành":
            case "đã thanh toán":
                holder.tvOrderStatus.setTextColor(context.getResources().getColor(android.R.color.holo_green_dark));
                break;
            case "đang giao":
            case "đang xử lý":
                holder.tvOrderStatus.setTextColor(context.getResources().getColor(android.R.color.holo_blue_dark));
                break;
            case "đã hủy":
                holder.tvOrderStatus.setTextColor(context.getResources().getColor(android.R.color.holo_red_dark));
                break;
            default:
                holder.tvOrderStatus.setTextColor(context.getResources().getColor(android.R.color.darker_gray));
                break;
        }

        // Set click listener
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onOrderClick(order);
            }
        });
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    public static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView tvOrderId, tvOrderDate, tvOrderTotal, tvOrderStatus;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            tvOrderId = itemView.findViewById(R.id.tvOrderId);
            tvOrderDate = itemView.findViewById(R.id.tvOrderDate);
            tvOrderTotal = itemView.findViewById(R.id.tvOrderTotal);
            tvOrderStatus = itemView.findViewById(R.id.tvOrderStatus);
        }
    }
}