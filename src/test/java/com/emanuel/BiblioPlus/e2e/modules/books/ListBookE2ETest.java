package com.emanuel.BiblioPlus.e2e.modules.books;

import com.emanuel.BiblioPlus.e2e.utils.GetJwtAuthenticationToken;
import com.emanuel.BiblioPlus.modules.books.domain.dtos.response.PaginatedBooksResponseDTO;
import com.emanuel.BiblioPlus.modules.users.domain.dtos.request.AuthenticationDTO;
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
public class ListBookE2ETest {

    @Autowired
    private WebTestClient testClient;

    private String listBooksUri = "/books";

    @Test
    void listBooksPaginatedReturnStatus200() {
        AuthenticationDTO authenticationDTO = new AuthenticationDTO("maria@gmail.com", "senha123");

        PaginatedBooksResponseDTO responseBody = testClient
                .get()
                .uri(listBooksUri)
                .headers(GetJwtAuthenticationToken.getHeaderJwtAccessToken(testClient, authenticationDTO.getEmail(), authenticationDTO.getPassword()))
                .exchange()
                .expectStatus().isOk()
                .expectBody(PaginatedBooksResponseDTO.class)
                .returnResult().getResponseBody();

        Assertions.assertThat(responseBody).isNotNull();
        Assertions.assertThat(responseBody).isExactlyInstanceOf(PaginatedBooksResponseDTO.class);
        Assertions.assertThat(responseBody.books().size()).isEqualTo(3);
        Assertions.assertThat(responseBody.current_page()).isEqualTo(0);
        Assertions.assertThat(responseBody.total_items()).isEqualTo(3);
        Assertions.assertThat(responseBody.total_pages()).isEqualTo(1);
    }

    @Test
    void listBooksPaginatedWithSizeReturnStatus200() {
        AuthenticationDTO authenticationDTO = new AuthenticationDTO("maria@gmail.com", "senha123");

        PaginatedBooksResponseDTO responseBody = testClient
                .get()
                .uri(listBooksUri + "?size=1")
                .headers(GetJwtAuthenticationToken.getHeaderJwtAccessToken(testClient, authenticationDTO.getEmail(), authenticationDTO.getPassword()))
                .exchange()
                .expectStatus().isOk()
                .expectBody(PaginatedBooksResponseDTO.class)
                .returnResult().getResponseBody();

        Assertions.assertThat(responseBody).isNotNull();
        Assertions.assertThat(responseBody).isExactlyInstanceOf(PaginatedBooksResponseDTO.class);
        Assertions.assertThat(responseBody.books().size()).isEqualTo(1);
        Assertions.assertThat(responseBody.current_page()).isEqualTo(0);
        Assertions.assertThat(responseBody.total_items()).isEqualTo(3);
        Assertions.assertThat(responseBody.total_pages()).isEqualTo(3);
    }

    @Test
    void listBooksPaginatedWithPageNumberAndSizeReturnStatus200() {
        AuthenticationDTO authenticationDTO = new AuthenticationDTO("maria@gmail.com", "senha123");

        PaginatedBooksResponseDTO responseBody = testClient
                .get()
                .uri(listBooksUri + "?size=1&pageNumber=1")
                .headers(GetJwtAuthenticationToken.getHeaderJwtAccessToken(testClient, authenticationDTO.getEmail(), authenticationDTO.getPassword()))
                .exchange()
                .expectStatus().isOk()
                .expectBody(PaginatedBooksResponseDTO.class)
                .returnResult().getResponseBody();

        Assertions.assertThat(responseBody).isNotNull();
        Assertions.assertThat(responseBody).isExactlyInstanceOf(PaginatedBooksResponseDTO.class);
        Assertions.assertThat(responseBody.books().size()).isEqualTo(1);
        Assertions.assertThat(responseBody.current_page()).isEqualTo(1);
        Assertions.assertThat(responseBody.total_items()).isEqualTo(3);
        Assertions.assertThat(responseBody.total_pages()).isEqualTo(3);
    }

    @Test
    void listBooksPaginatedWithUserThatNotHaveAdminRoleReturnStatus403(){
        AuthenticationDTO authenticationDTO = new AuthenticationDTO("joao@gmail.com", "senha123");
        testClient
                .get()
                .uri(listBooksUri)
                .headers(GetJwtAuthenticationToken.getHeaderJwtAccessToken(testClient, authenticationDTO.getEmail(), authenticationDTO.getPassword()))
                .exchange()
                .expectStatus().isForbidden()
                .expectBody().isEmpty();
    }

    @Test
    void listBooksPaginatedWithoutTokenReturnStatus403() {
        testClient
                .get()
                .uri(listBooksUri)
                .exchange()
                .expectStatus().isForbidden()
                .expectBody().isEmpty();
    }

}
