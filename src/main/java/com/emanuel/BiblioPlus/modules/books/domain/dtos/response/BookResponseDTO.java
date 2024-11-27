package com.emanuel.BiblioPlus.modules.books.domain.dtos.response;

import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

@Builder
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class BookResponseDTO {

   private UUID id;

   private String name;

   private LocalDate releaseDate;

   private String description;

   private Long quantityInStock;

   private String publisher;

   private String genre;
}
