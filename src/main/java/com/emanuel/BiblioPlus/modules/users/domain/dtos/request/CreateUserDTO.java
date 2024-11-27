package com.emanuel.BiblioPlus.modules.users.domain.dtos.request;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class CreateUserDTO {

    @NotBlank
    @Size(min = 3, max = 100)
    private String name;

    @NotBlank
    @NotNull
    @Email
    private String email;

    @NotBlank
    @Size(min = 6, max = 50)
    private String password;

    @NotBlank
    @Pattern(regexp = "^\\d{11}$", message = "invalid cpf format. Try again with another value in format of 11 numbers")
    private String cpf;

    @NotBlank
    @Pattern(regexp = "^(0[1-9]|[12][0-9]|3[01])/(0[1-9]|1[0-2])/\\d{4}$", message = "invalid birthday format. Try again with a date in format dd/MM/yyyy")
    private String birthDay;

    @NotBlank
    @Pattern(regexp = "^\\d{8}$")
    private String cep;
}
