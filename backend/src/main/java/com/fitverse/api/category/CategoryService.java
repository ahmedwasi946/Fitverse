package com.fitverse.api.category;

import com.fitverse.api.category.dto.CategoryRequest;
import com.fitverse.api.category.dto.CategoryResponse;
import com.fitverse.api.common.exception.DuplicateResourceException;
import com.fitverse.api.common.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public List<CategoryResponse> getAll() {
        return categoryRepository.findAll().stream().map(this::toResponse).toList();
    }

    public CategoryResponse getById(Long id) {
        return toResponse(getEntityById(id));
    }

    public Category getEntityById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.of("Category", id));
    }

    public CategoryResponse create(CategoryRequest request) {
        if (categoryRepository.existsBySlug(request.slug())) {
            throw new DuplicateResourceException("A category with slug '" + request.slug() + "' already exists");
        }
        Category category = Category.builder()
                .name(request.name())
                .slug(request.slug())
                .description(request.description())
                .build();
        return toResponse(categoryRepository.save(category));
    }

    public CategoryResponse update(Long id, CategoryRequest request) {
        Category category = getEntityById(id);
        if (!category.getSlug().equalsIgnoreCase(request.slug()) && categoryRepository.existsBySlug(request.slug())) {
            throw new DuplicateResourceException("A category with slug '" + request.slug() + "' already exists");
        }
        category.setName(request.name());
        category.setSlug(request.slug());
        category.setDescription(request.description());
        return toResponse(categoryRepository.save(category));
    }

    public void delete(Long id) {
        getEntityById(id);
        categoryRepository.deleteById(id);
    }

    private CategoryResponse toResponse(Category category) {
        return new CategoryResponse(category.getId(), category.getName(), category.getSlug(), category.getDescription());
    }
}
