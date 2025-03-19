package com.example.group1_petfood.controllers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.group1_petfood.database.DatabaseHelper;
import com.example.group1_petfood.models.Order;
import com.example.group1_petfood.models.Product;

import java.util.ArrayList;
import java.util.List;

public class OrderController {
    private DatabaseHelper dbHelper;

    public OrderController(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    public List<Order> getAllOrders() {
        List<Order> orders = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM orders", null);
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
