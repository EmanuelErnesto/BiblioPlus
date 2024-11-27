package com.emanuel.BiblioPlus.e2e.modules.users;

import com.emanuel.BiblioPlus.e2e.utils.GetJwtAuthenticationToken;
import com.emanuel.BiblioPlus.modules.users.domain.dtos.request.AuthenticationDTO;
import com.emanuel.BiblioPlus.modules.users.domain.dtos.response.PaginatedUsersResponseDTO;
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
public class ListUserE2ETest {

    @Autowired
    private WebTestClient testClient;

    private String listUsersUri = "/users";

    @Test
    void listUsersPaginatedReturnStatus200() {
        AuthenticationDTO authenticationDTO = new AuthenticationDTO("maria@gmail.com", "senha123");

        PaginatedUsersResponseDTO responseBody = testClient
                .get()
                .uri(listUsersUri)
                .headers(GetJwtAuthenticationToken.getHeaderJwtAccessToken(testClient, authenticationDTO.getEmail(), authenticationDTO.getPassword()))
                .exchange()
                .expectStatus().isOk()
                .expectBody(PaginatedUsersResponseDTO.class)
                .returnResult().getResponseBody();

        Assertions.assertThat(responseBody).isNotNull();
        Assertions.assertThat(responseBody).isExactlyInstanceOf(PaginatedUsersResponseDTO.class);
        Assertions.assertThat(responseBody.users().size()).isEqualTo(3);
        Assertions.assertThat(responseBody.current_page()).isEqualTo(0);
        Assertions.assertThat(responseBody.total_items()).isEqualTo(3);
        Assertions.assertThat(responseBody.total_pages()).isEqualTo(1);
    }

    @Test
    void listUsersPaginatedWithSizeReturnStatus200() {
        AuthenticationDTO authenticationDTO = new AuthenticationDTO("maria@gmail.com", "senha123");

        PaginatedUsersResponseDTO responseBody = testClient
                .get()
                .uri(listUsersUri + "?size=1")
                .headers(GetJwtAuthenticationToken.getHeaderJwtAccessToken(testClient, authenticationDTO.getEmail(), authenticationDTO.getPassword()))
                .exchange()
                .expectStatus().isOk()
                .expectBody(PaginatedUsersResponseDTO.class)
                .returnResult().getResponseBody();

        Assertions.assertThat(responseBody).isNotNull();
        Assertions.assertThat(responseBody).isExactlyInstanceOf(PaginatedUsersResponseDTO.class);
        Assertions.assertThat(responseBody.users().size()).isEqualTo(1);
        Assertions.assertThat(responseBody.current_page()).isEqualTo(0);
        Assertions.assertThat(responseBody.total_items()).isEqualTo(3);
        Assertions.assertThat(responseBody.total_pages()).isEqualTo(3);
    }

    @Test
    void listUsersPaginatedWithPageNumberAndSizeReturnStatus200() {
        AuthenticationDTO authenticationDTO = new AuthenticationDTO("maria@gmail.com", "senha123");

        PaginatedUsersResponseDTO responseBody = testClient
                .get()
                .uri(listUsersUri + "?size=1&pageNumber=1")
                .headers(GetJwtAuthenticationToken.getHeaderJwtAccessToken(testClient, authenticationDTO.getEmail(), authenticationDTO.getPassword()))
                .exchange()
                .expectStatus().isOk()
                .expectBody(PaginatedUsersResponseDTO.class)
                .returnResult().getResponseBody();

        Assertions.assertThat(responseBody).isNotNull();
        Assertions.assertThat(responseBody).isExactlyInstanceOf(PaginatedUsersResponseDTO.class);
        Assertions.assertThat(responseBody.users().size()).isEqualTo(1);
        Assertions.assertThat(responseBody.current_page()).isEqualTo(1);
        Assertions.assertThat(responseBody.total_items()).isEqualTo(3);
        Assertions.assertThat(responseBody.total_pages()).isEqualTo(3);
    }

    @Test
    void listUsersPaginatedWithUserThatNotHaveAdminRoleReturnStatus403(){
        AuthenticationDTO authenticationDTO = new AuthenticationDTO("joao@gmail.com", "senha123");
                 testClient
                .get()
                .uri(listUsersUri)
                .headers(GetJwtAuthenticationToken.getHeaderJwtAccessToken(testClient, authenticationDTO.getEmail(), authenticationDTO.getPassword()))
                .exchange()
                .expectStatus().isForbidden()
                .expectBody().isEmpty();
    }

    @Test
    void listUsersPaginatedWithoutTokenReturnStatus403() {
                testClient
                .get()
                .uri(listUsersUri)
                .exchange()
                .expectStatus().isForbidden()
                .expectBody().isEmpty();
    }
}
