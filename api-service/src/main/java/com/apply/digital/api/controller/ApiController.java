package com.apply.digital.api.controller;

import com.apply.digital.api.model.AlgoliaArticleDTO;
import com.apply.digital.api.model.PaginatedResponse;
import com.apply.digital.api.service.AlgoliaArticleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/articles")
public class ApiController {

  private final AlgoliaArticleService articleService;

  @Operation(
      summary = "Get filtered articles",
      description = "Filter articles by author, tags, title, and creation month.")
  @GetMapping
  public ResponseEntity<PaginatedResponse<AlgoliaArticleDTO>> getFilteredArticles(
      @Parameter(description = "Author of the article")
          @RequestParam(name = "author", required = false)
          String author,
      @Parameter(description = "Tags to filter by") @RequestParam(name = "tags", required = false)
          List<String> tags,
      @Parameter(description = "Title of the article")
          @RequestParam(name = "title", required = false)
          String title,
      @Parameter(description = "Month of creation (Jan-Dec)")
          @RequestParam(name = "month", required = false)
          String month,
      @ParameterObject @PageableDefault(value = 5) Pageable pageable) {

    Page<AlgoliaArticleDTO> result =
        articleService.findFilteredArticles(author, tags, title, month, pageable);

    PaginatedResponse<AlgoliaArticleDTO> response =
        new PaginatedResponse<>(
            result.getContent(),
            result.getTotalElements(),
            result.getTotalPages(),
            result.getNumber());
    return ResponseEntity.ok(response);
  }

  @PreAuthorize("hasAuthority('SCOPE_write')")
  @DeleteMapping("/{objectId}")
  public ResponseEntity<Void> deleteArticle(@PathVariable(name = "objectId") String objectId) {
    articleService.deleteByObjectId(objectId);
    return ResponseEntity.noContent().build(); // 204 No Content
  }
}
