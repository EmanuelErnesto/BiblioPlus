package com.emanuel.BiblioPlus.e2e.modules.orders;

import com.emanuel.BiblioPlus.e2e.utils.GetJwtAuthenticationToken;
import com.emanuel.BiblioPlus.modules.orders.domain.dtos.response.OrderResponseDTO;
import com.emanuel.BiblioPlus.modules.users.domain.dtos.request.AuthenticationDTO;
import com.emanuel.BiblioPlus.shared.consts.OrderExceptionConsts;
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
@Sql(scripts = "/sql/books/insert-books.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "/sql/orders/insert-orders.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "/sql/orders/clear-orders-table.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public class ShowOrderE2ETest {

    @Autowired
    private WebTestClient testClient;

    private String showOrdersUri = "/orders";


    @Test
    void findOrderThatExistsReturnWithStatus200() {
        String orderId = "b9d1102b-78fa-45d9-b441-72ed05fc9b52";
        AuthenticationDTO authenticationDTO = new AuthenticationDTO("maria@gmail.com", "senha123");

        OrderResponseDTO responseBody = testClient
                .get()
                .uri(showOrdersUri + "/" + orderId)
                .headers(GetJwtAuthenticationToken.getHeaderJwtAccessToken(testClient, authenticationDTO.getEmail(), authenticationDTO.getPassword()))
                .exchange()
                .expectStatus().isOk()
                .expectBody(OrderResponseDTO.class)
                .returnResult().getResponseBody();

        Assertions.assertThat(responseBody).isNotNull();
        Assertions.assertThat(responseBody).isExactlyInstanceOf(OrderResponseDTO.class);
        Assertions.assertThat(responseBody).hasFieldOrProperty("id");
    }

    @Test
    void findOrderWithIdThatNotExistsInDatabaseReturnStatus404() {
        String orderId = "454cc84b-a969-4ddc-b51c-77c546378bba";
        AuthenticationDTO authenticationDTO = new AuthenticationDTO("maria@gmail.com", "senha123");

        ApplicationException responseBody = testClient
                .get()
                .uri(showOrdersUri + "/" + orderId)
                .headers(GetJwtAuthenticationToken.getHeaderJwtAccessToken(testClient, authenticationDTO.getEmail(), authenticationDTO.getPassword()))
                .exchange()
                .expectStatus().isNotFound()
                .expectBody(ApplicationException.class)
                .returnResult().getResponseBody();


        Assertions.assertThat(responseBody).isNotNull();
        Assertions.assertThat(responseBody).isExactlyInstanceOf(ApplicationException.class);
        Assertions.assertThat(responseBody.getCode()).isEqualTo(404);
        Assertions.assertThat(responseBody.getStatus()).isEqualTo("Not Found");
        Assertions.assertThat(responseBody.getMethod()).isEqualTo("GET");
        Assertions.assertThat(responseBody.getMessage()).isEqualTo(OrderExceptionConsts.ORDER_NOT_FOUND);
    }

    @Test
    void findOrderWithIdInInvalidFormatReturnStatus400() {
        String orderId = "gdfgdfgdfg";
        AuthenticationDTO authenticationDTO = new AuthenticationDTO("maria@gmail.com", "senha123");

        ApplicationException responseBody = testClient
                .get()
                .uri(showOrdersUri + "/" + orderId)
                .headers(GetJwtAuthenticationToken.getHeaderJwtAccessToken(testClient, authenticationDTO.getEmail(), authenticationDTO.getPassword()))
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(ApplicationException.class)
                .returnResult().getResponseBody();

        Assertions.assertThat(responseBody).isNotNull();
        Assertions.assertThat(responseBody).isExactlyInstanceOf(ApplicationException.class);
        Assertions.assertThat(responseBody.getCode()).isEqualTo(400);
        Assertions.assertThat(responseBody.getStatus()).isEqualTo("Bad Request");
        Assertions.assertThat(responseBody.getMethod()).isEqualTo("GET");
    }
    @Test
    void findOrdersWithoutTokenReturnStatus403() {
        testClient
                .get()
                .uri(showOrdersUri)
                .exchange()
                .expectStatus().isForbidden()
                .expectBody().isEmpty();
    }
}
