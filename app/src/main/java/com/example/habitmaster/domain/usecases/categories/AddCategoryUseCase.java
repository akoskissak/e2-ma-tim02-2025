package com.example.habitmaster.domain.usecases.categories;

import com.example.habitmaster.data.repositories.CategoryRepository;
import com.example.habitmaster.domain.models.Category;

public class AddCategoryUseCase {

    private final CategoryRepository categoryRepository;

    public AddCategoryUseCase(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public boolean execute(Category category) {
        try {
            categoryRepository.addCategory(category);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}

