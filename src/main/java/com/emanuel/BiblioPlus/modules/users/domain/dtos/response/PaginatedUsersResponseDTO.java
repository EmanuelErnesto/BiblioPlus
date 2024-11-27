package com.emanuel.BiblioPlus.modules.users.domain.dtos.response;

import lombok.Builder;

import java.util.List;

@Builder
public record PaginatedUsersResponseDTO(
        List<UserResponseDTO> users,
        int current_page,
        long total_items,
        int total_pages
) {
}
