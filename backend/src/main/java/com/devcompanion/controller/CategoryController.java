package com.devcompanion.controller;

import com.devcompanion.domain.enums.Technology;
import com.devcompanion.dto.CategoryDto;
import com.devcompanion.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
@Tag(name = "Categories", description = "Endpoints for managing cheat-sheet technology categories")
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping
    @Operation(summary = "Get all categories", description = "Retrieves all cheat-sheet categories with cached performance.")
    public ResponseEntity<List<CategoryDto>> getAllCategories(
            @RequestParam(required = false) Technology technology
    ) {
        if (technology != null) {
            return ResponseEntity.ok(categoryService.getCategoriesByTechnology(technology));
        }
        return ResponseEntity.ok(categoryService.getAllCategories());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get category by ID")
    public ResponseEntity<CategoryDto> getCategoryById(@PathVariable UUID id) {
        return ResponseEntity.ok(categoryService.getCategoryById(id));
    }
}
