package com.example.group1_petfood.adapters;

import android.graphics.Paint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.group1_petfood.R;
import com.example.group1_petfood.models.CartItem;

import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {
    private static final String TAG = "CartAdapter";
    private List<CartItem> cartItems;
    private OnQuantityChangeListener quantityChangeListener;
    private OnItemRemoveListener itemRemoveListener;

    /**
     * Interface để xử lý thay đổi số lượng sản phẩm
     */
    public interface OnQuantityChangeListener {
        void onQuantityChanged(int position, int quantity);
    }

    /**
     * Interface để xử lý xóa sản phẩm
     */
    public interface OnItemRemoveListener {
        void onItemRemove(int position);
    }

    public CartAdapter(List<CartItem> cartItems,
                       OnQuantityChangeListener quantityChangeListener,
                       OnItemRemoveListener itemRemoveListener) {
        this.cartItems = cartItems;
        this.quantityChangeListener = quantityChangeListener;
        this.itemRemoveListener = itemRemoveListener;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_cart, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        try {
            CartItem cartItem = cartItems.get(position);

            // Thiết lập thông tin sản phẩm
            holder.productNameTextView.setText(cartItem.getProduct().getName());
            holder.productWeightTextView.setText(getProductWeight(cartItem));

            // Thiết lập giá
            double originalPrice = cartItem.getProduct().getPrice();
            double salePrice = originalPrice * 0.8; // Giảm giá 20%

            // Hiển thị giá gốc (gạch ngang)
            holder.originalPriceTextView.setText(String.format("%,.0f₫", originalPrice));
            holder.originalPriceTextView.setPaintFlags(
                    holder.originalPriceTextView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

            // Hiển thị giá khuyến mãi
            holder.salePriceTextView.setText(String.format("%,.0f₫", salePrice));

            // Thiết lập số lượng
            holder.quantityTextView.setText(String.valueOf(cartItem.getCart().getQuantity()));

            // Tải hình ảnh sản phẩm
            String imageUrl = cartItem.getProduct().getImageUrl();
            if (imageUrl != null && !imageUrl.isEmpty()) {
                int imageResId = holder.itemView.getContext().getResources()
                        .getIdentifier(imageUrl, "drawable",
                                holder.itemView.getContext().getPackageName());
                if (imageResId != 0) {
                    holder.productImageView.setImageResource(imageResId);
                } else {
                    holder.productImageView.setImageResource(R.drawable.slide_1_img);
                }
            } else {
                holder.productImageView.setImageResource(R.drawable.slide_1_img);
            }

            // Thiết lập sự kiện click
            setupClickListeners(holder, position);

        } catch (Exception e) {
            Log.e(TAG, "Lỗi khi hiển thị sản phẩm trong giỏ hàng: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private String getProductWeight(CartItem cartItem) {
        String description = cartItem.getProduct().getDescription();
        if (description != null && description.contains("Trọng lượng:")) {
            int startIndex = description.indexOf("Trọng lượng:") + 12;
            int endIndex = description.indexOf(".", startIndex);
            if (endIndex == -1) {
                endIndex = description.length();
            }
            if (startIndex < endIndex) {
                return description.substring(startIndex, endIndex).trim();
            }
        }
        return "1.5kg"; // Mặc định nếu không tìm thấy trọng lượng
    }

    private void setupClickListeners(CartViewHolder holder, int position) {
        // Nút tăng số lượng
        holder.increaseButton.setOnClickListener(v -> {
            int currentQuantity = Integer.parseInt(holder.quantityTextView.getText().toString());
            int newQuantity = currentQuantity + 1;
            if (newQuantity <= 99) { // Giới hạn số lượng tối đa
                holder.quantityTextView.setText(String.valueOf(newQuantity));
                if (quantityChangeListener != null) {
                    quantityChangeListener.onQuantityChanged(position, newQuantity);
                }
            }
        });

        // Nút giảm số lượng
        holder.decreaseButton.setOnClickListener(v -> {
            int currentQuantity = Integer.parseInt(holder.quantityTextView.getText().toString());
            if (currentQuantity > 1) {
                int newQuantity = currentQuantity - 1;
                holder.quantityTextView.setText(String.valueOf(newQuantity));
                if (quantityChangeListener != null) {
                    quantityChangeListener.onQuantityChanged(position, newQuantity);
                }
            }
        });

        // Nút xóa sản phẩm
        holder.removeButton.setOnClickListener(v -> {
            if (itemRemoveListener != null) {
                itemRemoveListener.onItemRemove(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return cartItems != null ? cartItems.size() : 0;
    }

    static class CartViewHolder extends RecyclerView.ViewHolder {
        ImageView productImageView;
        TextView productNameTextView;
        TextView productWeightTextView;
        TextView originalPriceTextView;
        TextView salePriceTextView;
        TextView quantityTextView;
        ImageButton decreaseButton;
        ImageButton increaseButton;
        ImageButton removeButton;

        CartViewHolder(@NonNull View itemView) {
            super(itemView);
            productImageView = itemView.findViewById(R.id.cartItemImageView);
            productNameTextView = itemView.findViewById(R.id.cartItemNameTextView);
            productWeightTextView = itemView.findViewById(R.id.cartItemWeightTextView);
            originalPriceTextView = itemView.findViewById(R.id.cartItemOriginalPriceTextView);
            salePriceTextView = itemView.findViewById(R.id.cartItemSalePriceTextView);
            quantityTextView = itemView.findViewById(R.id.cartItemQuantityTextView);
            decreaseButton = itemView.findViewById(R.id.cartItemDecreaseButton);
            increaseButton = itemView.findViewById(R.id.cartItemIncreaseButton);
            removeButton = itemView.findViewById(R.id.cartItemRemoveButton);
        }
    }
}