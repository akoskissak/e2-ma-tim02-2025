package com.example.habitmaster.domain.usecases.categories;

import com.example.habitmaster.data.repositories.CategoryRepository;
import com.example.habitmaster.domain.models.Category;

public class DeleteCategoryUseCase {
    private final CategoryRepository categoryRepository;

    public DeleteCategoryUseCase(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public boolean execute(String categoryId) {
        return categoryRepository.deleteCategory(categoryId);
    }
}
