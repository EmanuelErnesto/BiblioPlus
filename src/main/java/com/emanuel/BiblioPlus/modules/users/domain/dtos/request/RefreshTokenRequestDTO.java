package com.emanuel.BiblioPlus.modules.users.domain.dtos.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;


@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class RefreshTokenRequestDTO {

    @NotBlank
    @Pattern(regexp = "^[A-Za-z0-9-_]+(\\.[A-Za-z0-9-_]+){2}$", message = "Invalid JWT token format")
    private String refreshToken;
}
