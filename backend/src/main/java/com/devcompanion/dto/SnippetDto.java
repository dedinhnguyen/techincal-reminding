package com.devcompanion.dto;

import com.devcompanion.domain.enums.ComplexityLevel;
import com.devcompanion.domain.enums.Technology;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public record SnippetDto(
        UUID id,
        UUID categoryId,
        String categoryName,
        String title,
        String slug,
        String summary,
        String problemContext,
        String codeTemplate,
        String language,
        Technology technology,
        ComplexityLevel complexityLevel,
        Long viewCount,
        Set<TagDto> tags,
        List<SnippetVariationDto> variations,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) implements Serializable {}
