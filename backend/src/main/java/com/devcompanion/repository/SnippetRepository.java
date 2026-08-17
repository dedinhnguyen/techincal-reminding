package com.devcompanion.repository;

import com.devcompanion.domain.entity.Snippet;
import com.devcompanion.domain.enums.ComplexityLevel;
import com.devcompanion.domain.enums.Technology;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SnippetRepository extends JpaRepository<Snippet, UUID>, JpaSpecificationExecutor<Snippet> {

    @EntityGraph(attributePaths = {"category", "tags", "variations"})
    Optional<Snippet> findWithDetailsById(UUID id);

    @EntityGraph(attributePaths = {"category", "tags", "variations"})
    Optional<Snippet> findWithDetailsBySlug(String slug);

    @EntityGraph(attributePaths = {"category", "tags"})
    List<Snippet> findByCategoryId(UUID categoryId);

    @EntityGraph(attributePaths = {"category", "tags"})
    List<Snippet> findByTechnology(Technology technology);

    @EntityGraph(attributePaths = {"category", "tags"})
    List<Snippet> findByComplexityLevel(ComplexityLevel complexityLevel);

    @Query("SELECT s FROM Snippet s LEFT JOIN FETCH s.tags WHERE s.technology = :tech AND s.complexityLevel = :level")
    List<Snippet> findByTechAndLevel(@Param("tech") Technology tech, @Param("level") ComplexityLevel level);

    @Query("SELECT s FROM Snippet s JOIN s.tags t WHERE LOWER(t.name) = LOWER(:tagName)")
    List<Snippet> findByTagName(@Param("tagName") String tagName);

    @Query("""
        SELECT s FROM Snippet s
        WHERE LOWER(s.title) LIKE LOWER(CONCAT('%', :query, '%'))
           OR LOWER(s.summary) LIKE LOWER(CONCAT('%', :query, '%'))
           OR LOWER(s.codeTemplate) LIKE LOWER(CONCAT('%', :query, '%'))
    """)
    List<Snippet> searchFuzzy(@Param("query") String query);
}
