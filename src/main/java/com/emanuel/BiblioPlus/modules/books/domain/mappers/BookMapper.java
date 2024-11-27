package com.emanuel.BiblioPlus.modules.books.domain.mappers;

import com.emanuel.BiblioPlus.modules.books.domain.dtos.request.CreateBookDTO;
import com.emanuel.BiblioPlus.modules.books.domain.dtos.response.BookResponseDTO;
import com.emanuel.BiblioPlus.modules.books.domain.dtos.response.PaginatedBooksResponseDTO;
import com.emanuel.BiblioPlus.modules.books.infra.database.entities.BookModel;
import com.emanuel.BiblioPlus.modules.users.domain.mappers.UserMapper;
import org.springframework.data.domain.Page;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;

public class BookMapper {

    public static BookModel mappingCreateBookDTOToBookModel(CreateBookDTO bookDTO) {
        return BookModel
                .builder()
                .name(bookDTO.getName())
                .releaseDate(LocalDate.parse(bookDTO.getReleaseDate(), DateTimeFormatter.ofPattern("dd/MM/yyyy")))
                .description(bookDTO.getDescription())
                .quantityInStock(bookDTO.getQuantityInStock())
                .publisher(bookDTO.getPublisher())
                .genre(bookDTO.getGenre())
                .build();
    }

    public static BookResponseDTO mappingBookModelToBookResponseDTO(BookModel bookModel) {
        return BookResponseDTO
                .builder()
                .id(bookModel.getId())
                .name(bookModel.getName())
                .releaseDate(bookModel.getReleaseDate())
                .description(bookModel.getDescription())
                .quantityInStock(bookModel.getQuantityInStock())
                .publisher(bookModel.getPublisher())
                .genre(bookModel.getGenre())
                .build();
    }

    public static void setUpdateBookDataToBookModel(CreateBookDTO bookDTO, BookModel book) {
        book.setName(bookDTO.getName());
        book.setReleaseDate(LocalDate.parse(bookDTO.getReleaseDate(), DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        book.setDescription(bookDTO.getDescription());
        book.setQuantityInStock(bookDTO.getQuantityInStock());
        book.setPublisher(bookDTO.getPublisher());
        book.setGenre(bookDTO.getGenre());
    }

    public static PaginatedBooksResponseDTO mappingPaginatedBooksToPaginatedBookResponseDTO(Page<BookModel> booksPaginated) {
       return PaginatedBooksResponseDTO
               .builder()
               .books(booksPaginated
                       .getContent()
                       .stream()
                       .map(BookMapper::mappingBookModelToBookResponseDTO)
                       .toList())
               .current_page(booksPaginated.getNumber())
               .total_items(booksPaginated.getTotalElements())
               .total_pages(booksPaginated.getTotalPages())
               .build();
    }

}
