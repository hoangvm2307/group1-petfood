package com.example.group1_petfood.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.group1_petfood.R;
import com.example.group1_petfood.models.Order;
import java.util.List;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {
    private List<Order> orderList;

    public OrderAdapter(List<Order> orderList) {
        this.orderList = orderList;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.admin_item_order, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Order order = orderList.get(position);
        holder.tvId.setText("ID: " + order.getId());
        holder.tvUserId.setText("User ID: " + order.getUserId());
        holder.tvTotalAmount.setText("Total Amount: $" + order.getTotalAmount());
        holder.tvShippingAddress.setText("Shipping Address: " + order.getShippingAddress());
        holder.tvStatus.setText("Status: " + order.getStatus());
        holder.tvPaymentMethod.setText("Payment Method: " + order.getPaymentMethod());
        holder.tvPaymentStatus.setText("Payment Status: " + order.getPaymentStatus());
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    public static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView tvId, tvUserId, tvTotalAmount, tvShippingAddress, tvStatus, tvPaymentMethod, tvPaymentStatus;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            tvId = itemView.findViewById(R.id.tv_id);
            tvUserId = itemView.findViewById(R.id.tv_userId);
            tvTotalAmount = itemView.findViewById(R.id.tv_totalAmount);
            tvShippingAddress = itemView.findViewById(R.id.tv_shippingAddress);
            tvStatus = itemView.findViewById(R.id.tv_status);
            tvPaymentMethod = itemView.findViewById(R.id.tv_paymentMethod);
            tvPaymentStatus = itemView.findViewById(R.id.tv_paymentStatus);
        }
    }
    // Cập nhật danh sách đơn hàng
    public void updateList(List<Order> newList) {
        orderList.clear();
        orderList.addAll(newList);
        notifyDataSetChanged();
    }
}