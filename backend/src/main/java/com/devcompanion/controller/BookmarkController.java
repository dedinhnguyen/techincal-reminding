package com.devcompanion.controller;

import com.devcompanion.dto.SnippetDto;
import com.devcompanion.service.BookmarkService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/bookmarks")
@RequiredArgsConstructor
@Tag(name = "Bookmarks", description = "Endpoints for managing user favorite snippets and cheatsheets")
public class BookmarkController {

    private final BookmarkService bookmarkService;

    @GetMapping
    @Operation(summary = "Get all bookmarks for a user", description = "Retrieves the list of bookmarked snippets for the specified user or client token")
    public ResponseEntity<List<SnippetDto>> getUserBookmarks(
            @RequestHeader(value = "X-User-Id", defaultValue = "default-user") String userId
    ) {
        return ResponseEntity.ok(bookmarkService.getUserBookmarkedSnippets(userId));
    }

    @PostMapping("/{snippetId}/toggle")
    @Operation(summary = "Toggle bookmark status", description = "Adds or removes a snippet from the user's bookmarks")
    public ResponseEntity<Map<String, Object>> toggleBookmark(
            @PathVariable UUID snippetId,
            @RequestHeader(value = "X-User-Id", defaultValue = "default-user") String userId
    ) {
        boolean bookmarked = bookmarkService.toggleBookmark(userId, snippetId);
        return ResponseEntity.ok(Map.of(
                "snippetId", snippetId,
                "bookmarked", bookmarked,
                "message", bookmarked ? "Bookmark added" : "Bookmark removed"
        ));
    }

    @GetMapping("/{snippetId}/status")
    @Operation(summary = "Check bookmark status", description = "Returns true if the snippet is bookmarked by the user")
    public ResponseEntity<Map<String, Boolean>> checkStatus(
            @PathVariable UUID snippetId,
            @RequestHeader(value = "X-User-Id", defaultValue = "default-user") String userId
    ) {
        return ResponseEntity.ok(Map.of(
                "bookmarked", bookmarkService.isSnippetBookmarked(userId, snippetId)
        ));
    }
}
