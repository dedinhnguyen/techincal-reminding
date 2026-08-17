package com.devcompanion.dto;

import java.io.Serializable;
import java.util.Map;

public record InfraHealthDto(
        String status,
        Map<String, String> services,
        Map<String, Object> cacheStats,
        long totalSnippets,
        long totalCategories,
        long totalMongoTemplates
) implements Serializable {}
