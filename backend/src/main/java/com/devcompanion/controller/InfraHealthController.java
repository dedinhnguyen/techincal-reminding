package com.devcompanion.controller;

import com.devcompanion.dto.InfraHealthDto;
import com.devcompanion.service.InfraHealthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/health/infra")
@RequiredArgsConstructor
@Tag(name = "Infrastructure Monitor", description = "Live health and connectivity status for PostgreSQL, MongoDB, Redis, and Elasticsearch")
public class InfraHealthController {

    private final InfraHealthService infraHealthService;

    @GetMapping
    @Operation(summary = "Get overall infrastructure connectivity and cache metrics")
    public ResponseEntity<InfraHealthDto> getInfraHealth() {
        return ResponseEntity.ok(infraHealthService.getHealth());
    }
}
