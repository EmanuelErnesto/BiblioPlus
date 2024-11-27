package com.emanuel.BiblioPlus.modules.orders.domain.mappers;

import com.emanuel.BiblioPlus.modules.books.domain.dtos.response.PaginatedBooksResponseDTO;
import com.emanuel.BiblioPlus.modules.books.domain.mappers.BookMapper;
import com.emanuel.BiblioPlus.modules.books.infra.database.entities.BookModel;
import com.emanuel.BiblioPlus.modules.orders.domain.dtos.request.CreateOrderDTO;
import com.emanuel.BiblioPlus.modules.orders.domain.dtos.response.OrderResponseDTO;
import com.emanuel.BiblioPlus.modules.orders.domain.dtos.response.PaginatedOrderDTO;
import com.emanuel.BiblioPlus.modules.orders.infra.database.entities.OrderModel;
import com.emanuel.BiblioPlus.modules.orders.infra.database.entities.OrderStatus;
import org.springframework.data.domain.Page;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class OrderMapper {

    public static OrderModel mappingCreateOrderDTOToOrderEntity(CreateOrderDTO orderDTO) {
        return OrderModel
                .builder()
                .orderStatus(OrderStatus.DELIVERED)
                .startDate(mappingStringLocalDateToLocalDate(orderDTO.getStartDate()))
                .endDate(mappingStringLocalDateToLocalDate(orderDTO.getEndDate()))
                .build();
    }

    public static LocalDate mappingStringLocalDateToLocalDate(String dateToConvert) {
        return LocalDate.parse(dateToConvert, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    }

    public static void mappingUpdateOrderDataToOrderModel(OrderModel order, CreateOrderDTO orderDTO) {
        order.setStartDate(mappingStringLocalDateToLocalDate(orderDTO.getStartDate()));
        order.setEndDate((mappingStringLocalDateToLocalDate(orderDTO.getEndDate())));
    }

    public static OrderResponseDTO mappingOrderModelToOrderResponseDTO(OrderModel order) {
        return OrderResponseDTO
                .builder()
                .id(order.getId())
                .userId(order.getUser().getId())
                .bookId(order.getBook().getId())
                .startDate(order.getStartDate())
                .endDate(order.getEndDate())
                .build();
    }

    public static PaginatedOrderDTO mappingPaginatedOrdersToPaginatedOrderResponseDTO(Page<OrderModel> ordersPaginated) {
        return PaginatedOrderDTO
                .builder()
                .orders(ordersPaginated
                        .getContent()
                        .stream()
                        .map(OrderMapper::mappingOrderModelToOrderResponseDTO)
                        .toList())
                .current_page(ordersPaginated.getNumber())
                .total_items(ordersPaginated.getTotalElements())
                .total_pages(ordersPaginated.getTotalPages())
                .build();
    }
}
