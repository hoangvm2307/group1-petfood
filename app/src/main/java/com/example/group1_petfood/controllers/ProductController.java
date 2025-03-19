package com.example.group1_petfood.controllers;

import static android.content.ContentValues.TAG;

import android.content.ContentValues;
import android.content.Context;
import com.example.group1_petfood.database.DatabaseHelper;
import com.example.group1_petfood.models.Product;
import java.util.ArrayList;
import java.util.List;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class ProductController {
    private DatabaseHelper dbHelper;

    public ProductController(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    public List<Product> getFeaturedProducts() {
        List<Product> products = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM products WHERE stock_quantity > 0", null);
        if (cursor.moveToFirst()) {
            do {
                Product product = new Product();
                product.setId(cursor.getInt(cursor.getColumnIndexOrThrow("id")));
                product.setCategoryId(cursor.getInt(cursor.getColumnIndexOrThrow("category_id")));
                product.setName(cursor.getString(cursor.getColumnIndexOrThrow("name")));
                product.setBrand(cursor.getString(cursor.getColumnIndexOrThrow("brand")));
                product.setDescription(cursor.getString(cursor.getColumnIndexOrThrow("description")));
                product.setPrice(cursor.getDouble(cursor.getColumnIndexOrThrow("price")));
                product.setStockQuantity(cursor.getInt(cursor.getColumnIndexOrThrow("stock_quantity")));
                product.setImageUrl(cursor.getString(cursor.getColumnIndexOrThrow("image_url")));
                product.setCreatedAt(cursor.getString(cursor.getColumnIndexOrThrow("created_at")));
                product.setUpdatedAt(cursor.getString(cursor.getColumnIndexOrThrow("updated_at")));
                products.add(product);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return products;
    }/**
     * Lấy thông tin sản phẩm theo ID
     * @param productId ID của sản phẩm cần lấy
     * @return Đối tượng Product nếu tìm thấy, null nếu không tìm thấy
     */
    public Product getProductById(int productId) {
        Product product = null;
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        try {
            Cursor cursor = db.rawQuery("SELECT * FROM products WHERE id = ?",
                    new String[]{String.valueOf(productId)});

            if (cursor.moveToFirst()) {
                product = new Product();
                product.setId(cursor.getInt(cursor.getColumnIndexOrThrow("id")));
                product.setCategoryId(cursor.getInt(cursor.getColumnIndexOrThrow("category_id")));
                product.setName(cursor.getString(cursor.getColumnIndexOrThrow("name")));
                product.setBrand(cursor.getString(cursor.getColumnIndexOrThrow("brand")));
                product.setDescription(cursor.getString(cursor.getColumnIndexOrThrow("description")));
                product.setPrice(cursor.getDouble(cursor.getColumnIndexOrThrow("price")));
                product.setStockQuantity(cursor.getInt(cursor.getColumnIndexOrThrow("stock_quantity")));
                product.setImageUrl(cursor.getString(cursor.getColumnIndexOrThrow("image_url")));
                product.setCreatedAt(cursor.getString(cursor.getColumnIndexOrThrow("created_at")));
                product.setUpdatedAt(cursor.getString(cursor.getColumnIndexOrThrow("updated_at")));
            }

            cursor.close();
        } catch (Exception e) {
            Log.e(TAG, "Lỗi khi lấy sản phẩm theo ID: " + e.getMessage());
            e.printStackTrace();
        } finally {
            db.close();
        }

        return product;
    }
    public boolean addProduct(Product product) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("category_id", product.getCategoryId());
        values.put("name", product.getName());
        values.put("brand", product.getBrand());
        values.put("description", product.getDescription());
        values.put("price", product.getPrice());
        values.put("stock_quantity", product.getStockQuantity());
        values.put("image_url", product.getImageUrl());
        values.put("created_at", product.getCreatedAt());
        values.put("updated_at", product.getUpdatedAt());

        long result = db.insert("products", null, values);
        db.close();
        return result != -1;
    }
    public List<Product> getAllProducts() {
        List<Product> products = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM products", null);

        if (cursor.moveToFirst()) {
            do {
                Product product = new Product();
                product.setId(cursor.getInt(cursor.getColumnIndexOrThrow("id")));
                product.setCategoryId(cursor.getInt(cursor.getColumnIndexOrThrow("category_id")));
                product.setName(cursor.getString(cursor.getColumnIndexOrThrow("name")));
                product.setBrand(cursor.getString(cursor.getColumnIndexOrThrow("brand")));
                product.setDescription(cursor.getString(cursor.getColumnIndexOrThrow("description")));
                product.setPrice(cursor.getDouble(cursor.getColumnIndexOrThrow("price")));
                product.setStockQuantity(cursor.getInt(cursor.getColumnIndexOrThrow("stock_quantity")));
                product.setImageUrl(cursor.getString(cursor.getColumnIndexOrThrow("image_url")));
                product.setCreatedAt(cursor.getString(cursor.getColumnIndexOrThrow("created_at")));
                product.setUpdatedAt(cursor.getString(cursor.getColumnIndexOrThrow("updated_at")));
                products.add(product);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return products;
    }
    public boolean updateProduct(Product product) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("category_id", product.getCategoryId());
        values.put("name", product.getName());
        values.put("brand", product.getBrand());
        values.put("description", product.getDescription());
        values.put("price", product.getPrice());
        values.put("stock_quantity", product.getStockQuantity());
        values.put("image_url", product.getImageUrl());
        values.put("updated_at", product.getUpdatedAt());

        int result = db.update("products", values, "id = ?", new String[]{String.valueOf(product.getId())});
        db.close();
        return result > 0;
    }
    public boolean deleteProduct(int productId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int result = db.delete("products", "id = ?", new String[]{String.valueOf(productId)});
        db.close();
        return result > 0;
    }
}