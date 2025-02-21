package com.example.group1_petfood.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String TAG = "DatabaseHelper";
    private static final String DATABASE_NAME = "petfood_store.db";
    private static final int DATABASE_VERSION = 1;

    // Table Names
    private static final String TABLE_USERS = "users";
    private static final String TABLE_CATEGORIES = "categories";
    private static final String TABLE_PRODUCTS = "products";
    private static final String TABLE_CART = "cart";
    private static final String TABLE_ORDERS = "orders";
    private static final String TABLE_ORDER_ITEMS = "order_items";
    private static final String TABLE_STORE_LOCATIONS = "store_locations";
    private static final String TABLE_CHAT_MESSAGES = "chat_messages";
    private static final String TABLE_NOTIFICATIONS = "notifications";

    // Common column names
    private static final String KEY_ID = "id";
    private static final String KEY_CREATED_AT = "created_at";
    private static final String KEY_UPDATED_AT = "updated_at";

    // SQL Create Tables
    private static final String CREATE_TABLE_USERS = "CREATE TABLE IF NOT EXISTS " + TABLE_USERS + " (" +
            KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "username TEXT UNIQUE NOT NULL, " +
            "email TEXT UNIQUE NOT NULL, " +
            "password TEXT NOT NULL, " +
            "full_name TEXT NOT NULL, " +
            "phone TEXT, " +
            "address TEXT, " +
            KEY_CREATED_AT + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
            KEY_UPDATED_AT + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP)";
    private static final String CREATE_TABLE_CATEGORIES = "CREATE TABLE IF NOT EXISTS " + TABLE_CATEGORIES + " (" +
            KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "name TEXT NOT NULL, " +
            "description TEXT, " +
            "image_url TEXT, " +
            KEY_CREATED_AT + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
            KEY_UPDATED_AT + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP)";

    private static final String CREATE_TABLE_PRODUCTS = "CREATE TABLE IF NOT EXISTS " + TABLE_PRODUCTS + " (" +
            KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "category_id INTEGER, " +
            "name TEXT NOT NULL, " +
            "brand TEXT, " +
            "description TEXT, " +
            "price DECIMAL NOT NULL, " +
            "stock_quantity INTEGER DEFAULT 0, " +
            "image_url TEXT, " +
            KEY_CREATED_AT + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
            KEY_UPDATED_AT + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
            "FOREIGN KEY (category_id) REFERENCES " + TABLE_CATEGORIES + "(" + KEY_ID + "))";

    private static final String CREATE_TABLE_CART = "CREATE TABLE IF NOT EXISTS " + TABLE_CART + " (" +
            KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "user_id INTEGER NOT NULL, " +
            "product_id INTEGER NOT NULL, " +
            "quantity INTEGER DEFAULT 1, " +
            KEY_CREATED_AT + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
            KEY_UPDATED_AT + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
            "FOREIGN KEY (user_id) REFERENCES " + TABLE_USERS + "(" + KEY_ID + "), " +
            "FOREIGN KEY (product_id) REFERENCES " + TABLE_PRODUCTS + "(" + KEY_ID + "))";

    private static final String CREATE_TABLE_ORDERS = "CREATE TABLE IF NOT EXISTS " + TABLE_ORDERS + " (" +
            KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "user_id INTEGER NOT NULL, " +
            "total_amount DECIMAL NOT NULL, " +
            "shipping_address TEXT NOT NULL, " +
            "status TEXT DEFAULT 'pending', " +
            "payment_method TEXT NOT NULL, " +
            "payment_status TEXT DEFAULT 'pending', " +
            KEY_CREATED_AT + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
            KEY_UPDATED_AT + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
            "FOREIGN KEY (user_id) REFERENCES " + TABLE_USERS + "(" + KEY_ID + "))";

    private static final String CREATE_TABLE_ORDER_ITEMS = "CREATE TABLE IF NOT EXISTS " + TABLE_ORDER_ITEMS + " (" +
            KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "order_id INTEGER NOT NULL, " +
            "product_id INTEGER NOT NULL, " +
            "quantity INTEGER NOT NULL, " +
            "price DECIMAL NOT NULL, " +
            KEY_CREATED_AT + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
            "FOREIGN KEY (order_id) REFERENCES " + TABLE_ORDERS + "(" + KEY_ID + "), " +
            "FOREIGN KEY (product_id) REFERENCES " + TABLE_PRODUCTS + "(" + KEY_ID + "))";

    private static final String CREATE_TABLE_STORE_LOCATIONS = "CREATE TABLE IF NOT EXISTS " + TABLE_STORE_LOCATIONS + " (" +
            KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "name TEXT NOT NULL, " +
            "address TEXT NOT NULL, " +
            "latitude DECIMAL(10,8) NOT NULL, " +
            "longitude DECIMAL(11,8) NOT NULL, " +
            "phone TEXT, " +
            "opening_hours TEXT, " +
            KEY_CREATED_AT + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
            KEY_UPDATED_AT + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP)";

    private static final String CREATE_TABLE_CHAT_MESSAGES = "CREATE TABLE IF NOT EXISTS " + TABLE_CHAT_MESSAGES + " (" +
            KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "user_id INTEGER NOT NULL, " +
            "message TEXT NOT NULL, " +
            "is_from_store BOOLEAN DEFAULT 0, " +
            "read_status BOOLEAN DEFAULT 0, " +
            KEY_CREATED_AT + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
            "FOREIGN KEY (user_id) REFERENCES " + TABLE_USERS + "(" + KEY_ID + "))";

    private static final String CREATE_TABLE_NOTIFICATIONS = "CREATE TABLE IF NOT EXISTS " + TABLE_NOTIFICATIONS + " (" +
            KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "user_id INTEGER NOT NULL, " +
            "title TEXT NOT NULL, " +
            "message TEXT NOT NULL, " +
            "type TEXT NOT NULL, " +
            "read_status BOOLEAN DEFAULT 0, " +
            KEY_CREATED_AT + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
            "FOREIGN KEY (user_id) REFERENCES " + TABLE_USERS + "(" + KEY_ID + "))";

    // Constructor
    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            db.execSQL(CREATE_TABLE_USERS);
            db.execSQL(CREATE_TABLE_CATEGORIES);
            db.execSQL(CREATE_TABLE_PRODUCTS);
            db.execSQL(CREATE_TABLE_CART);
            db.execSQL(CREATE_TABLE_ORDERS);
            db.execSQL(CREATE_TABLE_ORDER_ITEMS);
            db.execSQL(CREATE_TABLE_STORE_LOCATIONS);
            db.execSQL(CREATE_TABLE_CHAT_MESSAGES);
            db.execSQL(CREATE_TABLE_NOTIFICATIONS);
            Log.d(TAG, "Database tables created successfully");
        } catch (Exception e) {
            Log.e(TAG, "Error creating tables", e);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        try {
            Log.w(TAG, "Upgrading database from version " + oldVersion + " to " + newVersion);

            // Drop all tables
            String[] tables = { TABLE_USERS, TABLE_CATEGORIES, TABLE_PRODUCTS, TABLE_CART,
                    TABLE_ORDERS, TABLE_ORDER_ITEMS, TABLE_STORE_LOCATIONS,
                    TABLE_CHAT_MESSAGES, TABLE_NOTIFICATIONS };

            for (String table : tables) {
                db.execSQL("DROP TABLE IF EXISTS " + table);
            }

            // Create new tables
            onCreate(db);
        } catch (Exception e) {
            Log.e(TAG, "Error upgrading database", e);
        }
    }

    // Helper methods
    public void closeDB() {
        SQLiteDatabase db = this.getReadableDatabase();
        if (db != null && db.isOpen()) {
            db.close();
        }
    }

    public void resetTables() {
        SQLiteDatabase db = this.getWritableDatabase();
        onUpgrade(db, db.getVersion(), DATABASE_VERSION);
    }

    // Transaction wrapper
    public void executeInTransaction(DatabaseOperation operation) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            db.beginTransaction();
            operation.execute(db);
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.e(TAG, "Error in transaction", e);
        } finally {
            db.endTransaction();
        }
    }

    // Interface for transaction operations
    public interface DatabaseOperation {
        void execute(SQLiteDatabase db);
    }
}