package com.example.group1_petfood.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.example.group1_petfood.activities.LoginActivity;
import com.example.group1_petfood.controllers.UserController;
import com.example.group1_petfood.models.User;
import com.example.group1_petfood.models.UserRole;


public class AccessControl {

    public static boolean isAdmin(Context context) {
        int userId = UserUtils.getCurrentUserId(context);
        UserController userController = new UserController(context);
        User user = userController.getUserById(userId);
        return user != null && user.getRole() == UserRole.ADMIN;
    }


    public static boolean isStaff(Context context) {
        int userId = UserUtils.getCurrentUserId(context);
        UserController userController = new UserController(context);
        User user = userController.getUserById(userId);
        return user != null && user.getRole() == UserRole.STAFF;
    }


    public static boolean isAdminOrStaff(Context context) {
        int userId = UserUtils.getCurrentUserId(context);
        UserController userController = new UserController(context);
        return userController.isAdminOrStaff(userId);
    }

    public static boolean requireAdmin(Activity activity) {
        if (!isAdmin(activity)) {
            showAccessDeniedMessage(activity, "Tính năng này chỉ dành cho Admin");
            return false;
        }
        return true;
    }


    public static boolean requireAdminOrStaff(Activity activity) {
        if (!isAdminOrStaff(activity)) {
            showAccessDeniedMessage(activity, "Tính năng này chỉ dành cho nhân viên");
            return false;
        }
        return true;
    }

    public static boolean requireLogin(Activity activity) {
        if (!UserUtils.isLoggedIn(activity)) {
            Toast.makeText(activity, "Vui lòng đăng nhập để tiếp tục", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(activity, LoginActivity.class);
            activity.startActivity(intent);
            return false;
        }
        return true;
    }

    private static void showAccessDeniedMessage(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }
}