package com.emanuel.BiblioPlus.modules.users.domain.dtos.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class AuthenticationDTO {

    @NotBlank
    @Pattern(regexp = "^[a-z0-9.+-]+@[a-z0-9.+-]+\\.[a-z]{2,}$", message = "invalid email format. Try again with a valid email in format user@example.com")
    private String email;

    @NotBlank
    @Size(min = 6, max = 50)
    private String password;
}
