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
public class CreateOrderE2ETest {

    @Autowired
    private WebTestClient testClient;

    private String createOrderUri = "/orders";

    @Test
    void createOrderWithValidDataReturnStatus201(){
        AuthenticationDTO authenticationDTO = new AuthenticationDTO("joao@gmail.com", "senha123");

        CreateOrderDTO createOrderDTO = CreateOrderDTO
                .builder()
                .userId("1c97fc13-93bb-4dde-8cf3-ba156f195f82")
                .bookId("d8f426fe-71f9-46ed-8d0b-b39a4f88db42")
                .startDate("26/12/2025")
                .endDate("30/12/2025")
                .build();

        OrderResponseDTO responseBody = testClient
                .post()
                .uri(createOrderUri)
                .contentType(MediaType.APPLICATION_JSON)
                .headers(GetJwtAuthenticationToken.getHeaderJwtAccessToken(testClient ,authenticationDTO.getEmail(), authenticationDTO.getPassword()))
                .bodyValue(createOrderDTO)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(OrderResponseDTO.class)
                .returnResult().getResponseBody();

        Assertions.assertThat(responseBody).isNotNull();
        Assertions.assertThat(responseBody).isExactlyInstanceOf(OrderResponseDTO.class);
        Assertions.assertThat(responseBody).hasFieldOrProperty("id");
        Assertions.assertThat(responseBody.getUserId()).isEqualTo(UUID.fromString(createOrderDTO.getUserId()));
        Assertions.assertThat(responseBody.getBookId()).isEqualTo(UUID.fromString(createOrderDTO.getBookId()));
    }

    @Test
    void createOrderWithBookThatNotExistsReturnStatus404(){
        AuthenticationDTO authenticationDTO = new AuthenticationDTO("joao@gmail.com", "senha123");

        CreateOrderDTO createOrderDTO = CreateOrderDTO
                .builder()
                .userId("1c97fc13-93bb-4dde-8cf3-ba156f195f82")
                .bookId("e87386ae-7bd0-47f2-97af-e4a21f85a0ca")
                .startDate("26/12/2025")
                .endDate("30/12/2025")
                .build();

        ApplicationException responseBody = testClient
                .post()
                .uri(createOrderUri)
                .contentType(MediaType.APPLICATION_JSON)
                .headers(GetJwtAuthenticationToken.getHeaderJwtAccessToken(testClient ,authenticationDTO.getEmail(), authenticationDTO.getPassword()))
                .bodyValue(createOrderDTO)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody(ApplicationException.class)
                .returnResult().getResponseBody();

        Assertions.assertThat(responseBody).isNotNull();
        Assertions.assertThat(responseBody).isExactlyInstanceOf(ApplicationException.class);
        Assertions.assertThat(responseBody.getCode()).isEqualTo(404);
        Assertions.assertThat(responseBody.getStatus()).isEqualTo("Not Found");
        Assertions.assertThat(responseBody.getMethod()).isEqualTo("POST");
        Assertions.assertThat(responseBody.getMessage()).isEqualTo(BookExceptionConsts.BOOK_NOT_FOUND);
    }

    @Test
    void createOrderWithUserThatNotExistsReturnStatus404(){
        AuthenticationDTO authenticationDTO = new AuthenticationDTO("joao@gmail.com", "senha123");

        CreateOrderDTO createOrderDTO = CreateOrderDTO
                .builder()
                .userId("30411fb8-5d8d-436d-9dec-4f40b9da2d52")
                .bookId("d8f426fe-71f9-46ed-8d0b-b39a4f88db42")
                .startDate("26/12/2025")
                .endDate("30/12/2025")
                .build();

        ApplicationException responseBody = testClient
                .post()
                .uri(createOrderUri)
                .contentType(MediaType.APPLICATION_JSON)
                .headers(GetJwtAuthenticationToken.getHeaderJwtAccessToken(testClient ,authenticationDTO.getEmail(), authenticationDTO.getPassword()))
                .bodyValue(createOrderDTO)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody(ApplicationException.class)
                .returnResult().getResponseBody();

        Assertions.assertThat(responseBody).isNotNull();
        Assertions.assertThat(responseBody).isExactlyInstanceOf(ApplicationException.class);
        Assertions.assertThat(responseBody.getCode()).isEqualTo(404);
        Assertions.assertThat(responseBody.getStatus()).isEqualTo("Not Found");
        Assertions.assertThat(responseBody.getMethod()).isEqualTo("POST");
        Assertions.assertThat(responseBody.getMessage()).isEqualTo(UserExceptionConsts.USER_NOT_FOUND);
    }

    @Test
    void createOrderWithStartDateAfterEndDateReturnStatus400(){
        AuthenticationDTO authenticationDTO = new AuthenticationDTO("joao@gmail.com", "senha123");

        CreateOrderDTO createOrderDTO = CreateOrderDTO
                .builder()
                .userId("1c97fc13-93bb-4dde-8cf3-ba156f195f82")
                .bookId("d8f426fe-71f9-46ed-8d0b-b39a4f88db42")
                .startDate("01/12/2025")
                .endDate("30/01/2025")
                .build();

        ApplicationException responseBody = testClient
                .post()
                .uri(createOrderUri)
                .contentType(MediaType.APPLICATION_JSON)
                .headers(GetJwtAuthenticationToken.getHeaderJwtAccessToken(testClient ,authenticationDTO.getEmail(), authenticationDTO.getPassword()))
                .bodyValue(createOrderDTO)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(ApplicationException.class)
                .returnResult().getResponseBody();

        Assertions.assertThat(responseBody).isNotNull();
        Assertions.assertThat(responseBody).isExactlyInstanceOf(ApplicationException.class);
        Assertions.assertThat(responseBody.getCode()).isEqualTo(400);
        Assertions.assertThat(responseBody.getStatus()).isEqualTo("Bad Request");
        Assertions.assertThat(responseBody.getMethod()).isEqualTo("POST");
        Assertions.assertThat(responseBody.getMessage()).isEqualTo(OrderExceptionConsts.ORDER_START_DATE_CANNOT_AFTER_END);
    }

    @Test
    void createOrderWithEndDateBeforeStartDateReturnStatus400(){
        AuthenticationDTO authenticationDTO = new AuthenticationDTO("joao@gmail.com", "senha123");

        CreateOrderDTO createOrderDTO = CreateOrderDTO
                .builder()
                .userId("1c97fc13-93bb-4dde-8cf3-ba156f195f82")
                .bookId("d8f426fe-71f9-46ed-8d0b-b39a4f88db42")
                .startDate("30/12/2025")
                .endDate("20/10/2025")
                .build();

        ApplicationException responseBody = testClient
                .post()
                .uri(createOrderUri)
                .contentType(MediaType.APPLICATION_JSON)
                .headers(GetJwtAuthenticationToken.getHeaderJwtAccessToken(testClient ,authenticationDTO.getEmail(), authenticationDTO.getPassword()))
                .bodyValue(createOrderDTO)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(ApplicationException.class)
                .returnResult().getResponseBody();

        Assertions.assertThat(responseBody).isNotNull();
        Assertions.assertThat(responseBody).isExactlyInstanceOf(ApplicationException.class);
        Assertions.assertThat(responseBody.getCode()).isEqualTo(400);
        Assertions.assertThat(responseBody.getStatus()).isEqualTo("Bad Request");
        Assertions.assertThat(responseBody.getMethod()).isEqualTo("POST");
        Assertions.assertThat(responseBody.getMessage()).isEqualTo(OrderExceptionConsts.ORDER_START_DATE_CANNOT_AFTER_END);
    }

    @Test
    void createOrderWithStartDateInPastReturnStatus400(){
        AuthenticationDTO authenticationDTO = new AuthenticationDTO("joao@gmail.com", "senha123");

        CreateOrderDTO createOrderDTO = CreateOrderDTO
                .builder()
                .userId("1c97fc13-93bb-4dde-8cf3-ba156f195f82")
                .bookId("d8f426fe-71f9-46ed-8d0b-b39a4f88db42")
                .startDate("30/12/2021")
                .endDate("20/10/2025")
                .build();

        ApplicationException responseBody = testClient
                .post()
                .uri(createOrderUri)
                .contentType(MediaType.APPLICATION_JSON)
                .headers(GetJwtAuthenticationToken.getHeaderJwtAccessToken(testClient ,authenticationDTO.getEmail(), authenticationDTO.getPassword()))
                .bodyValue(createOrderDTO)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(ApplicationException.class)
                .returnResult().getResponseBody();

        Assertions.assertThat(responseBody).isNotNull();
        Assertions.assertThat(responseBody).isExactlyInstanceOf(ApplicationException.class);
        Assertions.assertThat(responseBody.getCode()).isEqualTo(400);
        Assertions.assertThat(responseBody.getStatus()).isEqualTo("Bad Request");
        Assertions.assertThat(responseBody.getMethod()).isEqualTo("POST");
        Assertions.assertThat(responseBody.getMessage()).isEqualTo(OrderExceptionConsts.ORDER_START_DATE_CANNOT_BE_IN_PAST);
    }

    @Test
    void createOrderWithEndDateInPastReturnStatus400(){
        AuthenticationDTO authenticationDTO = new AuthenticationDTO("joao@gmail.com", "senha123");

        CreateOrderDTO createOrderDTO = CreateOrderDTO
                .builder()
                .userId("1c97fc13-93bb-4dde-8cf3-ba156f195f82")
                .bookId("d8f426fe-71f9-46ed-8d0b-b39a4f88db42")
                .startDate("30/12/2025")
                .endDate("20/10/2021")
                .build();

        ApplicationException responseBody = testClient
                .post()
                .uri(createOrderUri)
                .contentType(MediaType.APPLICATION_JSON)
                .headers(GetJwtAuthenticationToken.getHeaderJwtAccessToken(testClient ,authenticationDTO.getEmail(), authenticationDTO.getPassword()))
                .bodyValue(createOrderDTO)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(ApplicationException.class)
                .returnResult().getResponseBody();

        Assertions.assertThat(responseBody).isNotNull();
        Assertions.assertThat(responseBody).isExactlyInstanceOf(ApplicationException.class);
        Assertions.assertThat(responseBody.getCode()).isEqualTo(400);
        Assertions.assertThat(responseBody.getStatus()).isEqualTo("Bad Request");
        Assertions.assertThat(responseBody.getMethod()).isEqualTo("POST");
        Assertions.assertThat(responseBody.getMessage()).isEqualTo(OrderExceptionConsts.ORDER_END_DATE_CANNOT_BE_IN_PAST);
    }

    @Test
    void createOrderWhenAlreadyHaveDoAOrderInIntervalReturnStatus400(){
        AuthenticationDTO authenticationDTO = new AuthenticationDTO("joao@gmail.com", "senha123");

        CreateOrderDTO createOrderDTO = CreateOrderDTO
                .builder()
                .userId("1c97fc13-93bb-4dde-8cf3-ba156f195f82")
                .bookId("d8f426fe-71f9-46ed-8d0b-b39a4f88db42")
                .startDate("26/12/2025")
                .endDate("30/12/2025")
                .build();

        testClient
                .post()
                .uri(createOrderUri)
                .contentType(MediaType.APPLICATION_JSON)
                .headers(GetJwtAuthenticationToken.getHeaderJwtAccessToken(testClient ,authenticationDTO.getEmail(), authenticationDTO.getPassword()))
                .bodyValue(createOrderDTO)
                .exchange();

        ApplicationException responseBody = testClient
                .post()
                .uri(createOrderUri)
                .contentType(MediaType.APPLICATION_JSON)
                .headers(GetJwtAuthenticationToken.getHeaderJwtAccessToken(testClient ,authenticationDTO.getEmail(), authenticationDTO.getPassword()))
                .bodyValue(createOrderDTO)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(ApplicationException.class)
                .returnResult().getResponseBody();

        Assertions.assertThat(responseBody).isNotNull();
        Assertions.assertThat(responseBody).isExactlyInstanceOf(ApplicationException.class);
        Assertions.assertThat(responseBody.getCode()).isEqualTo(400);
        Assertions.assertThat(responseBody.getStatus()).isEqualTo("Bad Request");
        Assertions.assertThat(responseBody.getMethod()).isEqualTo("POST");
        Assertions.assertThat(responseBody.getMessage()).isEqualTo(OrderExceptionConsts.USER_ALREADY_HAVE_ORDER_IN_THIS_PERIOD);
    }

    @Test
    void createOrderWhenUserAlreadyHaveAOrderWithStatusDeliveredInIOtherInIntervalReturnStatus400(){
        AuthenticationDTO authenticationDTO = new AuthenticationDTO("joao@gmail.com", "senha123");

        CreateOrderDTO createOrderDTO = CreateOrderDTO
                .builder()
                .userId("1c97fc13-93bb-4dde-8cf3-ba156f195f82")
                .bookId("d8f426fe-71f9-46ed-8d0b-b39a4f88db42")
                .startDate("26/12/2025")
                .endDate("30/12/2025")
                .build();


        CreateOrderDTO createOrderDTO2 = CreateOrderDTO
                .builder()
                .userId("1c97fc13-93bb-4dde-8cf3-ba156f195f82")
                .bookId("d8f426fe-71f9-46ed-8d0b-b39a4f88db42")
                .startDate("03/05/2026")
                .endDate("15/05/2026")
                .build();

        testClient
                .post()
                .uri(createOrderUri)
                .contentType(MediaType.APPLICATION_JSON)
                .headers(GetJwtAuthenticationToken.getHeaderJwtAccessToken(testClient ,authenticationDTO.getEmail(), authenticationDTO.getPassword()))
                .bodyValue(createOrderDTO)
                .exchange();

        ApplicationException responseBody = testClient
                .post()
                .uri(createOrderUri)
                .contentType(MediaType.APPLICATION_JSON)
                .headers(GetJwtAuthenticationToken.getHeaderJwtAccessToken(testClient ,authenticationDTO.getEmail(), authenticationDTO.getPassword()))
                .bodyValue(createOrderDTO2)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(ApplicationException.class)
                .returnResult().getResponseBody();

        Assertions.assertThat(responseBody).isNotNull();
        Assertions.assertThat(responseBody).isExactlyInstanceOf(ApplicationException.class);
        Assertions.assertThat(responseBody.getCode()).isEqualTo(400);
        Assertions.assertThat(responseBody.getStatus()).isEqualTo("Bad Request");
        Assertions.assertThat(responseBody.getMethod()).isEqualTo("POST");
        Assertions.assertThat(responseBody.getMessage()).isEqualTo(OrderExceptionConsts.USER_HAVE_ORDER_PENDENT_TO_RETURN);
    }

    @Test
    void createOrder0fABookThatHaveStockEqualZeroReturnStatus400(){
        AuthenticationDTO authenticationDTO = new AuthenticationDTO("joao@gmail.com", "senha123");

        CreateOrderDTO createOrderDTO = CreateOrderDTO
                .builder()
                .userId("1c97fc13-93bb-4dde-8cf3-ba156f195f82")
                .bookId("f4b198bb-2a7e-43c2-9d63-e4a3e39cbe34")
                .startDate("26/12/2025")
                .endDate("30/12/2025")
                .build();


        ApplicationException responseBody = testClient
                .post()
                .uri(createOrderUri)
                .contentType(MediaType.APPLICATION_JSON)
                .headers(GetJwtAuthenticationToken.getHeaderJwtAccessToken(testClient ,authenticationDTO.getEmail(), authenticationDTO.getPassword()))
                .bodyValue(createOrderDTO)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(ApplicationException.class)
                .returnResult().getResponseBody();

        Assertions.assertThat(responseBody).isNotNull();
        Assertions.assertThat(responseBody).isExactlyInstanceOf(ApplicationException.class);
        Assertions.assertThat(responseBody.getCode()).isEqualTo(400);
        Assertions.assertThat(responseBody.getStatus()).isEqualTo("Bad Request");
        Assertions.assertThat(responseBody.getMethod()).isEqualTo("POST");
        Assertions.assertThat(responseBody.getMessage()).isEqualTo(OrderExceptionConsts.BOOK_OUT_OF_STOCK);
    }

    @Test
    void createOrderWithEmptyUserIdReturnsStatus400() {
        AuthenticationDTO authenticationDTO = new AuthenticationDTO("joao@gmail.com", "senha123");

        CreateOrderDTO createOrderDTO = CreateOrderDTO
                .builder()
                .userId("")
                .bookId("d8f426fe-71f9-46ed-8d0b-b39a4f88db42")
                .startDate("01/12/2025")
                .endDate("30/12/2025")
                .build();

        ApplicationException responseBody = testClient
                .post()
                .uri(createOrderUri)
                .contentType(MediaType.APPLICATION_JSON)
                .headers(GetJwtAuthenticationToken.getHeaderJwtAccessToken(testClient ,authenticationDTO.getEmail(), authenticationDTO.getPassword()))
                .bodyValue(createOrderDTO)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(ApplicationException.class)
                .returnResult().getResponseBody();

        Assertions.assertThat(responseBody).isNotNull();
        Assertions.assertThat(responseBody).isExactlyInstanceOf(ApplicationException.class);
        Assertions.assertThat(responseBody.getCode()).isEqualTo(400);
        Assertions.assertThat(responseBody.getStatus()).isEqualTo("Bad Request");
        Assertions.assertThat(responseBody.getMethod()).isEqualTo("POST");
        Assertions.assertThat(responseBody.getErrors()).hasFieldOrProperty("userId");
    }

    @Test
    void createOrderWithInvalidUserIdReturnsStatus400() {
        AuthenticationDTO authenticationDTO = new AuthenticationDTO("joao@gmail.com", "senha123");

        CreateOrderDTO createOrderDTO = CreateOrderDTO
                .builder()
                .userId("dsadasfas")
                .bookId("d8f426fe-71f9-46ed-8d0b-b39a4f88db42")
                .startDate("01/12/2025")
                .endDate("30/12/2025")
                .build();

        ApplicationException responseBody = testClient
                .post()
                .uri(createOrderUri)
                .contentType(MediaType.APPLICATION_JSON)
                .headers(GetJwtAuthenticationToken.getHeaderJwtAccessToken(testClient ,authenticationDTO.getEmail(), authenticationDTO.getPassword()))
                .bodyValue(createOrderDTO)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(ApplicationException.class)
                .returnResult().getResponseBody();

        Assertions.assertThat(responseBody).isNotNull();
        Assertions.assertThat(responseBody).isExactlyInstanceOf(ApplicationException.class);
        Assertions.assertThat(responseBody.getCode()).isEqualTo(400);
        Assertions.assertThat(responseBody.getStatus()).isEqualTo("Bad Request");
        Assertions.assertThat(responseBody.getMethod()).isEqualTo("POST");
        Assertions.assertThat(responseBody.getErrors()).hasFieldOrProperty("userId");
    }


    @Test
    void createOrderWithEmptyBodyValuesReturnsStatus400() {
        AuthenticationDTO authenticationDTO = new AuthenticationDTO("joao@gmail.com", "senha123");

        CreateOrderDTO createOrderDTO = CreateOrderDTO
                .builder()
                .userId("")
                .bookId("")
                .startDate("")
                .endDate("")
                .build();

        ApplicationException responseBody = testClient
                .post()
                .uri(createOrderUri)
                .contentType(MediaType.APPLICATION_JSON)
                .headers(GetJwtAuthenticationToken.getHeaderJwtAccessToken(testClient ,authenticationDTO.getEmail(), authenticationDTO.getPassword()))
                .bodyValue(createOrderDTO)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(ApplicationException.class)
                .returnResult().getResponseBody();

        Assertions.assertThat(responseBody).isNotNull();
        Assertions.assertThat(responseBody).isExactlyInstanceOf(ApplicationException.class);
        Assertions.assertThat(responseBody.getCode()).isEqualTo(400);
        Assertions.assertThat(responseBody.getStatus()).isEqualTo("Bad Request");
        Assertions.assertThat(responseBody.getMethod()).isEqualTo("POST");
        Assertions.assertThat(responseBody.getErrors()).hasFieldOrProperty("userId");
        Assertions.assertThat(responseBody.getErrors()).hasFieldOrProperty("bookId");
        Assertions.assertThat(responseBody.getErrors()).hasFieldOrProperty("startDate");
        Assertions.assertThat(responseBody.getErrors()).hasFieldOrProperty("endDate");
    }

    @Test
    void createOrderWithEmptyStartDateReturnsStatus400() {
        AuthenticationDTO authenticationDTO = new AuthenticationDTO("joao@gmail.com", "senha123");

        CreateOrderDTO createOrderDTO = CreateOrderDTO
                .builder()
                .userId("1c97fc13-93bb-4dde-8cf3-ba156f195f82")
                .bookId("d8f426fe-71f9-46ed-8d0b-b39a4f88db42")
                .startDate("")
                .endDate("30/12/2025")
                .build();

        ApplicationException responseBody = testClient
                .post()
                .uri(createOrderUri)
                .contentType(MediaType.APPLICATION_JSON)
                .headers(GetJwtAuthenticationToken.getHeaderJwtAccessToken(testClient ,authenticationDTO.getEmail(), authenticationDTO.getPassword()))
                .bodyValue(createOrderDTO)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(ApplicationException.class)
                .returnResult().getResponseBody();

        Assertions.assertThat(responseBody).isNotNull();
        Assertions.assertThat(responseBody).isExactlyInstanceOf(ApplicationException.class);
        Assertions.assertThat(responseBody.getCode()).isEqualTo(400);
        Assertions.assertThat(responseBody.getStatus()).isEqualTo("Bad Request");
        Assertions.assertThat(responseBody.getMethod()).isEqualTo("POST");
        Assertions.assertThat(responseBody.getErrors()).hasFieldOrProperty("startDate");
    }

    @Test
    void createOrderWithInvalidStartDateReturnsStatus400() {
        AuthenticationDTO authenticationDTO = new AuthenticationDTO("joao@gmail.com", "senha123");

        CreateOrderDTO createOrderDTO = CreateOrderDTO
                .builder()
                .userId("1c97fc13-93bb-4dde-8cf3-ba156f195f82")
                .bookId("d8f426fe-71f9-46ed-8d0b-b39a4f88db42")
                .startDate("dsadasdasdsa")
                .endDate("30/12/2025")
                .build();

        ApplicationException responseBody = testClient
                .post()
                .uri(createOrderUri)
                .contentType(MediaType.APPLICATION_JSON)
                .headers(GetJwtAuthenticationToken.getHeaderJwtAccessToken(testClient ,authenticationDTO.getEmail(), authenticationDTO.getPassword()))
                .bodyValue(createOrderDTO)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(ApplicationException.class)
                .returnResult().getResponseBody();

        Assertions.assertThat(responseBody).isNotNull();
        Assertions.assertThat(responseBody).isExactlyInstanceOf(ApplicationException.class);
        Assertions.assertThat(responseBody.getCode()).isEqualTo(400);
        Assertions.assertThat(responseBody.getStatus()).isEqualTo("Bad Request");
        Assertions.assertThat(responseBody.getMethod()).isEqualTo("POST");
        Assertions.assertThat(responseBody.getErrors()).hasFieldOrProperty("startDate");
    }

    @Test
    void createOrderWithEmptyEndDateReturnsStatus400() {
        AuthenticationDTO authenticationDTO = new AuthenticationDTO("joao@gmail.com", "senha123");

        CreateOrderDTO createOrderDTO = CreateOrderDTO
                .builder()
                .userId("1c97fc13-93bb-4dde-8cf3-ba156f195f82")
                .bookId("d8f426fe-71f9-46ed-8d0b-b39a4f88db42")
                .startDate("01/12/2025")
                .endDate("")
                .build();

        ApplicationException responseBody = testClient
                .post()
                .uri(createOrderUri)
                .contentType(MediaType.APPLICATION_JSON)
                .headers(GetJwtAuthenticationToken.getHeaderJwtAccessToken(testClient ,authenticationDTO.getEmail(), authenticationDTO.getPassword()))
                .bodyValue(createOrderDTO)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(ApplicationException.class)
                .returnResult().getResponseBody();

        Assertions.assertThat(responseBody).isNotNull();
        Assertions.assertThat(responseBody).isExactlyInstanceOf(ApplicationException.class);
        Assertions.assertThat(responseBody.getCode()).isEqualTo(400);
        Assertions.assertThat(responseBody.getStatus()).isEqualTo("Bad Request");
        Assertions.assertThat(responseBody.getMethod()).isEqualTo("POST");
        Assertions.assertThat(responseBody.getErrors()).hasFieldOrProperty("endDate");
    }

    @Test
    void createOrderWithInvalidEndDateFormatReturnsStatus400() {
        AuthenticationDTO authenticationDTO = new AuthenticationDTO("joao@gmail.com", "senha123");

        CreateOrderDTO createOrderDTO = CreateOrderDTO
                .builder()
                .userId("1c97fc13-93bb-4dde-8cf3-ba156f195f82")
                .bookId("d8f426fe-71f9-46ed-8d0b-b39a4f88db42")
                .startDate("01/12/2025")
                .endDate("2025-13-01") // Formato inválido
                .build();

        ApplicationException responseBody = testClient
                .post()
                .uri(createOrderUri)
                .contentType(MediaType.APPLICATION_JSON)
                .headers(GetJwtAuthenticationToken.getHeaderJwtAccessToken(testClient ,authenticationDTO.getEmail(), authenticationDTO.getPassword()))
                .bodyValue(createOrderDTO)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(ApplicationException.class)
                .returnResult().getResponseBody();

        Assertions.assertThat(responseBody).isNotNull();
        Assertions.assertThat(responseBody).isExactlyInstanceOf(ApplicationException.class);
        Assertions.assertThat(responseBody.getCode()).isEqualTo(400);
        Assertions.assertThat(responseBody.getStatus()).isEqualTo("Bad Request");
        Assertions.assertThat(responseBody.getMethod()).isEqualTo("POST");
        Assertions.assertThat(responseBody.getErrors()).hasFieldOrProperty("endDate");
    }

    @Test
    void createOrderWithoutTokenReturnStatus403(){
        CreateOrderDTO createOrderDTO = CreateOrderDTO
                .builder()
                .userId("1c97fc13-93bb-4dde-8cf3-ba156f195f82")
                .bookId("d8f426fe-71f9-46ed-8d0b-b39a4f88db42")
                .startDate("26/12/2025")
                .endDate("30/12/2025")
                .build();

         testClient
                .post()
                .uri(createOrderUri)
                .contentType(MediaType.APPLICATION_JSON)
                 .bodyValue(createOrderDTO)
                .exchange()
                .expectStatus().isForbidden()
                 .expectBody().isEmpty();
            }

}
