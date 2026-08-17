package com.devcompanion.dto;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

public record BookmarkDto(
        UUID id,
        String userId,
        SnippetDto snippet,
        LocalDateTime createdAt
) implements Serializable {}
