package com.emanuel.BiblioPlus.modules.books.services;

import com.emanuel.BiblioPlus.modules.books.domain.dtos.request.CreateBookDTO;
import com.emanuel.BiblioPlus.modules.books.domain.mappers.BookMapper;
import com.emanuel.BiblioPlus.modules.books.infra.database.entities.BookModel;
import com.emanuel.BiblioPlus.modules.books.infra.database.repositories.BookRepository;
import com.emanuel.BiblioPlus.shared.consts.BookExceptionConsts;
import com.emanuel.BiblioPlus.shared.exceptions.HttpBadRequestException;
import com.emanuel.BiblioPlus.shared.exceptions.HttpNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Service
public class BookService {

    @Autowired
    private BookRepository bookRepository;

    @Transactional
    public BookModel create(CreateBookDTO createBookDTO) {
      Optional<BookModel> bookNameAlreadyExists =
              bookRepository.findByName(createBookDTO.getName());

      if(bookNameAlreadyExists.isPresent() && bookNameAlreadyExists.get().getPublisher().equals(createBookDTO.getPublisher())) {
        throw new HttpBadRequestException(BookExceptionConsts.BOOK_ALREADY_EXISTS);
      }

      BookModel bookToCreate = BookMapper.mappingCreateBookDTOToBookModel(createBookDTO);

      return bookRepository.save(bookToCreate);
    }

    @Transactional(readOnly = true)
    public BookModel show(String id) {
        return bookRepository.findById(UUID.fromString(id))
                .orElseThrow(() -> new HttpNotFoundException(BookExceptionConsts.BOOK_NOT_FOUND));
    }

    @Transactional(readOnly = true)
    public Page<BookModel> list(Integer pageNumber, Integer size) {
        return bookRepository.findAll(PageRequest.of(pageNumber, size));
    }

    @Transactional
    public void delete(String id) {
        BookModel book = show(id);

        bookRepository.delete(book);
    }

    @Transactional
    public BookModel update(String id, CreateBookDTO bookDTO) {

        BookModel book = show(id);

        Optional<BookModel> bookSameNameWithOtherPublisher =
                bookRepository.findByNameAndPublisher(bookDTO.getName(), bookDTO.getPublisher());

        if(bookSameNameWithOtherPublisher.isPresent() && !Objects.equals(
                bookSameNameWithOtherPublisher.get().getId()
                ,book.getId())) {
            throw new HttpBadRequestException(BookExceptionConsts.BOOK_ALREADY_EXISTS);
        }

        BookMapper.setUpdateBookDataToBookModel(bookDTO, book);

        return bookRepository.save(book);
    }

}
