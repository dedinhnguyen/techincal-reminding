package com.devcompanion.service;

import com.devcompanion.domain.entity.Category;
import com.devcompanion.domain.enums.Technology;
import com.devcompanion.dto.CategoryDto;
import com.devcompanion.exception.ResourceNotFoundException;
import com.devcompanion.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class CategoryService {

    private final CategoryRepository categoryRepository;

    @Transactional(readOnly = true)
    @Cacheable(value = "categories", key = "'all'")
    public List<CategoryDto> getAllCategories() {
        log.info("Fetching all categories from database (cache miss)");
        return categoryRepository.findAll().stream()
                .map(this::mapToDto)
                .toList();
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "categories", key = "#technology.name()")
    public List<CategoryDto> getCategoriesByTechnology(Technology technology) {
        log.info("Fetching categories for tech: {}", technology);
        return categoryRepository.findByTechnology(technology).stream()
                .map(this::mapToDto)
                .toList();
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "categories", key = "#id.toString()")
    public CategoryDto getCategoryById(UUID id) {
        return categoryRepository.findById(id)
                .map(this::mapToDto)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with ID: " + id));
    }

    @Transactional(readOnly = true)
    public Category getCategoryEntity(UUID id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with ID: " + id));
    }

    @Transactional
    @CacheEvict(value = "categories", allEntries = true)
    public CategoryDto createCategory(String name, String slug, String description, String icon, Technology technology) {
        Category category = Category.builder()
                .name(name)
                .slug(slug)
                .description(description)
                .icon(icon)
                .technology(technology)
                .build();
        Category saved = categoryRepository.save(category);
        return mapToDto(saved);
    }

    public CategoryDto mapToDto(Category c) {
        return new CategoryDto(
                c.getId(),
                c.getName(),
                c.getSlug(),
                c.getDescription(),
                c.getIcon(),
                c.getTechnology(),
                c.getSnippets() != null ? c.getSnippets().size() : 0
        );
    }
}
