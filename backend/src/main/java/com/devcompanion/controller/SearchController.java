package com.devcompanion.controller;

import com.devcompanion.dto.SearchResponseDto;
import com.devcompanion.service.SearchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/search")
@RequiredArgsConstructor
@Tag(name = "Search", description = "High-performance full-text search powered by Elasticsearch & PostgreSQL fallback with Redis cache")
public class SearchController {

    private final SearchService searchService;

    @GetMapping
    @Operation(summary = "Search snippets across titles, summaries, and code templates")
    public ResponseEntity<SearchResponseDto> search(@RequestParam("q") String query) {
        if (query == null || query.isBlank()) {
            return ResponseEntity.ok(new SearchResponseDto("", "NONE", 0, 0, java.util.List.of()));
        }
        return ResponseEntity.ok(searchService.search(query.trim()));
    }
}
