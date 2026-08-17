package com.devcompanion.controller;

import com.devcompanion.domain.enums.ComplexityLevel;
import com.devcompanion.domain.enums.Technology;
import com.devcompanion.dto.CreateSnippetRequest;
import com.devcompanion.dto.SnippetDto;
import com.devcompanion.service.SnippetService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/snippets")
@RequiredArgsConstructor
@Tag(name = "Snippets", description = "Endpoints for CRUD and querying fullstack architectural cheatsheets and snippets")
public class SnippetController {

    private final SnippetService snippetService;

    @GetMapping
    @Operation(summary = "Get all snippets", description = "Query snippets with optional filtering by technology, complexity, category, or tag.")
    public ResponseEntity<List<SnippetDto>> getAllSnippets(
            @RequestParam(required = false) Technology technology,
            @RequestParam(required = false) ComplexityLevel complexity,
            @RequestParam(required = false) UUID categoryId,
            @RequestParam(required = false) String tag
    ) {
        return ResponseEntity.ok(snippetService.getAllSnippets(technology, complexity, categoryId, tag));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get snippet by ID", description = "Retrieves full snippet details including all implementation variations.")
    public ResponseEntity<SnippetDto> getSnippetById(@PathVariable UUID id) {
        return ResponseEntity.ok(snippetService.getSnippetById(id));
    }

    @GetMapping("/slug/{slug}")
    @Operation(summary = "Get snippet by Slug")
    public ResponseEntity<SnippetDto> getSnippetBySlug(@PathVariable String slug) {
        return ResponseEntity.ok(snippetService.getSnippetBySlug(slug));
    }

    @PostMapping
    @Operation(summary = "Create a new snippet with variations")
    public ResponseEntity<SnippetDto> createSnippet(@Valid @RequestBody CreateSnippetRequest request) {
        SnippetDto created = snippetService.createSnippet(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }
}
