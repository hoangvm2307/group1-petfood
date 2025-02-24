package com.example.group1_petfood.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.group1_petfood.R;
import com.example.group1_petfood.models.Category;

import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {
    private List<Category> categories;
    private Context context;

    public CategoryAdapter(List<Category> categories) {
        this.categories = categories;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.item_category, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        try {
            Category category = categories.get(position);
            holder.titleTextView.setText(category.getName());

            // Load category image
            String imageUrl = category.getImageUrl();
            if (imageUrl != null && !imageUrl.isEmpty()) {
                try {
                    int imageResId = context.getResources().getIdentifier(imageUrl, "drawable", context.getPackageName());
                    if (imageResId != 0) {
                        holder.imageView.setImageResource(imageResId);
                    } else {
                        holder.imageView.setImageResource(R.drawable.slide_1_img);
                    }
                } catch (Exception e) {
                    holder.imageView.setImageResource(R.drawable.slide_1_img);
                }
            } else {
                holder.imageView.setImageResource(R.drawable.slide_1_img);
            }
//        holder.itemView.setOnClickListener(v -> {
//            // Handle category click
//            Intent intent = new Intent(context, CategoryProductsActivity.class);
//            intent.putExtra("category_id", category.getId());
//            context.startActivity(intent);
//        });

            // Click listener code...
        } catch (Exception e) {
            e.printStackTrace();
            // Đặt giá trị mặc định cho các view để tránh hiển thị lỗi
            holder.titleTextView.setText("Unknown Category");
            holder.imageView.setImageResource(R.drawable.slide_1_img);
        }
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    static class CategoryViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView titleTextView;

        CategoryViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.categoryImage);
            titleTextView = itemView.findViewById(R.id.categoryTitle);
        }
    }
}