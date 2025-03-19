package com.example.group1_petfood.controllers;

import android.content.Context;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.group1_petfood.database.DatabaseHelper;
import com.example.group1_petfood.models.Cart;
import com.example.group1_petfood.models.Product;

import java.util.ArrayList;
import java.util.List;

public class CartController {
    private static final String TAG = "CartController";
    private DatabaseHelper dbHelper;
    private Context context;

    // ID người dùng mặc định khi chưa đăng nhập
    private static final int DEFAULT_USER_ID = 1;

    public CartController(Context context) {
        this.context = context;
        this.dbHelper = new DatabaseHelper(context);
    }

    /**
     * Thêm sản phẩm vào giỏ hàng
     * @param productId ID của sản phẩm
     * @param quantity Số lượng sản phẩm
     * @return true nếu thêm thành công, false nếu thất bại
     */
    public boolean addToCart(int productId, int quantity) {
        return addToCart(DEFAULT_USER_ID, productId, quantity);
    }

    /**
     * Thêm sản phẩm vào giỏ hàng của một người dùng cụ thể
     * @param userId ID của người dùng
     * @param productId ID của sản phẩm
     * @param quantity Số lượng sản phẩm
     * @return true nếu thêm thành công, false nếu thất bại
     */
    public boolean addToCart(int userId, int productId, int quantity) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        boolean success = false;

        try {
            // Kiểm tra sản phẩm đã có trong giỏ hàng chưa
            Cursor cursor = db.rawQuery(
                    "SELECT * FROM cart WHERE user_id = ? AND product_id = ?",
                    new String[]{String.valueOf(userId), String.valueOf(productId)});

            if (cursor.moveToFirst()) {
                // Nếu sản phẩm đã có trong giỏ hàng, cập nhật số lượng
                int currentQuantity = cursor.getInt(cursor.getColumnIndexOrThrow("quantity"));
                int newQuantity = currentQuantity + quantity;

                ContentValues values = new ContentValues();
                values.put("quantity", newQuantity);
                values.put("updated_at", getCurrentTimestamp());

                int updatedRows = db.update("cart", values,
                        "user_id = ? AND product_id = ?",
                        new String[]{String.valueOf(userId), String.valueOf(productId)});

                success = updatedRows > 0;
                Log.d(TAG, "Cập nhật sản phẩm trong giỏ hàng: " + success +
                        ", Số lượng mới: " + newQuantity);
            } else {
                // Nếu sản phẩm chưa có trong giỏ hàng, thêm mới
                ContentValues values = new ContentValues();
                values.put("user_id", userId);
                values.put("product_id", productId);
                values.put("quantity", quantity);
                values.put("created_at", getCurrentTimestamp());
                values.put("updated_at", getCurrentTimestamp());

                long newRowId = db.insert("cart", null, values);
                success = newRowId != -1;
                Log.d(TAG, "Thêm sản phẩm mới vào giỏ hàng: " + success +
                        ", ID: " + newRowId);
            }

            cursor.close();
        } catch (Exception e) {
            Log.e(TAG, "Lỗi khi thêm vào giỏ hàng: " + e.getMessage());
            e.printStackTrace();
        } finally {
            db.close();
        }

        return success;
    }

    /**
     * Xóa sản phẩm khỏi giỏ hàng
     * @param productId ID của sản phẩm
     * @return true nếu xóa thành công, false nếu thất bại
     */
    public boolean removeFromCart(int productId) {
        return removeFromCart(DEFAULT_USER_ID, productId);
    }

    /**
     * Xóa sản phẩm khỏi giỏ hàng của một người dùng cụ thể
     * @param userId ID của người dùng
     * @param productId ID của sản phẩm
     * @return true nếu xóa thành công, false nếu thất bại
     */
    public boolean removeFromCart(int userId, int productId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        boolean success = false;

        try {
            int deletedRows = db.delete("cart",
                    "user_id = ? AND product_id = ?",
                    new String[]{String.valueOf(userId), String.valueOf(productId)});

            success = deletedRows > 0;
            Log.d(TAG, "Xóa sản phẩm khỏi giỏ hàng: " + success +
                    ", Số dòng bị xóa: " + deletedRows);
        } catch (Exception e) {
            Log.e(TAG, "Lỗi khi xóa khỏi giỏ hàng: " + e.getMessage());
            e.printStackTrace();
        } finally {
            db.close();
        }

        return success;
    }

    /**
     * Cập nhật số lượng sản phẩm trong giỏ hàng
     * @param productId ID của sản phẩm
     * @param quantity Số lượng mới
     * @return true nếu cập nhật thành công, false nếu thất bại
     */
    public boolean updateCartItemQuantity(int productId, int quantity) {
        return updateCartItemQuantity(DEFAULT_USER_ID, productId, quantity);
    }

    /**
     * Cập nhật số lượng sản phẩm trong giỏ hàng của một người dùng cụ thể
     * @param userId ID của người dùng
     * @param productId ID của sản phẩm
     * @param quantity Số lượng mới
     * @return true nếu cập nhật thành công, false nếu thất bại
     */
    public boolean updateCartItemQuantity(int userId, int productId, int quantity) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        boolean success = false;

        try {
            ContentValues values = new ContentValues();
            values.put("quantity", quantity);
            values.put("updated_at", getCurrentTimestamp());

            int updatedRows = db.update("cart", values,
                    "user_id = ? AND product_id = ?",
                    new String[]{String.valueOf(userId), String.valueOf(productId)});

            success = updatedRows > 0;
            Log.d(TAG, "Cập nhật số lượng trong giỏ hàng: " + success +
                    ", Số lượng mới: " + quantity);
        } catch (Exception e) {
            Log.e(TAG, "Lỗi khi cập nhật số lượng: " + e.getMessage());
            e.printStackTrace();
        } finally {
            db.close();
        }

        return success;
    }

    /**
     * Lấy tất cả sản phẩm trong giỏ hàng
     * @return Danh sách các đối tượng Cart
     */
    public List<Cart> getCartItems() {
        return getCartItems(DEFAULT_USER_ID);
    }

    /**
     * Lấy tất cả sản phẩm trong giỏ hàng của một người dùng cụ thể
     * @param userId ID của người dùng
     * @return Danh sách các đối tượng Cart
     */
    public List<Cart> getCartItems(int userId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        List<Cart> cartItems = new ArrayList<>();

        try {
            Cursor cursor = db.rawQuery(
                    "SELECT * FROM cart WHERE user_id = ?",
                    new String[]{String.valueOf(userId)});

            if (cursor.moveToFirst()) {
                do {
                    Cart cart = new Cart();
                    cart.setId(cursor.getInt(cursor.getColumnIndexOrThrow("id")));
                    cart.setUserId(cursor.getInt(cursor.getColumnIndexOrThrow("user_id")));
                    cart.setProductId(cursor.getInt(cursor.getColumnIndexOrThrow("product_id")));
                    cart.setQuantity(cursor.getInt(cursor.getColumnIndexOrThrow("quantity")));
                    cart.setCreatedAt(cursor.getString(cursor.getColumnIndexOrThrow("created_at")));
                    cart.setUpdatedAt(cursor.getString(cursor.getColumnIndexOrThrow("updated_at")));

                    cartItems.add(cart);
                } while (cursor.moveToNext());
            }

            cursor.close();
            Log.d(TAG, "Lấy " + cartItems.size() + " sản phẩm từ giỏ hàng");
        } catch (Exception e) {
            Log.e(TAG, "Lỗi khi lấy sản phẩm từ giỏ hàng: " + e.getMessage());
            e.printStackTrace();
        } finally {
            db.close();
        }

        return cartItems;
    }

    /**
     * Đếm số lượng sản phẩm trong giỏ hàng
     * @return Số lượng sản phẩm trong giỏ hàng
     */
    public int getCartItemCount() {
        return getCartItemCount(DEFAULT_USER_ID);
    }

    /**
     * Đếm số lượng sản phẩm trong giỏ hàng của một người dùng cụ thể
     * @param userId ID của người dùng
     * @return Số lượng sản phẩm trong giỏ hàng
     */
    public int getCartItemCount(int userId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        int count = 0;

        try {
            Cursor cursor = db.rawQuery(
                    "SELECT SUM(quantity) FROM cart WHERE user_id = ?",
                    new String[]{String.valueOf(userId)});

            if (cursor.moveToFirst()) {
                count = cursor.getInt(0);
            }

            cursor.close();
        } catch (Exception e) {
            Log.e(TAG, "Lỗi khi đếm số lượng sản phẩm: " + e.getMessage());
            e.printStackTrace();
        } finally {
            db.close();
        }

        return count;
    }

    /**
     * Lấy tổng giá trị giỏ hàng
     * @return Tổng giá trị giỏ hàng
     */
    public double getCartTotal() {
        return getCartTotal(DEFAULT_USER_ID);
    }

    /**
     * Lấy tổng giá trị giỏ hàng của một người dùng cụ thể
     * @param userId ID của người dùng
     * @return Tổng giá trị giỏ hàng
     */
    public double getCartTotal(int userId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        double total = 0;

        try {
            Cursor cursor = db.rawQuery(
                    "SELECT SUM(p.price * c.quantity) FROM cart c " +
                            "JOIN products p ON c.product_id = p.id " +
                            "WHERE c.user_id = ?",
                    new String[]{String.valueOf(userId)});

            if (cursor.moveToFirst()) {
                total = cursor.getDouble(0);
            }

            cursor.close();
        } catch (Exception e) {
            Log.e(TAG, "Lỗi khi tính tổng giá trị giỏ hàng: " + e.getMessage());
            e.printStackTrace();
        } finally {
            db.close();
        }

        return total;
    }

    /**
     * Xóa tất cả sản phẩm trong giỏ hàng
     * @return true nếu xóa thành công, false nếu thất bại
     */
    public boolean clearCart() {
        return clearCart(DEFAULT_USER_ID);
    }

    /**
     * Xóa tất cả sản phẩm trong giỏ hàng của một người dùng cụ thể
     * @param userId ID của người dùng
     * @return true nếu xóa thành công, false nếu thất bại
     */
    public boolean clearCart(int userId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        boolean success = false;

        try {
            int deletedRows = db.delete("cart",
                    "user_id = ?",
                    new String[]{String.valueOf(userId)});

            success = deletedRows > 0;
            Log.d(TAG, "Xóa giỏ hàng: " + success +
                    ", Số dòng bị xóa: " + deletedRows);
        } catch (Exception e) {
            Log.e(TAG, "Lỗi khi xóa giỏ hàng: " + e.getMessage());
            e.printStackTrace();
        } finally {
            db.close();
        }

        return success;
    }

    /**
     * Lấy thời gian hiện tại dưới dạng chuỗi
     * @return Chuỗi thời gian hiện tại
     */
    private String getCurrentTimestamp() {
        return java.text.DateFormat.getDateTimeInstance().format(new java.util.Date());
    }
    public void debugPrintAllCartItems() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        try {
            Log.d(TAG, "===== DEBUG: TẤT CẢ CÁC MỤC TRONG BẢNG CART =====");

            Cursor cursor = db.rawQuery("SELECT * FROM cart", null);
            Log.d(TAG, "Tổng số bản ghi trong bảng cart: " + cursor.getCount());

            if (cursor.moveToFirst()) {
                do {
                    int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
                    int userId = cursor.getInt(cursor.getColumnIndexOrThrow("user_id"));
                    int productId = cursor.getInt(cursor.getColumnIndexOrThrow("product_id"));
                    int quantity = cursor.getInt(cursor.getColumnIndexOrThrow("quantity"));
                    String createdAt = cursor.getString(cursor.getColumnIndexOrThrow("created_at"));

                    Log.d(TAG, "Cart{id=" + id + ", userId=" + userId +
                            ", productId=" + productId + ", quantity=" + quantity +
                            ", createdAt=" + createdAt + "}");
                } while (cursor.moveToNext());
            }

            cursor.close();

            // Kiểm tra bảng products
            cursor = db.rawQuery("SELECT COUNT(*) FROM products", null);
            if (cursor.moveToFirst()) {
                int count = cursor.getInt(0);
                Log.d(TAG, "Tổng số sản phẩm trong bảng products: " + count);
            }
            cursor.close();

            // Kiểm tra DEFAULT_USER_ID
            Log.d(TAG, "DEFAULT_USER_ID đang được sử dụng: " + CartController.DEFAULT_USER_ID);

            // Kiểm tra cấu trúc bảng cart
            cursor = db.rawQuery("PRAGMA table_info(cart)", null);
            Log.d(TAG, "Cấu trúc bảng cart:");
            if (cursor.moveToFirst()) {
                do {
                    String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
                    String type = cursor.getString(cursor.getColumnIndexOrThrow("type"));
                    Log.d(TAG, "Cột: " + name + ", Kiểu: " + type);
                } while (cursor.moveToNext());
            }
            cursor.close();

        } catch (Exception e) {
            Log.e(TAG, "Lỗi khi debug giỏ hàng: " + e.getMessage());
            e.printStackTrace();
        } finally {
            db.close();
        }
    }
}