package com.example.group1_petfood.controllers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.group1_petfood.database.DatabaseHelper;
import com.example.group1_petfood.models.User;

import java.util.ArrayList;
import java.util.List;

public class UserController {
    private static final String TAG = "UserController";
    private DatabaseHelper dbHelper;
    private Context context;

    public UserController(Context context) {
        this.context = context;
        this.dbHelper = new DatabaseHelper(context);
    }

    /**
     * Thêm người dùng mới vào cơ sở dữ liệu
     * @param user Đối tượng User cần thêm
     * @return ID của người dùng mới nếu thành công, -1 nếu thất bại
     */
    public long registerUser(User user) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        long userId = -1;

        try {
            ContentValues values = new ContentValues();
            values.put("username", user.getUsername());
            values.put("email", user.getEmail());
            values.put("password", user.getPassword()); // Lưu ý: Trong ứng dụng thực tế, nên mã hóa mật khẩu
            values.put("full_name", user.getFullName());
            values.put("phone", user.getPhone());
            values.put("address", user.getAddress());
            values.put("created_at", getCurrentTimestamp());
            values.put("updated_at", getCurrentTimestamp());

            userId = db.insert("users", null, values);
            Log.d(TAG, "Đã đăng ký người dùng mới với ID: " + userId);
        } catch (Exception e) {
            Log.e(TAG, "Lỗi khi đăng ký người dùng: " + e.getMessage());
            e.printStackTrace();
        } finally {
            db.close();
        }

        return userId;
    }

    /**
     * Kiểm tra xem email đã tồn tại trong cơ sở dữ liệu chưa
     * @param email Email cần kiểm tra
     * @return true nếu email đã tồn tại, false nếu chưa
     */
    public boolean isEmailExists(String email) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        boolean exists = false;

        try {
            Cursor cursor = db.query("users", new String[]{"id"},
                    "email = ?", new String[]{email},
                    null, null, null);
            exists = cursor.getCount() > 0;
            cursor.close();
        } catch (Exception e) {
            Log.e(TAG, "Lỗi khi kiểm tra email: " + e.getMessage());
            e.printStackTrace();
        } finally {
            db.close();
        }

        return exists;
    }

    /**
     * Kiểm tra tài khoản đăng nhập
     * @param email Email người dùng
     * @param password Mật khẩu
     * @return User nếu thông tin đăng nhập đúng, null nếu không đúng
     */
    public User checkLogin(String email, String password) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        User user = null;

        try {
            Cursor cursor = db.query("users", null,
                    "email = ? AND password = ?", new String[]{email, password},
                    null, null, null);

            if (cursor.moveToFirst()) {
                user = new User();
                user.setId(cursor.getInt(cursor.getColumnIndexOrThrow("id")));
                user.setUsername(cursor.getString(cursor.getColumnIndexOrThrow("username")));
                user.setEmail(cursor.getString(cursor.getColumnIndexOrThrow("email")));
                user.setFullName(cursor.getString(cursor.getColumnIndexOrThrow("full_name")));
                user.setPhone(cursor.getString(cursor.getColumnIndexOrThrow("phone")));
                user.setAddress(cursor.getString(cursor.getColumnIndexOrThrow("address")));
                user.setCreatedAt(cursor.getString(cursor.getColumnIndexOrThrow("created_at")));
                user.setUpdatedAt(cursor.getString(cursor.getColumnIndexOrThrow("updated_at")));
            }
            cursor.close();
        } catch (Exception e) {
            Log.e(TAG, "Lỗi khi kiểm tra đăng nhập: " + e.getMessage());
            e.printStackTrace();
        } finally {
            db.close();
        }

        return user;
    }

    /**
     * Lấy thông tin người dùng theo ID
     * @param userId ID của người dùng
     * @return Đối tượng User nếu tìm thấy, null nếu không tìm thấy
     */
    public User getUserById(int userId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        User user = null;

        try {
            Cursor cursor = db.query("users", null,
                    "id = ?", new String[]{String.valueOf(userId)},
                    null, null, null);

            if (cursor.moveToFirst()) {
                user = new User();
                user.setId(cursor.getInt(cursor.getColumnIndexOrThrow("id")));
                user.setUsername(cursor.getString(cursor.getColumnIndexOrThrow("username")));
                user.setEmail(cursor.getString(cursor.getColumnIndexOrThrow("email")));
                user.setFullName(cursor.getString(cursor.getColumnIndexOrThrow("full_name")));
                user.setPhone(cursor.getString(cursor.getColumnIndexOrThrow("phone")));
                user.setAddress(cursor.getString(cursor.getColumnIndexOrThrow("address")));
                user.setCreatedAt(cursor.getString(cursor.getColumnIndexOrThrow("created_at")));
                user.setUpdatedAt(cursor.getString(cursor.getColumnIndexOrThrow("updated_at")));
            }
            cursor.close();
        } catch (Exception e) {
            Log.e(TAG, "Lỗi khi lấy thông tin người dùng: " + e.getMessage());
            e.printStackTrace();
        } finally {
            db.close();
        }

        return user;
    }

    /**
     * Cập nhật thông tin người dùng
     * @param user Đối tượng User với thông tin đã cập nhật
     * @return true nếu cập nhật thành công, false nếu thất bại
     */
    public boolean updateUser(User user) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        boolean success = false;

        try {
            ContentValues values = new ContentValues();
            values.put("username", user.getUsername());
            values.put("email", user.getEmail());
            values.put("full_name", user.getFullName());
            values.put("phone", user.getPhone());
            values.put("address", user.getAddress());
            values.put("updated_at", getCurrentTimestamp());

            int rowsAffected = db.update("users", values,
                    "id = ?", new String[]{String.valueOf(user.getId())});
            success = rowsAffected > 0;
        } catch (Exception e) {
            Log.e(TAG, "Lỗi khi cập nhật thông tin người dùng: " + e.getMessage());
            e.printStackTrace();
        } finally {
            db.close();
        }

        return success;
    }

    /**
     * Cập nhật mật khẩu người dùng
     * @param userId ID của người dùng
     * @param newPassword Mật khẩu mới
     * @return true nếu cập nhật thành công, false nếu thất bại
     */
    public boolean updatePassword(int userId, String newPassword) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        boolean success = false;

        try {
            ContentValues values = new ContentValues();
            values.put("password", newPassword); // Lưu ý: Nên mã hóa mật khẩu
            values.put("updated_at", getCurrentTimestamp());

            int rowsAffected = db.update("users", values,
                    "id = ?", new String[]{String.valueOf(userId)});
            success = rowsAffected > 0;
        } catch (Exception e) {
            Log.e(TAG, "Lỗi khi cập nhật mật khẩu: " + e.getMessage());
            e.printStackTrace();
        } finally {
            db.close();
        }

        return success;
    }

    /**
     * Lấy danh sách tất cả người dùng (thường chỉ dành cho admin)
     * @return Danh sách các đối tượng User
     */
    public List<User> getAllUsers() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        List<User> userList = new ArrayList<>();

        try {
            Cursor cursor = db.query("users", null,
                    null, null, null, null, "id ASC");

            if (cursor.moveToFirst()) {
                do {
                    User user = new User();
                    user.setId(cursor.getInt(cursor.getColumnIndexOrThrow("id")));
                    user.setUsername(cursor.getString(cursor.getColumnIndexOrThrow("username")));
                    user.setEmail(cursor.getString(cursor.getColumnIndexOrThrow("email")));
                    user.setFullName(cursor.getString(cursor.getColumnIndexOrThrow("full_name")));
                    user.setPhone(cursor.getString(cursor.getColumnIndexOrThrow("phone")));
                    user.setAddress(cursor.getString(cursor.getColumnIndexOrThrow("address")));
                    user.setCreatedAt(cursor.getString(cursor.getColumnIndexOrThrow("created_at")));
                    user.setUpdatedAt(cursor.getString(cursor.getColumnIndexOrThrow("updated_at")));

                    userList.add(user);
                } while (cursor.moveToNext());
            }
            cursor.close();
        } catch (Exception e) {
            Log.e(TAG, "Lỗi khi lấy danh sách người dùng: " + e.getMessage());
            e.printStackTrace();
        } finally {
            db.close();
        }

        return userList;
    }

    /**
     * Xóa người dùng (thường chỉ dành cho admin)
     * @param userId ID của người dùng cần xóa
     * @return true nếu xóa thành công, false nếu thất bại
     */
    public boolean deleteUser(int userId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        boolean success = false;

        try {
            int rowsAffected = db.delete("users",
                    "id = ?", new String[]{String.valueOf(userId)});
            success = rowsAffected > 0;
        } catch (Exception e) {
            Log.e(TAG, "Lỗi khi xóa người dùng: " + e.getMessage());
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
}