package com.apply.digital;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import com.apply.digital.api.model.AlgoliaArticleDTO;
import com.apply.digital.api.service.AlgoliaArticleService;
import com.apply.digital.db.entities.AlgoliaArticleEntity;
import com.apply.digital.db.entities.AlgoliaTagEntity;
import com.apply.digital.db.repositories.ArticleRepository;
import java.time.Instant;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

@ExtendWith(MockitoExtension.class)
class AlgoliaArticleServiceTest {

  @Mock private ArticleRepository repository;

  @InjectMocks private AlgoliaArticleService service;

  @Test
  void shouldReturnFilteredArticlesByAuthorAndTitle() {
    // Given
    String author = "alice";
    List<String> tags = List.of("java", "spring");
    String title = "introduction";
    String month = "JANUARY";
    Pageable pageable = PageRequest.of(0, 10);

    AlgoliaArticleEntity article = new AlgoliaArticleEntity();
    article.setObjectId("123");
    article.setAuthor(author);
    article.setStoryTitle("Introduction to Spring");
    article.setCreatedAt(Instant.parse("2023-01-15T12:00:00Z"));
    article.setTags(Set.of(new AlgoliaTagEntity(1L, "java"), new AlgoliaTagEntity(2L, "spring")));

    Page<AlgoliaArticleEntity> mockPage = new PageImpl<>(List.of(article));
    when(repository.findAll(any(Specification.class), eq(pageable))).thenReturn(mockPage);

    // When
    Page<AlgoliaArticleDTO> result =
        service.findFilteredArticles(author, tags, title, month, pageable);

    // Then
    assertEquals(1, result.getTotalElements());
    AlgoliaArticleDTO dto = result.getContent().get(0);
    assertEquals("alice", dto.getAuthor());
    assertEquals("Introduction to Spring", dto.getStoryTitle());
    assertTrue(dto.getTags().containsAll(tags));
  }

  @Test
  void shouldThrowExceptionForInvalidMonth() {
    String invalidMonth = "not-a-month";
    Pageable pageable = Pageable.unpaged();

    RuntimeException ex =
        assertThrows(
            RuntimeException.class,
            () -> service.findFilteredArticles(null, null, null, invalidMonth, pageable));

    assertEquals("Invalid month name: not-a-month", ex.getMessage());
  }

  @Test
  void shouldCallDeleteByObjectId() {
    String objectId = "abc123";

    service.deleteByObjectId(objectId);

    verify(repository).deleteByObjectId(objectId);
  }
}
