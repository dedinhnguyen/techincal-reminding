package com.devcompanion.dto;

import com.devcompanion.domain.enums.Technology;

import java.io.Serializable;
import java.util.UUID;

public record CategoryDto(
        UUID id,
        String name,
        String slug,
        String description,
        String icon,
        Technology technology,
        int snippetCount
) implements Serializable {}
