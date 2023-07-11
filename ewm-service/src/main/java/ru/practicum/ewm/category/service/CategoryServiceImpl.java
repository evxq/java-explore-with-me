package ru.practicum.ewm.category.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.category.CategoryMapper;
import ru.practicum.ewm.category.CategoryRepository;
import ru.practicum.ewm.category.dto.CategoryDto;
import ru.practicum.ewm.category.model.Category;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.utility.PageQualifier;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    @Override
    public CategoryDto addCategory(CategoryDto categoryDto) {
        Category category = CategoryMapper.toCategory(categoryDto);
        Category newCategory = categoryRepository.save(category);
        log.info("Создана категория id={}", newCategory.getId());

        return CategoryMapper.toCategoryDto(newCategory);
    }

    @Override
    @Transactional
    public CategoryDto updateCategory(CategoryDto categoryDto, Long catId) {
        Category existCategory = categoryRepository.findById(catId)
                .orElseThrow(() -> {
                    log.warn("Category with id={} was not found", catId);
                    throw new NotFoundException(String.format("Category with id=%d was not found", catId));
                });
        if (categoryDto.getName() != null) {
            existCategory.setName(categoryDto.getName());
        }
        Category updCategory = categoryRepository.save(existCategory);
        log.info("Обновлена категория id={}", catId);

        return CategoryMapper.toCategoryDto(updCategory);
    }

    @Override
    public void deleteCategory(Long catId) {                    // УДАЛЕНИЕ ВОЗМОЖНО ТОЛЬКО ЕСЛИ С КАТЕГОРИЕЙ НЕ СВЯЗАНО НИ ОДНОГО СОБЫТИЯ
        categoryRepository.findById(catId)
                .orElseThrow(() -> {
                    log.warn("Category with id={} was not found", catId);
                    throw new NotFoundException(String.format("Category  with id=%d was not found", catId));
                });
        categoryRepository.deleteById(catId);
        log.info("Удалена категория id={}", catId);
    }

    @Override
    public List<CategoryDto> getAllCategories(Integer from, Integer size) {
        log.info("Вызван список всех категорий");

        return categoryRepository.findAll(PageQualifier.definePage(from, size))
                .stream().map(CategoryMapper::toCategoryDto).collect(Collectors.toList());
    }

    @Override
    public CategoryDto getCategoryById(Long catId) {
        Category category = categoryRepository.findById(catId)
                .orElseThrow(() -> {
                    log.warn("Category with id={} was not found", catId);
//                    throw new NotFoundException(String.format("Category with id=%d was not found", catId));
                    throw new NotFoundException("Category with id=%d was not found");
                });
        log.info("Вызвана категория id={}", catId);

        return CategoryMapper.toCategoryDto(category);
    }

}
