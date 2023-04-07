package ru.practicum.ewm_service.category;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm_service.category.dto.CategoryDto;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryService categoryService;

    //admin
    @PostMapping("/admin/categories")
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryDto postCategory(@RequestBody @Valid CategoryDto categoryDto) {
        return categoryService.createCategory(categoryDto);
    }

    @PatchMapping("/admin/categories/{catId}")
    public CategoryDto postCategory(@PathVariable Integer catId,
                                    @RequestBody @Valid CategoryDto categoryDto) {
        return categoryService.updateCategory(catId, categoryDto);
    }

    @DeleteMapping("/admin/categories/{catId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCategory(@PathVariable Integer catId) {
        categoryService.deleteCategory(catId);
    }

    //public
    @GetMapping("/categories")
    public List<CategoryDto> getCategories(
            @RequestParam(name = "from", defaultValue = "0") Integer from,
            @RequestParam(name = "size", defaultValue = "10") Integer size) {
        return categoryService.getCategories(from, size);
    }

    @GetMapping("/categories/{catId}")
    public CategoryDto getCategory(@PathVariable Integer catId) {
        return categoryService.getCategory(catId);
    }
}
