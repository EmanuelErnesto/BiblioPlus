package com.emanuel.BiblioPlus.e2e.modules.users;

import com.emanuel.BiblioPlus.modules.users.domain.dtos.request.AuthenticationDTO;
import com.emanuel.BiblioPlus.modules.users.domain.dtos.request.RefreshTokenRequestDTO;
import com.emanuel.BiblioPlus.modules.users.domain.dtos.response.TokenResponseDTO;
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
@Sql(scripts = "/sql/users/clear-users-table.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public class CreateSessionE2ETest {

    @Autowired
    private WebTestClient testClient;

    private String authenticationUri = "/auth/login";
    private String refreshTokenUri = "/auth/refresh-token";

    @Test
    public void authenticateWithValidCredentialsReturnTokenWithStatus200() {
        AuthenticationDTO authenticationDTO = new AuthenticationDTO("joao@gmail.com", "senha123");

        TokenResponseDTO responseBody = testClient
                .post()
                .uri(authenticationUri)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(authenticationDTO)
                .exchange()
                .expectStatus().isOk()
                .expectBody(TokenResponseDTO.class)
                .returnResult().getResponseBody();

        Assertions.assertThat(responseBody).isNotNull();
        Assertions.assertThat(responseBody).isExactlyInstanceOf(TokenResponseDTO.class);
        Assertions.assertThat(responseBody).hasFieldOrProperty("accessToken");
        Assertions.assertThat(responseBody).hasFieldOrProperty("refreshToken");
    }

    @Test
    public void getRefreshTokenWithAValidRefreshToken() {
        AuthenticationDTO authenticationDTO = new AuthenticationDTO("joao@gmail.com", "senha123");

        String refreshToken = testClient
                .post()
                .uri(authenticationUri)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(authenticationDTO)
                .exchange()
                .expectStatus().isOk()
                .expectBody(TokenResponseDTO.class)
                .returnResult().getResponseBody().refreshToken();

        RefreshTokenRequestDTO refreshTokenRequestDTO = new RefreshTokenRequestDTO(refreshToken);

        TokenResponseDTO responseBody = testClient
                .post()
                .uri(refreshTokenUri)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(refreshTokenRequestDTO)
                .exchange()
                .expectStatus().isOk()
                .expectBody(TokenResponseDTO.class)
                .returnResult().getResponseBody();

        Assertions.assertThat(responseBody).isNotNull();
        Assertions.assertThat(responseBody).isExactlyInstanceOf(TokenResponseDTO.class);        Assertions.assertThat(responseBody).hasFieldOrProperty("accessToken");
        Assertions.assertThat(responseBody).hasFieldOrProperty("accessToken");
        Assertions.assertThat(responseBody).hasFieldOrProperty("refreshToken");

    }

    @Test
    public void getRefreshTokenWithRevokedRefreshTokenReturnsStatus401() {
        AuthenticationDTO authenticationDTO = new AuthenticationDTO("joao@gmail.com", "senha123");

        String refreshToken = testClient
                .post()
                .uri(authenticationUri)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(authenticationDTO)
                .exchange()
                .expectStatus().isOk()
                .expectBody(TokenResponseDTO.class)
                .returnResult().getResponseBody().refreshToken();

        RefreshTokenRequestDTO refreshTokenRequestDTO = new RefreshTokenRequestDTO(refreshToken);

            testClient
                .post()
                .uri(refreshTokenUri)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(refreshTokenRequestDTO)
                .exchange()
                .expectStatus().isOk()
                .expectBody(TokenResponseDTO.class)
                .returnResult().getResponseBody();

            ApplicationException responseBody = testClient
                    .post()
                    .uri(refreshTokenUri)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(refreshTokenRequestDTO)
                    .exchange()
                    .expectStatus().isUnauthorized()
                    .expectBody(ApplicationException.class)
                    .returnResult().getResponseBody();

        Assertions.assertThat(responseBody).isNotNull();
        Assertions.assertThat(responseBody).isExactlyInstanceOf(ApplicationException.class);
        Assertions.assertThat(responseBody.getCode()).isEqualTo(401);
        Assertions.assertThat(responseBody.getStatus()).isEqualTo("Unauthorized");
        Assertions.assertThat(responseBody.getMethod()).isEqualTo("POST");

    }

    @Test
    public void authenticateWithEmailOfAUserThatNotExistsReturnStatus400() {
        AuthenticationDTO authenticationDTO = new AuthenticationDTO("randomuser@gmail.com", "senha123");

        ApplicationException responseBody = testClient
                .post()
                .uri(authenticationUri)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(authenticationDTO)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(ApplicationException.class)
                .returnResult().getResponseBody();

        Assertions.assertThat(responseBody).isNotNull();
        Assertions.assertThat(responseBody.getCode()).isEqualTo(400);
        Assertions.assertThat(responseBody.getStatus()).isEqualTo("Bad Request");
        Assertions.assertThat(responseBody.getMethod()).isEqualTo("POST");
        Assertions.assertThat(responseBody).isExactlyInstanceOf(ApplicationException.class);
    }

    @Test
    public void authenticateWithInvalidPasswordReturnStatus400() {
        AuthenticationDTO authenticationDTO = new AuthenticationDTO("joão@gmail.com", "randompassword");

        ApplicationException responseBody = testClient
                .post()
                .uri(authenticationUri)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(authenticationDTO)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(ApplicationException.class)
                .returnResult().getResponseBody();

        Assertions.assertThat(responseBody).isNotNull();
        Assertions.assertThat(responseBody.getCode()).isEqualTo(400);
        Assertions.assertThat(responseBody.getStatus()).isEqualTo("Bad Request");
        Assertions.assertThat(responseBody.getMethod()).isEqualTo("POST");
        Assertions.assertThat(responseBody).isExactlyInstanceOf(ApplicationException.class);
    }

    @Test
    public void authenticateWithEmptyEmailReturnStatus400() {
        AuthenticationDTO authenticationDTO = new AuthenticationDTO("", "randompassword");

        ApplicationException responseBody = testClient
                .post()
                .uri(authenticationUri)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(authenticationDTO)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(ApplicationException.class)
                .returnResult().getResponseBody();

        Assertions.assertThat(responseBody).isNotNull();
        Assertions.assertThat(responseBody).isExactlyInstanceOf(ApplicationException.class);
        Assertions.assertThat(responseBody.getCode()).isEqualTo(400);
        Assertions.assertThat(responseBody.getStatus()).isEqualTo("Bad Request");
        Assertions.assertThat(responseBody.getMethod()).isEqualTo("POST");
    }

    @Test
    public void authenticateWithEmptyPasswordReturnStatus400() {
        AuthenticationDTO authenticationDTO = new AuthenticationDTO("joão@gmail.com", "");

        ApplicationException responseBody = testClient
                .post()
                .uri(authenticationUri)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(authenticationDTO)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(ApplicationException.class)
                .returnResult().getResponseBody();

        Assertions.assertThat(responseBody).isNotNull();
        Assertions.assertThat(responseBody.getCode()).isEqualTo(400);
        Assertions.assertThat(responseBody.getStatus()).isEqualTo("Bad Request");
        Assertions.assertThat(responseBody.getMethod()).isEqualTo("POST");
        Assertions.assertThat(responseBody).isExactlyInstanceOf(ApplicationException.class);
    }

    @Test
    public void authenticateWithEmptyEmailAndPasswordReturnStatus400() {
        AuthenticationDTO authenticationDTO = new AuthenticationDTO("", "");

        ApplicationException responseBody = testClient
                .post()
                .uri(authenticationUri)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(authenticationDTO)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(ApplicationException.class)
                .returnResult().getResponseBody();

        Assertions.assertThat(responseBody).isNotNull();
        Assertions.assertThat(responseBody.getCode()).isEqualTo(400);
        Assertions.assertThat(responseBody.getStatus()).isEqualTo("Bad Request");
        Assertions.assertThat(responseBody.getMethod()).isEqualTo("POST");
        Assertions.assertThat(responseBody).isExactlyInstanceOf(ApplicationException.class);
    }

}
