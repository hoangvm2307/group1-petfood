package com.example.group1_petfood.controllers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.group1_petfood.database.DatabaseHelper;
import com.example.group1_petfood.models.Order;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class OrderController {
    private DatabaseHelper dbHelper;

    public OrderController(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    public List<Order> getAllOrders() {
        List<Order> orders = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM orders ORDER BY created_at DESC", null);
        if (cursor.moveToFirst()) {
            do {
                Order order = new Order();
                order.setId(cursor.getInt(cursor.getColumnIndexOrThrow("id")));
                order.setUserId(cursor.getInt(cursor.getColumnIndexOrThrow("user_id")));
                order.setTotalAmount(cursor.getDouble(cursor.getColumnIndexOrThrow("total_amount")));
                order.setShippingAddress(cursor.getString(cursor.getColumnIndexOrThrow("shipping_address")));
                order.setStatus(cursor.getString(cursor.getColumnIndexOrThrow("status")));
                order.setPaymentMethod(cursor.getString(cursor.getColumnIndexOrThrow("payment_method")));
                order.setPaymentStatus(cursor.getString(cursor.getColumnIndexOrThrow("payment_status")));
                order.setCreatedAt(cursor.getString(cursor.getColumnIndexOrThrow("created_at")));
                order.setUpdatedAt(cursor.getString(cursor.getColumnIndexOrThrow("updated_at")));
                orders.add(order);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return orders;
    }

    public List<Order> getOrdersFromLastWeek() {
        List<Order> orders = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        // Tạo ngày 7 ngày trước
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, -7);
        Date sevenDaysAgo = calendar.getTime();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String dateString = dateFormat.format(sevenDaysAgo);

        // Query để lấy đơn hàng trong 7 ngày gần nhất
        String query = "SELECT * FROM orders WHERE created_at >= ? ORDER BY created_at DESC";
        Cursor cursor = db.rawQuery(query, new String[]{dateString});

        if (cursor.moveToFirst()) {
            do {
                Order order = new Order();
                order.setId(cursor.getInt(cursor.getColumnIndexOrThrow("id")));
                order.setUserId(cursor.getInt(cursor.getColumnIndexOrThrow("user_id")));
                order.setTotalAmount(cursor.getDouble(cursor.getColumnIndexOrThrow("total_amount")));
                order.setShippingAddress(cursor.getString(cursor.getColumnIndexOrThrow("shipping_address")));
                order.setStatus(cursor.getString(cursor.getColumnIndexOrThrow("status")));
                order.setPaymentMethod(cursor.getString(cursor.getColumnIndexOrThrow("payment_method")));
                order.setPaymentStatus(cursor.getString(cursor.getColumnIndexOrThrow("payment_status")));
                order.setCreatedAt(cursor.getString(cursor.getColumnIndexOrThrow("created_at")));
                order.setUpdatedAt(cursor.getString(cursor.getColumnIndexOrThrow("updated_at")));
                orders.add(order);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return orders;
    }

    public long addOrder(int userId, double totalAmount, String shippingAddress, String status,
                         String paymentMethod, String paymentStatus, String createdAt, String updatedAt) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("user_id", userId);
        values.put("total_amount", totalAmount);
        values.put("shipping_address", shippingAddress);
        values.put("status", status);
        values.put("payment_method", paymentMethod);
        values.put("payment_status", paymentStatus);
        values.put("created_at", createdAt);
        values.put("updated_at", updatedAt);
        long id = db.insert("orders", null, values);
        db.close();
        return id;
    }

    public int updateOrder(Order order) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("user_id", order.getUserId());
        values.put("total_amount", order.getTotalAmount());
        values.put("shipping_address", order.getShippingAddress());
        values.put("status", order.getStatus());
        values.put("payment_method", order.getPaymentMethod());
        values.put("payment_status", order.getPaymentStatus());
        values.put("created_at", order.getCreatedAt());
        values.put("updated_at", order.getUpdatedAt());
        return db.update("orders", values, "id = ?", new String[]{String.valueOf(order.getId())});
    }

    public void deleteOrder(int orderId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete("orders", "id = ?", new String[]{String.valueOf(orderId)});
        db.close();
    }

    public int getOrderCount() {
        List<Order> orders = getAllOrders();
        return orders.size();
    }
}