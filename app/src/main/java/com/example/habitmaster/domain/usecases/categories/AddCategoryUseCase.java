package com.example.habitmaster.domain.usecases.categories;

import static org.chromium.base.ThreadUtils.runOnUiThread;

import com.example.habitmaster.data.repositories.CategoryRepository;
import com.example.habitmaster.domain.models.Category;
import com.example.habitmaster.utils.exceptions.ColorNotUniqueException;
import com.example.habitmaster.utils.exceptions.NameNotUniqueException;

import javax.security.auth.callback.Callback;

public class AddCategoryUseCase {

    private final CategoryRepository categoryRepository;

    public interface Callback {
        void onSuccess(Category category);
        void onError(String errorMessage);
    }

    public AddCategoryUseCase(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public void execute(Category category, Callback callback) {
        new Thread(() -> {
            try {
                categoryRepository.addCategory(category);
                runOnUiThread(() -> callback.onSuccess(category));
            } catch (NameNotUniqueException e) {
                runOnUiThread(() -> callback.onError("Category name already exists"));
            } catch (ColorNotUniqueException e) {
                runOnUiThread(() -> callback.onError("Category color already exists"));
            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> callback.onError("Failed to add category"));
            }
        }).start();
    }

}

