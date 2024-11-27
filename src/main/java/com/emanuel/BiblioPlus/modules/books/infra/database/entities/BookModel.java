package com.emanuel.BiblioPlus.modules.books.infra.database.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
@Entity
@Table(name = "tb_books",
        indexes = {
        @Index(name = "name_index", columnList = "name"),
        @Index(name = "pb_index", columnList = "publisher")
})
public class BookModel {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "release_date", nullable = false)
    private LocalDate releaseDate;

    @Column(name = "description", nullable = false, length = 500)
    private String description;

    @Column(name = "quantity_in_stock", nullable = false)
    private Long quantityInStock;

    @Column(name = "genre", nullable = false, length = 20)
    private String genre;

    @Column(name = "publisher", nullable = false)
    private String publisher;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Version
    private int version;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

}
