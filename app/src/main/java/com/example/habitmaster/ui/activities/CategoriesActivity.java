package com.example.habitmaster.ui.activities;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.habitmaster.R;
import com.example.habitmaster.domain.models.Category;
import com.example.habitmaster.services.CategoryService;
import com.example.habitmaster.ui.adapters.CategoryAdapter;
import com.example.habitmaster.utils.Prefs;

import java.util.List;
import java.util.UUID;

import yuku.ambilwarna.AmbilWarnaDialog;

public class CategoriesActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private Button btnAddCategory;
    private CategoryAdapter adapter;
    private CategoryService categoryService;
    private Prefs prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categories);

        prefs =  new Prefs(this);

        recyclerView = findViewById(R.id.recyclerViewCategories);
        btnAddCategory = findViewById(R.id.btnAddCategory);

        categoryService = new CategoryService(this);

        List<Category> categories = categoryService.getUserCategories(prefs.getUid());

        adapter = new CategoryAdapter(this, categories);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        btnAddCategory.setOnClickListener(v -> {
//            openAddCategoryDialog();
            openCategoryDialog(null);
        });

    }

    public void openCategoryDialog(@Nullable Category category) {
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_category, null);

        EditText input = dialogView.findViewById(R.id.etCategoryName);
        View colorPreview = dialogView.findViewById(R.id.viewColorPreview);

        final int[] selectedColor = {category != null ? category.getColor() : Color.parseColor("#6200EE")};
        colorPreview.setBackgroundColor(selectedColor[0]);

        if (category != null) {
            input.setText(category.getName());
        }

        colorPreview.setOnClickListener(v -> {
            AmbilWarnaDialog colorPicker = new AmbilWarnaDialog(this, selectedColor[0], new AmbilWarnaDialog.OnAmbilWarnaListener() {
                @Override
                public void onOk(AmbilWarnaDialog dialog, int color) {
                    selectedColor[0] = color;
                    colorPreview.setBackgroundColor(color);
                }

                @Override
                public void onCancel(AmbilWarnaDialog dialog) {}
            });
            colorPicker.show();
        });

        String title = category == null ? "Add New Category" : "Edit Category";

        new AlertDialog.Builder(this)
                .setTitle(title)
                .setView(dialogView)
                .setPositiveButton(category == null ? "Add" : "Save", (dialog, which) -> {
                    String name = input.getText().toString().trim();
                    if (!name.isEmpty()) {
                        if (category == null) {
                            Category newCategory = new Category();
                            newCategory.setId(UUID.randomUUID().toString());
                            newCategory.setUserId(prefs.getUid());
                            newCategory.setName(name);
                            newCategory.setColor(selectedColor[0]);

                            categoryService.addCategory(newCategory, new CategoryService.Callback() {
                                @Override
                                public void onSuccess(Category category) {
                                    adapter.addCategory(category);
                                    recyclerView.scrollToPosition(adapter.getItemCount() - 1);
                                }

                                @Override
                                public void onError(String errorMessage) {
                                    Toast.makeText(CategoriesActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                                }
                            });

                        } else {
                            category.setName(name);
                            category.setColor(selectedColor[0]);
                            categoryService.updateCategory(category, new CategoryService.Callback() {
                                @Override
                                public void onSuccess(Category category) {
                                    adapter.updateCategory(category);
                                }

                                @Override
                                public void onError(String errorMessage) {
                                    Toast.makeText(CategoriesActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    } else {
                        Toast.makeText(this, "Category name cannot be empty", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}