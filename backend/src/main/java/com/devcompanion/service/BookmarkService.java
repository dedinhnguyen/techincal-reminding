package com.devcompanion.service;

import com.devcompanion.domain.entity.Snippet;
import com.devcompanion.domain.entity.UserBookmark;
import com.devcompanion.dto.BookmarkDto;
import com.devcompanion.dto.SnippetDto;
import com.devcompanion.exception.ResourceNotFoundException;
import com.devcompanion.repository.SnippetRepository;
import com.devcompanion.repository.UserBookmarkRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookmarkService {

    private final UserBookmarkRepository bookmarkRepository;
    private final SnippetRepository snippetRepository;
    private final SnippetService snippetService;

    @Transactional(readOnly = true)
    public List<SnippetDto> getUserBookmarkedSnippets(String userId) {
        return bookmarkRepository.findByUserIdOrderByCreatedAtDesc(userId).stream()
                .map(ub -> snippetService.mapToDto(ub.getSnippet()))
                .toList();
    }

    @Transactional(readOnly = true)
    public boolean isSnippetBookmarked(String userId, UUID snippetId) {
        return bookmarkRepository.existsByUserIdAndSnippetId(userId, snippetId);
    }

    @Transactional
    public boolean toggleBookmark(String userId, UUID snippetId) {
        Optional<UserBookmark> existing = bookmarkRepository.findByUserIdAndSnippetId(userId, snippetId);
        if (existing.isPresent()) {
            bookmarkRepository.delete(existing.get());
            log.info("Removed bookmark for user: {} on snippet: {}", userId, snippetId);
            return false;
        } else {
            Snippet snippet = snippetRepository.findById(snippetId)
                    .orElseThrow(() -> new ResourceNotFoundException("Snippet not found with ID: " + snippetId));

            UserBookmark bookmark = UserBookmark.builder()
                    .userId(userId)
                    .snippet(snippet)
                    .build();
            bookmarkRepository.save(bookmark);
            log.info("Added bookmark for user: {} on snippet: {}", userId, snippetId);
            return true;
        }
    }
}
