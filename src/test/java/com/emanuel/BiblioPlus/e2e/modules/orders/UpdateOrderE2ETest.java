package com.emanuel.BiblioPlus.e2e.modules.orders;

import com.emanuel.BiblioPlus.e2e.utils.GetJwtAuthenticationToken;
import com.emanuel.BiblioPlus.modules.orders.domain.dtos.request.CreateOrderDTO;
import com.emanuel.BiblioPlus.modules.orders.domain.dtos.response.OrderResponseDTO;
import com.emanuel.BiblioPlus.modules.users.domain.dtos.request.AuthenticationDTO;
import com.emanuel.BiblioPlus.shared.consts.BookExceptionConsts;
import com.emanuel.BiblioPlus.shared.consts.OrderExceptionConsts;
import com.emanuel.BiblioPlus.shared.consts.UserExceptionConsts;
import com.emanuel.BiblioPlus.shared.exceptions.ApplicationException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.UUID;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Sql(scripts = "/sql/users/insert-users.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "/sql/books/insert-books.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "/sql/orders/insert-orders.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "/sql/orders/clear-orders-table.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public class UpdateOrderE2ETest {

    @Autowired
    private WebTestClient testClient;

    private String updateOrderUri = "/orders";

    @Test
    void updateOrderWithValidDataReturnStatus200(){
        String orderId = "e5fbc8a1-0981-48cc-b8a5-b9256e2a079f";
        AuthenticationDTO authenticationDTO = new AuthenticationDTO("anacarla@gmail.com", "senha123");

        CreateOrderDTO updateOrderDTO = CreateOrderDTO
                .builder()
                .userId("5765c65e-2fd0-4df7-963c-2345c5f9f164")
                .bookId("8c2c78f3-5d33-44d2-a8de-896327a7b765")
                .startDate("26/12/2025")
                .endDate("30/12/2025")
                .build();

        OrderResponseDTO responseBody = testClient
                .put()
                .uri(updateOrderUri + "/" + orderId)
                .contentType(MediaType.APPLICATION_JSON)
                .headers(GetJwtAuthenticationToken.getHeaderJwtAccessToken(testClient ,authenticationDTO.getEmail(), authenticationDTO.getPassword()))
                .bodyValue(updateOrderDTO)
                .exchange()
                .expectStatus().isOk()
                .expectBody(OrderResponseDTO.class)
                .returnResult().getResponseBody();

        Assertions.assertThat(responseBody).isNotNull();
        Assertions.assertThat(responseBody).isExactlyInstanceOf(OrderResponseDTO.class);
        Assertions.assertThat(responseBody).hasFieldOrProperty("id");
        Assertions.assertThat(responseBody.getUserId()).isEqualTo(UUID.fromString(updateOrderDTO.getUserId()));
        Assertions.assertThat(responseBody.getBookId()).isEqualTo(UUID.fromString(updateOrderDTO.getBookId()));
    }

    @Test
    void updateOrderWithBookThatNotExistsReturnStatus404(){
        String orderId = "6c2b12d7-b18d-442f-a5d7-3b5ab3e21762";
        AuthenticationDTO authenticationDTO = new AuthenticationDTO("joao@gmail.com", "senha123");

        CreateOrderDTO updateOrderDTO = CreateOrderDTO
                .builder()
                .userId("1c97fc13-93bb-4dde-8cf3-ba156f195f82")
                .bookId("e87386ae-7bd0-47f2-97af-e4a21f85a0ca")
                .startDate("26/12/2025")
                .endDate("30/12/2025")
                .build();

        ApplicationException responseBody = testClient
                .put()
                .uri(updateOrderUri + "/" + orderId)
                .contentType(MediaType.APPLICATION_JSON)
                .headers(GetJwtAuthenticationToken.getHeaderJwtAccessToken(testClient ,authenticationDTO.getEmail(), authenticationDTO.getPassword()))
                .bodyValue(updateOrderDTO)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody(ApplicationException.class)
                .returnResult().getResponseBody();

        Assertions.assertThat(responseBody).isNotNull();
        Assertions.assertThat(responseBody).isExactlyInstanceOf(ApplicationException.class);
        Assertions.assertThat(responseBody.getCode()).isEqualTo(404);
        Assertions.assertThat(responseBody.getStatus()).isEqualTo("Not Found");
        Assertions.assertThat(responseBody.getMethod()).isEqualTo("PUT");
        Assertions.assertThat(responseBody.getMessage()).isEqualTo(BookExceptionConsts.BOOK_NOT_FOUND);
    }

    @Test
    void updateOrderWithUserThatNotExistsReturnStatus404(){
        String orderId = "6c2b12d7-b18d-442f-a5d7-3b5ab3e21762";
        AuthenticationDTO authenticationDTO = new AuthenticationDTO("joao@gmail.com", "senha123");

        CreateOrderDTO updateOrderDTO = CreateOrderDTO
                .builder()
                .userId("30411fb8-5d8d-436d-9dec-4f40b9da2d52")
                .bookId("d8f426fe-71f9-46ed-8d0b-b39a4f88db42")
                .startDate("26/12/2025")
                .endDate("30/12/2025")
                .build();

        ApplicationException responseBody = testClient
                .put()
                .uri(updateOrderUri + "/" + orderId)
                .contentType(MediaType.APPLICATION_JSON)
                .headers(GetJwtAuthenticationToken.getHeaderJwtAccessToken(testClient ,authenticationDTO.getEmail(), authenticationDTO.getPassword()))
                .bodyValue(updateOrderDTO)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody(ApplicationException.class)
                .returnResult().getResponseBody();

        Assertions.assertThat(responseBody).isNotNull();
        Assertions.assertThat(responseBody).isExactlyInstanceOf(ApplicationException.class);
        Assertions.assertThat(responseBody.getCode()).isEqualTo(404);
        Assertions.assertThat(responseBody.getStatus()).isEqualTo("Not Found");
        Assertions.assertThat(responseBody.getMethod()).isEqualTo("PUT");
        Assertions.assertThat(responseBody.getMessage()).isEqualTo(UserExceptionConsts.USER_NOT_FOUND);
    }

    @Test
    void updateOrderWithStartDateAfterEndDateReturnStatus400(){
        String orderId = "6c2b12d7-b18d-442f-a5d7-3b5ab3e21762";
        AuthenticationDTO authenticationDTO = new AuthenticationDTO("joao@gmail.com", "senha123");

        CreateOrderDTO updateOrderDTO = CreateOrderDTO
                .builder()
                .userId("1c97fc13-93bb-4dde-8cf3-ba156f195f82")
                .bookId("d8f426fe-71f9-46ed-8d0b-b39a4f88db42")
                .startDate("01/12/2025")
                .endDate("30/01/2025")
                .build();

        ApplicationException responseBody = testClient
                .put()
                .uri(updateOrderUri + "/" + orderId)
                .contentType(MediaType.APPLICATION_JSON)
                .headers(GetJwtAuthenticationToken.getHeaderJwtAccessToken(testClient ,authenticationDTO.getEmail(), authenticationDTO.getPassword()))
                .bodyValue(updateOrderDTO)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(ApplicationException.class)
                .returnResult().getResponseBody();

        Assertions.assertThat(responseBody).isNotNull();
        Assertions.assertThat(responseBody).isExactlyInstanceOf(ApplicationException.class);
        Assertions.assertThat(responseBody.getCode()).isEqualTo(400);
        Assertions.assertThat(responseBody.getStatus()).isEqualTo("Bad Request");
        Assertions.assertThat(responseBody.getMethod()).isEqualTo("PUT");
        Assertions.assertThat(responseBody.getMessage()).isEqualTo(OrderExceptionConsts.ORDER_START_DATE_CANNOT_AFTER_END);
    }

    @Test
    void updateOrderWithEndDateBeforeStartDateReturnStatus400(){
        String orderId = "6c2b12d7-b18d-442f-a5d7-3b5ab3e21762";
        AuthenticationDTO authenticationDTO = new AuthenticationDTO("joao@gmail.com", "senha123");

        CreateOrderDTO updateOrderDTO = CreateOrderDTO
                .builder()
                .userId("1c97fc13-93bb-4dde-8cf3-ba156f195f82")
                .bookId("d8f426fe-71f9-46ed-8d0b-b39a4f88db42")
                .startDate("30/12/2025")
                .endDate("20/10/2025")
                .build();

        ApplicationException responseBody = testClient
                .put()
                .uri(updateOrderUri + "/" + orderId)
                .contentType(MediaType.APPLICATION_JSON)
                .headers(GetJwtAuthenticationToken.getHeaderJwtAccessToken(testClient ,authenticationDTO.getEmail(), authenticationDTO.getPassword()))
                .bodyValue(updateOrderDTO)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(ApplicationException.class)
                .returnResult().getResponseBody();

        Assertions.assertThat(responseBody).isNotNull();
        Assertions.assertThat(responseBody).isExactlyInstanceOf(ApplicationException.class);
        Assertions.assertThat(responseBody.getCode()).isEqualTo(400);
        Assertions.assertThat(responseBody.getStatus()).isEqualTo("Bad Request");
        Assertions.assertThat(responseBody.getMethod()).isEqualTo("PUT");
        Assertions.assertThat(responseBody.getMessage()).isEqualTo(OrderExceptionConsts.ORDER_START_DATE_CANNOT_AFTER_END);
    }

    @Test
    void updateOrderWithStartDateInPastReturnStatus400(){
        String orderId = "6c2b12d7-b18d-442f-a5d7-3b5ab3e21762";
        AuthenticationDTO authenticationDTO = new AuthenticationDTO("joao@gmail.com", "senha123");

        CreateOrderDTO updateOrderDTO = CreateOrderDTO
                .builder()
                .userId("1c97fc13-93bb-4dde-8cf3-ba156f195f82")
                .bookId("d8f426fe-71f9-46ed-8d0b-b39a4f88db42")
                .startDate("30/12/2021")
                .endDate("20/10/2025")
                .build();

        ApplicationException responseBody = testClient
                .put()
                .uri(updateOrderUri + "/" + orderId)
                .contentType(MediaType.APPLICATION_JSON)
                .headers(GetJwtAuthenticationToken.getHeaderJwtAccessToken(testClient ,authenticationDTO.getEmail(), authenticationDTO.getPassword()))
                .bodyValue(updateOrderDTO)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(ApplicationException.class)
                .returnResult().getResponseBody();

        Assertions.assertThat(responseBody).isNotNull();
        Assertions.assertThat(responseBody).isExactlyInstanceOf(ApplicationException.class);
        Assertions.assertThat(responseBody.getCode()).isEqualTo(400);
        Assertions.assertThat(responseBody.getStatus()).isEqualTo("Bad Request");
        Assertions.assertThat(responseBody.getMethod()).isEqualTo("PUT");
        Assertions.assertThat(responseBody.getMessage()).isEqualTo(OrderExceptionConsts.ORDER_START_DATE_CANNOT_BE_IN_PAST);
    }

    @Test
    void updateOrderWithEndDateInPastReturnStatus400(){
        String orderId = "6c2b12d7-b18d-442f-a5d7-3b5ab3e21762";
        AuthenticationDTO authenticationDTO = new AuthenticationDTO("joao@gmail.com", "senha123");

        CreateOrderDTO updateOrderDTO = CreateOrderDTO
                .builder()
                .userId("1c97fc13-93bb-4dde-8cf3-ba156f195f82")
                .bookId("d8f426fe-71f9-46ed-8d0b-b39a4f88db42")
                .startDate("30/12/2025")
                .endDate("20/10/2021")
                .build();

        ApplicationException responseBody = testClient
                .put()
                .uri(updateOrderUri + "/" + orderId)
                .contentType(MediaType.APPLICATION_JSON)
                .headers(GetJwtAuthenticationToken.getHeaderJwtAccessToken(testClient ,authenticationDTO.getEmail(), authenticationDTO.getPassword()))
                .bodyValue(updateOrderDTO)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(ApplicationException.class)
                .returnResult().getResponseBody();

        Assertions.assertThat(responseBody).isNotNull();
        Assertions.assertThat(responseBody).isExactlyInstanceOf(ApplicationException.class);
        Assertions.assertThat(responseBody.getCode()).isEqualTo(400);
        Assertions.assertThat(responseBody.getStatus()).isEqualTo("Bad Request");
        Assertions.assertThat(responseBody.getMethod()).isEqualTo("PUT");
        Assertions.assertThat(responseBody.getMessage()).isEqualTo(OrderExceptionConsts.ORDER_END_DATE_CANNOT_BE_IN_PAST);
    }

    @Test
    void updateOrder0fABookThatHaveStockEqualZeroReturnStatus400(){
        String orderId = "6c2b12d7-b18d-442f-a5d7-3b5ab3e21762";
        AuthenticationDTO authenticationDTO = new AuthenticationDTO("joao@gmail.com", "senha123");

        CreateOrderDTO updateOrderDTO = CreateOrderDTO
                .builder()
                .userId("1c97fc13-93bb-4dde-8cf3-ba156f195f82")
                .bookId("f4b198bb-2a7e-43c2-9d63-e4a3e39cbe34")
                .startDate("26/12/2025")
                .endDate("30/12/2025")
                .build();


        ApplicationException responseBody = testClient
                .put()
                .uri(updateOrderUri + "/" + orderId)
                .contentType(MediaType.APPLICATION_JSON)
                .headers(GetJwtAuthenticationToken.getHeaderJwtAccessToken(testClient ,authenticationDTO.getEmail(), authenticationDTO.getPassword()))
                .bodyValue(updateOrderDTO)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(ApplicationException.class)
                .returnResult().getResponseBody();

        Assertions.assertThat(responseBody).isNotNull();
        Assertions.assertThat(responseBody).isExactlyInstanceOf(ApplicationException.class);
        Assertions.assertThat(responseBody.getCode()).isEqualTo(400);
        Assertions.assertThat(responseBody.getStatus()).isEqualTo("Bad Request");
        Assertions.assertThat(responseBody.getMethod()).isEqualTo("PUT");
        Assertions.assertThat(responseBody.getMessage()).isEqualTo(OrderExceptionConsts.BOOK_OUT_OF_STOCK);
    }

    @Test
    void updateOrderWithEmptyUserIdReturnsStatus400() {
        String orderId = "6c2b12d7-b18d-442f-a5d7-3b5ab3e21762";
        AuthenticationDTO authenticationDTO = new AuthenticationDTO("joao@gmail.com", "senha123");

        CreateOrderDTO updateOrderDTO = CreateOrderDTO
                .builder()
                .userId("")
                .bookId("d8f426fe-71f9-46ed-8d0b-b39a4f88db42")
                .startDate("01/12/2025")
                .endDate("30/12/2025")
                .build();

        ApplicationException responseBody = testClient
                .put()
                .uri(updateOrderUri + "/" + orderId)
                .contentType(MediaType.APPLICATION_JSON)
                .headers(GetJwtAuthenticationToken.getHeaderJwtAccessToken(testClient ,authenticationDTO.getEmail(), authenticationDTO.getPassword()))
                .bodyValue(updateOrderDTO)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(ApplicationException.class)
                .returnResult().getResponseBody();

        Assertions.assertThat(responseBody).isNotNull();
        Assertions.assertThat(responseBody).isExactlyInstanceOf(ApplicationException.class);
        Assertions.assertThat(responseBody.getCode()).isEqualTo(400);
        Assertions.assertThat(responseBody.getStatus()).isEqualTo("Bad Request");
        Assertions.assertThat(responseBody.getMethod()).isEqualTo("PUT");
        Assertions.assertThat(responseBody.getErrors()).hasFieldOrProperty("userId");
    }

    @Test
    void updateOrderWithInvalidUserIdReturnsStatus400() {
        String orderId = "6c2b12d7-b18d-442f-a5d7-3b5ab3e21762";
        AuthenticationDTO authenticationDTO = new AuthenticationDTO("joao@gmail.com", "senha123");

        CreateOrderDTO updateOrderDTO = CreateOrderDTO
                .builder()
                .userId("dsadasfas")
                .bookId("d8f426fe-71f9-46ed-8d0b-b39a4f88db42")
                .startDate("01/12/2025")
                .endDate("30/12/2025")
                .build();

        ApplicationException responseBody = testClient
                .put()
                .uri(updateOrderUri + "/" + orderId)
                .contentType(MediaType.APPLICATION_JSON)
                .headers(GetJwtAuthenticationToken.getHeaderJwtAccessToken(testClient ,authenticationDTO.getEmail(), authenticationDTO.getPassword()))
                .bodyValue(updateOrderDTO)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(ApplicationException.class)
                .returnResult().getResponseBody();

        Assertions.assertThat(responseBody).isNotNull();
        Assertions.assertThat(responseBody).isExactlyInstanceOf(ApplicationException.class);
        Assertions.assertThat(responseBody.getCode()).isEqualTo(400);
        Assertions.assertThat(responseBody.getStatus()).isEqualTo("Bad Request");
        Assertions.assertThat(responseBody.getMethod()).isEqualTo("PUT");
        Assertions.assertThat(responseBody.getErrors()).hasFieldOrProperty("userId");
    }


    @Test
    void updateOrderWithEmptyBodyValuesReturnsStatus400() {
        String orderId = "6c2b12d7-b18d-442f-a5d7-3b5ab3e21762";
        AuthenticationDTO authenticationDTO = new AuthenticationDTO("joao@gmail.com", "senha123");

        CreateOrderDTO updateOrderDTO = CreateOrderDTO
                .builder()
                .userId("")
                .bookId("")
                .startDate("")
                .endDate("")
                .build();

        ApplicationException responseBody = testClient
                .put()
                .uri(updateOrderUri + "/" + orderId)
                .contentType(MediaType.APPLICATION_JSON)
                .headers(GetJwtAuthenticationToken.getHeaderJwtAccessToken(testClient ,authenticationDTO.getEmail(), authenticationDTO.getPassword()))
                .bodyValue(updateOrderDTO)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(ApplicationException.class)
                .returnResult().getResponseBody();

        Assertions.assertThat(responseBody).isNotNull();
        Assertions.assertThat(responseBody).isExactlyInstanceOf(ApplicationException.class);
        Assertions.assertThat(responseBody.getCode()).isEqualTo(400);
        Assertions.assertThat(responseBody.getStatus()).isEqualTo("Bad Request");
        Assertions.assertThat(responseBody.getMethod()).isEqualTo("PUT");
        Assertions.assertThat(responseBody.getErrors()).hasFieldOrProperty("userId");
        Assertions.assertThat(responseBody.getErrors()).hasFieldOrProperty("bookId");
        Assertions.assertThat(responseBody.getErrors()).hasFieldOrProperty("startDate");
        Assertions.assertThat(responseBody.getErrors()).hasFieldOrProperty("endDate");
    }

    @Test
    void updateOrderWithEmptyStartDateReturnsStatus400() {
        String orderId = "6c2b12d7-b18d-442f-a5d7-3b5ab3e21762";
        AuthenticationDTO authenticationDTO = new AuthenticationDTO("joao@gmail.com", "senha123");

        CreateOrderDTO updateOrderDTO = CreateOrderDTO
                .builder()
                .userId("1c97fc13-93bb-4dde-8cf3-ba156f195f82")
                .bookId("d8f426fe-71f9-46ed-8d0b-b39a4f88db42")
                .startDate("")
                .endDate("30/12/2025")
                .build();

        ApplicationException responseBody = testClient
                .put()
                .uri(updateOrderUri + "/" + orderId)
                .contentType(MediaType.APPLICATION_JSON)
                .headers(GetJwtAuthenticationToken.getHeaderJwtAccessToken(testClient ,authenticationDTO.getEmail(), authenticationDTO.getPassword()))
                .bodyValue(updateOrderDTO)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(ApplicationException.class)
                .returnResult().getResponseBody();

        Assertions.assertThat(responseBody).isNotNull();
        Assertions.assertThat(responseBody).isExactlyInstanceOf(ApplicationException.class);
        Assertions.assertThat(responseBody.getCode()).isEqualTo(400);
        Assertions.assertThat(responseBody.getStatus()).isEqualTo("Bad Request");
        Assertions.assertThat(responseBody.getMethod()).isEqualTo("PUT");
        Assertions.assertThat(responseBody.getErrors()).hasFieldOrProperty("startDate");
    }

    @Test
    void updateOrderWithInvalidStartDateReturnsStatus400() {
        String orderId = "6c2b12d7-b18d-442f-a5d7-3b5ab3e21762";
        AuthenticationDTO authenticationDTO = new AuthenticationDTO("joao@gmail.com", "senha123");

        CreateOrderDTO updateOrderDTO = CreateOrderDTO
                .builder()
                .userId("1c97fc13-93bb-4dde-8cf3-ba156f195f82")
                .bookId("d8f426fe-71f9-46ed-8d0b-b39a4f88db42")
                .startDate("dsadasdasdsa")
                .endDate("30/12/2025")
                .build();

        ApplicationException responseBody = testClient
                .put()
                .uri(updateOrderUri + "/" + orderId)
                .contentType(MediaType.APPLICATION_JSON)
                .headers(GetJwtAuthenticationToken.getHeaderJwtAccessToken(testClient ,authenticationDTO.getEmail(), authenticationDTO.getPassword()))
                .bodyValue(updateOrderDTO)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(ApplicationException.class)
                .returnResult().getResponseBody();

        Assertions.assertThat(responseBody).isNotNull();
        Assertions.assertThat(responseBody).isExactlyInstanceOf(ApplicationException.class);
        Assertions.assertThat(responseBody.getCode()).isEqualTo(400);
        Assertions.assertThat(responseBody.getStatus()).isEqualTo("Bad Request");
        Assertions.assertThat(responseBody.getMethod()).isEqualTo("PUT");
        Assertions.assertThat(responseBody.getErrors()).hasFieldOrProperty("startDate");
    }

    @Test
    void updateOrderWithEmptyEndDateReturnsStatus400() {
        String orderId = "6c2b12d7-b18d-442f-a5d7-3b5ab3e21762";
        AuthenticationDTO authenticationDTO = new AuthenticationDTO("joao@gmail.com", "senha123");

        CreateOrderDTO updateOrderDTO = CreateOrderDTO
                .builder()
                .userId("1c97fc13-93bb-4dde-8cf3-ba156f195f82")
                .bookId("d8f426fe-71f9-46ed-8d0b-b39a4f88db42")
                .startDate("01/12/2025")
                .endDate("")
                .build();

        ApplicationException responseBody = testClient
                .put()
                .uri(updateOrderUri + "/" + orderId)
                .contentType(MediaType.APPLICATION_JSON)
                .headers(GetJwtAuthenticationToken.getHeaderJwtAccessToken(testClient ,authenticationDTO.getEmail(), authenticationDTO.getPassword()))
                .bodyValue(updateOrderDTO)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(ApplicationException.class)
                .returnResult().getResponseBody();

        Assertions.assertThat(responseBody).isNotNull();
        Assertions.assertThat(responseBody).isExactlyInstanceOf(ApplicationException.class);
        Assertions.assertThat(responseBody.getCode()).isEqualTo(400);
        Assertions.assertThat(responseBody.getStatus()).isEqualTo("Bad Request");
        Assertions.assertThat(responseBody.getMethod()).isEqualTo("PUT");
        Assertions.assertThat(responseBody.getErrors()).hasFieldOrProperty("endDate");
    }

    @Test
    void updateOrderWithInvalidEndDateFormatReturnsStatus400() {
        String orderId = "6c2b12d7-b18d-442f-a5d7-3b5ab3e21762";
        AuthenticationDTO authenticationDTO = new AuthenticationDTO("joao@gmail.com", "senha123");

        CreateOrderDTO updateOrderDTO = CreateOrderDTO
                .builder()
                .userId("1c97fc13-93bb-4dde-8cf3-ba156f195f82")
                .bookId("d8f426fe-71f9-46ed-8d0b-b39a4f88db42")
                .startDate("01/12/2025")
                .endDate("2025-13-01") // Formato inválido
                .build();

        ApplicationException responseBody = testClient
                .put()
                .uri(updateOrderUri + "/" + orderId)
                .contentType(MediaType.APPLICATION_JSON)
                .headers(GetJwtAuthenticationToken.getHeaderJwtAccessToken(testClient ,authenticationDTO.getEmail(), authenticationDTO.getPassword()))
                .bodyValue(updateOrderDTO)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(ApplicationException.class)
                .returnResult().getResponseBody();

        Assertions.assertThat(responseBody).isNotNull();
        Assertions.assertThat(responseBody).isExactlyInstanceOf(ApplicationException.class);
        Assertions.assertThat(responseBody.getCode()).isEqualTo(400);
        Assertions.assertThat(responseBody.getStatus()).isEqualTo("Bad Request");
        Assertions.assertThat(responseBody.getMethod()).isEqualTo("PUT");
        Assertions.assertThat(responseBody.getErrors()).hasFieldOrProperty("endDate");
    }

    @Test
    void updateOrderWithoutTokenReturnStatus403(){
        String orderId = "6c2b12d7-b18d-442f-a5d7-3b5ab3e21762";
        CreateOrderDTO updateOrderDTO = CreateOrderDTO
                .builder()
                .userId("1c97fc13-93bb-4dde-8cf3-ba156f195f82")
                .bookId("d8f426fe-71f9-46ed-8d0b-b39a4f88db42")
                .startDate("26/12/2025")
                .endDate("30/12/2025")
                .build();

         testClient
                .put()
                .uri(updateOrderUri + "/" + orderId)
                .contentType(MediaType.APPLICATION_JSON)
                 .bodyValue(updateOrderDTO)
                .exchange()
                .expectStatus().isForbidden()
                 .expectBody().isEmpty();
            }

}
