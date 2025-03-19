package com.example.group1_petfood.models;

public class User {
    private int id;
    private String username;
    private String email;
    private String password;
    private String fullName;
    private String phone;
    private String address;
    private UserRole role; // Thêm thuộc tính role
    private String createdAt;
    private String updatedAt;

    // Constructor
    public User() {
        // Mặc định người dùng mới sẽ có vai trò CUSTOMER
        this.role = UserRole.CUSTOMER;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public UserRole getRole() { return role; }
    public void setRole(UserRole role) { this.role = role; }

    // Đặt role từ chuỗi
    public void setRoleFromString(String roleStr) {
        this.role = UserRole.fromString(roleStr);
    }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    public String getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(String updatedAt) { this.updatedAt = updatedAt; }
}