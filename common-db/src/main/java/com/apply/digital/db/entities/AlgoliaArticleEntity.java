package com.apply.digital.db.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "algolia_articles", uniqueConstraints = @UniqueConstraint(columnNames = "objectId"))
@Getter
@Setter
public class AlgoliaArticleEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String objectId;

    private String author;

    @Column(name = "comment_text", columnDefinition = "TEXT")
    private String commentText;

    private Instant createdAt;

    private Instant updatedAt;

    private Long parentId;

    private Long storyId;

    private String storyTitle;

    private String storyUrl;

    @Column(nullable = false, columnDefinition = "BOOLEAN DEFAULT TRUE")
    private boolean active;

    @ManyToMany
    @JoinTable(
            name = "article_tags",
            joinColumns = @JoinColumn(name = "article_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    private Set<AlgoliaTagEntity> tags = new HashSet<>();
}