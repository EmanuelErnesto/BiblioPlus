package com.emanuel.BiblioPlus.modules.users.domain.dtos.response;


import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class UserResponseDTO {

    private UUID id;
    private String name;
    private String email;
    private String cpf;
    private LocalDate birthDay;
    private String role;
    private String cep;
    private String patio;
    private String complement;
    private String neighborhood;
    private String locality;
    private String uf;
}
