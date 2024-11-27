package com.emanuel.BiblioPlus.modules.users.domain.dtos.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class UpdateUserPasswordDTO {

    @NotBlank
    @Size(min = 6, max = 50)
    private String oldPassword;

    @NotBlank
    @Size(min = 6, max = 50)
    private String newPassword;

    @NotBlank
    @Size(min = 6, max = 50)
    private String passwordConfirmation;
}
