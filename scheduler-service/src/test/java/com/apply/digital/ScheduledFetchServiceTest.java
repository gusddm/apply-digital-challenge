package com.apply.digital;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.apply.digital.db.entities.AlgoliaTagEntity;
import com.apply.digital.db.repositories.ArticleRepository;
import com.apply.digital.db.repositories.TagRepository;
import com.apply.digital.scheduler.service.ScheduledFetchService;
import java.net.http.HttpClient;
import java.net.http.HttpResponse;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ScheduledFetchServiceTest {

  @Mock ArticleRepository articleRepository;

  @Mock TagRepository tagRepository;

  @Mock HttpClient httpClient;

  @Mock HttpResponse<String> httpResponse;

  ScheduledFetchService fetchService;

  final String dummyApiUrl = "http://mocked.api/hn";

  @BeforeEach
  void setUp() {
    fetchService =
        new ScheduledFetchService(dummyApiUrl, articleRepository, tagRepository, httpClient);
  }

  @Test
  void testFetchNews_savesNewArticle_whenNotExists() throws Exception {
    // Given
    String jsonResponse =
        """
        {
            "hits": [
                {
                    "objectID": "abc123",
                    "story_title": "Test Title",
                    "story_url": "http://example.com/story",
                    "author": "johndoe",
                    "created_at": "2025-04-10T12:00:00Z",
                    "_tags": ["java", "spring"]
                }
            ]
        }
        """;

    when(httpClient.send(any(), any())).thenAnswer(invocation -> httpResponse);
    when(httpResponse.body()).thenReturn(jsonResponse);
    when(articleRepository.existsByObjectId("abc123")).thenReturn(false);

    AlgoliaTagEntity javaTag = new AlgoliaTagEntity();
    javaTag.setId(1L);
    javaTag.setName("java");

    AlgoliaTagEntity springTag = new AlgoliaTagEntity();
    springTag.setId(2L);
    springTag.setName("spring");

    when(tagRepository.findByName("java")).thenReturn(Optional.of(javaTag));
    when(tagRepository.findByName("spring")).thenReturn(Optional.of(springTag));

    // When
    fetchService.fetchNews();

    // Then
    verify(articleRepository)
        .save(
            argThat(
                article ->
                    article.getObjectId().equals("abc123")
                        && article.getStoryTitle().equals("Test Title")
                        && article.getAuthor().equals("johndoe")
                        && article.getTags().containsAll(Set.of(javaTag, springTag))));
  }
}
