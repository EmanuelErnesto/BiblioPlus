package com.emanuel.BiblioPlus.e2e.modules.users;

import com.emanuel.BiblioPlus.e2e.utils.GetJwtAuthenticationToken;
import com.emanuel.BiblioPlus.modules.users.domain.dtos.request.AuthenticationDTO;
import com.emanuel.BiblioPlus.modules.users.domain.dtos.response.UserResponseDTO;
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
public class ShowUserE2ETest {

    @Autowired
    private WebTestClient testClient;

    private String showUserUri = "/users";

    @Test
    void findUserThatExistsWithStatus200() {
        String userId = "29e186bc-3e2d-42c1-89b2-4097cb6c7c66";
        AuthenticationDTO authenticationDTO = new AuthenticationDTO("maria@gmail.com", "senha123");

        UserResponseDTO responseBody = testClient
                .get()
                .uri(showUserUri + "/" + userId)
                .headers(GetJwtAuthenticationToken.getHeaderJwtAccessToken(testClient, authenticationDTO.getEmail(), authenticationDTO.getPassword()))
                .exchange()
                .expectStatus().isOk()
                .expectBody(UserResponseDTO.class)
                .returnResult().getResponseBody();


        Assertions.assertThat(responseBody).isNotNull();
        Assertions.assertThat(responseBody).isExactlyInstanceOf(UserResponseDTO.class);
        Assertions.assertThat(responseBody.getEmail()).isEqualTo(authenticationDTO.getEmail());
        Assertions.assertThat(responseBody).hasFieldOrProperty("id");
    }

    @Test
    void findUserWithThisCpfReturnStatus200() {
        String userCpf = "66228049232";
        AuthenticationDTO authenticationDTO = new AuthenticationDTO("maria@gmail.com", "senha123");

        UserResponseDTO responseBody = testClient
                .get()
                .uri(showUserUri + "/search" + "?cpf=" + userCpf)
                .headers(GetJwtAuthenticationToken.getHeaderJwtAccessToken(testClient, authenticationDTO.getEmail(), authenticationDTO.getPassword()))
                .exchange()
                .expectStatus().isOk()
                .expectBody(UserResponseDTO.class)
                .returnResult().getResponseBody();


        Assertions.assertThat(responseBody).isNotNull();
        Assertions.assertThat(responseBody).isExactlyInstanceOf(UserResponseDTO.class);
        Assertions.assertThat(responseBody.getEmail()).isEqualTo(authenticationDTO.getEmail());
        Assertions.assertThat(responseBody).hasFieldOrProperty("id");

    }

    @Test
    void findUserWithCpfThatNotExistsReturnStatus404() {
        String userCpf = "56565374988";
        AuthenticationDTO authenticationDTO = new AuthenticationDTO("maria@gmail.com", "senha123");

        ApplicationException responseBody = testClient
                .get()
                .uri(showUserUri + "/search" + "?cpf=" + userCpf)
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
        Assertions.assertThat(responseBody.getMessage()).isEqualTo(UserExceptionConsts.USER_NOT_FOUND);

    }

    @Test
    void findUserWithIdThatNotExistsInDatabaseReturnStatus404() {
        String userId = "2e29b0bb-0d8e-40ff-af0c-f7ed0d9db117";
        AuthenticationDTO authenticationDTO = new AuthenticationDTO("maria@gmail.com", "senha123");

        ApplicationException responseBody = testClient
                .get()
                .uri(showUserUri + "/" + userId)
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
        Assertions.assertThat(responseBody.getMessage()).isEqualTo(UserExceptionConsts.USER_NOT_FOUND);
    }

    @Test
    void findWithUserThatNotHaveAdminRoleReturnStatus403(){
        String userId = "29e186bc-3e2d-42c1-89b2-4097cb6c7c66";
        AuthenticationDTO authenticationDTO = new AuthenticationDTO("joao@gmail.com", "senha123");
                testClient
                .get()
                .uri(showUserUri + "/" + userId)
                .headers(GetJwtAuthenticationToken.getHeaderJwtAccessToken(testClient, authenticationDTO.getEmail(), authenticationDTO.getPassword()))
                .exchange()
                .expectStatus().isForbidden()
                .expectBody().isEmpty();
    }

    @Test
    void findUserWithIdInInvalidFormatReturnStatus400() {
        String userId = "gdfgdfgdfg";
        AuthenticationDTO authenticationDTO = new AuthenticationDTO("maria@gmail.com", "senha123");

        ApplicationException responseBody = testClient
                .get()
                .uri(showUserUri + "/" + userId)
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
}
