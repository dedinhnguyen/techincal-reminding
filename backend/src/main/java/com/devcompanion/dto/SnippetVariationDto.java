package com.devcompanion.dto;

import com.devcompanion.domain.enums.VariationType;

import java.io.Serializable;
import java.util.UUID;

public record SnippetVariationDto(
        UUID id,
        VariationType variationType,
        String codeSnippet,
        String explanation,
        String prosAndCons,
        String runtimePerformanceNote
) implements Serializable {}
