package com.example.group1_petfood.adapters;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.group1_petfood.R;
import com.example.group1_petfood.models.Product;
import com.squareup.picasso.Picasso;

import java.util.List;
import android.content.Context;
import android.content.Intent;
import com.example.group1_petfood.activities.UserProductDetailActivity;
import com.example.group1_petfood.models.Product;
import com.squareup.picasso.Picasso;
import java.util.List;

public class RelatedProductsAdapter extends RecyclerView.Adapter<RelatedProductsAdapter.ViewHolder> {
    private final List<Product> productList;
    private final Context context;

    public RelatedProductsAdapter(Context context, List<Product> productList) {
        this.context = context;
        this.productList = productList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Make sure you have the correct item layout (not an activity layout)
        View view = LayoutInflater.from(context).inflate(R.layout.activity_related_products_adapter, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Product product = productList.get(position);

        // Set Product Name & Price
        holder.productName.setText(product.getName());
        holder.productPrice.setText(String.valueOf(product.getPrice()));

        // Load Image using Picasso (with placeholder & error fallback)
        Picasso.get()
                .load(product.getImageUrl())
                .placeholder(R.drawable.placeholder_image) // Show while loading
                .error(R.drawable.placeholder_image) // Show if there's an error
                .into(holder.productImage);

        // Click listener to open product detail
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, UserProductDetailActivity.class);
            intent.putExtra("product_id", product.getId()); // Pass product object
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return (productList != null) ? productList.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView productImage;
        TextView productName, productPrice;

        public ViewHolder(View itemView) {
            super(itemView);
            productImage = itemView.findViewById(R.id.ivProductImage);
            productName = itemView.findViewById(R.id.tvProductName);
            productPrice = itemView.findViewById(R.id.tvProductPrice);
        }
    }
}

