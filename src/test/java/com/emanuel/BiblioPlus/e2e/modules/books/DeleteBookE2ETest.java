package com.emanuel.BiblioPlus.e2e.modules.books;


import com.emanuel.BiblioPlus.e2e.utils.GetJwtAuthenticationToken;
import com.emanuel.BiblioPlus.modules.users.domain.dtos.request.AuthenticationDTO;
import com.emanuel.BiblioPlus.shared.consts.BookExceptionConsts;
import com.emanuel.BiblioPlus.shared.consts.UserExceptionConsts;
import com.emanuel.BiblioPlus.shared.exceptions.ApplicationException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Sql(scripts = "/sql/users/insert-users.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "/sql/users/clear-users-table.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
@Sql(scripts = "/sql/books/insert-books.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "/sql/books/clear-books-table.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public class DeleteBookE2ETest {

    @Autowired
    private WebTestClient testClient;

    private String deleteBooksUri = "/books";

    @Test
    void deleteUserThatExistsWithStatus204() {
        String bookId = "d8f426fe-71f9-46ed-8d0b-b39a4f88db42";
        AuthenticationDTO authenticationDTO = new AuthenticationDTO("maria@gmail.com", "senha123");

        var responseBody = testClient
                .delete()
                .uri(deleteBooksUri + "/" + bookId)
                .headers(GetJwtAuthenticationToken.getHeaderJwtAccessToken(testClient, authenticationDTO.getEmail(), authenticationDTO.getPassword()))
                .exchange()
                .expectStatus().isNoContent()
                .expectBody().isEmpty();
    }

    @Test
    void deleteWithIdThatNotExistsInDatabaseReturnStatus404() {
        String bookId = "1fcd84cf-4601-467c-b2a7-5b6fb7a908a2";
        AuthenticationDTO authenticationDTO = new AuthenticationDTO("maria@gmail.com", "senha123");

        ApplicationException responseBody = testClient
                .delete()
                .uri(deleteBooksUri + "/" + bookId)
                .headers(GetJwtAuthenticationToken.getHeaderJwtAccessToken(testClient, authenticationDTO.getEmail(), authenticationDTO.getPassword()))
                .exchange()
                .expectStatus().isNotFound()
                .expectBody(ApplicationException.class)
                .returnResult().getResponseBody();


        Assertions.assertThat(responseBody).isNotNull();
        Assertions.assertThat(responseBody).isExactlyInstanceOf(ApplicationException.class);
        Assertions.assertThat(responseBody.getCode()).isEqualTo(404);
        Assertions.assertThat(responseBody.getStatus()).isEqualTo("Not Found");
        Assertions.assertThat(responseBody.getMethod()).isEqualTo("DELETE");
        Assertions.assertThat(responseBody.getMessage()).isEqualTo(BookExceptionConsts.BOOK_NOT_FOUND);
    }

    @Test
    void deleteUserWithIdInInvalidFormatReturnStatus400() {
        String bookId = "gdfgdfgdfg";
        AuthenticationDTO authenticationDTO = new AuthenticationDTO("maria@gmail.com", "senha123");

        ApplicationException responseBody = testClient
                .delete()
                .uri(deleteBooksUri + "/" + bookId)
                .headers(GetJwtAuthenticationToken.getHeaderJwtAccessToken(testClient, authenticationDTO.getEmail(), authenticationDTO.getPassword()))
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(ApplicationException.class)
                .returnResult().getResponseBody();

        Assertions.assertThat(responseBody).isNotNull();
        Assertions.assertThat(responseBody).isExactlyInstanceOf(ApplicationException.class);
        Assertions.assertThat(responseBody.getCode()).isEqualTo(400);
        Assertions.assertThat(responseBody.getStatus()).isEqualTo("Bad Request");
        Assertions.assertThat(responseBody.getMethod()).isEqualTo("DELETE");
    }

    @Test
    void deleteUserWithoutTokenReturnStatus403(){
        String bookId = "2e29b0bb-0d8e-40ff-af0c-f7ed0d9db117";
        testClient
                .delete()
                .uri(deleteBooksUri + "/" + bookId)
                .exchange()
                .expectStatus().isForbidden()
                .expectBody().isEmpty();
    }


}
