package com.emanuel.BiblioPlus.modules.orders.domain.dtos.response;

import lombok.Builder;
import java.time.LocalDate;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;



@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class OrderResponseDTO {

    private UUID id;
    private UUID userId;
    private UUID bookId;
    private LocalDate startDate;
    private LocalDate endDate;

}

