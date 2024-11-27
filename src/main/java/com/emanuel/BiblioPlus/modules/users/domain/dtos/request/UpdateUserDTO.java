package com.emanuel.BiblioPlus.modules.users.domain.dtos.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class UpdateUserDTO {

    @NotBlank
    @Size(min = 3, max = 100)
    private String name;

    @NotBlank
    @Pattern(regexp = "^[a-z0-9.+-]+@[a-z0-9.+-]+\\.[a-z]{2,}$", message = "invalid email format. Try again with a valid email in format user@example.com")
    private String email;

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
