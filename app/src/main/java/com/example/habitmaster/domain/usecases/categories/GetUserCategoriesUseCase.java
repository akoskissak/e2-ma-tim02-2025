package com.example.habitmaster.domain.usecases.categories;

import com.example.habitmaster.data.repositories.CategoryRepository;
import com.example.habitmaster.domain.models.Category;

import java.util.List;

public class GetUserCategoriesUseCase {
    private final CategoryRepository categoryRepo;

    public GetUserCategoriesUseCase(CategoryRepository categoryRepo) {
        this.categoryRepo = categoryRepo;
    }

    public List<Category> execute(String userId) {
        return categoryRepo.getUserCategories(userId);
    }
}
