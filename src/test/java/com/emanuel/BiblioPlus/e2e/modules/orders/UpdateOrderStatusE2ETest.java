package com.emanuel.BiblioPlus.e2e.modules.orders;



import com.emanuel.BiblioPlus.e2e.utils.GetJwtAuthenticationToken;
import com.emanuel.BiblioPlus.modules.orders.domain.dtos.request.UpdateOrderStatusDTO;
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

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Sql(scripts = "/sql/users/insert-users.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "/sql/books/insert-books.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "/sql/orders/insert-orders.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "/sql/orders/insert-pendent-order.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "/sql/orders/clear-orders-table.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public class UpdateOrderStatusE2ETest {

    @Autowired
    private WebTestClient testClient;

    private String updateOrderStatusUri = "/orders";


    @Test
    void updateOrderStatusWithValidDataReturnStatus204(){
        AuthenticationDTO authenticationDTO = new AuthenticationDTO("maria@gmail.com", "senha123");
        String orderId = "e4bbeb00-6ecd-4f7b-ac76-65f9d8e6ce8a";


        UpdateOrderStatusDTO orderStatusDTO = UpdateOrderStatusDTO
                .builder()
                .userId("1c97fc13-93bb-4dde-8cf3-ba156f195f82")
                .bookId("d8f426fe-71f9-46ed-8d0b-b39a4f88db42")
                .build();

        var responseBody = testClient
                .patch()
                .uri(updateOrderStatusUri + "/" + orderId)
                .headers(GetJwtAuthenticationToken.getHeaderJwtAccessToken(testClient, authenticationDTO.getEmail(), authenticationDTO.getPassword()))
                .bodyValue(orderStatusDTO)
                .exchange()
                .expectStatus().isNoContent()
                .expectBody().isEmpty();
    }

    @Test
    void updateOrderStatusWithOrderThatNotExistsReturnStatus404(){
        AuthenticationDTO authenticationDTO = new AuthenticationDTO("maria@gmail.com", "senha123");
        String orderId = "76bc0f1e-7417-493f-bad1-b0d5a0a8c8bd";

        UpdateOrderStatusDTO orderStatusDTO = UpdateOrderStatusDTO
                .builder()
                .userId("1c97fc13-93bb-4dde-8cf3-ba156f195f82")
                .bookId("d8f426fe-71f9-46ed-8d0b-b39a4f88db42")
                .build();

        ApplicationException responseBody = testClient
                .patch()
                .uri(updateOrderStatusUri + "/" + orderId)
                .contentType(MediaType.APPLICATION_JSON)
                .headers(GetJwtAuthenticationToken.getHeaderJwtAccessToken(testClient ,authenticationDTO.getEmail(), authenticationDTO.getPassword()))
                .bodyValue(orderStatusDTO)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody(ApplicationException.class)
                .returnResult().getResponseBody();

        Assertions.assertThat(responseBody).isNotNull();
        Assertions.assertThat(responseBody).isExactlyInstanceOf(ApplicationException.class);
        Assertions.assertThat(responseBody.getCode()).isEqualTo(404);
        Assertions.assertThat(responseBody.getStatus()).isEqualTo("Not Found");
        Assertions.assertThat(responseBody.getMethod()).isEqualTo("PATCH");
        Assertions.assertThat(responseBody.getMessage()).isEqualTo(OrderExceptionConsts.ORDER_NOT_FOUND);
    }


    @Test
    void updateOrderStatusWithUserThatNotExistsReturnStatus404(){
        AuthenticationDTO authenticationDTO = new AuthenticationDTO("maria@gmail.com", "senha123");
        String orderId = "e4bbeb00-6ecd-4f7b-ac76-65f9d8e6ce8a";


        UpdateOrderStatusDTO orderStatusDTO = UpdateOrderStatusDTO
                .builder()
                .userId("e66213b4-d36f-4700-b048-86fd391f0efb")
                .bookId("d8f426fe-71f9-46ed-8d0b-b39a4f88db42")
                .build();

        ApplicationException responseBody = testClient
                .patch()
                .uri(updateOrderStatusUri + "/" + orderId)
                .contentType(MediaType.APPLICATION_JSON)
                .headers(GetJwtAuthenticationToken.getHeaderJwtAccessToken(testClient ,authenticationDTO.getEmail(), authenticationDTO.getPassword()))
                .bodyValue(orderStatusDTO)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody(ApplicationException.class)
                .returnResult().getResponseBody();

        Assertions.assertThat(responseBody).isNotNull();
        Assertions.assertThat(responseBody).isExactlyInstanceOf(ApplicationException.class);
        Assertions.assertThat(responseBody.getCode()).isEqualTo(404);
        Assertions.assertThat(responseBody.getStatus()).isEqualTo("Not Found");
        Assertions.assertThat(responseBody.getMethod()).isEqualTo("PATCH");
        Assertions.assertThat(responseBody.getMessage()).isEqualTo(UserExceptionConsts.USER_NOT_FOUND);

    }

    @Test
    void updateOrderStatusWithBookThatNotExistsReturnStatus404(){
        AuthenticationDTO authenticationDTO = new AuthenticationDTO("maria@gmail.com", "senha123");
        String orderId = "e4bbeb00-6ecd-4f7b-ac76-65f9d8e6ce8a";

        UpdateOrderStatusDTO orderStatusDTO = UpdateOrderStatusDTO
                .builder()
                .userId("1c97fc13-93bb-4dde-8cf3-ba156f195f82")
                .bookId("3c32ac8d-2694-4712-bdfe-83a134b9f568")
                .build();

        ApplicationException responseBody = testClient
                .patch()
                .uri(updateOrderStatusUri + "/" + orderId)
                .contentType(MediaType.APPLICATION_JSON)
                .headers(GetJwtAuthenticationToken.getHeaderJwtAccessToken(testClient ,authenticationDTO.getEmail(), authenticationDTO.getPassword()))
                .bodyValue(orderStatusDTO)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody(ApplicationException.class)
                .returnResult().getResponseBody();

        Assertions.assertThat(responseBody).isNotNull();
        Assertions.assertThat(responseBody).isExactlyInstanceOf(ApplicationException.class);
        Assertions.assertThat(responseBody.getCode()).isEqualTo(404);
        Assertions.assertThat(responseBody.getStatus()).isEqualTo("Not Found");
        Assertions.assertThat(responseBody.getMethod()).isEqualTo("PATCH");
        Assertions.assertThat(responseBody.getMessage()).isEqualTo(BookExceptionConsts.BOOK_NOT_FOUND);
    }

    @Test
    void updateOrderStatusWithUserThatNotAdminRoleReturnStatus403(){
        AuthenticationDTO authenticationDTO = new AuthenticationDTO("joao@gmail.com", "senha123");
        String orderId = "e4bbeb00-6ecd-4f7b-ac76-65f9d8e6ce8a";


        UpdateOrderStatusDTO orderStatusDTO = UpdateOrderStatusDTO
                .builder()
                .userId("1c97fc13-93bb-4dde-8cf3-ba156f195f82")
                .bookId("d8f426fe-71f9-46ed-8d0b-b39a4f88db42")
                .build();

        testClient
                .patch()
                .uri(updateOrderStatusUri + "/" + orderId)
                .headers(GetJwtAuthenticationToken.getHeaderJwtAccessToken(testClient, authenticationDTO.getEmail(), authenticationDTO.getPassword()))
                .bodyValue(orderStatusDTO)
                .exchange()
                .expectStatus().isForbidden()
                .expectBody().isEmpty();
    }

    @Test
    void updateOrderWithoutTokenReturnStatus403(){
        String orderId = "e4bbeb00-6ecd-4f7b-ac76-65f9d8e6ce8a";

        UpdateOrderStatusDTO orderStatusDTO = UpdateOrderStatusDTO
                .builder()
                .userId("1c97fc13-93bb-4dde-8cf3-ba156f195f82")
                .bookId("d8f426fe-71f9-46ed-8d0b-b39a4f88db42")
                .build();

         testClient
                .patch()
                .uri(updateOrderStatusUri + "/" + orderId)
                 .bodyValue(orderStatusDTO)
                 .exchange()
                 .expectStatus().isForbidden()
                .expectBody().isEmpty();
    }
}
