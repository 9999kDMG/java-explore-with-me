package ru.practicum.ewm_service.category;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.ewm_service.category.dto.CategoryDto;
import ru.practicum.ewm_service.exception.ConflictException;
import ru.practicum.ewm_service.exception.NotFoundException;
import ru.practicum.ewm_service.utils.Pagination;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;

    public CategoryDto createCategory(CategoryDto categoryDto) {
        Category category = CategoryMapper.toCategory(categoryDto);
        try {
            return CategoryMapper.toCategoryDto(categoryRepository.save(category));
        } catch (DataIntegrityViolationException e) {
            throw new ConflictException("not unique category");
        }
    }

    public CategoryDto updateCategory(Integer catId, CategoryDto categoryDto) {
        Category category = getCategoryOrThrow(catId);
        category.setName(categoryDto.getName());
        try {
            return CategoryMapper.toCategoryDto(categoryRepository.save(category));
        } catch (DataIntegrityViolationException e) {
            throw new ConflictException("not unique category");
        }

    }

    public void deleteCategory(Integer catId) {
        Category category = getCategoryOrThrow(catId);
        try {
            categoryRepository.delete(category);
        } catch (DataIntegrityViolationException e) {
            throw new ConflictException("you can't delete a used category");
        }

    }

    public List<CategoryDto> getCategories(Integer from, Integer size) {
        Pageable pageable = Pagination.getPageOrThrow(from, size);
        return categoryRepository.findAll(pageable)
                .stream()
                .map(CategoryMapper::toCategoryDto)
                .collect(Collectors.toList());
    }

    public CategoryDto getCategory(Integer catId) {
        Category category = getCategoryOrThrow(catId);
        return CategoryMapper.toCategoryDto(category);
    }

    public Category getCategoryOrThrow(Integer id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("category id N%s", id)));
    }
}
