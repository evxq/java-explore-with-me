package ru.practicum.ewm.category;

import java.util.List;

public interface CategoryService {

    CategoryDto addCategory(CategoryDto categoryDto);

    CategoryDto updateCategory(CategoryDto categoryDto, Long categoryId);

    List<CategoryDto> getAllCategories(Integer from, Integer size);

    Category getCategoryByIdIfExists(Long categoryId);

    void deleteCategory(Long categoryId);

}
