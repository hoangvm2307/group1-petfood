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
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.group1_petfood.R;
import com.example.group1_petfood.models.Product;

import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {
    private static final String TAG = "ProductAdapter";
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
        try {
            Product product = products.get(position);

            // Set product details
            holder.nameTextView.setText(product.getName());

            // Lấy mô tả sản phẩm để tìm trọng lượng
            String description = product.getDescription();
            String weight = "";

            // Tìm thông tin trọng lượng từ mô tả
            if (description != null && description.contains("Trọng lượng:")) {
                int startIndex = description.indexOf("Trọng lượng:") + 12;
                int endIndex = description.indexOf(".", startIndex);
                if (endIndex == -1) {
                    endIndex = description.length();
                }
                if (startIndex < endIndex) {
                    weight = description.substring(startIndex, endIndex).trim();
                }
            }

            // Nếu không tìm thấy trọng lượng từ mô tả, hiển thị giá trị mặc định
            if (weight.isEmpty()) {
                weight = "(400g - 1.5kg)";
            }

            holder.weightTextView.setText(weight);

            // Format và hiển thị giá
            double originalPrice = product.getPrice();
            double salePrice = originalPrice * 0.8; // Giá giảm 20%

            holder.originalPriceTextView.setText(String.format("%,.0f₫", originalPrice));
            holder.originalPriceTextView.setPaintFlags(holder.originalPriceTextView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            holder.salePriceTextView.setText(String.format("%,.0f₫", salePrice));

            // Tính và hiển thị phần trăm giảm giá
            int discount = 20; // Mặc định giảm 20%
            holder.discountTextView.setText("-" + discount + "%");
            holder.discountTextView.setVisibility(View.VISIBLE);

            // Tải hình ảnh sản phẩm
            String imageUrl = product.getImageUrl();
            Log.d(TAG, "Tải hình ảnh: " + imageUrl);

            if (imageUrl != null && !imageUrl.isEmpty()) {
                int imageResId = context.getResources().getIdentifier(imageUrl, "drawable", context.getPackageName());
                if (imageResId != 0) {
                    holder.imageView.setImageResource(imageResId);
                    Log.d(TAG, "Đã tìm thấy hình ảnh: " + imageUrl);
                } else {
                    holder.imageView.setImageResource(R.drawable.slide_1_img);
                    Log.d(TAG, "Không tìm thấy hình ảnh: " + imageUrl);
                }
            } else {
                holder.imageView.setImageResource(R.drawable.slide_1_img);
                Log.d(TAG, "URL hình ảnh trống hoặc null");
            }

            holder.addToCartButton.setOnClickListener(v -> {
                // Xử lý thêm vào giỏ hàng
                Log.d(TAG, "Thêm vào giỏ: " + product.getName());
            });

            holder.itemView.setOnClickListener(v -> {
                // Xử lý khi click vào sản phẩm
                Log.d(TAG, "Xem chi tiết sản phẩm: " + product.getName());
                // Tạm thời bỏ comment vì chưa có ProductDetailActivity
                // Intent intent = new Intent(context, ProductDetailActivity.class);
                // intent.putExtra("product_id", product.getId());
                // context.startActivity(intent);
            });
        } catch (Exception e) {
            Log.e(TAG, "Lỗi khi hiển thị sản phẩm: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return products != null ? products.size() : 0;
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