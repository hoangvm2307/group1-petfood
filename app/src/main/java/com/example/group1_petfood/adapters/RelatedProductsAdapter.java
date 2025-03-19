package com.example.group1_petfood.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.group1_petfood.R;
import com.example.group1_petfood.activities.UserProductDetailActivity;
import com.example.group1_petfood.models.Product;

import java.util.List;

public class RelatedProductsAdapter extends RecyclerView.Adapter<RelatedProductsAdapter.ViewHolder> {
    private static final String TAG = "RelatedProductsAdapter";
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

        // Set Product Name
        holder.productName.setText(product.getName());

        // Format price with thousand separator (similar to UserProductDetailActivity)
        double originalPrice = product.getPrice();
        double salePrice = originalPrice * 0.8; // Giảm giá 20% như trong UserProductDetailActivity
        holder.productPrice.setText(String.format("%,.0f₫", salePrice));
        holder.productOriginalPrice.setText(String.format("%,.0f₫", originalPrice));
        holder.productOriginalPrice.setPaintFlags(holder.productOriginalPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        // Load image using same approach as UserProductDetailActivity
        displayProductImage(holder.productImage, product);

        // Click listener to open product detail
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, UserProductDetailActivity.class);
            intent.putExtra("product_id", product.getId()); // Pass product ID
            context.startActivity(intent);
        });
    }

    /**
     * Hiển thị hình ảnh sản phẩm giống như trong UserProductDetailActivity
     */
    private void displayProductImage(ImageView imageView, Product product) {
        // Lấy URL hình ảnh từ sản phẩm
        String imageUrl = product.getImageUrl();
        Log.d(TAG, "Hiển thị hình ảnh sản phẩm: " + imageUrl);

        // Kiểm tra imageUrl có tồn tại không
        if (imageUrl != null && !imageUrl.isEmpty()) {
            // Tìm ID tài nguyên dựa trên tên
            int imageResId = context.getResources().getIdentifier(imageUrl, "drawable", context.getPackageName());

            if (imageResId != 0) {
                // Nếu tìm thấy, hiển thị hình ảnh
                imageView.setImageResource(imageResId);
                Log.d(TAG, "Đã tìm thấy hình ảnh: " + imageUrl + ", ResID: " + imageResId);
            } else {
                // Nếu không tìm thấy, hiển thị hình ảnh mặc định
                imageView.setImageResource(R.drawable.slide_1_img);
                Log.d(TAG, "Không tìm thấy hình ảnh: " + imageUrl);
            }
        } else {
            // Nếu URL hình ảnh trống, hiển thị hình ảnh mặc định
            imageView.setImageResource(R.drawable.slide_1_img);
            Log.d(TAG, "URL hình ảnh trống hoặc null");
        }
    }

    @Override
    public int getItemCount() {
        return (productList != null) ? productList.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView productImage;
        TextView productName, productPrice;
        TextView productOriginalPrice;
        public ViewHolder(View itemView) {
            super(itemView);
            productImage = itemView.findViewById(R.id.ivProductImage);
            productName = itemView.findViewById(R.id.tvProductName);
            productPrice = itemView.findViewById(R.id.tvProductPrice);
            productOriginalPrice = itemView.findViewById(R.id.tvProductOriginalPrice);
        }
    }
}