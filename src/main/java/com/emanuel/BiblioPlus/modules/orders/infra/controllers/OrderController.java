package com.emanuel.BiblioPlus.modules.orders.infra.controllers;

import com.emanuel.BiblioPlus.modules.orders.domain.dtos.request.CreateOrderDTO;
import com.emanuel.BiblioPlus.modules.orders.domain.dtos.request.UpdateOrderStatusDTO;
import com.emanuel.BiblioPlus.modules.orders.domain.dtos.response.OrderResponseDTO;
import com.emanuel.BiblioPlus.modules.orders.domain.dtos.response.PaginatedOrderDTO;
import com.emanuel.BiblioPlus.modules.orders.domain.mappers.OrderMapper;
import com.emanuel.BiblioPlus.modules.orders.infra.database.entities.OrderModel;
import com.emanuel.BiblioPlus.modules.orders.services.OrderService;
import com.emanuel.BiblioPlus.shared.exceptions.ApplicationException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Tag(name = "orders", description = "Endpoints for create, read, update and delete orders")
@RestController
@RequestMapping("/orders")
@Validated
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Operation(summary = "create a new order", description = "Resource for create a new order",
            security = @SecurityRequirement(name = "security"),
            responses = {
                    @ApiResponse(responseCode = "201", description = "Order created successfully.",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = OrderResponseDTO.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid field inserted.",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApplicationException.class))),
                    @ApiResponse(responseCode = "400", description = "same name and publisher already used",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApplicationException.class))),
                    @ApiResponse(responseCode = "403", description = "you must provide a token to access this resource",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Void.class))),
                    @ApiResponse(responseCode = "401", description = "you must provide a valid access token to access this resource. Refresh token don't be accepted",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApplicationException.class)))
            })
    @PostMapping
    public ResponseEntity<OrderResponseDTO> create(
            @RequestBody
            @Valid
            CreateOrderDTO body
    ) {
        OrderModel order = orderService.create(body);
        return ResponseEntity.status(HttpStatus.CREATED).body(OrderMapper.mappingOrderModelToOrderResponseDTO(order));
    }

    @Operation(summary = "Return a list of paginated orders", description = "Resource that return a list of orders",
            security = @SecurityRequirement(name = "security"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Orders returned successfully",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = PaginatedOrderDTO.class))),
                    @ApiResponse(responseCode = "403", description = "you must provide a token to access this resource",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Void.class))),
                    @ApiResponse(responseCode = "401", description = "you must provide a valid access token to access this resource. Refresh token don't be accepted",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApplicationException.class)))
            })
    @GetMapping
    public ResponseEntity<PaginatedOrderDTO> list(
            @RequestParam(defaultValue = "0") @PositiveOrZero final Integer pageNumber,
            @RequestParam(defaultValue = "5") @PositiveOrZero final Integer size) {

        Page<OrderModel> orders = orderService.list(pageNumber, size);

        return ResponseEntity.ok(OrderMapper.mappingPaginatedOrdersToPaginatedOrderResponseDTO(orders));
    }

    @Operation(summary = "Show a existent order", description = "Resource that return a existent order",
            security = @SecurityRequirement(name = "security"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "order returned successfully",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = OrderResponseDTO.class))),
                    @ApiResponse(responseCode = "404", description = "order not found",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApplicationException.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid id",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApplicationException.class))
                    ),
                    @ApiResponse(responseCode = "403", description = "you must provide a token to access this resource",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Void.class))),
                    @ApiResponse(responseCode = "401", description = "you must provide a valid access token to access this resource. Refresh token don't be accepted",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApplicationException.class)))
            })
    @GetMapping("/{id}")
    public ResponseEntity<OrderResponseDTO> show(
            @PathVariable("id")
            @Pattern(regexp = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-4[0-9a-fA-F]{3}-[89abAB][0-9a-fA-F]{3}-[0-9a-fA-F]{12}$")
            String id){

       OrderModel order = orderService.show(id);

        return ResponseEntity.ok(OrderMapper.mappingOrderModelToOrderResponseDTO(order));
    }

    @Operation(summary = "Update a existent order", description = "Resource that can update a existent order",
            security = @SecurityRequirement(name = "security"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Order updated successfully",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = OrderResponseDTO.class))),
                    @ApiResponse(responseCode = "404", description = "Order not found",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApplicationException.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid id",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApplicationException.class))),
                    @ApiResponse(responseCode = "400", description = "Other Order with this same name and publisher already exists",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApplicationException.class))),
                    @ApiResponse(responseCode = "403", description = "you must provide a token to access this resource",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Void.class))),
                    @ApiResponse(responseCode = "401", description = "you must provide a valid access token to access this resource. Refresh token don't be accepted",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApplicationException.class)))
            })
    @PutMapping("/{id}")
    public ResponseEntity<OrderResponseDTO> update(
            @PathVariable("id")
            @Pattern(regexp = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-4[0-9a-fA-F]{3}-[89abAB][0-9a-fA-F]{3}-[0-9a-fA-F]{12}$")
            String id,
            @RequestBody
            @Valid
            CreateOrderDTO body) {


        OrderModel order = orderService.update(id, body);

        return ResponseEntity.ok(OrderMapper.mappingOrderModelToOrderResponseDTO(order));
    }

    @Operation(summary = "Delete a existent order", description = "Resource that delete a existent order",
            security = @SecurityRequirement(name = "security"),
            responses = {
                    @ApiResponse(responseCode = "204", description = "Order deleted successfully",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Void.class))),
                    @ApiResponse(responseCode = "404", description = "Order not found",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApplicationException.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid id inserted",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApplicationException.class))),
                    @ApiResponse(responseCode = "403", description = "you must provide a token to access this resource",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Void.class))),
                    @ApiResponse(responseCode = "401", description = "you must provide a valid access token to access this resource. Refresh token don't be accepted",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApplicationException.class)))
            })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable("id")
            @Pattern(regexp = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-4[0-9a-fA-F]{3}-[89abAB][0-9a-fA-F]{3}-[0-9a-fA-F]{12}$")

            String id) {

        orderService.delete(id);

        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Update status of an existent order", description = "Resource that update a status of an existent order",
            security = @SecurityRequirement(name = "security"),
            responses = {
                    @ApiResponse(responseCode = "204", description = "Order status updated successfully",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Void.class))),
                    @ApiResponse(responseCode = "404", description = "Order not found",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApplicationException.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid id inserted",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApplicationException.class))),
                    @ApiResponse(responseCode = "403", description = "you must provide a token to access this resource",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Void.class))),
                    @ApiResponse(responseCode = "401", description = "you must provide a valid access token to access this resource. Refresh token don't be accepted",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApplicationException.class)))
            })
    @PatchMapping("/{id}")
    public ResponseEntity<Void> updateOrderStatus(
            @PathVariable("id")
            @Pattern(regexp = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-4[0-9a-fA-F]{3}-[89abAB][0-9a-fA-F]{3}-[0-9a-fA-F]{12}$")
            String id,
            @RequestBody @Valid UpdateOrderStatusDTO body
            ) {

        orderService.updateOrderStatus(id, body.getUserId(), body.getBookId());

        return ResponseEntity.noContent().build();
    }
}
