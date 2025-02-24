package com.example.group1_petfood.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.group1_petfood.R;
import com.example.group1_petfood.models.Product;

import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {
    private List<Product> products;
    private Context context;

    public ProductAdapter(List<Product> products) {
        this.products = products;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.item_product, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
       try{
           Product product = products.get(position);

           // Set product details
           holder.nameTextView.setText(product.getName());
           holder.weightTextView.setText(product.getStockQuantity());

           // Format and set prices
           String originalPrice = String.format("%,d₫", product.getPrice());
           String salePrice = String.format("%,d₫", product.getPrice() * 0.8);

           holder.originalPriceTextView.setText(originalPrice);
           holder.originalPriceTextView.setPaintFlags(holder.originalPriceTextView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
           holder.salePriceTextView.setText(salePrice);

           // Calculate and set discount percentage
           if (product.getPrice() > product.getPrice() * 0.8) {
               int discount = (int) ((1 - (float)product.getPrice() * 0.8/product.getPrice()) * 100);
               holder.discountTextView.setText("-" + discount + "%");
               holder.discountTextView.setVisibility(View.VISIBLE);
           } else {
               holder.discountTextView.setVisibility(View.GONE);
           }

           // Load product image
           int imageResId = context.getResources().getIdentifier(product.getImageUrl(), "drawable", context.getPackageName());
           if (imageResId != 0) {
               holder.imageView.setImageResource(imageResId);
           } else {
               holder.imageView.setImageResource(R.drawable.slide_1_img);
           }

           holder.addToCartButton.setOnClickListener(v -> {
               // Handle add to cart action
           });

//        holder.itemView.setOnClickListener(v -> {
//            // Handle product click
//            Intent intent = new Intent(context, ProductDetailActivity.class);
//            intent.putExtra("product_id", product.getId());
//            context.startActivity(intent);
//        });
       }catch (Exception e){

       }
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    static class ProductViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView nameTextView;
        TextView weightTextView;
        TextView originalPriceTextView;
        TextView salePriceTextView;
        TextView discountTextView;
        Button addToCartButton;

        ProductViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.productImage);
            nameTextView = itemView.findViewById(R.id.productName);
            weightTextView = itemView.findViewById(R.id.productWeight);
            originalPriceTextView = itemView.findViewById(R.id.originalPrice);
            salePriceTextView = itemView.findViewById(R.id.salePrice);
            discountTextView = itemView.findViewById(R.id.discountPercentage);
            addToCartButton = itemView.findViewById(R.id.addToCartButton);
        }
    }
}