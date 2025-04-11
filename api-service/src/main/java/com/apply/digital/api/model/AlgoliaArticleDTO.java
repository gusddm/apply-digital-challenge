package com.apply.digital.api.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.Instant;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AlgoliaArticleDTO {
  private String objectId;
  private String author;
  private String commentText;
  private String createdAt;
  private Long parentId;
  private long storyId;
  private String storyTitle;
  private String storyUrl;
  private Instant updatedAt;
  private Set<String> tags;
}
