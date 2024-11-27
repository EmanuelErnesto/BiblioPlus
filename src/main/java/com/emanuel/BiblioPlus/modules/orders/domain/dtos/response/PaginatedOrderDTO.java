package com.emanuel.BiblioPlus.modules.orders.domain.dtos.response;



import lombok.Builder;

import java.util.List;

@Builder
public record PaginatedOrderDTO(
        List<OrderResponseDTO> orders,
        int current_page,
        long total_items,
        int total_pages
) {
}
