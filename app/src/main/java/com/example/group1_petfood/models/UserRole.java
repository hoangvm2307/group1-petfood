package com.example.group1_petfood.models;

// Enum để định nghĩa các vai trò trong hệ thống
public enum UserRole {
    ADMIN("admin"),    // Quản trị viên cao nhất, có toàn quyền
    STAFF("staff"),    // Nhân viên, có quyền quản lý sản phẩm, đơn hàng, không thể quản lý người dùng khác
    CUSTOMER("customer"); // Khách hàng, chỉ có quyền đặt hàng và quản lý tài khoản của mình

    private final String roleName;

    UserRole(String roleName) {
        this.roleName = roleName;
    }

    public String getRoleName() {
        return roleName;
    }

    // Chuyển đổi từ chuỗi sang enum
    public static UserRole fromString(String roleName) {
        for (UserRole role : UserRole.values()) {
            if (role.getRoleName().equalsIgnoreCase(roleName)) {
                return role;
            }
        }
        // Mặc định là CUSTOMER nếu không tìm thấy vai trò phù hợp
        return CUSTOMER;
    }
}