package com.emanuel.BiblioPlus.e2e.modules.orders;


import com.emanuel.BiblioPlus.e2e.utils.GetJwtAuthenticationToken;
import com.emanuel.BiblioPlus.modules.orders.domain.dtos.response.PaginatedOrderDTO;
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
@Sql(scripts = "/sql/books/insert-books.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "/sql/orders/insert-orders.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "/sql/orders/clear-orders-table.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public class ListOrderE2ETest {


    @Autowired
    private WebTestClient testClient;

    private String listOrderUri = "/orders";

    @Test
    void listOrdersPaginatedReturnStatus200() {
        AuthenticationDTO authenticationDTO = new AuthenticationDTO("maria@gmail.com", "senha123");

        PaginatedOrderDTO responseBody = testClient
                .get()
                .uri(listOrderUri)
                .headers(GetJwtAuthenticationToken.getHeaderJwtAccessToken(testClient, authenticationDTO.getEmail(), authenticationDTO.getPassword()))
                .exchange()
                .expectStatus().isOk()
                .expectBody(PaginatedOrderDTO.class)
                .returnResult().getResponseBody();

        Assertions.assertThat(responseBody).isNotNull();
        Assertions.assertThat(responseBody).isExactlyInstanceOf(PaginatedOrderDTO.class);
        Assertions.assertThat(responseBody.orders().size()).isEqualTo(3);
        Assertions.assertThat(responseBody.current_page()).isEqualTo(0);
        Assertions.assertThat(responseBody.total_items()).isEqualTo(3);
        Assertions.assertThat(responseBody.total_pages()).isEqualTo(1);
    }

    @Test
    void listOrdersPaginatedWithSizeReturnStatus200() {
        AuthenticationDTO authenticationDTO = new AuthenticationDTO("maria@gmail.com", "senha123");

        PaginatedOrderDTO responseBody = testClient
                .get()
                .uri(listOrderUri + "?size=1")
                .headers(GetJwtAuthenticationToken.getHeaderJwtAccessToken(testClient, authenticationDTO.getEmail(), authenticationDTO.getPassword()))
                .exchange()
                .expectStatus().isOk()
                .expectBody(PaginatedOrderDTO.class)
                .returnResult().getResponseBody();

        Assertions.assertThat(responseBody).isNotNull();
        Assertions.assertThat(responseBody).isExactlyInstanceOf(PaginatedOrderDTO.class);
        Assertions.assertThat(responseBody.orders().size()).isEqualTo(1);
        Assertions.assertThat(responseBody.current_page()).isEqualTo(0);
        Assertions.assertThat(responseBody.total_items()).isEqualTo(3);
        Assertions.assertThat(responseBody.total_pages()).isEqualTo(3);
    }

    @Test
    void listOrdersPaginatedWithPageNumberAndSizeReturnStatus200() {
        AuthenticationDTO authenticationDTO = new AuthenticationDTO("maria@gmail.com", "senha123");

        PaginatedOrderDTO responseBody = testClient
                .get()
                .uri(listOrderUri + "?size=1&pageNumber=1")
                .headers(GetJwtAuthenticationToken.getHeaderJwtAccessToken(testClient, authenticationDTO.getEmail(), authenticationDTO.getPassword()))
                .exchange()
                .expectStatus().isOk()
                .expectBody(PaginatedOrderDTO.class)
                .returnResult().getResponseBody();

        Assertions.assertThat(responseBody).isNotNull();
        Assertions.assertThat(responseBody).isExactlyInstanceOf(PaginatedOrderDTO.class);
        Assertions.assertThat(responseBody.orders().size()).isEqualTo(1);
        Assertions.assertThat(responseBody.current_page()).isEqualTo(1);
        Assertions.assertThat(responseBody.total_items()).isEqualTo(3);
        Assertions.assertThat(responseBody.total_pages()).isEqualTo(3);
    }

    @Test
    void listOrdersPaginatedWithoutTokenReturnStatus403() {
        testClient
                .get()
                .uri(listOrderUri)
                .exchange()
                .expectStatus().isForbidden()
                .expectBody().isEmpty();
    }
}
