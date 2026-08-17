package com.devcompanion.dto;

import com.devcompanion.domain.enums.ComplexityLevel;
import com.devcompanion.domain.enums.Technology;
import com.devcompanion.domain.enums.VariationType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.io.Serializable;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public record CreateSnippetRequest(
        @NotNull(message = "Category ID is required")
        UUID categoryId,

        @NotBlank(message = "Title is required")
        String title,

        @NotBlank(message = "Summary is required")
        String summary,

        String problemContext,

        @NotBlank(message = "Code template is required")
        String codeTemplate,

        @NotBlank(message = "Language is required")
        String language,

        @NotNull(message = "Technology is required")
        Technology technology,

        @NotNull(message = "Complexity level is required")
        ComplexityLevel complexityLevel,

        Set<String> tagNames,

        List<CreateVariationRequest> variations
) implements Serializable {
    public record CreateVariationRequest(
            VariationType variationType,
            String codeSnippet,
            String explanation,
            String prosAndCons,
            String runtimePerformanceNote
    ) implements Serializable {}
}
