package com.example.group1_petfood.controllers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.group1_petfood.database.DatabaseHelper;
import com.example.group1_petfood.models.User;
import com.example.group1_petfood.models.UserRole;

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
            values.put("password", user.getPassword());
            values.put("full_name", user.getFullName());
            values.put("phone", user.getPhone());
            values.put("address", user.getAddress());
            values.put("role", user.getRole().getRoleName()); // Lưu vai trò vào DB
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
     * Tạo tài khoản Admin đầu tiên nếu chưa có
     * @return true nếu tạo thành công hoặc đã tồn tại, false nếu có lỗi
     */
    public boolean createDefaultAdminIfNeeded() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        boolean success = true;

        try {
            // Kiểm tra xem đã có Admin nào chưa
            Cursor cursor = db.query(
                    "users",
                    new String[]{"count(*)"},
                    "role = ?",
                    new String[]{"admin"},
                    null, null, null);

            int adminCount = 0;
            if (cursor.moveToFirst()) {
                adminCount = cursor.getInt(0);
            }
            cursor.close();

            // Nếu chưa có Admin nào, tạo tài khoản Admin mặc định
            if (adminCount == 0) {
                User adminUser = new User();
                adminUser.setUsername("admin");
                adminUser.setEmail("admin@petfood.com");
                adminUser.setPassword("admin123"); // Trong thực tế, nên mã hóa mật khẩu
                adminUser.setFullName("Administrator");
                adminUser.setRole(UserRole.ADMIN);

                long id = registerUser(adminUser);
                success = id != -1;
                Log.d(TAG, "Tạo Admin mặc định " + (success ? "thành công" : "thất bại"));
            }
        } catch (Exception e) {
            Log.e(TAG, "Lỗi khi tạo Admin mặc định: " + e.getMessage());
            success = false;
        } finally {
            db.close();
        }

        return success;
    }

    /**
     * Kiểm tra xem người dùng có vai trò cụ thể không
     * @param userId ID của người dùng
     * @param role Vai trò cần kiểm tra
     * @return true nếu người dùng có vai trò đó, false nếu không
     */
    public boolean hasRole(int userId, UserRole role) {
        User user = getUserById(userId);
        return user != null && user.getRole() == role;
    }

    /**
     * Kiểm tra xem người dùng có quyền quản trị không (Admin hoặc Staff)
     * @param userId ID của người dùng
     * @return true nếu người dùng là Admin hoặc Staff, false nếu không
     */
    public boolean isAdminOrStaff(int userId) {
        User user = getUserById(userId);
        return user != null && (user.getRole() == UserRole.ADMIN || user.getRole() == UserRole.STAFF);
    }

    /**
     * Cập nhật vai trò của người dùng (chỉ Admin mới có quyền thực hiện)
     * @param userId ID của người dùng cần cập nhật
     * @param newRole Vai trò mới
     * @return true nếu cập nhật thành công, false nếu thất bại
     */
    public boolean updateUserRole(int userId, UserRole newRole) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        boolean success = false;

        try {
            ContentValues values = new ContentValues();
            values.put("role", newRole.getRoleName());
            values.put("updated_at", getCurrentTimestamp());

            int rowsAffected = db.update("users", values,
                    "id = ?", new String[]{String.valueOf(userId)});
            success = rowsAffected > 0;
        } catch (Exception e) {
            Log.e(TAG, "Lỗi khi cập nhật vai trò người dùng: " + e.getMessage());
            e.printStackTrace();
        } finally {
            db.close();
        }

        return success;
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
                user = extractUserFromCursor(cursor);
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
                user = extractUserFromCursor(cursor);
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
            // Chỉ Admin mới cập nhật được vai trò, nếu không sẽ không thay đổi vai trò
            // values.put("role", user.getRole().getRoleName());
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
                    User user = extractUserFromCursor(cursor);
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
     * Helper method để trích xuất thông tin User từ Cursor
     */
    private User extractUserFromCursor(Cursor cursor) {
        User user = new User();
        user.setId(cursor.getInt(cursor.getColumnIndexOrThrow("id")));
        user.setUsername(cursor.getString(cursor.getColumnIndexOrThrow("username")));
        user.setEmail(cursor.getString(cursor.getColumnIndexOrThrow("email")));
        user.setFullName(cursor.getString(cursor.getColumnIndexOrThrow("full_name")));
        user.setPhone(cursor.getString(cursor.getColumnIndexOrThrow("phone")));
        user.setAddress(cursor.getString(cursor.getColumnIndexOrThrow("address")));

        // Đọc và đặt vai trò từ cơ sở dữ liệu
        String roleStr = cursor.getString(cursor.getColumnIndexOrThrow("role"));
        user.setRoleFromString(roleStr);

        user.setCreatedAt(cursor.getString(cursor.getColumnIndexOrThrow("created_at")));
        user.setUpdatedAt(cursor.getString(cursor.getColumnIndexOrThrow("updated_at")));
        return user;
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

    public int getUserCount() {
        List<User> users = getAllUsers();
        return users.size();
    }

    /**
     * Lấy số lượng người dùng theo vai trò
     * @param role Vai trò cần đếm
     * @return Số lượng người dùng có vai trò đã chỉ định
     */
    public int getUserCountByRole(UserRole role) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        int count = 0;

        try {
            Cursor cursor = db.rawQuery(
                    "SELECT COUNT(*) FROM users WHERE role = ?",
                    new String[]{role.getRoleName()});

            if (cursor.moveToFirst()) {
                count = cursor.getInt(0);
            }
            cursor.close();
        } catch (Exception e) {
            Log.e(TAG, "Lỗi khi đếm người dùng theo vai trò: " + e.getMessage());
        } finally {
            db.close();
        }

        return count;
    }

    /**
     * Lấy danh sách người dùng theo vai trò
     * @param role Vai trò cần lọc
     * @return Danh sách người dùng có vai trò đã chỉ định
     */
    public List<User> getUsersByRole(UserRole role) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        List<User> userList = new ArrayList<>();

        try {
            Cursor cursor = db.query("users", null,
                    "role = ?", new String[]{role.getRoleName()},
                    null, null, "id ASC");

            if (cursor.moveToFirst()) {
                do {
                    User user = extractUserFromCursor(cursor);
                    userList.add(user);
                } while (cursor.moveToNext());
            }
            cursor.close();
        } catch (Exception e) {
            Log.e(TAG, "Lỗi khi lấy danh sách người dùng theo vai trò: " + e.getMessage());
        } finally {
            db.close();
        }

        return userList;
    }
}