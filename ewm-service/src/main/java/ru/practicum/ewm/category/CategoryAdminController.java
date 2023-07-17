package ru.practicum.ewm.category;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/admin/categories")
public class CategoryAdminController {

    private final CategoryService categoryService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryDto adminAddCategory(@Valid @RequestBody CategoryDto categoryDto) {
        CategoryDto newCategory = categoryService.addCategory(categoryDto);
        log.info("Создана категория id={}", newCategory.getId());
        return newCategory;
    }

    @PatchMapping("/{categoryId}")
    public CategoryDto adminUpdateCategory(@Valid @RequestBody CategoryDto categoryDto,
                                           @PathVariable Long categoryId) {
        CategoryDto updCategory = categoryService.updateCategory(categoryDto, categoryId);
        log.info("Обновлена категория id={}", categoryId);
        return updCategory;
    }

    @DeleteMapping("/{categoryId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void adminDeleteCategory(@PathVariable Long categoryId) {
        categoryService.deleteCategory(categoryId);
        log.info("Удалена категория id={}", categoryId);
    }

}
