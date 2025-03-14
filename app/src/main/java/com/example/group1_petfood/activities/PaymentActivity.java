package com.example.group1_petfood.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.group1_petfood.R;
import com.example.group1_petfood.controllers.CartController;
import com.example.group1_petfood.controllers.OrderController;
import com.example.group1_petfood.models.CartItem;
import com.example.group1_petfood.utils.UserUtils;
import com.example.group1_petfood.zalopay.Api.CreateOrder;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import vn.zalopay.sdk.Environment;
import vn.zalopay.sdk.ZaloPayError;
import vn.zalopay.sdk.ZaloPaySDK;
import vn.zalopay.sdk.listeners.PayOrderListener;

public class PaymentActivity extends AppCompatActivity {
    private static final String TAG = "PaymentActivity";
    private static final int DEFAULT_USER_ID = 1;

    private TextView totalAmountTextView;
    private Button payButton;
    private Button cancelButton;

    private CartController cartController;
    private OrderController orderController;
    private double totalAmount;
    private List<CartItem> cartItems;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        // Khởi tạo ZaloPay SDK
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        ZaloPaySDK.init(2553, Environment.SANDBOX);

        // Khởi tạo controllers
        cartController = new CartController(this);
        orderController = new OrderController(this);

        // Setup views
        totalAmountTextView = findViewById(R.id.textViewTotalAmount);
        payButton = findViewById(R.id.buttonPay);
        cancelButton = findViewById(R.id.buttonCancel);

        // Lấy dữ liệu từ intent
        totalAmount = getIntent().getDoubleExtra("total_amount", 0);
        totalAmountTextView.setText(String.format("%,.0f₫", totalAmount));

        // Thiết lập listeners
        payButton.setOnClickListener(v -> processPayment());
        cancelButton.setOnClickListener(v -> {
            setResult(RESULT_CANCELED);
            finish();
        });
    }

    private void processPayment() {
        try {
            String totalString = String.format("%.0f", totalAmount);
            String totalFormatted = String.format("%,.0f₫", totalAmount);

            Toast.makeText(this, "Đang xử lý thanh toán...", Toast.LENGTH_SHORT).show();

            CreateOrder orderApi = new CreateOrder();
            JSONObject data = orderApi.createOrder(totalString);
            String code = data.getString("return_code");

            if (code.equals("1")) {
                String token = data.getString("zp_trans_token");

                // Ghi log để debug
                Log.d(TAG, "Bắt đầu thanh toán ZaloPay với token: " + token);

                ZaloPaySDK.getInstance().payOrder(this, token, "demozpdk://app", new PayOrderListener() {
                    @Override
                    public void onPaymentSucceeded(String transactionId, String transToken, String appTransID) {
                        // Ghi log debug
                        Log.d(TAG, "onPaymentSucceeded được gọi: " + transactionId);

                        // Tạo đơn hàng mới
                        createNewOrder();

                        // Xóa giỏ hàng
                        clearCart();

                        // Chuyển đến màn hình thông báo thành công
                        Intent intent = new Intent(PaymentActivity.this, PaymentNotification.class);
                        intent.putExtra("result", "Thanh toán thành công");
                        intent.putExtra("total", "Bạn đã thanh toán " + totalFormatted);
                        startActivity(intent);

                        // Kết thúc activity
                        setResult(RESULT_OK);
                        finish();
                    }

                    @Override
                    public void onPaymentCanceled(String transToken, String appTransID) {
                        Log.d(TAG, "onPaymentCanceled được gọi: " + appTransID);

                        Intent intent = new Intent(PaymentActivity.this, PaymentNotification.class);
                        intent.putExtra("result", "Thanh toán đã được hủy");
                        startActivity(intent);

                        setResult(RESULT_CANCELED);
                        finish();
                    }

                    @Override
                    public void onPaymentError(ZaloPayError errorCode, String transToken, String appTransID) {
                        Log.d(TAG, "onPaymentError được gọi: " + errorCode.toString());

                        Intent intent = new Intent(PaymentActivity.this, PaymentNotification.class);
                        intent.putExtra("result", "Lỗi thanh toán: " + errorCode.toString());
                        startActivity(intent);

                        setResult(RESULT_CANCELED);
                        finish();
                    }
                });
            } else {
                Toast.makeText(this, "Không thể tạo thanh toán: " + data.getString("return_message"), Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Log.e(TAG, "Lỗi khi xử lý thanh toán: " + e.getMessage());
            e.printStackTrace();

            Toast.makeText(this, "Lỗi thanh toán: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    // Phương thức tạo đơn hàng mới
    private void createNewOrder() {
        try {
            int userId = UserUtils.getCurrentUserId(this);
            // Lấy thời gian hiện tại
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            String currentTime = sdf.format(new Date());

            // Tạo đơn hàng mới
            long orderId = orderController.addOrder(
                    userId,
                    totalAmount,
                    "Địa chỉ mặc định", // Có thể thay đổi để lấy từ người dùng
                    "Đã thanh toán", // Trạng thái đơn hàng
                    "ZaloPay", // Phương thức thanh toán
                    "Đã thanh toán", // Trạng thái thanh toán
                    currentTime, // Thời gian tạo
                    currentTime  // Thời gian cập nhật
            );

            // Ghi log thông tin
            Log.d(TAG, "Đã tạo đơn hàng mới với ID: " + orderId);

            // TODO: Thêm các sản phẩm vào bảng order_items nếu cần

        } catch (Exception e) {
            Log.e(TAG, "Lỗi khi tạo đơn hàng: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Phương thức xóa giỏ hàng
    private void clearCart() {
        try {
            boolean success = cartController.clearCart();
            if (success) {
                Log.d(TAG, "Đã xóa tất cả các sản phẩm trong giỏ hàng");
            } else {
                Log.e(TAG, "Không thể xóa giỏ hàng");
            }
        } catch (Exception e) {
            Log.e(TAG, "Lỗi khi xóa giỏ hàng: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        // Ghi log để debug
        Log.d(TAG, "onNewIntent được gọi với action: " + (intent.getAction() != null ? intent.getAction() : "null"));

        // Phải gọi phương thức này để ZaloPay SDK xử lý callback
        ZaloPaySDK.getInstance().onResult(intent);
    }
}