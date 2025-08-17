package com.example.habitmaster.services;

import android.content.Context;

import com.example.habitmaster.data.repositories.CategoryRepository;
import com.example.habitmaster.domain.models.Category;
import com.example.habitmaster.domain.usecases.categories.AddCategoryUseCase;
import com.example.habitmaster.domain.usecases.categories.GetUserCategoriesUseCase;
import com.example.habitmaster.domain.usecases.categories.UpdateCategoryUseCase;

import java.util.ArrayList;
import java.util.List;

public class CategoryService {
    private AddCategoryUseCase addCategoryUseCase;
    private GetUserCategoriesUseCase getUserCategoriesUseCase;
    private UpdateCategoryUseCase updateCategoryUseCase;

    public interface Callback {
        void onSuccess(Category category);
        void onError(String errorMessage);
    }

    public CategoryService(Context context) {
        var categoryRepo = new CategoryRepository(context);
        this.addCategoryUseCase = new AddCategoryUseCase(categoryRepo);
        this.getUserCategoriesUseCase = new GetUserCategoriesUseCase(categoryRepo);
        this.updateCategoryUseCase = new UpdateCategoryUseCase(categoryRepo);
    }

    public List<Category> getUserCategories(String userId) {
        if (userId == null || userId.isEmpty()) {
            return new ArrayList<>();
        }

        return getUserCategoriesUseCase.execute(userId);
    }

    public void addCategory(Category category, Callback callback) {
        addCategoryUseCase.execute(category, new AddCategoryUseCase.Callback() {
            @Override
            public void onSuccess(Category category) {
                callback.onSuccess(category);
            }

            @Override
            public void onError(String errorMessage) {
                callback.onError(errorMessage);
            }
        });
    }

    public void updateCategory(Category category, Callback callback) {
        updateCategoryUseCase.execute(category, new UpdateCategoryUseCase.Callback() {
            @Override
            public void onSuccess(Category category) {
                callback.onSuccess(category);
            }

            @Override
            public void onError(String errorMessage) {
                callback.onError("Failed to update category");
            }
        });
    }
}

