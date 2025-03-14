package com.example.group1_petfood.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class UserUtils {
    private static final String PREF_NAME = "user_pref";
    private static final String KEY_USER_ID = "user_id";
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
     * Kiểm tra xem người dùng đã đăng nhập hay chưa
     * @param context Context của ứng dụng
     * @return true nếu người dùng đã đăng nhập, false nếu chưa
     */
    public static boolean isLoggedIn(Context context) {
        if (context == null) {
            return false;
        }

        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean("is_logged_in", false);
    }
}