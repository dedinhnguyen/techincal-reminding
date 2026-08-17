package com.devcompanion.repository;

import com.devcompanion.domain.entity.UserBookmark;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserBookmarkRepository extends JpaRepository<UserBookmark, UUID> {

    @EntityGraph(attributePaths = {"snippet", "snippet.category", "snippet.tags"})
    List<UserBookmark> findByUserIdOrderByCreatedAtDesc(String userId);

    Optional<UserBookmark> findByUserIdAndSnippetId(String userId, UUID snippetId);

    boolean existsByUserIdAndSnippetId(String userId, UUID snippetId);

    void deleteByUserIdAndSnippetId(String userId, UUID snippetId);
}
