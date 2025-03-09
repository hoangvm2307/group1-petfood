package com.example.group1_petfood.adapters;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import android.content.Context;
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

public class AdminCategoryAdapter extends RecyclerView.Adapter<AdminCategoryAdapter.AdminCategoryViewHolder>{
    private final Context context;
    private final List<Category> categoryList;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public AdminCategoryAdapter(Context context, List<Category> categoryList) {
        this.context = context;
        this.categoryList = categoryList;
    }

    @NonNull
    @Override
    public AdminCategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.admin_item_category, parent, false);
        return new AdminCategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdminCategoryViewHolder holder, int position) {
        Category category = categoryList.get(position);
        holder.textViewCategoryName.setText(category.getName());
        holder.textViewCategoryDescription.setText(category.getDescription());
        String imageUrl = category.getImageUrl();
        if (imageUrl != null && !imageUrl.isEmpty()) {
            int imageResId = context.getResources().getIdentifier(imageUrl, "drawable", context.getPackageName());

            if (imageResId != 0) {
                // Nếu ảnh là từ drawable
                holder.imageViewCategory.setImageResource(imageResId);
            } else {
                // Nếu ảnh là từ thư viện (URI hoặc URL)
                Glide.with(context)
                        .load(imageUrl)
                        .placeholder(R.drawable.slide_1_img) // Ảnh mặc định khi đang load
                        .error(R.drawable.slide_1_img) // Ảnh lỗi khi không tải được
                        .into(holder.imageViewCategory);
            }
        } else {
            // Nếu không có ảnh, đặt ảnh mặc định
            holder.imageViewCategory.setImageResource(R.drawable.slide_1_img);
        }
    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }

    public class AdminCategoryViewHolder extends RecyclerView.ViewHolder {
        TextView textViewCategoryName, textViewCategoryDescription;
        ImageView imageViewCategory;
        public AdminCategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewCategoryName = itemView.findViewById(R.id.textViewCategoryName);
            textViewCategoryDescription = itemView.findViewById(R.id.textViewCategoryDescription);
            imageViewCategory = itemView.findViewById(R.id.imageViewCategory);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onItemClick(position);
                        }
                    }
                }
            });
        }
    }
}
