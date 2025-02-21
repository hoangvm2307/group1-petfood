package com.example.group1_petfood.activities;

import android.os.Bundle;
import android.util.Log;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;


import com.example.group1_petfood.R;
import com.example.group1_petfood.database.DatabaseHelper;


public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // Initialize database
        initializeDatabase();
    }

    private void initializeDatabase() {
        try {
            dbHelper = new DatabaseHelper(this);
            // Open database to create tables
            dbHelper.getWritableDatabase();
            Log.d(TAG, "Database initialized successfully");
            
            // Insert sample data if needed
            insertSampleData();
        } catch (Exception e) {
            Log.e(TAG, "Error initializing database", e);
        }
    }

    private void insertSampleData() {
        dbHelper.executeInTransaction(db -> {
            try {
                // Insert sample categories
                db.execSQL("INSERT OR IGNORE INTO categories (name, description) VALUES (?, ?)", 
                    new Object[]{"Dog Food", "High quality food for dogs"});
                db.execSQL("INSERT OR IGNORE INTO categories (name, description) VALUES (?, ?)", 
                    new Object[]{"Cat Food", "Premium food for cats"});
                
                // Insert sample products
                db.execSQL("INSERT OR IGNORE INTO products (category_id, name, brand, price, stock_quantity) VALUES (?, ?, ?, ?, ?)",
                    new Object[]{1, "Premium Dog Food", "Royal Canin", 29.99, 100});
                
                Log.d(TAG, "Sample data inserted successfully");
            } catch (Exception e) {
                Log.e(TAG, "Error inserting sample data", e);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dbHelper != null) {
            dbHelper.closeDB();
            Log.d(TAG, "Database connection closed");
        }
    }
}