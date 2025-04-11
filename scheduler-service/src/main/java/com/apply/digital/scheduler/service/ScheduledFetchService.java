package com.apply.digital.scheduler.service;

import com.apply.digital.db.entities.AlgoliaArticleEntity;
import com.apply.digital.db.entities.AlgoliaTagEntity;
import com.apply.digital.db.repositories.ArticleRepository;
import com.apply.digital.db.repositories.TagRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
public class ScheduledFetchService {
  private final HttpClient client;
  private final String apiUrl;
  private final ArticleRepository articleRepository;
  private final TagRepository tagRepository;
  private static final ObjectMapper objectMapper = new ObjectMapper();

  public ScheduledFetchService(
      @Value("${algolia.java-articles.url}") String apiUrl,
      ArticleRepository articleRepository,
      TagRepository tagRepository,
      HttpClient client) {
    this.apiUrl = apiUrl;
    this.articleRepository = articleRepository;
    this.tagRepository = tagRepository;
    this.client = client;
  }

  @Scheduled(fixedRateString = "${scheduler.fetch.rate:3600000}")
  @Transactional
  public void fetchNews() {
    try {
      HttpRequest request = HttpRequest.newBuilder().uri(URI.create(apiUrl)).build();

      HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

      log.info(
          "API response: {}",
          response.body().substring(0, Math.min(500, response.body().length())) + "...");

      JsonNode root = objectMapper.readTree(response.body());
      JsonNode hits = root.get("hits");

      for (JsonNode hit : hits) {

        String objectId = hit.get("objectID").asText();

        if (!articleRepository.existsByObjectId(objectId)) {
          AlgoliaArticleEntity item = new AlgoliaArticleEntity();
          item.setObjectId(objectId);
          item.setStoryTitle(getAsStringOrNull.apply("story_title", hit));
          item.setStoryUrl(getAsStringOrNull.apply("story_url", hit));
          item.setAuthor(getAsStringOrNull.apply("author", hit));

          String createdAtStr = getAsStringOrNull.apply("created_at", hit);
          Instant createdAt =
              createdAtStr != null
                  ? Instant.parse(createdAtStr)
                  : Instant.now(); // or skip the item
          item.setCreatedAt(createdAt);

          Set<String> tags =
              StreamSupport.stream(hit.get("_tags").spliterator(), false)
                  .map(JsonNode::asText)
                  .filter(tag -> tag != null && !tag.isBlank())
                  .collect(Collectors.toSet());

          item.setTags(findOrCreateTags(tags));

          articleRepository.save(item);
          log.info("Saved new HN item: {}", objectId);
        }
      }
    } catch (Exception e) {
      log.error("Failed to fetch API", e);
    }
  }

  private Set<AlgoliaTagEntity> findOrCreateTags(Set<String> tagNames) {
    Set<AlgoliaTagEntity> tags = new HashSet<>();

    for (String name : tagNames) {
      tagRepository
          .findByName(name)
          .ifPresentOrElse(
              tags::add,
              () -> {
                AlgoliaTagEntity newTag = new AlgoliaTagEntity();
                newTag.setName(name);
                tags.add(tagRepository.save(newTag));
              });
    }
    return tags;
  }

  private static final BiFunction<String, JsonNode, String> getAsStringOrNull =
      (key, node) -> node.hasNonNull(key) ? node.get(key).asText() : null;
}
