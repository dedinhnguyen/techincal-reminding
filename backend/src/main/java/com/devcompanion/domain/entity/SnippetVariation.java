package com.devcompanion.domain.entity;

import com.devcompanion.domain.enums.VariationType;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.util.UUID;

@Entity
@Table(name = "snippet_variations", indexes = {
        @Index(name = "idx_variation_type", columnList = "variationType")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class SnippetVariation implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "snippet_id", nullable = false)
    @JsonIgnoreProperties("variations")
    private Snippet snippet;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private VariationType variationType;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String codeSnippet;

    @Column(columnDefinition = "TEXT")
    private String explanation;

    @Column(columnDefinition = "TEXT")
    private String prosAndCons;

    @Column(length = 255)
    private String runtimePerformanceNote;
}
