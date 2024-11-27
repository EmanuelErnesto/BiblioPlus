package com.emanuel.BiblioPlus.e2e.modules.users;


import com.emanuel.BiblioPlus.e2e.utils.GetJwtAuthenticationToken;
import com.emanuel.BiblioPlus.modules.users.domain.dtos.request.AuthenticationDTO;
import com.emanuel.BiblioPlus.modules.users.domain.dtos.request.CreateUserDTO;
import com.emanuel.BiblioPlus.modules.users.domain.dtos.request.UpdateUserDTO;
import com.emanuel.BiblioPlus.modules.users.domain.dtos.response.UserResponseDTO;
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
public class UpdateUserE2ETest {

    @Autowired
    private WebTestClient testClient;

    private String updateUserUri = "/users";

    @Test
    void updateUserWithValidDataReturn200(){
        String userId = "29e186bc-3e2d-42c1-89b2-4097cb6c7c66";
        AuthenticationDTO authenticationDTO = new AuthenticationDTO("maria@gmail.com", "senha123");

        UpdateUserDTO userDTO = UpdateUserDTO
                .builder()
                .name("update-user-001")
                .email("update-user-001@gmail.com")
                .cpf("24448711382")
                .birthDay("01/02/2000")
                .cep("55850000")
                .build();

        UserResponseDTO responseBody = testClient
                .put()
                .uri(updateUserUri + "/" + userId)
                .contentType(MediaType.APPLICATION_JSON)
                .headers(GetJwtAuthenticationToken.getHeaderJwtAccessToken(testClient,authenticationDTO.getEmail(), authenticationDTO.getPassword()))
                .bodyValue(userDTO)
                .exchange()
                .expectStatus().isOk()
                .expectBody(UserResponseDTO.class)
                .returnResult().getResponseBody();

        Assertions.assertThat(responseBody).isNotNull();
        Assertions.assertThat(responseBody).isExactlyInstanceOf(UserResponseDTO.class);
        Assertions.assertThat(responseBody).hasFieldOrProperty("id");
        Assertions.assertThat(responseBody.getName()).isEqualTo(userDTO.getName());
        Assertions.assertThat(responseBody.getEmail()).isEqualTo(userDTO.getEmail());
        Assertions.assertThat(responseBody.getCpf()).isEqualTo(userDTO.getCpf());
        Assertions.assertThat(responseBody.getCep()).isEqualTo(userDTO.getCep());
    }

    @Test
    void updateUserWithValidDataAndCepDifferentOfTheUserCepReturn200(){
        String userId = "29e186bc-3e2d-42c1-89b2-4097cb6c7c66";
        AuthenticationDTO authenticationDTO = new AuthenticationDTO("maria@gmail.com", "senha123");

        UpdateUserDTO userDTO = UpdateUserDTO
                .builder()
                .name("update-user-001")
                .email("update-user-001@gmail.com")
                .cpf("24448711382")
                .birthDay("01/02/2000")
                .cep("01001000")
                .build();

        UserResponseDTO responseBody = testClient
                .put()
                .uri(updateUserUri + "/" + userId)
                .contentType(MediaType.APPLICATION_JSON)
                .headers(GetJwtAuthenticationToken.getHeaderJwtAccessToken(testClient,authenticationDTO.getEmail(), authenticationDTO.getPassword()))
                .bodyValue(userDTO)
                .exchange()
                .expectStatus().isOk()
                .expectBody(UserResponseDTO.class)
                .returnResult().getResponseBody();

        Assertions.assertThat(responseBody).isNotNull();
        Assertions.assertThat(responseBody).isExactlyInstanceOf(UserResponseDTO.class);
        Assertions.assertThat(responseBody).hasFieldOrProperty("id");
        Assertions.assertThat(responseBody.getName()).isEqualTo(userDTO.getName());
        Assertions.assertThat(responseBody.getEmail()).isEqualTo(userDTO.getEmail());
        Assertions.assertThat(responseBody.getCpf()).isEqualTo(userDTO.getCpf());
        Assertions.assertThat(responseBody.getCep()).isEqualTo("01001000");
        Assertions.assertThat(responseBody.getPatio()).isEqualTo("Praça da Sé");
        Assertions.assertThat(responseBody.getComplement()).isEqualTo("lado ímpar");
        Assertions.assertThat(responseBody.getNeighborhood()).isEqualTo("Sé");
        Assertions.assertThat(responseBody.getLocality()).isEqualTo("São Paulo");
        Assertions.assertThat(responseBody.getUf()).isEqualTo("SP");
    }

    @Test
    void updateUserWithEmailThatAlreadyExistsReturnError400() {
        String userId = "29e186bc-3e2d-42c1-89b2-4097cb6c7c66";
        AuthenticationDTO authenticationDTO = new AuthenticationDTO("maria@gmail.com", "senha123");

        UpdateUserDTO userDTO = UpdateUserDTO
                .builder()
                .name("update-user-001")
                .email("joao@gmail.com")
                .cpf("24448711382")
                .birthDay("01/02/2000")
                .cep("55850000")
                .build();

        ApplicationException responseBody = testClient
                .put()
                .uri(updateUserUri + "/" + userId)
                .contentType(MediaType.APPLICATION_JSON)
                .headers(GetJwtAuthenticationToken.getHeaderJwtAccessToken(testClient, authenticationDTO.getEmail(), authenticationDTO.getPassword()))
                .bodyValue(userDTO)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(ApplicationException.class)
                .returnResult().getResponseBody();

        Assertions.assertThat(responseBody).isNotNull();
        Assertions.assertThat(responseBody).isExactlyInstanceOf(ApplicationException.class);
        Assertions.assertThat(responseBody.getCode()).isEqualTo(400);
        Assertions.assertThat(responseBody.getStatus()).isEqualTo("Bad Request");
        Assertions.assertThat(responseBody.getMethod()).isEqualTo("PUT");
        Assertions.assertThat(responseBody.getMessage()).isEqualTo(UserExceptionConsts.EMAIL_ALREADY_EXISTS);

    }

    @Test
    void updateUserWithCpfThatAlreadyExistsReturnError400() {
        String userId = "29e186bc-3e2d-42c1-89b2-4097cb6c7c66";
        AuthenticationDTO authenticationDTO = new AuthenticationDTO("maria@gmail.com", "senha123");

        UpdateUserDTO userDTO = UpdateUserDTO
                .builder()
                .name("update-user-001")
                .email("update-user-001@gmail.com")
                .cpf("63928498150")
                .birthDay("01/02/2000")
                .cep("55850000")
                .build();

        ApplicationException responseBody = testClient
                .put()
                .uri(updateUserUri + "/" + userId)
                .contentType(MediaType.APPLICATION_JSON)
                .headers(GetJwtAuthenticationToken.getHeaderJwtAccessToken(testClient, authenticationDTO.getEmail(), authenticationDTO.getPassword()))
                .bodyValue(userDTO)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(ApplicationException.class)
                .returnResult().getResponseBody();

        Assertions.assertThat(responseBody).isNotNull();
        Assertions.assertThat(responseBody).isExactlyInstanceOf(ApplicationException.class);
        Assertions.assertThat(responseBody.getCode()).isEqualTo(400);
        Assertions.assertThat(responseBody.getStatus()).isEqualTo("Bad Request");
        Assertions.assertThat(responseBody.getMethod()).isEqualTo("PUT");
        Assertions.assertThat(responseBody.getMessage()).isEqualTo(UserExceptionConsts.CPF_ALREADY_EXISTS);
    }

    @Test
    void updateUserWithEmptyValuesInBodyReturnsStatus400() {
        String userId = "29e186bc-3e2d-42c1-89b2-4097cb6c7c66";
        AuthenticationDTO authenticationDTO = new AuthenticationDTO("maria@gmail.com", "senha123");
        UpdateUserDTO userDTO = UpdateUserDTO
                .builder()
                .name("")
                .email("")
                .cpf("")
                .birthDay("")
                .cep("")
                .build();


        ApplicationException responseBody = testClient
                .put()
                .uri(updateUserUri + "/" + userId)
                .contentType(MediaType.APPLICATION_JSON)
                .headers(GetJwtAuthenticationToken.getHeaderJwtAccessToken(testClient, authenticationDTO.getEmail(), authenticationDTO.getPassword()))
                .bodyValue(userDTO)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(ApplicationException.class)
                .returnResult().getResponseBody();

        Assertions.assertThat(responseBody).isNotNull();
        Assertions.assertThat(responseBody).isExactlyInstanceOf(ApplicationException.class);
        Assertions.assertThat(responseBody.getCode()).isEqualTo(400);
        Assertions.assertThat(responseBody.getStatus()).isEqualTo("Bad Request");
        Assertions.assertThat(responseBody.getMethod()).isEqualTo("PUT");
        Assertions.assertThat(responseBody.getMessage()).isEqualTo(ValidationExceptionConsts.VALIDATION_ERROR);
        Assertions.assertThat(responseBody.getErrors()).hasFieldOrProperty("name");
        Assertions.assertThat(responseBody.getErrors()).hasFieldOrProperty("email");
        Assertions.assertThat(responseBody.getErrors()).hasFieldOrProperty("cpf");
        Assertions.assertThat(responseBody.getErrors()).hasFieldOrProperty("birthDay");
        Assertions.assertThat(responseBody.getErrors()).hasFieldOrProperty("cep");
    }

    @Test
    void updateUserWithEmptyNameInBodyReturnsStatus400() {
        String userId = "29e186bc-3e2d-42c1-89b2-4097cb6c7c66";
        AuthenticationDTO authenticationDTO = new AuthenticationDTO("maria@gmail.com", "senha123");

        UpdateUserDTO userDTO = UpdateUserDTO
                .builder()
                .name("")
                .email("update-user-001@gmail.com")
                .cpf("24448711382")
                .birthDay("01/02/2000")
                .cep("55850000")
                .build();


        ApplicationException responseBody = testClient
                .put()
                .uri(updateUserUri + "/" + userId)
                .contentType(MediaType.APPLICATION_JSON)
                .headers(GetJwtAuthenticationToken.getHeaderJwtAccessToken(testClient, authenticationDTO.getEmail(), authenticationDTO.getPassword()))
                .bodyValue(userDTO)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(ApplicationException.class)
                .returnResult().getResponseBody();

        Assertions.assertThat(responseBody).isNotNull();
        Assertions.assertThat(responseBody).isExactlyInstanceOf(ApplicationException.class);
        Assertions.assertThat(responseBody.getCode()).isEqualTo(400);
        Assertions.assertThat(responseBody.getStatus()).isEqualTo("Bad Request");
        Assertions.assertThat(responseBody.getMethod()).isEqualTo("PUT");
        Assertions.assertThat(responseBody.getMessage()).isEqualTo(ValidationExceptionConsts.VALIDATION_ERROR);
        Assertions.assertThat(responseBody.getErrors()).hasFieldOrProperty("name");
    }

    @Test
    void updateUserWithNameLengthLess3CharactersInBodyReturnsStatus400() {
        String userId = "29e186bc-3e2d-42c1-89b2-4097cb6c7c66";
        AuthenticationDTO authenticationDTO = new AuthenticationDTO("maria@gmail.com", "senha123");

        UpdateUserDTO userDTO = UpdateUserDTO
                .builder()
                .name("up")
                .email("update-user-001@gmail.com")
                .cpf("24448711382")
                .birthDay("01/02/2000")
                .cep("55850000")
                .build();


        ApplicationException responseBody = testClient
                .put()
                .uri(updateUserUri + "/" + userId)
                .contentType(MediaType.APPLICATION_JSON)
                .headers(GetJwtAuthenticationToken.getHeaderJwtAccessToken(testClient, authenticationDTO.getEmail(), authenticationDTO.getPassword()))
                .bodyValue(userDTO)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(ApplicationException.class)
                .returnResult().getResponseBody();

        Assertions.assertThat(responseBody).isNotNull();
        Assertions.assertThat(responseBody).isExactlyInstanceOf(ApplicationException.class);
        Assertions.assertThat(responseBody.getCode()).isEqualTo(400);
        Assertions.assertThat(responseBody.getStatus()).isEqualTo("Bad Request");
        Assertions.assertThat(responseBody.getMethod()).isEqualTo("PUT");
        Assertions.assertThat(responseBody.getMessage()).isEqualTo(ValidationExceptionConsts.VALIDATION_ERROR);
        Assertions.assertThat(responseBody.getErrors()).hasFieldOrProperty("name");
    }

    @Test
    void updateUserWithEmptyEmailInBodyReturnsStatus400() {
        String userId = "29e186bc-3e2d-42c1-89b2-4097cb6c7c66";
        AuthenticationDTO authenticationDTO = new AuthenticationDTO("maria@gmail.com", "senha123");

        UpdateUserDTO userDTO = UpdateUserDTO
                .builder()
                .name("update-user-003")
                .email("")
                .cpf("24448711382")
                .birthDay("01/02/2000")
                .cep("55850000")
                .build();


        ApplicationException responseBody = testClient
                .put()
                .uri(updateUserUri + "/" + userId)
                .contentType(MediaType.APPLICATION_JSON)
                .headers(GetJwtAuthenticationToken.getHeaderJwtAccessToken(testClient, authenticationDTO.getEmail(), authenticationDTO.getPassword()))
                .bodyValue(userDTO)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(ApplicationException.class)
                .returnResult().getResponseBody();

        Assertions.assertThat(responseBody).isNotNull();
        Assertions.assertThat(responseBody).isExactlyInstanceOf(ApplicationException.class);
        Assertions.assertThat(responseBody.getCode()).isEqualTo(400);
        Assertions.assertThat(responseBody.getStatus()).isEqualTo("Bad Request");
        Assertions.assertThat(responseBody.getMethod()).isEqualTo("PUT");
        Assertions.assertThat(responseBody.getMessage()).isEqualTo(ValidationExceptionConsts.VALIDATION_ERROR);
        Assertions.assertThat(responseBody.getErrors()).hasFieldOrProperty("email");
    }

    @Test
    void updateUserWithInvalidEmailFormatInBodyReturnsStatus400() {
        String userId = "29e186bc-3e2d-42c1-89b2-4097cb6c7c66";
        AuthenticationDTO authenticationDTO = new AuthenticationDTO("maria@gmail.com", "senha123");

        UpdateUserDTO userDTO = UpdateUserDTO
                .builder()
                .name("update-user-003")
                .email("fvdsfgsdfsfs")
                .cpf("24448711382")
                .birthDay("01/02/2000")
                .cep("55850000")
                .build();


        ApplicationException responseBody = testClient
                .put()
                .uri(updateUserUri + "/" + userId)
                .contentType(MediaType.APPLICATION_JSON)
                .headers(GetJwtAuthenticationToken.getHeaderJwtAccessToken(testClient, authenticationDTO.getEmail(), authenticationDTO.getPassword()))
                .bodyValue(userDTO)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(ApplicationException.class)
                .returnResult().getResponseBody();

        Assertions.assertThat(responseBody).isNotNull();
        Assertions.assertThat(responseBody).isExactlyInstanceOf(ApplicationException.class);
        Assertions.assertThat(responseBody.getCode()).isEqualTo(400);
        Assertions.assertThat(responseBody.getStatus()).isEqualTo("Bad Request");
        Assertions.assertThat(responseBody.getMethod()).isEqualTo("PUT");
        Assertions.assertThat(responseBody.getMessage()).isEqualTo(ValidationExceptionConsts.VALIDATION_ERROR);
        Assertions.assertThat(responseBody.getErrors()).hasFieldOrProperty("email");
    }

    @Test
    void updateUserWithEmptyCpfInBodyReturnsStatus400() {
        String userId = "29e186bc-3e2d-42c1-89b2-4097cb6c7c66";
        AuthenticationDTO authenticationDTO = new AuthenticationDTO("maria@gmail.com", "senha123");

        UpdateUserDTO userDTO = UpdateUserDTO
                .builder()
                .name("update-user-003")
                .email("update-user-003@gmail.com")
                .cpf("")
                .birthDay("01/02/2000")
                .cep("55850000")
                .build();


        ApplicationException responseBody = testClient
                .put()
                .uri(updateUserUri + "/" + userId)
                .contentType(MediaType.APPLICATION_JSON)
                .headers(GetJwtAuthenticationToken.getHeaderJwtAccessToken(testClient, authenticationDTO.getEmail(), authenticationDTO.getPassword()))
                .bodyValue(userDTO)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(ApplicationException.class)
                .returnResult().getResponseBody();

        Assertions.assertThat(responseBody).isNotNull();
        Assertions.assertThat(responseBody).isExactlyInstanceOf(ApplicationException.class);
        Assertions.assertThat(responseBody.getCode()).isEqualTo(400);
        Assertions.assertThat(responseBody.getStatus()).isEqualTo("Bad Request");
        Assertions.assertThat(responseBody.getMethod()).isEqualTo("PUT");
        Assertions.assertThat(responseBody.getMessage()).isEqualTo(ValidationExceptionConsts.VALIDATION_ERROR);
        Assertions.assertThat(responseBody.getErrors()).hasFieldOrProperty("cpf");
    }

    @Test
    void updateUserWithInvalidCpfFormatInBodyReturnsStatus400() {
        String userId = "29e186bc-3e2d-42c1-89b2-4097cb6c7c66";
        AuthenticationDTO authenticationDTO = new AuthenticationDTO("maria@gmail.com", "senha123");

        UpdateUserDTO userDTO = UpdateUserDTO
                .builder()
                .name("update-user-003")
                .email("update-user-003@gmail.com")
                .cpf("fdsafdsfsfsdfs")
                .birthDay("01/02/2000")
                .cep("55850000")
                .build();


        ApplicationException responseBody = testClient
                .put()
                .uri(updateUserUri + "/" + userId)
                .contentType(MediaType.APPLICATION_JSON)
                .headers(GetJwtAuthenticationToken.getHeaderJwtAccessToken(testClient, authenticationDTO.getEmail(), authenticationDTO.getPassword()))
                .bodyValue(userDTO)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(ApplicationException.class)
                .returnResult().getResponseBody();

        Assertions.assertThat(responseBody).isNotNull();
        Assertions.assertThat(responseBody).isExactlyInstanceOf(ApplicationException.class);
        Assertions.assertThat(responseBody.getCode()).isEqualTo(400);
        Assertions.assertThat(responseBody.getStatus()).isEqualTo("Bad Request");
        Assertions.assertThat(responseBody.getMethod()).isEqualTo("PUT");
        Assertions.assertThat(responseBody.getMessage()).isEqualTo(ValidationExceptionConsts.VALIDATION_ERROR);
        Assertions.assertThat(responseBody.getErrors()).hasFieldOrProperty("cpf");
    }

    @Test
    void updateUserWithEmptyBirthdayInBodyReturnsStatus400() {
        String userId = "29e186bc-3e2d-42c1-89b2-4097cb6c7c66";
        AuthenticationDTO authenticationDTO = new AuthenticationDTO("maria@gmail.com", "senha123");

        UpdateUserDTO userDTO = UpdateUserDTO
                .builder()
                .name("update-user-003")
                .email("update-user-003@gmail.com")
                .cpf("96146532800")
                .birthDay("")
                .cep("55850000")
                .build();


        ApplicationException responseBody = testClient
                .put()
                .uri(updateUserUri + "/" + userId)
                .contentType(MediaType.APPLICATION_JSON)
                .headers(GetJwtAuthenticationToken.getHeaderJwtAccessToken(testClient, authenticationDTO.getEmail(), authenticationDTO.getPassword()))
                .bodyValue(userDTO)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(ApplicationException.class)
                .returnResult().getResponseBody();

        Assertions.assertThat(responseBody).isNotNull();
        Assertions.assertThat(responseBody).isExactlyInstanceOf(ApplicationException.class);
        Assertions.assertThat(responseBody.getCode()).isEqualTo(400);
        Assertions.assertThat(responseBody.getStatus()).isEqualTo("Bad Request");
        Assertions.assertThat(responseBody.getMethod()).isEqualTo("PUT");
        Assertions.assertThat(responseBody.getMessage()).isEqualTo(ValidationExceptionConsts.VALIDATION_ERROR);
        Assertions.assertThat(responseBody.getErrors()).hasFieldOrProperty("birthDay");
    }

    @Test
    void updateUserWithInvalidBirthDayFormatInBodyReturnsStatus400() {
        String userId = "29e186bc-3e2d-42c1-89b2-4097cb6c7c66";
        AuthenticationDTO authenticationDTO = new AuthenticationDTO("maria@gmail.com", "senha123");

        UpdateUserDTO userDTO = UpdateUserDTO
                .builder()
                .name("update-user-003")
                .email("update-user-003@gmail.com")
                .cpf("96146532800")
                .birthDay("dsadadadadacas")
                .cep("55850000")
                .build();


        ApplicationException responseBody = testClient
                .put()
                .uri(updateUserUri + "/" + userId)
                .contentType(MediaType.APPLICATION_JSON)
                .headers(GetJwtAuthenticationToken.getHeaderJwtAccessToken(testClient, authenticationDTO.getEmail(), authenticationDTO.getPassword()))
                .bodyValue(userDTO)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(ApplicationException.class)
                .returnResult().getResponseBody();

        Assertions.assertThat(responseBody).isNotNull();
        Assertions.assertThat(responseBody).isExactlyInstanceOf(ApplicationException.class);
        Assertions.assertThat(responseBody.getCode()).isEqualTo(400);
        Assertions.assertThat(responseBody.getStatus()).isEqualTo("Bad Request");
        Assertions.assertThat(responseBody.getMethod()).isEqualTo("PUT");
        Assertions.assertThat(responseBody.getMessage()).isEqualTo(ValidationExceptionConsts.VALIDATION_ERROR);
        Assertions.assertThat(responseBody.getErrors()).hasFieldOrProperty("birthDay");
    }

    @Test
    void updateUserWithEmptyCepInBodyReturnsStatus400() {
        String userId = "29e186bc-3e2d-42c1-89b2-4097cb6c7c66";
        AuthenticationDTO authenticationDTO = new AuthenticationDTO("maria@gmail.com", "senha123");

        UpdateUserDTO userDTO = UpdateUserDTO
                .builder()
                .name("update-user-003")
                .email("update-user-003@gmail.com")
                .cpf("96146532800")
                .birthDay("06/03/1989")
                .cep("")
                .build();


        ApplicationException responseBody = testClient
                .put()
                .uri(updateUserUri + "/" + userId)
                .contentType(MediaType.APPLICATION_JSON)
                .headers(GetJwtAuthenticationToken.getHeaderJwtAccessToken(testClient, authenticationDTO.getEmail(), authenticationDTO.getPassword()))
                .bodyValue(userDTO)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(ApplicationException.class)
                .returnResult().getResponseBody();

        Assertions.assertThat(responseBody).isNotNull();
        Assertions.assertThat(responseBody).isExactlyInstanceOf(ApplicationException.class);
        Assertions.assertThat(responseBody.getCode()).isEqualTo(400);
        Assertions.assertThat(responseBody.getStatus()).isEqualTo("Bad Request");
        Assertions.assertThat(responseBody.getMethod()).isEqualTo("PUT");
        Assertions.assertThat(responseBody.getMessage()).isEqualTo(ValidationExceptionConsts.VALIDATION_ERROR);
        Assertions.assertThat(responseBody.getErrors()).hasFieldOrProperty("cep");
    }

    @Test
    void updateUserWithInvalidCepFormatInBodyReturnsStatus400() {
        String userId = "29e186bc-3e2d-42c1-89b2-4097cb6c7c66";
        AuthenticationDTO authenticationDTO = new AuthenticationDTO("maria@gmail.com", "senha123");

        UpdateUserDTO userDTO = UpdateUserDTO
                .builder()
                .name("update-user-003")
                .email("update-user-003@gmail.com")
                .cpf("96146532800")
                .birthDay("06/03/1989")
                .cep("dfsdgfhdfgd")
                .build();


        ApplicationException responseBody = testClient
                .put()
                .uri(updateUserUri + "/" + userId)
                .contentType(MediaType.APPLICATION_JSON)
                .headers(GetJwtAuthenticationToken.getHeaderJwtAccessToken(testClient, authenticationDTO.getEmail(), authenticationDTO.getPassword()))
                .bodyValue(userDTO)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(ApplicationException.class)
                .returnResult().getResponseBody();

        Assertions.assertThat(responseBody).isNotNull();
        Assertions.assertThat(responseBody).isExactlyInstanceOf(ApplicationException.class);
        Assertions.assertThat(responseBody.getCode()).isEqualTo(400);
        Assertions.assertThat(responseBody.getStatus()).isEqualTo("Bad Request");
        Assertions.assertThat(responseBody.getMethod()).isEqualTo("PUT");
        Assertions.assertThat(responseBody.getMessage()).isEqualTo(ValidationExceptionConsts.VALIDATION_ERROR);
        Assertions.assertThat(responseBody.getErrors()).hasFieldOrProperty("cep");
    }
}
