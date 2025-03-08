package com.example.group1_petfood.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * Lớp DatabaseInitializer dùng để khởi tạo và thêm dữ liệu mẫu cho cơ sở dữ liệu
 */
public class DatabaseInitializer {
    private static final String TAG = "DatabaseInitializer";
    private DatabaseHelper dbHelper;
    private Context context;

    public DatabaseInitializer(Context context) {
        this.context = context;
        this.dbHelper = new DatabaseHelper(context);
    }
    public void clearAllCartItems() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        try {
            db.execSQL("DELETE FROM cart");
            // Hoặc có thể sử dụng: db.delete("cart", null, null);
        } finally {
            db.close();
        }
    }
    /**
     * Khởi tạo cơ sở dữ liệu và thêm dữ liệu mẫu
     */
    public void initializeDatabase() {
        try {
            // Mở cơ sở dữ liệu để tạo các bảng
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            Log.d(TAG, "Cơ sở dữ liệu đã được khởi tạo thành công");
            // insertSampleData();
//            clearAllCartItems();
        } catch (Exception e) {
            Log.e(TAG, "Lỗi khi khởi tạo cơ sở dữ liệu", e);
        }
    }

    /**
     * Thêm dữ liệu mẫu vào cơ sở dữ liệu
     */
    private void insertSampleData() {
        dbHelper.executeInTransaction(db -> {
            try {
                // Thêm danh mục sản phẩm
                insertCategories(db);

                // Thêm sản phẩm thức ăn cho chó
                insertDogFoods(db);

                // Thêm sản phẩm thức ăn cho mèo
                insertCatFoods(db);

                Log.d(TAG, "Đã chèn dữ liệu mẫu thành công");
            } catch (Exception e) {
                Log.e(TAG, "Lỗi khi chèn dữ liệu mẫu", e);
            }
        });
    }

    /**
     * Thêm các danh mục sản phẩm
     */
    private void insertCategories(SQLiteDatabase db) {
        // Xóa dữ liệu cũ (nếu có)
        db.execSQL("DELETE FROM categories");

        // Thêm các danh mục mới
        db.execSQL("INSERT OR IGNORE INTO categories (id, name, description, image_url) VALUES (1, 'Thức ăn cho chó', 'Các loại thức ăn dinh dưỡng dành cho chó', 'dog_food_category')");
        db.execSQL("INSERT OR IGNORE INTO categories (id, name, description, image_url) VALUES (2, 'Thức ăn cho mèo', 'Các loại thức ăn dinh dưỡng dành cho mèo', 'cat_food_category')");
        db.execSQL("INSERT OR IGNORE INTO categories (id, name, description, image_url) VALUES (3, 'Phụ kiện', 'Các loại phụ kiện dành cho thú cưng', 'accessories_category')");
        db.execSQL("INSERT OR IGNORE INTO categories (id, name, description, image_url) VALUES (4, 'Đồ chơi', 'Đồ chơi cho thú cưng', 'toys_category')");
        db.execSQL("INSERT OR IGNORE INTO categories (id, name, description, image_url) VALUES (5, 'Chăm sóc sức khỏe', 'Sản phẩm chăm sóc sức khỏe thú cưng', 'healthcare_category')");
    }

    /**
     * Thêm sản phẩm thức ăn cho chó
     */
    private void insertDogFoods(SQLiteDatabase db) {
        // Xóa dữ liệu cũ (nếu có)
        db.execSQL("DELETE FROM products WHERE category_id = 1");

        // Thêm sản phẩm thức ăn cho chó (từ hình 1)
        db.execSQL("INSERT OR IGNORE INTO products (category_id, name, brand, description, price, stock_quantity, image_url) " +
                "VALUES (1, 'Hạt Keos cho chó con, vị gà trứng sữa', 'Keos', 'Thức ăn cho chó con từ 2-12 tháng tuổi, giàu canxi và DHA', 28000, 100, 'dog_food_1_v1')");

        db.execSQL("INSERT OR IGNORE INTO products (category_id, name, brand, description, price, stock_quantity, image_url) " +
                "VALUES (1, 'Hạt Keos cho chó trưởng thành, vị bò rau củ', 'Keos', 'Thức ăn cao cấp dành cho chó trưởng thành, giàu protein', 23000, 150, 'dog_food_2_v1')");

        db.execSQL("INSERT OR IGNORE INTO products (category_id, name, brand, description, price, stock_quantity, image_url) " +
                "VALUES (1, 'Hạt Keos cho chó trưởng thành, vị cá biển & rau củ', 'Keos', 'Thức ăn hỗ trợ da lông, tiêu hóa tốt cho chó trưởng thành', 86000, 80, 'dog_food_3_v1')");

        db.execSQL("INSERT OR IGNORE INTO products (category_id, name, brand, description, price, stock_quantity, image_url) " +
                "VALUES (1, 'Hạt Keos cho chó trưởng thành, vị gà rau củ', 'Keos', 'Thức ăn cho chó trưởng thành từ 1-7 tuổi, cân bằng dinh dưỡng', 23000, 120, 'dog_food_4_v1')");

        db.execSQL("INSERT OR IGNORE INTO products (category_id, name, brand, description, price, stock_quantity, image_url) " +
                "VALUES (1, 'Hạt Keos+ bổ sung năng lượng cho chó, vị thịt gà & cừu', 'Keos+', 'Thức ăn cao cấp hỗ trợ năng lượng cho chó hoạt động nhiều', 37000, 90, 'dog_food_5_v1')");

        db.execSQL("INSERT OR IGNORE INTO products (category_id, name, brand, description, price, stock_quantity, image_url) " +
                "VALUES (1, 'Hạt Keos+ Dành Riêng Cho Chó Size Nhỏ, Vị Thịt Cừu & Gạo', 'Keos+', 'Thức ăn cao cấp dành riêng cho chó size nhỏ dưới 10kg', 158000, 65, 'dog_food_6_v1')");

        db.execSQL("INSERT OR IGNORE INTO products (category_id, name, brand, description, price, stock_quantity, image_url) " +
                "VALUES (1, 'Hạt Keos+ bổ trợ tiêu hóa cho chó, vị thịt cừu & gạo', 'Keos+', 'Thức ăn hỗ trợ tiêu hóa và đường ruột cho chó nhạy cảm', 32000, 75, 'dog_food_7_v1')");

        db.execSQL("INSERT OR IGNORE INTO products (category_id, name, brand, description, price, stock_quantity, image_url) " +
                "VALUES (1, 'Hạt Novopet cho chó trưởng thành - Vị thịt gà', 'Novopet', 'Thức ăn nhập khẩu cho chó trưởng thành, nhiều protein và ít chất béo', 75000, 60, 'dog_food_8_v1')");

        db.execSQL("INSERT OR IGNORE INTO products (category_id, name, brand, description, price, stock_quantity, image_url) " +
                "VALUES (1, 'Pate Plaisir cho chó vị gà sữa - Nhập khẩu chính hãng Pháp', 'Plaisir', 'Pate cao cấp nhập khẩu từ Pháp cho chó mọi lứa tuổi', 29000, 200, 'dog_food_8_v2')");
    }

    /**
     * Thêm sản phẩm thức ăn cho mèo
     */
    private void insertCatFoods(SQLiteDatabase db) {
        // Xóa dữ liệu cũ (nếu có)
        db.execSQL("DELETE FROM products WHERE category_id = 2");

        // Thêm sản phẩm thức ăn cho mèo (từ hình 2)
        db.execSQL("INSERT OR IGNORE INTO products (category_id, name, brand, description, price, stock_quantity, image_url) " +
                "VALUES (2, 'Hạt Keos cho mèo mọi lứa tuổi vị cá ngừ', 'Keos', 'Thức ăn cho mèo mọi độ tuổi, giàu omega 3 và 6', 27000, 100, 'cat_food_1_v1')");

        db.execSQL("INSERT OR IGNORE INTO products (category_id, name, brand, description, price, stock_quantity, image_url) " +
                "VALUES (2, 'Hạt Keos cho mèo mọi lứa tuổi vị gà', 'Keos', 'Thức ăn dành cho mèo kén ăn mọi lứa tuổi', 110000, 80, 'cat_food_2_v1')");

        db.execSQL("INSERT OR IGNORE INTO products (category_id, name, brand, description, price, stock_quantity, image_url) " +
                "VALUES (2, 'Hạt Keos cho mèo mọi lứa tuổi vị hải sản', 'Keos', 'Thức ăn cân bằng dinh dưỡng cho mèo từ 1 tuổi trở lên', 27000, 90, 'cat_food_3_v1')");

        db.execSQL("INSERT OR IGNORE INTO products (category_id, name, brand, description, price, stock_quantity, image_url) " +
                "VALUES (2, 'Hạt Keos+ dành riêng cho mèo con, vị cá ngừ', 'Keos+', 'Thức ăn giàu DHA và canxi cho mèo con dưới 12 tháng tuổi', 80000, 70, 'cat_food_4_v1')");

        db.execSQL("INSERT OR IGNORE INTO products (category_id, name, brand, description, price, stock_quantity, image_url) " +
                "VALUES (2, 'Hạt Keos+ bổ trợ giảm búi lông cho mèo, vị cá ngừ', 'Keos+', 'Thức ăn chức năng giúp giảm búi lông, hỗ trợ tiêu hóa cho mèo', 42000, 60, 'cat_food_5_v1')");

        db.execSQL("INSERT OR IGNORE INTO products (category_id, name, brand, description, price, stock_quantity, image_url) " +
                "VALUES (2, 'Hạt Keos+ bổ trợ tiết niệu cho mèo, vị cá biển', 'Keos+', 'Thức ăn hỗ trợ hệ tiết niệu và ngăn ngừa sỏi cho mèo', 44000, 55, 'cat_food_6_v1')");

        db.execSQL("INSERT OR IGNORE INTO products (category_id, name, brand, description, price, stock_quantity, image_url) " +
                "VALUES (2, 'Thức ăn dinh dưỡng cho mèo trưởng thành NOVOPET Cat', 'Novopet', 'Thức ăn nhập khẩu cao cấp cho mèo trưởng thành', 55000, 70, 'cat_food_7_v1')");
    }

    /**
     * Đóng kết nối cơ sở dữ liệu
     */
    public void closeDatabase() {
        if (dbHelper != null) {
            dbHelper.closeDB();
            Log.d(TAG, "Đã đóng kết nối cơ sở dữ liệu");
        }
    }
}