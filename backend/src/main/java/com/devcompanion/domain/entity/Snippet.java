package com.devcompanion.domain.entity;

import com.devcompanion.domain.enums.ComplexityLevel;
import com.devcompanion.domain.enums.Technology;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.*;

@Entity
@Table(name = "snippets", indexes = {
        @Index(name = "idx_snippet_slug", columnList = "slug", unique = true),
        @Index(name = "idx_snippet_tech", columnList = "technology"),
        @Index(name = "idx_snippet_complexity", columnList = "complexityLevel")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Snippet implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    @JsonIgnoreProperties("snippets")
    private Category category;

    @Column(nullable = false, length = 150)
    private String title;

    @Column(nullable = false, unique = true, length = 180)
    private String slug;

    @Column(columnDefinition = "TEXT")
    private String summary;

    @Column(columnDefinition = "TEXT")
    private String problemContext;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String codeTemplate;

    @Column(nullable = false, length = 50)
    private String language;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private Technology technology;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private ComplexityLevel complexityLevel;

    @Builder.Default
    @Column(nullable = false)
    private Long viewCount = 0L;

    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "snippet_tags",
            joinColumns = @JoinColumn(name = "snippet_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    @Builder.Default
    @JsonIgnoreProperties("snippets")
    private Set<Tag> tags = new HashSet<>();

    @OneToMany(mappedBy = "snippet", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    @JsonIgnoreProperties("snippet")
    private List<SnippetVariation> variations = new ArrayList<>();

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    public void addVariation(SnippetVariation variation) {
        variations.add(variation);
        variation.setSnippet(this);
    }

    public void addTag(Tag tag) {
        tags.add(tag);
        tag.getSnippets().add(this);
    }
}
