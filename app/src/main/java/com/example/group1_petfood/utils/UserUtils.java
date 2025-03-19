package com.example.group1_petfood.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.group1_petfood.models.UserRole;

public class UserUtils {
    private static final String PREF_NAME = "user_pref";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_IS_LOGGED_IN = "is_logged_in";
    private static final String KEY_USER_ROLE = "user_role"; // Thêm key cho role
    private static final int DEFAULT_USER_ID = 1;

    /**
     * Lấy ID của người dùng đang đăng nhập hiện tại
     * @param context Context của ứng dụng
     * @return ID người dùng đang đăng nhập, hoặc DEFAULT_USER_ID nếu chưa đăng nhập
     */
    public static int getCurrentUserId(Context context) {
        if (context == null) {
            return DEFAULT_USER_ID;
        }

        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getInt(KEY_USER_ID, DEFAULT_USER_ID);
    }

    /**
     * Lấy vai trò của người dùng hiện tại
     * @param context Context của ứng dụng
     * @return UserRole của người dùng hiện tại (mặc định là CUSTOMER)
     */
    public static UserRole getCurrentUserRole(Context context) {
        if (context == null) {
            return UserRole.CUSTOMER;
        }

        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        String roleStr = sharedPreferences.getString(KEY_USER_ROLE, UserRole.CUSTOMER.getRoleName());
        return UserRole.fromString(roleStr);
    }

    /**
     * Kiểm tra xem người dùng đã đăng nhập hay chưa
     * @param context Context của ứng dụng
     * @return true nếu người dùng đã đăng nhập, false nếu chưa
     */
    public static boolean isLoggedIn(Context context) {
        if (context == null) {
            return false;
        }

        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    /**
     * Kiểm tra xem người dùng hiện tại có phải là Admin không
     * @param context Context của ứng dụng
     * @return true nếu người dùng hiện tại là Admin, false nếu không phải
     */
    public static boolean isAdmin(Context context) {
        return getCurrentUserRole(context) == UserRole.ADMIN;
    }

    /**
     * Kiểm tra xem người dùng hiện tại có phải là Staff không
     * @param context Context của ứng dụng
     * @return true nếu người dùng hiện tại là Staff, false nếu không phải
     */
    public static boolean isStaff(Context context) {
        return getCurrentUserRole(context) == UserRole.STAFF;
    }

    /**
     * Kiểm tra xem người dùng hiện tại có phải là Admin hoặc Staff không
     * @param context Context của ứng dụng
     * @return true nếu người dùng hiện tại là Admin hoặc Staff, false nếu không phải
     */
    public static boolean isAdminOrStaff(Context context) {
        UserRole role = getCurrentUserRole(context);
        return role == UserRole.ADMIN || role == UserRole.STAFF;
    }

    /**
     * Lưu vai trò của người dùng hiện tại
     * @param context Context của ứng dụng
     * @param role Vai trò mới của người dùng
     */
    public static void saveUserRole(Context context, UserRole role) {
        if (context == null) {
            return;
        }

        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_USER_ROLE, role.getRoleName());
        editor.apply();
    }

    /**
     * Đăng xuất người dùng (xóa thông tin đăng nhập)
     * @param context Context của ứng dụng
     */
    public static void logout(Context context) {
        if (context == null) {
            return;
        }

        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }
}