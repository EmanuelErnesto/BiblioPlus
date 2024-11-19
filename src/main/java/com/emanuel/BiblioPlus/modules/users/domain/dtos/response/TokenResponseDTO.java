package com.emanuel.BiblioPlus.modules.users.domain.dtos.response;

import lombok.Builder;

@Builder
public record TokenResponseDTO(String accessToken, String refreshToken) {
}
