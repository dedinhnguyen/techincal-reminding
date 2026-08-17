package com.devcompanion.repository;

import com.devcompanion.domain.entity.SnippetVariation;
import com.devcompanion.domain.enums.VariationType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SnippetVariationRepository extends JpaRepository<SnippetVariation, UUID> {
    List<SnippetVariation> findBySnippetId(UUID snippetId);
    List<SnippetVariation> findByVariationType(VariationType variationType);
}
