package com.example.group1_petfood.activities;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.group1_petfood.R;
import com.example.group1_petfood.adapters.UserAdapter;
import com.example.group1_petfood.controllers.UserController;
import com.example.group1_petfood.models.User;
import com.example.group1_petfood.models.UserRole;
import com.example.group1_petfood.utils.AccessControl;
import com.example.group1_petfood.utils.UserUtils;

import java.util.ArrayList;
import java.util.List;

public class UserManagementActivity extends AppCompatActivity {
    private RecyclerView recyclerViewUsers;
    private UserAdapter userAdapter;
    private UserController userController;
    private EditText searchEditText;
    private List<User> userList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_management);

        // Kiểm tra quyền Admin
        if (!AccessControl.requireAdmin(this)) {
            finish();
            return;
        }

        // Khởi tạo UserController
        userController = new UserController(this);

        // Ánh xạ view
        searchEditText = findViewById(R.id.searchEditText);
        recyclerViewUsers = findViewById(R.id.recyclerViewUsers);

        // Thiết lập RecyclerView
        recyclerViewUsers.setLayoutManager(new LinearLayoutManager(this));
        loadUsers();

        // Xử lý sự kiện tìm kiếm người dùng
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterUsers(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    // Tải danh sách người dùng
    private void loadUsers() {
        userList = userController.getAllUsers();
        userAdapter = new UserAdapter(userList, this, (user, option) -> {
            switch (option) {
                case VIEW:
                    viewUserDetails(user);
                    break;
                case EDIT:
                    showRoleChangeDialog(user);
                    break;
                case DELETE:
                    confirmDeleteUser(user);
                    break;
            }
        });
        recyclerViewUsers.setAdapter(userAdapter);
    }

    // Hiển thị chi tiết người dùng
    private void viewUserDetails(User user) {
        // Có thể thực hiện nhiều hơn trong tương lai
        Toast.makeText(this, "Xem chi tiết: " + user.getUsername(), Toast.LENGTH_SHORT).show();
    }

    // Hiển thị hộp thoại thay đổi vai trò người dùng
    private void showRoleChangeDialog(User user) {
        String[] roles = {"Admin", "Staff", "Customer"};
        int currentRoleIndex = 0;

        switch (user.getRole()) {
            case ADMIN:
                currentRoleIndex = 0;
                break;
            case STAFF:
                currentRoleIndex = 1;
                break;
            case CUSTOMER:
                currentRoleIndex = 2;
                break;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Thay đổi vai trò người dùng");
        builder.setSingleChoiceItems(roles, currentRoleIndex, null);
        builder.setPositiveButton("Cập nhật", (dialog, which) -> {
            int selectedPosition = ((AlertDialog) dialog).getListView().getCheckedItemPosition();
            UserRole newRole;

            switch (selectedPosition) {
                case 0:
                    newRole = UserRole.ADMIN;
                    break;
                case 1:
                    newRole = UserRole.STAFF;
                    break;
                default:
                    newRole = UserRole.CUSTOMER;
                    break;
            }

            updateUserRole(user, newRole);
        });

        builder.setNegativeButton("Hủy", null);
        builder.show();
    }

    // Cập nhật vai trò người dùng
    private void updateUserRole(User user, UserRole newRole) {
        if (userController.updateUserRole(user.getId(), newRole)) {
            Toast.makeText(this, "Đã cập nhật vai trò thành " + newRole.getRoleName(), Toast.LENGTH_SHORT).show();
            loadUsers(); // Tải lại danh sách
        } else {
            Toast.makeText(this, "Không thể cập nhật vai trò", Toast.LENGTH_SHORT).show();
        }
    }

    // Xác nhận xóa người dùng
    private void confirmDeleteUser(User user) {
        // Không cho phép xóa chính mình
        int currentUserId = UserUtils.getCurrentUserId(this);
        if (user.getId() == currentUserId) {
            Toast.makeText(this, "Không thể xóa tài khoản đang sử dụng", Toast.LENGTH_SHORT).show();
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Xác nhận xóa");
        builder.setMessage("Bạn có chắc chắn muốn xóa người dùng " + user.getUsername() + "?");
        builder.setPositiveButton("Xóa", (dialog, which) -> {
            if (userController.deleteUser(user.getId())) {
                Toast.makeText(UserManagementActivity.this, "Đã xóa người dùng", Toast.LENGTH_SHORT).show();
                loadUsers(); // Tải lại danh sách
            } else {
                Toast.makeText(UserManagementActivity.this, "Không thể xóa người dùng", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Hủy", null);
        builder.show();
    }

    // Lọc danh sách người dùng theo từ khóa
    private void filterUsers(String query) {
        List<User> filteredList = new ArrayList<>();
        for (User user : userList) {
            if (user.getUsername().toLowerCase().contains(query.toLowerCase()) ||
                    user.getEmail().toLowerCase().contains(query.toLowerCase()) ||
                    user.getFullName().toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(user);
            }
        }
        userAdapter.updateList(filteredList);
    }
}