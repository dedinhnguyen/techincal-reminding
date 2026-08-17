package com.devcompanion.service;

import com.devcompanion.domain.document.SnippetSearchDocument;
import com.devcompanion.domain.entity.Snippet;
import com.devcompanion.dto.SearchResponseDto;
import com.devcompanion.dto.SnippetDto;
import com.devcompanion.repository.SnippetRepository;
import com.devcompanion.repository.SnippetSearchRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class SearchService {

    private final SnippetRepository snippetRepository;
    private final SnippetService snippetService;
    private final SnippetSearchRepository snippetSearchRepository;

    @Value("${app.elasticsearch.enabled:false}")
    private boolean elasticsearchEnabled;

    @Cacheable(value = "search_results", key = "#query.toLowerCase().trim()")
    public SearchResponseDto search(String query) {
        long startTime = System.currentTimeMillis();
        String engineUsed;
        List<SnippetDto> results;

        if (elasticsearchEnabled) {
            try {
                log.info("Executing Elasticsearch full-text search for query: {}", query);
                List<SnippetSearchDocument> esDocs = snippetSearchRepository
                        .findByTitleContainingOrSummaryContainingOrCodeTemplateContaining(query, query, query);
                engineUsed = "ELASTICSEARCH";

                // Map matched doc IDs back to full snippet DTOs
                List<String> ids = esDocs.stream().map(SnippetSearchDocument::getId).toList();
                results = snippetRepository.findAll().stream()
                        .filter(s -> ids.contains(s.getId().toString()))
                        .map(snippetService::mapToDto)
                        .toList();
            } catch (Exception ex) {
                log.warn("Elasticsearch search failed or unreachable, falling back to PostgreSQL JPA: {}", ex.getMessage());
                results = fallbackPostgresSearch(query);
                engineUsed = "POSTGRES_JPA_CRITERIA (Fallback)";
            }
        } else {
            results = fallbackPostgresSearch(query);
            engineUsed = "POSTGRES_JPA_CRITERIA";
        }

        long took = System.currentTimeMillis() - startTime;
        return new SearchResponseDto(query, engineUsed, results.size(), took, results);
    }

    private List<SnippetDto> fallbackPostgresSearch(String query) {
        List<Snippet> list = snippetRepository.searchFuzzy(query);
        return list.stream().map(snippetService::mapToDto).toList();
    }
}
