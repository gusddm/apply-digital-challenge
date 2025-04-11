package com.apply.digital;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.apply.digital.api.ApiServiceApplication;
import com.apply.digital.api.model.AlgoliaArticleDTO;
import com.apply.digital.api.service.AlgoliaArticleService;
import com.apply.digital.db.entities.AlgoliaArticleEntity;
import com.apply.digital.db.entities.AlgoliaTagEntity;
import com.apply.digital.db.repositories.ArticleRepository;
import com.apply.digital.db.repositories.TagRepository;
import java.time.Instant;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
@SpringBootTest(classes = ApiServiceApplication.class)
class ArticleServiceIntegrationTest {

  @Container @ServiceConnection
  static PostgreSQLContainer<?> postgres =
      new PostgreSQLContainer<>("postgres:15").withPrivilegedMode(true);

  @Autowired ArticleRepository repository;
  @Autowired TagRepository tagRepository;
  @Autowired AlgoliaArticleService service;

  @BeforeEach
  void setUp() {
    repository.deleteAll();

    AlgoliaArticleEntity article = new AlgoliaArticleEntity();
    article.setObjectId("123");
    article.setAuthor("alice-integration");
    article.setStoryTitle("Introduction to Spring");
    article.setCreatedAt(Instant.parse("2023-01-15T12:00:00Z"));

    var javaTag = new AlgoliaTagEntity();
    javaTag.setName("java");

    var springTag = new AlgoliaTagEntity();
    springTag.setName("spring");

    tagRepository.saveAll(Set.of(javaTag, springTag));

    article.setTags(Set.of(javaTag, springTag));

    repository.save(article);
  }

  @Test
  void shouldReturnFilteredArticlesByAuthorAndTitle() {
    Pageable pageable = PageRequest.of(0, 10);

    Page<AlgoliaArticleDTO> result =
        service.findFilteredArticles(
            "alice-integration", List.of("java", "spring"), "Introduction", "January", pageable);

    assertEquals(1, result.getTotalElements());

    AlgoliaArticleDTO dto = result.getContent().get(0);
    assertEquals("alice-integration", dto.getAuthor());
    assertEquals("Introduction to Spring", dto.getStoryTitle());
    assertTrue(dto.getTags().containsAll(List.of("java", "spring")));
  }
}
