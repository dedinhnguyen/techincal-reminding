package com.devcompanion.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

import java.io.Serializable;
import java.util.List;

public record QueryBuilderRequest(
        @NotBlank(message = "Entity name is required")
        String entityName,

        @NotBlank(message = "Target field is required")
        String fieldName,

        String fieldType, // e.g. "String", "Integer", "LocalDateTime", "Status"

        String operator,  // "EQUALS", "CONTAINING_IGNORE_CASE", "IN", "BETWEEN", "GREATER_THAN", "IS_NULL"

        List<String> additionalConditions, // secondary fields

        boolean isTopResult, // Top 3 / Top 10

        int topCount,

        boolean isOrderBy,

        String orderByField,

        String orderDirection, // "ASC", "DESC"

        boolean isPageable,

        boolean isAsyncCompletableFuture,

        boolean isCountOrExists // "COUNT", "EXISTS", "FIND"
) implements Serializable {}
