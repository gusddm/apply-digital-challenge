package com.apply.digital.api.model;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PaginatedResponse<T> {
  private List<T> content;
  private long totalItems;
  private int totalPages;
  private int currentPage;
}
