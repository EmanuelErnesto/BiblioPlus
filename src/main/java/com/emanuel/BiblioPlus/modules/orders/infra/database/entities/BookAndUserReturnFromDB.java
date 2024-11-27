package com.emanuel.BiblioPlus.modules.orders.infra.database.entities;

import com.emanuel.BiblioPlus.modules.books.infra.database.entities.BookModel;
import com.emanuel.BiblioPlus.modules.users.infra.database.entities.UserModel;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class BookAndUserReturnFromDB {

    private UserModel user;
    private BookModel book;
}
