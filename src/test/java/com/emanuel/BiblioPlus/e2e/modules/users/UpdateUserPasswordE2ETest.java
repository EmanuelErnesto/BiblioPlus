package com.emanuel.BiblioPlus.e2e.modules.users;


import com.emanuel.BiblioPlus.e2e.utils.GetJwtAuthenticationToken;
import com.emanuel.BiblioPlus.modules.users.domain.dtos.request.AuthenticationDTO;
import com.emanuel.BiblioPlus.modules.users.domain.dtos.request.UpdateUserPasswordDTO;
import com.emanuel.BiblioPlus.shared.consts.UserExceptionConsts;
import com.emanuel.BiblioPlus.shared.consts.ValidationExceptionConsts;
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
public class UpdateUserPasswordE2ETest {

    @Autowired
    private WebTestClient testClient;

    private String updateUserPasswordUri = "/users";

    @Test
    void updateUserPasswordWithValidDataReturnStatus200(){
        String userId = "29e186bc-3e2d-42c1-89b2-4097cb6c7c66";
        AuthenticationDTO authenticationDTO = new AuthenticationDTO("maria@gmail.com", "senha123");

        UpdateUserPasswordDTO userPasswordDTO = UpdateUserPasswordDTO
                .builder()
                .oldPassword("senha123")
                .newPassword("novasenha123")
                .passwordConfirmation("novasenha123")
                .build();

        testClient
                .patch()
                .uri(updateUserPasswordUri + "/" + userId)
                .contentType(MediaType.APPLICATION_JSON)
                .headers(GetJwtAuthenticationToken.getHeaderJwtAccessToken(testClient, authenticationDTO.getEmail(), authenticationDTO.getPassword()))
                .bodyValue(userPasswordDTO)
                .exchange()
                .expectStatus().isNoContent()
                .expectBody().isEmpty();
    }

    @Test
    void updateUserPasswordWithPasswordConfirmationDifferentOfNewPasswordReturnStatus400(){
        String userId = "29e186bc-3e2d-42c1-89b2-4097cb6c7c66";
        AuthenticationDTO authenticationDTO = new AuthenticationDTO("maria@gmail.com", "senha123");

        UpdateUserPasswordDTO userPasswordDTO = UpdateUserPasswordDTO
                .builder()
                .oldPassword("senha123")
                .newPassword("novasenha123")
                .passwordConfirmation("senhaaleatoria123")
                .build();

        ApplicationException responseBody = testClient
                .patch()
                .uri(updateUserPasswordUri + "/" + userId)
                .contentType(MediaType.APPLICATION_JSON)
                .headers(GetJwtAuthenticationToken.getHeaderJwtAccessToken(testClient, authenticationDTO.getEmail(), authenticationDTO.getPassword()))
                .bodyValue(userPasswordDTO)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(ApplicationException.class)
                .returnResult().getResponseBody();

        Assertions.assertThat(responseBody).isNotNull();
        Assertions.assertThat(responseBody).isExactlyInstanceOf(ApplicationException.class);
        Assertions.assertThat(responseBody.getCode()).isEqualTo(400);
        Assertions.assertThat(responseBody.getStatus()).isEqualTo("Bad Request");
        Assertions.assertThat(responseBody.getMethod()).isEqualTo("PATCH");
        Assertions.assertThat(responseBody.getMessage()).isEqualTo(UserExceptionConsts.PASSWORD_MUST_EQUAL);
    }

    @Test
    void updateUserPasswordWithUserIdOfAUserThatNotExistsReturnStatus404(){
        String userId = "996047e6-3822-4820-a01c-545d656e4e00";
        AuthenticationDTO authenticationDTO = new AuthenticationDTO("maria@gmail.com", "senha123");

        UpdateUserPasswordDTO userPasswordDTO = UpdateUserPasswordDTO
                .builder()
                .oldPassword("senha123")
                .newPassword("novasenha123")
                .passwordConfirmation("novasenha123")
                .build();

        ApplicationException responseBody = testClient
                .patch()
                .uri(updateUserPasswordUri + "/" + userId)
                .contentType(MediaType.APPLICATION_JSON)
                .headers(GetJwtAuthenticationToken.getHeaderJwtAccessToken(testClient, authenticationDTO.getEmail(), authenticationDTO.getPassword()))
                .bodyValue(userPasswordDTO)
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
    void updateUserPasswordWithOldPasswordDifferentOfOldPasswordOfUserReturnStatus400(){
        String userId = "29e186bc-3e2d-42c1-89b2-4097cb6c7c66";
        AuthenticationDTO authenticationDTO = new AuthenticationDTO("maria@gmail.com", "senha123");

        UpdateUserPasswordDTO userPasswordDTO = UpdateUserPasswordDTO
                .builder()
                .oldPassword("senhaaleatoria1234")
                .newPassword("novasenha123")
                .passwordConfirmation("novasenha123")
                .build();

        ApplicationException responseBody = testClient
                .patch()
                .uri(updateUserPasswordUri + "/" + userId)
                .contentType(MediaType.APPLICATION_JSON)
                .headers(GetJwtAuthenticationToken.getHeaderJwtAccessToken(testClient, authenticationDTO.getEmail(), authenticationDTO.getPassword()))
                .bodyValue(userPasswordDTO)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(ApplicationException.class)
                .returnResult().getResponseBody();

        Assertions.assertThat(responseBody).isNotNull();
        Assertions.assertThat(responseBody).isExactlyInstanceOf(ApplicationException.class);
        Assertions.assertThat(responseBody.getCode()).isEqualTo(400);
        Assertions.assertThat(responseBody.getStatus()).isEqualTo("Bad Request");
        Assertions.assertThat(responseBody.getMethod()).isEqualTo("PATCH");
        Assertions.assertThat(responseBody.getMessage()).isEqualTo(UserExceptionConsts.PASSWORD_DOES_NOT_MATCH);
    }

    @Test
    void updateUserPasswordWithEmptyBodyValuesReturnStatus400(){

        String userId = "29e186bc-3e2d-42c1-89b2-4097cb6c7c66";
        AuthenticationDTO authenticationDTO = new AuthenticationDTO("maria@gmail.com", "senha123");

        UpdateUserPasswordDTO userPasswordDTO = UpdateUserPasswordDTO
                .builder()
                .oldPassword("")
                .newPassword("")
                .passwordConfirmation("")
                .build();

        ApplicationException responseBody = testClient
                .patch()
                .uri(updateUserPasswordUri + "/" + userId)
                .contentType(MediaType.APPLICATION_JSON)
                .headers(GetJwtAuthenticationToken.getHeaderJwtAccessToken(testClient, authenticationDTO.getEmail(), authenticationDTO.getPassword()))
                .bodyValue(userPasswordDTO)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(ApplicationException.class)
                .returnResult().getResponseBody();


        Assertions.assertThat(responseBody).isNotNull();
        Assertions.assertThat(responseBody).isExactlyInstanceOf(ApplicationException.class);
        Assertions.assertThat(responseBody.getCode()).isEqualTo(400);
        Assertions.assertThat(responseBody.getStatus()).isEqualTo("Bad Request");
        Assertions.assertThat(responseBody.getMethod()).isEqualTo("PATCH");
        Assertions.assertThat(responseBody.getMessage()).isEqualTo(ValidationExceptionConsts.VALIDATION_ERROR);
        Assertions.assertThat(responseBody.getErrors()).hasFieldOrProperty("oldPassword");
        Assertions.assertThat(responseBody.getErrors()).hasFieldOrProperty("newPassword");
        Assertions.assertThat(responseBody.getErrors()).hasFieldOrProperty("passwordConfirmation");
    }

    @Test
    void updateUserPasswordWithEmptyOldPasswordValueReturnStatus400(){

        String userId = "29e186bc-3e2d-42c1-89b2-4097cb6c7c66";
        AuthenticationDTO authenticationDTO = new AuthenticationDTO("maria@gmail.com", "senha123");

        UpdateUserPasswordDTO userPasswordDTO = UpdateUserPasswordDTO
                .builder()
                .oldPassword("")
                .newPassword("novasenha")
                .passwordConfirmation("novasenha")
                .build();

        ApplicationException responseBody = testClient
                .patch()
                .uri(updateUserPasswordUri + "/" + userId)
                .contentType(MediaType.APPLICATION_JSON)
                .headers(GetJwtAuthenticationToken.getHeaderJwtAccessToken(testClient, authenticationDTO.getEmail(), authenticationDTO.getPassword()))
                .bodyValue(userPasswordDTO)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(ApplicationException.class)
                .returnResult().getResponseBody();


        Assertions.assertThat(responseBody).isNotNull();
        Assertions.assertThat(responseBody).isExactlyInstanceOf(ApplicationException.class);
        Assertions.assertThat(responseBody.getCode()).isEqualTo(400);
        Assertions.assertThat(responseBody.getStatus()).isEqualTo("Bad Request");
        Assertions.assertThat(responseBody.getMethod()).isEqualTo("PATCH");
        Assertions.assertThat(responseBody.getMessage()).isEqualTo(ValidationExceptionConsts.VALIDATION_ERROR);
        Assertions.assertThat(responseBody.getErrors()).hasFieldOrProperty("oldPassword");
    }

    @Test
    void updateUserPasswordWithEmptyNewPasswordValueReturnStatus400(){

        String userId = "29e186bc-3e2d-42c1-89b2-4097cb6c7c66";
        AuthenticationDTO authenticationDTO = new AuthenticationDTO("maria@gmail.com", "senha123");

        UpdateUserPasswordDTO userPasswordDTO = UpdateUserPasswordDTO
                .builder()
                .oldPassword("senha123")
                .newPassword("")
                .passwordConfirmation("novasenha")
                .build();

        ApplicationException responseBody = testClient
                .patch()
                .uri(updateUserPasswordUri + "/" + userId)
                .contentType(MediaType.APPLICATION_JSON)
                .headers(GetJwtAuthenticationToken.getHeaderJwtAccessToken(testClient, authenticationDTO.getEmail(), authenticationDTO.getPassword()))
                .bodyValue(userPasswordDTO)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(ApplicationException.class)
                .returnResult().getResponseBody();


        Assertions.assertThat(responseBody).isNotNull();
        Assertions.assertThat(responseBody).isExactlyInstanceOf(ApplicationException.class);
        Assertions.assertThat(responseBody.getCode()).isEqualTo(400);
        Assertions.assertThat(responseBody.getStatus()).isEqualTo("Bad Request");
        Assertions.assertThat(responseBody.getMethod()).isEqualTo("PATCH");
        Assertions.assertThat(responseBody.getMessage()).isEqualTo(ValidationExceptionConsts.VALIDATION_ERROR);
        Assertions.assertThat(responseBody.getErrors()).hasFieldOrProperty("newPassword");
    }

    @Test
    void updateUserPasswordWithEmptyPasswordConfirmationValueReturnStatus400(){

        String userId = "29e186bc-3e2d-42c1-89b2-4097cb6c7c66";
        AuthenticationDTO authenticationDTO = new AuthenticationDTO("maria@gmail.com", "senha123");

        UpdateUserPasswordDTO userPasswordDTO = UpdateUserPasswordDTO
                .builder()
                .oldPassword("senha123")
                .newPassword("novasenha")
                .passwordConfirmation("")
                .build();

        ApplicationException responseBody = testClient
                .patch()
                .uri(updateUserPasswordUri + "/" + userId)
                .contentType(MediaType.APPLICATION_JSON)
                .headers(GetJwtAuthenticationToken.getHeaderJwtAccessToken(testClient, authenticationDTO.getEmail(), authenticationDTO.getPassword()))
                .bodyValue(userPasswordDTO)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(ApplicationException.class)
                .returnResult().getResponseBody();


        Assertions.assertThat(responseBody).isNotNull();
        Assertions.assertThat(responseBody).isExactlyInstanceOf(ApplicationException.class);
        Assertions.assertThat(responseBody.getCode()).isEqualTo(400);
        Assertions.assertThat(responseBody.getStatus()).isEqualTo("Bad Request");
        Assertions.assertThat(responseBody.getMethod()).isEqualTo("PATCH");
        Assertions.assertThat(responseBody.getMessage()).isEqualTo(ValidationExceptionConsts.VALIDATION_ERROR);
        Assertions.assertThat(responseBody.getErrors()).hasFieldOrProperty("passwordConfirmation");
    }

}
