package com.example.group1_petfood.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.group1_petfood.R;
import com.example.group1_petfood.activities.ProductDetailActivity;
import com.example.group1_petfood.models.Product;

import java.util.List;

public class AdminProductAdapter extends RecyclerView.Adapter<AdminProductAdapter.AdminProductViewHolder> {
    private List<Product> products;
    private Context context;
    private OnProductActionListener listener;

    public AdminProductAdapter(List<Product> products, OnProductActionListener listener) {
        this.products = products;
        this.listener = listener;
    }

    @NonNull
    @Override
    public AdminProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.item_admin_product, parent, false);
        return new AdminProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdminProductViewHolder holder, int position) {
        Product product = products.get(position);
        holder.nameTextView.setText(product.getName());
        holder.priceTextView.setText(String.format("%,.0f₫", product.getPrice()));
        holder.stockTextView.setText("Số lượng: " + product.getStockQuantity());

        // Mở trang chi tiết sản phẩm khi nhấn vào item
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ProductDetailActivity.class);
            intent.putExtra("product", product);
            context.startActivity(intent);
        });

        holder.btnEdit.setOnClickListener(v -> {
            if (listener != null) {
                listener.onEditProduct(product);
            }
        });

        holder.btnDelete.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDeleteProduct(product);
            }
        });
    }

    @Override
    public int getItemCount() {
        return products != null ? products.size() : 0;
    }

    static class AdminProductViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView, priceTextView, stockTextView;
        ImageButton btnEdit, btnDelete; // Change this line

        AdminProductViewHolder(View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.productName);
            priceTextView = itemView.findViewById(R.id.productPrice);
            stockTextView = itemView.findViewById(R.id.productStock);
            btnEdit = itemView.findViewById(R.id.btnEdit); // Correct casting
            btnDelete = itemView.findViewById(R.id.btnDelete); // Correct casting
        }
    }

    // Interface để xử lý sự kiện sửa/xóa sản phẩm
    public interface OnProductActionListener {
        void onEditProduct(Product product);
        void onDeleteProduct(Product product);
    }
}