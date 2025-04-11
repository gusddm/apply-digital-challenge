package com.apply.digital.api.service;

import com.apply.digital.api.model.AlgoliaArticleDTO;
import com.apply.digital.db.entities.AlgoliaArticleEntity;
import com.apply.digital.db.entities.AlgoliaTagEntity;
import com.apply.digital.db.repositories.ArticleRepository;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AlgoliaArticleService {

  private final ArticleRepository repository;

  public Page<AlgoliaArticleDTO> findFilteredArticles(
      String author, List<String> tags, String title, String month, Pageable pageable) {

    Specification<AlgoliaArticleEntity> spec = Specification.where(null);
    spec =
        spec.and(
            (root, query, cb) -> cb.isTrue(cb.coalesce(root.get("active"), cb.literal(false))));

    if (author != null && !author.isBlank()) {
      spec = spec.and((root, query, cb) -> cb.equal(root.get("author"), author));
    }

    if (title != null && !title.isBlank()) {
      spec =
          spec.and(
              (root, query, cb) ->
                  cb.like(cb.lower(root.get("storyTitle")), "%" + title.toLowerCase() + "%"));
    }

    if (tags != null && !tags.isEmpty()) {
      spec =
          spec.and(
              (root, query, cb) -> {
                assert query != null;
                query.distinct(true);

                Join<AlgoliaArticleEntity, AlgoliaTagEntity> tagJoin =
                    root.join("tags", JoinType.INNER);

                List<Predicate> likePredicates =
                    tags.stream()
                        .map(
                            fragment ->
                                cb.like(
                                    cb.lower(tagJoin.get("name")),
                                    "%" + fragment.toLowerCase() + "%"))
                        .toList();

                return cb.or(likePredicates.toArray(new Predicate[0]));
              });
    }

    if (month != null && !month.isBlank()) {
      Month monthQuery = parseMonth(month);
      spec =
          spec.and(
              (root, query, cb) -> {
                // PostgreSQL returns a double by default, we need to cast or round
                Expression<Integer> castedMonth =
                    cb.function(
                        "date_part", Integer.class, cb.literal("month"), root.get("createdAt"));

                return cb.equal(castedMonth, monthQuery.getValue());
              });
    }
    return repository.findAll(spec, pageable).map(AlgoliaArticleService::toDTO);
  }

  private static Month parseMonth(String month) {
    try {
      return Month.valueOf(month.toUpperCase(Locale.ENGLISH));
    } catch (IllegalArgumentException e) {
      throw new RuntimeException("Invalid month name: " + month);
    }
  }

  private static AlgoliaArticleDTO toDTO(AlgoliaArticleEntity entity) {
    if (entity == null) return null;

    DateTimeFormatter formatter =
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
            .withZone(ZoneId.of("America/Argentina/Buenos_Aires"));

    AlgoliaArticleDTO dto = new AlgoliaArticleDTO();
    dto.setObjectId(entity.getObjectId());
    dto.setAuthor(entity.getAuthor());
    dto.setCommentText(entity.getCommentText());
    dto.setCreatedAt(formatter.format(entity.getCreatedAt()));
    dto.setParentId(entity.getParentId());
    dto.setStoryId(entity.getStoryId() != null ? entity.getStoryId() : 0);
    dto.setStoryTitle(entity.getStoryTitle());
    dto.setStoryUrl(entity.getStoryUrl());
    dto.setTags(
        entity.getTags().stream().map(AlgoliaTagEntity::getName).collect(Collectors.toSet()));

    return dto;
  }

  public void deleteByObjectId(String objectId) {
    repository.deleteByObjectId(objectId);
  }
}
