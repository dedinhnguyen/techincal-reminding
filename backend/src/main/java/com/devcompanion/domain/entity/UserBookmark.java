package com.devcompanion.domain.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "user_bookmarks", indexes = {
        @Index(name = "idx_bookmark_user_snippet", columnList = "userId, snippet_id", unique = true),
        @Index(name = "idx_bookmark_user", columnList = "userId")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class UserBookmark implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, length = 100)
    private String userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "snippet_id", nullable = false)
    @JsonIgnoreProperties({"variations", "tags"})
    private Snippet snippet;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;
}
