package com.emanuel.BiblioPlus.modules.books.domain.dtos.response;

import lombok.Builder;

import java.util.List;

@Builder
public record PaginatedBooksResponseDTO(
        List<BookResponseDTO> books,
        int current_page,
        long total_items,
        int total_pages
) {
}
