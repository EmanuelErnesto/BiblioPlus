package com.emanuel.BiblioPlus.modules.books.infra.controllers;

import com.emanuel.BiblioPlus.modules.books.domain.dtos.request.CreateBookDTO;
import com.emanuel.BiblioPlus.modules.books.domain.dtos.response.BookResponseDTO;
import com.emanuel.BiblioPlus.modules.books.domain.dtos.response.PaginatedBooksResponseDTO;
import com.emanuel.BiblioPlus.modules.books.domain.mappers.BookMapper;
import com.emanuel.BiblioPlus.modules.books.infra.database.entities.BookModel;
import com.emanuel.BiblioPlus.modules.books.services.BookService;
import com.emanuel.BiblioPlus.shared.exceptions.ApplicationException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.PositiveOrZero;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Tag(name = "books", description = "this endpoint can create, read, update and delete a book")
@RestController
@RequestMapping("/books")
@Validated
public class BookController {

    @Autowired
    private BookService bookService;

    @Operation(summary = "create a new book", description = "Resource for create a new book",
            security = @SecurityRequirement(name = "security"),
            responses = {
                    @ApiResponse(responseCode = "201", description = "Book create successfully.",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = BookResponseDTO.class))),
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
    public ResponseEntity<BookResponseDTO> create(@RequestBody @Valid CreateBookDTO body) {
        BookModel bookCreated = bookService.create(body);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(BookMapper.mappingBookModelToBookResponseDTO(bookCreated));
    }


    @Operation(summary = "Return a list of paginated books", description = "Resource that return a list of books",
            security = @SecurityRequirement(name = "security"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Books returned successfully",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = PaginatedBooksResponseDTO.class))),
                    @ApiResponse(responseCode = "403", description = "you must provide a token to access this resource",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Void.class))),
                    @ApiResponse(responseCode = "401", description = "you must provide a valid access token to access this resource. Refresh token don't be accepted",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApplicationException.class)))
            })
    @GetMapping
    public ResponseEntity<PaginatedBooksResponseDTO> list(
            @RequestParam(defaultValue = "0") @PositiveOrZero final Integer pageNumber,
            @RequestParam(defaultValue = "5") @PositiveOrZero final Integer size)  {

        Page<BookModel> books = bookService.list(pageNumber, size);

        return ResponseEntity.ok(BookMapper.mappingPaginatedBooksToPaginatedBookResponseDTO(books));
    }

    @Operation(summary = "Show a existent book", description = "Resource that return a existent book",
            security = @SecurityRequirement(name = "security"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "book returned successfully",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = BookResponseDTO.class))),
                    @ApiResponse(responseCode = "404", description = "book not found",
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
    public ResponseEntity<BookResponseDTO> show(
            @PathVariable("id")
            @Pattern(regexp = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-4[0-9a-fA-F]{3}-[89abAB][0-9a-fA-F]{3}-[0-9a-fA-F]{12}$")
            String id){

        BookModel book = bookService.show(id);

        return ResponseEntity.ok(BookMapper.mappingBookModelToBookResponseDTO(book));
    }

    @Operation(summary = "Update a existent book", description = "Resource that can update a existent book",
            security = @SecurityRequirement(name = "security"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Book updated successfully",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = BookResponseDTO.class))),
                    @ApiResponse(responseCode = "404", description = "Book not found",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApplicationException.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid id",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApplicationException.class))),
                    @ApiResponse(responseCode = "400", description = "Other Book with this same name and publisher already exists",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApplicationException.class))),
                    @ApiResponse(responseCode = "403", description = "you must provide a token to access this resource",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Void.class))),
                    @ApiResponse(responseCode = "401", description = "you must provide a valid access token to access this resource. Refresh token don't be accepted",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApplicationException.class)))
            })
    @PutMapping("/{id}")
    public ResponseEntity<BookResponseDTO> update(
            @PathVariable("id")
            @Pattern(regexp = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-4[0-9a-fA-F]{3}-[89abAB][0-9a-fA-F]{3}-[0-9a-fA-F]{12}$")
            String id,
            @RequestBody
            @Valid
            CreateBookDTO body) {


        BookModel book = bookService.update(id, body);

        return ResponseEntity.ok(BookMapper.mappingBookModelToBookResponseDTO(book));
    }


    @Operation(summary = "Delete a existent book", description = "Resource that delete a existent book",
            security = @SecurityRequirement(name = "security"),
            responses = {
                    @ApiResponse(responseCode = "204", description = "Book deleted successfully",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Void.class))),
                    @ApiResponse(responseCode = "404", description = "Book not found",
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

        bookService.delete(id);

        return ResponseEntity.noContent().build();
    }

}
