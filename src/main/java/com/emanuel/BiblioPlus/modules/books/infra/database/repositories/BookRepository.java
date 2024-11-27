package com.emanuel.BiblioPlus.modules.books.infra.database.repositories;

import com.emanuel.BiblioPlus.modules.books.infra.database.entities.BookModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface BookRepository extends JpaRepository<BookModel, UUID> {

    Optional<BookModel> findByName(String name);

    Optional<BookModel> findByNameAndPublisher(String name, String publisher);

}
