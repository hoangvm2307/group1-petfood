package com.example.group1_petfood.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.content.DialogInterface;
import android.widget.Toast;
import android.net.Uri;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.bumptech.glide.Glide;
import com.example.group1_petfood.R;
import com.example.group1_petfood.adapters.AdminCategoryAdapter;
import com.example.group1_petfood.database.DatabaseHelper;
import com.example.group1_petfood.models.Category;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.example.group1_petfood.controllers.CategoryController;

import java.util.List;

public class CategoryListActivity extends AppCompatActivity {
    private DatabaseHelper dbhelper;
    private RecyclerView recyclerView;
    private FloatingActionButton fabAdd;
    private List<Category> categoryList;
    private AdminCategoryAdapter adapter;
    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri imageUri;
    private CategoryController categoryController;
    private ImageView tempImageView;

    private ImageView imageViewCategory;
    @Override
    public void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_list);

        Log.d("DEBUG_ACTIVITY", "CategoryListActivity started!");

        dbhelper = new DatabaseHelper(this);

        recyclerView = findViewById(R.id.recyclerViewCategories);
        fabAdd = findViewById(R.id.fabAddCategory);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        loadCategories();

        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAddCategoryDialog();
            }
        });
    }

    private void showAddCategoryDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add Category");

        View view = LayoutInflater.from(this).inflate(R.layout.dialog_add_category, null);
        final EditText editTextCategoryName = view.findViewById(R.id.editTextCategoryName);
        final EditText editTextCategoryDescription = view.findViewById(R.id.editTextCategoryDescription);
        imageViewCategory = view.findViewById(R.id.imageViewCategory);

        tempImageView = imageViewCategory; // Lưu vào biến tạm

        imageViewCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
            }
        });
        builder.setView(view);


        builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String name = editTextCategoryName.getText().toString().trim();
                String description = editTextCategoryDescription.getText().toString().trim();
                String imageUrl = (imageUri != null) ? imageUri.toString() : "";


                if (name.isEmpty()) {
                    Toast.makeText(CategoryListActivity.this, "Please enter category name", Toast.LENGTH_SHORT).show();
                } else {
                    long id = categoryController.addCategory(name, description, imageUrl);
                    if (id != -1) {
                        loadCategories();
                        Toast.makeText(CategoryListActivity.this, "Category added successfully", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(CategoryListActivity.this, "Failed to add category", Toast.LENGTH_SHORT).show();
                    }
                }
            }



        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.create().show();
    }



    private void loadCategories() {
        categoryController = new CategoryController(this);
        categoryList = categoryController.getAllCategories();
         Log.d("DEBUG_CATEGORY", "Size of categoryList: " + (categoryList != null ? categoryList.size() : "null"));
        adapter = new AdminCategoryAdapter(this, categoryList);
        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(new AdminCategoryAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                showUpdateDeleteDialog(position);
            }
        });
    }

    private void showUpdateDeleteDialog(final int position) {
        final Category category = categoryList.get(position);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Update/Delete Category");

        View view = LayoutInflater.from(this).inflate(R.layout.dialog_add_category, null);
        final EditText editTextCategoryName = view.findViewById(R.id.editTextCategoryName);
        final EditText editTextCategoryDescription = view.findViewById(R.id.editTextCategoryDescription);
        final ImageView imageViewCategory = view.findViewById(R.id.imageViewCategory);

        tempImageView = imageViewCategory;

        editTextCategoryName.setText(category.getName());
        editTextCategoryDescription.setText(category.getDescription());

        String imageUrl = category.getImageUrl();
        if (imageUrl != null && !imageUrl.isEmpty()) {
            Glide.with(this)
                    .load(imageUrl)
                    .placeholder(R.drawable.slide_1_img)
                    .error(R.drawable.slide_1_img)
                    .into(imageViewCategory);
        } else {
            imageViewCategory.setImageResource(R.drawable.slide_1_img);
        }

        imageViewCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openFileChooser();
            }
        });

        builder.setView(view);
        builder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String name = editTextCategoryName.getText().toString().trim();
                String description = editTextCategoryDescription.getText().toString().trim();
                String newImageUrl = (imageUri != null) ? imageUri.toString() : category.getImageUrl();
                if (name.isEmpty()) {
                    Toast.makeText(CategoryListActivity.this, "Please enter category name", Toast.LENGTH_SHORT).show();
                } else {
                    category.setName(name);
                    category.setDescription(description);
                    category.setImageUrl(newImageUrl);
                    int result = categoryController.updateCategory(category);
                    if (result > 0) {
                        loadCategories();
                        Toast.makeText(CategoryListActivity.this, "Category updated successfully", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(CategoryListActivity.this, "Failed to update category", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        builder.setNegativeButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                confirmDelete(category.getId());
            }
        });

        builder.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    private void confirmDelete(final int categoryId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirm Delete");
        builder.setMessage("Are you sure you want to delete this category?");

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                categoryController.deleteCategory(categoryId);
                loadCategories();
                Toast.makeText(CategoryListActivity.this, "Category deleted successfully", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.create().show();
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();

            Log.d("DEBUG_IMAGE", "tempImageView: " + tempImageView);
            Log.d("DEBUG_IMAGE", "imageUri: " + imageUri.toString());

            if (tempImageView != null) {
                tempImageView.setImageURI(imageUri);
            } else {
                Log.e("DEBUG_IMAGE", "tempImageView is null");
            }
        }
    }
}
