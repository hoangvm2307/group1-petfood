package com.example.group1_petfood.fragments;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.group1_petfood.R;
import com.example.group1_petfood.activities.PaymentActivity;
import com.example.group1_petfood.adapters.CartAdapter;
import com.example.group1_petfood.controllers.CartController;
import com.example.group1_petfood.controllers.ProductController;
import com.example.group1_petfood.models.Cart;
import com.example.group1_petfood.models.CartItem;
import com.example.group1_petfood.models.Product;

import java.util.ArrayList;
import java.util.List;

public class CartDialogFragment extends DialogFragment {
    private static final String TAG = "CartDialogFragment";
    private static final int PAYMENT_REQUEST_CODE = 1001;

    private RecyclerView cartRecyclerView;
    private TextView totalPriceTextView;
    private TextView emptyCartTextView;
    private Button checkoutButton;
    private ImageButton closeButton;
    private TextView titleTextView;

    private CartAdapter cartAdapter;
    private CartController cartController;
    private ProductController productController;
    private List<CartItem> cartItems;
    private double totalPrice = 0;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.SlideRightDialog);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cart_dialog, container, false);

        // Khởi tạo controllers
        cartController = new CartController(requireContext());
        productController = new ProductController(requireContext());

        // Khởi tạo views
        initializeViews(view);

        // Lấy dữ liệu giỏ hàng
        loadCartData();

        // Thiết lập sự kiện click
        setupClickListeners();

        return view;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        // Thiết lập hiển thị full màn hình và trượt từ phải
        Window window = dialog.getWindow();
        if (window != null) {
            WindowManager.LayoutParams params = window.getAttributes();
            params.gravity = Gravity.END;
            params.width = WindowManager.LayoutParams.MATCH_PARENT;
            params.height = WindowManager.LayoutParams.MATCH_PARENT;
            window.setAttributes(params);

            // Thiết lập animation trượt từ phải sang
            window.setWindowAnimations(R.style.SlideRightAnimation);
        }

        return dialog;
    }

    private void initializeViews(View view) {
        cartRecyclerView = view.findViewById(R.id.cartRecyclerView);
        totalPriceTextView = view.findViewById(R.id.totalPriceTextView);
        emptyCartTextView = view.findViewById(R.id.emptyCartTextView);
        checkoutButton = view.findViewById(R.id.checkoutButton);
        closeButton = view.findViewById(R.id.closeButton);
        titleTextView = view.findViewById(R.id.titleTextView);

        // Thiết lập RecyclerView
        cartRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
    }

    private void loadCartData() {

        try {
            // Lấy danh sách sản phẩm trong giỏ hàng
            List<Cart> cartList = cartController.getCartItems();

            cartItems = new ArrayList<>();

            if (cartList.isEmpty()) {
                // Hiển thị thông báo giỏ hàng trống

                showEmptyCart();
                return;
            }

            totalPrice = 0;

            for (Cart cart : cartList) {
                Product product = productController.getProductById(cart.getProductId());
                int productId = cart.getProductId();
                Log.e(TAG,  "Product ID: " + productId);
                if (product != null) {
                    CartItem cartItem = new CartItem(cart, product);
                    cartItems.add(cartItem);

                    // Tính giá tiền (sử dụng giá khuyến mãi 80%)
                    double itemPrice = product.getPrice() * 0.8 * cart.getQuantity();
                    totalPrice += itemPrice;
                }
            }

            // Hiển thị danh sách sản phẩm
            if (cartItems.isEmpty()) {
                showEmptyCart();
            } else {
                showCartItems();
            }
        } catch (Exception e) {
            Log.e(TAG, "Lỗi khi tải dữ liệu giỏ hàng: " + e.getMessage());
            e.printStackTrace();
            showEmptyCart();
        }
    }

    private void showEmptyCart() {
        emptyCartTextView.setVisibility(View.VISIBLE);
        cartRecyclerView.setVisibility(View.GONE);
        totalPriceTextView.setText("0₫");
        checkoutButton.setEnabled(false);
    }

    private void showCartItems() {
        emptyCartTextView.setVisibility(View.GONE);
        cartRecyclerView.setVisibility(View.VISIBLE);
        totalPriceTextView.setText(String.format("%,.0f₫", totalPrice));
        checkoutButton.setEnabled(true);

        // Khởi tạo adapter
        cartAdapter = new CartAdapter(cartItems, this::updateCartItemQuantity, this::removeCartItem);
        cartRecyclerView.setAdapter(cartAdapter);
    }

    private void setupClickListeners() {
        // Nút đóng
        closeButton.setOnClickListener(v -> dismiss());

        // Nút thanh toán - chuyển sang PaymentActivity
        checkoutButton.setOnClickListener(v -> {
            if (cartItems.isEmpty()) {
                Toast.makeText(requireContext(), "Giỏ hàng của bạn đang trống", Toast.LENGTH_SHORT).show();
            } else {
                // Chuyển đến màn hình thanh toán
                Intent intent = new Intent(requireContext(), PaymentActivity.class);
                intent.putExtra("total_amount", totalPrice);
                startActivityForResult(intent, PAYMENT_REQUEST_CODE);

                // Đóng dialog giỏ hàng
                dismiss();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PAYMENT_REQUEST_CODE) {
            if (resultCode == getActivity().RESULT_OK) {
                // Thanh toán thành công - giỏ hàng đã được xử lý trong PaymentActivity
                Log.d(TAG, "Thanh toán thành công, đơn hàng đã được tạo và giỏ hàng đã bị xóa");
            } else {
                // Thanh toán bị hủy hoặc thất bại
                Log.d(TAG, "Thanh toán không thành công");
            }
        }
    }

    public void updateCartItemQuantity(int position, int quantity) {
        try {
            if (position < 0 || position >= cartItems.size()) return;

            CartItem cartItem = cartItems.get(position);
            Cart cart = cartItem.getCart();

            // Cập nhật số lượng trong cơ sở dữ liệu
            boolean success = cartController.updateCartItemQuantity(cart.getProductId(), quantity);

            if (success) {
                // Cập nhật danh sách hiển thị
                cart.setQuantity(quantity);

                // Tính lại tổng tiền
                recalculateTotalPrice();

                // Cập nhật giao diện
                cartAdapter.notifyItemChanged(position);
            }
        } catch (Exception e) {
            Log.e(TAG, "Lỗi khi cập nhật số lượng: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void removeCartItem(int position) {
        try {
            if (position < 0 || position >= cartItems.size()) return;

            CartItem cartItem = cartItems.get(position);

            // Xóa khỏi cơ sở dữ liệu
            boolean success = cartController.removeFromCart(cartItem.getCart().getProductId());

            if (success) {
                // Xóa khỏi danh sách hiển thị
                cartItems.remove(position);

                // Thông báo cho adapter
                cartAdapter.notifyItemRemoved(position);
                cartAdapter.notifyItemRangeChanged(position, cartItems.size());

                // Tính lại tổng tiền
                recalculateTotalPrice();

                // Kiểm tra nếu giỏ hàng trống
                if (cartItems.isEmpty()) {
                    showEmptyCart();
                }

                // Cập nhật badge giỏ hàng ở MainActivity
                updateCartBadge();
            }
        } catch (Exception e) {
            Log.e(TAG, "Lỗi khi xóa sản phẩm: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void recalculateTotalPrice() {
        totalPrice = 0;
        for (CartItem item : cartItems) {
            double itemPrice = item.getProduct().getPrice() * 0.8 * item.getCart().getQuantity();
            totalPrice += itemPrice;
        }
        totalPriceTextView.setText(String.format("%,.0f₫", totalPrice));
    }

    // Cập nhật badge giỏ hàng
    private void updateCartBadge() {
        if (getActivity() != null) {
            TextView cartBadge = getActivity().findViewById(R.id.cartBadge);
            if (cartBadge != null) {
                int itemCount = cartController.getCartItemCount();
                if (itemCount > 0) {
                    cartBadge.setText(String.valueOf(itemCount));
                    cartBadge.setVisibility(View.VISIBLE);
                } else {
                    cartBadge.setVisibility(View.GONE);
                }
            }
        }
    }
}