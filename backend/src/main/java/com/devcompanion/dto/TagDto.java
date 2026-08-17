package com.devcompanion.dto;

import java.io.Serializable;
import java.util.UUID;

public record TagDto(
        UUID id,
        String name,
        String colorCode
) implements Serializable {}
