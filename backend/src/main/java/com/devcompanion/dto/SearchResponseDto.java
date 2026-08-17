package com.devcompanion.dto;

import java.io.Serializable;
import java.util.List;

public record SearchResponseDto(
        String query,
        String engineUsed, // "ELASTICSEARCH" or "POSTGRES_JPA_CRITERIA"
        long totalHits,
        long tookMillis,
        List<SnippetDto> results
) implements Serializable {}
