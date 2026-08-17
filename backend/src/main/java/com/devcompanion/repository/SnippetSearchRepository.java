package com.devcompanion.repository;

import com.devcompanion.domain.document.SnippetSearchDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SnippetSearchRepository extends ElasticsearchRepository<SnippetSearchDocument, String> {
    List<SnippetSearchDocument> findByTitleContainingOrSummaryContainingOrCodeTemplateContaining(String title, String summary, String code);
    List<SnippetSearchDocument> findByTechnology(String technology);
}
