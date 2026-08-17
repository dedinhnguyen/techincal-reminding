package com.devcompanion.controller;

import com.devcompanion.dto.QueryBuilderRequest;
import com.devcompanion.dto.QueryBuilderResponse;
import com.devcompanion.service.QueryBuilderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/query-builder")
@RequiredArgsConstructor
@Tag(name = "Query Builder", description = "Dynamic code generator for Spring Data JPA, JPQL, Criteria API, and MongoTemplate")
public class QueryBuilderController {

    private final QueryBuilderService queryBuilderService;

    @PostMapping("/generate")
    @Operation(summary = "Generate Spring Data JPA / Criteria / Mongo queries based on interactive parameters")
    public ResponseEntity<QueryBuilderResponse> generateQueries(@Valid @RequestBody QueryBuilderRequest request) {
        return ResponseEntity.ok(queryBuilderService.generateQueryPatterns(request));
    }
}
