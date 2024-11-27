package com.emanuel.BiblioPlus.modules.books.domain.dtos.request;

import jakarta.validation.constraints.*;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
@Getter
@Setter
public class CreateBookDTO {

    @NotBlank
    @Size(min = 5, max = 200)
    private String name;

    @NotBlank
    @Pattern(regexp = "^(0[1-9]|[12][0-9]|3[01])/(0[1-9]|1[0-2])/\\d{4}$")
    private String releaseDate;

    @NotBlank
    @Size(min = 10, max = 500)
    private String description;

    @NotNull
    @Min(0L)
    @Max(1000L)
    private Long quantityInStock;

    @NotBlank
    @Size(min = 3, max = 50)
    private String publisher;

    @NotBlank()
    @Size(min = 4, max = 30)
    private String genre;
}
