package ru.practicum.ewm.category;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
        Category newCategory = categoryRepository.save(CategoryMapper.toCategory(categoryDto));
        return CategoryMapper.toCategoryDto(newCategory);
    }

    @Override
    @Transactional
    public CategoryDto updateCategory(CategoryDto categoryDto, Long categoryId) {
        Category existCategory = getCategoryByIdWithExistChecking(categoryId);
        if (categoryDto.getName() != null) {
            existCategory.setName(categoryDto.getName());
        }
        Category updCategory = categoryRepository.save(existCategory);
        return CategoryMapper.toCategoryDto(updCategory);
    }

    @Override
    public void deleteCategory(Long categoryId) {
        getCategoryByIdWithExistChecking(categoryId);
        categoryRepository.deleteById(categoryId);
    }

    @Override
    public List<CategoryDto> getAllCategories(Integer from, Integer size) {
        return categoryRepository.findAll(PageQualifier.definePage(from, size))
                .stream().map(CategoryMapper::toCategoryDto).collect(Collectors.toList());
    }

    @Override
    public Category getCategoryByIdWithExistChecking(Long categoryId) {
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> {
                    log.warn("Category with id={} was not found", categoryId);
                    throw new NotFoundException(String.format("Category with id=%d was not found", categoryId));
                });
    }

}
