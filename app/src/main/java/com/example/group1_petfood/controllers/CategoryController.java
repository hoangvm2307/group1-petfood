package com.example.group1_petfood.controllers;

import android.content.ContentValues;
import android.content.Context;
import com.example.group1_petfood.database.DatabaseHelper;
import com.example.group1_petfood.models.Category;
import java.util.ArrayList;
import java.util.List;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class CategoryController {
    private DatabaseHelper dbHelper;

    public CategoryController(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    public List<Category> getAllCategories() {
        List<Category> categories = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM categories", null);
        if (cursor.moveToFirst()) {
            do {
                Category category = new Category();
                category.setId(cursor.getInt(cursor.getColumnIndexOrThrow("id")));
                category.setName(cursor.getString(cursor.getColumnIndexOrThrow("name")));
                category.setDescription(cursor.getString(cursor.getColumnIndexOrThrow("description")));
                category.setImageUrl(cursor.getString(cursor.getColumnIndexOrThrow("image_url")));
                category.setCreatedAt(cursor.getString(cursor.getColumnIndexOrThrow("created_at")));
                category.setUpdatedAt(cursor.getString(cursor.getColumnIndexOrThrow("updated_at")));
                categories.add(category);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return categories;
    }

    public long addCategory(String name, String description, String imageUrl){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name", name);
        values.put("description", description);
        values.put("image_url", imageUrl);
        long id = db.insert("categories", null, values);
        db.close();
        return id;
    }

    public int updateCategory(Category category){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name", category.getName());
        values.put("description", category.getDescription());
        values.put("image_url", category.getImageUrl());
        return db.update("categories", values, "id" + " = ?", new String[]{String.valueOf(category.getId())});
    }

    public void deleteCategory(int categoryId){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete("categories", "id" + " = ?", new String[]{String.valueOf(categoryId)});
        db.close();
    }
}