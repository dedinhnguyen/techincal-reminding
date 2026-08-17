package com.devcompanion.service;

import com.devcompanion.domain.entity.Category;
import com.devcompanion.domain.entity.Snippet;
import com.devcompanion.domain.entity.SnippetVariation;
import com.devcompanion.domain.entity.Tag;
import com.devcompanion.domain.enums.ComplexityLevel;
import com.devcompanion.domain.enums.Technology;
import com.devcompanion.dto.*;
import com.devcompanion.exception.ResourceNotFoundException;
import com.devcompanion.repository.CategoryRepository;
import com.devcompanion.repository.SnippetRepository;
import com.devcompanion.repository.SnippetVariationRepository;
import com.devcompanion.repository.TagRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class SnippetService {

    private final SnippetRepository snippetRepository;
    private final CategoryRepository categoryRepository;
    private final TagRepository tagRepository;
    private final SnippetVariationRepository snippetVariationRepository;

    @Transactional(readOnly = true)
    public List<SnippetDto> getAllSnippets(Technology technology, ComplexityLevel complexity, UUID categoryId, String tag) {
        log.info("Querying snippets with filters: tech={}, complexity={}, categoryId={}, tag={}", technology, complexity, categoryId, tag);

        List<Snippet> list;
        if (tag != null && !tag.isBlank()) {
            list = snippetRepository.findByTagName(tag);
        } else if (technology != null && complexity != null) {
            list = snippetRepository.findByTechAndLevel(technology, complexity);
        } else if (technology != null) {
            list = snippetRepository.findByTechnology(technology);
        } else if (complexity != null) {
            list = snippetRepository.findByComplexityLevel(complexity);
        } else if (categoryId != null) {
            list = snippetRepository.findByCategoryId(categoryId);
        } else {
            list = snippetRepository.findAll();
        }

        return list.stream().map(this::mapToDto).toList();
    }

    @Transactional
    @Cacheable(value = "snippets", key = "#id.toString()")
    public SnippetDto getSnippetById(UUID id) {
        Snippet snippet = snippetRepository.findWithDetailsById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Snippet not found with ID: " + id));

        snippet.setViewCount(snippet.getViewCount() + 1);
        snippetRepository.save(snippet);

        return mapToDto(snippet);
    }

    @Transactional(readOnly = true)
    public SnippetDto getSnippetBySlug(String slug) {
        Snippet snippet = snippetRepository.findWithDetailsBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Snippet not found with slug: " + slug));
        return mapToDto(snippet);
    }

    @Transactional
    @CacheEvict(value = {"snippets", "categories"}, allEntries = true)
    public SnippetDto createSnippet(CreateSnippetRequest request) {
        Category category = categoryRepository.findById(request.categoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with ID: " + request.categoryId()));

        String baseSlug = request.title().toLowerCase().replaceAll("[^a-z0-9]+", "-").replaceAll("^-|-$", "");
        String slug = baseSlug + "-" + System.currentTimeMillis() % 10000;

        Snippet snippet = Snippet.builder()
                .category(category)
                .title(request.title())
                .slug(slug)
                .summary(request.summary())
                .problemContext(request.problemContext())
                .codeTemplate(request.codeTemplate())
                .language(request.language())
                .technology(request.technology())
                .complexityLevel(request.complexityLevel())
                .viewCount(0L)
                .build();

        // Handle Tags
        if (request.tagNames() != null) {
            for (String tagName : request.tagNames()) {
                Tag tag = tagRepository.findByNameIgnoreCase(tagName)
                        .orElseGet(() -> tagRepository.save(Tag.builder().name(tagName.toLowerCase()).colorCode("#38bdf8").build()));
                snippet.addTag(tag);
            }
        }

        // Handle Variations
        if (request.variations() != null) {
            for (CreateSnippetRequest.CreateVariationRequest vReq : request.variations()) {
                SnippetVariation variation = SnippetVariation.builder()
                        .variationType(vReq.variationType())
                        .codeSnippet(vReq.codeSnippet())
                        .explanation(vReq.explanation())
                        .prosAndCons(vReq.prosAndCons())
                        .runtimePerformanceNote(vReq.runtimePerformanceNote())
                        .build();
                snippet.addVariation(variation);
            }
        }

        Snippet saved = snippetRepository.save(snippet);
        return mapToDto(saved);
    }

    public SnippetDto mapToDto(Snippet s) {
        Set<TagDto> tagDtos = s.getTags() == null ? Collections.emptySet() :
                s.getTags().stream()
                        .map(t -> new TagDto(t.getId(), t.getName(), t.getColorCode()))
                        .collect(Collectors.toSet());

        List<SnippetVariationDto> variationDtos = s.getVariations() == null ? Collections.emptyList() :
                s.getVariations().stream()
                        .map(v -> new SnippetVariationDto(
                                v.getId(),
                                v.getVariationType(),
                                v.getCodeSnippet(),
                                v.getExplanation(),
                                v.getProsAndCons(),
                                v.getRuntimePerformanceNote()
                        ))
                        .toList();

        return new SnippetDto(
                s.getId(),
                s.getCategory() != null ? s.getCategory().getId() : null,
                s.getCategory() != null ? s.getCategory().getName() : "General",
                s.getTitle(),
                s.getSlug(),
                s.getSummary(),
                s.getProblemContext(),
                s.getCodeTemplate(),
                s.getLanguage(),
                s.getTechnology(),
                s.getComplexityLevel(),
                s.getViewCount(),
                tagDtos,
                variationDtos,
                s.getCreatedAt(),
                s.getUpdatedAt()
        );
    }
}
