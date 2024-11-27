package com.emanuel.BiblioPlus.e2e.modules.users;

import com.emanuel.BiblioPlus.modules.users.domain.dtos.request.CreateUserDTO;
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
public class CreateUserE2ETest {

    @Autowired
    private WebTestClient testClient;

    private String createUserUrl = "/users";


    @Test
    void createUserWithValidDataReturnStatus201 () {
        CreateUserDTO userDTO = CreateUserDTO
                .builder()
                .name("user-001")
                .email("user-001@gmail.com")
                .password("123456")
                .cpf("84813204473")
                .birthDay("01/05/1990")
                .cep("01001000")
                .build();

        UserResponseDTO responseBody = testClient
                .post()
                .uri(createUserUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(userDTO)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(UserResponseDTO.class)
                .returnResult().getResponseBody();

        Assertions.assertThat(responseBody).isNotNull();
        Assertions.assertThat(responseBody).isExactlyInstanceOf(UserResponseDTO.class);
        Assertions.assertThat(responseBody.getName()).isEqualTo(userDTO.getName());
        Assertions.assertThat(responseBody.getCep()).isEqualTo("01001000");
        Assertions.assertThat(responseBody.getPatio()).isEqualTo("Praça da Sé");
        Assertions.assertThat(responseBody.getComplement()).isEqualTo("lado ímpar");
        Assertions.assertThat(responseBody.getNeighborhood()).isEqualTo("Sé");
        Assertions.assertThat(responseBody.getLocality()).isEqualTo("São Paulo");
        Assertions.assertThat(responseBody.getUf()).isEqualTo("SP");
    }

    @Test
    void createUserWithEmailThatAlreadyExistsInDBReturnsStatus400() {
        CreateUserDTO userDTO = CreateUserDTO
                .builder()
                .name("user-002")
                .email("joao@gmail.com")
                .password("123456")
                .cpf("84813204473")
                .birthDay("01/05/1990")
                .cep("55850000")
                .build();

        ApplicationException responseBody = testClient
                .post()
                .uri(createUserUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(userDTO)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(ApplicationException.class)
                .returnResult().getResponseBody();

        Assertions.assertThat(responseBody).isNotNull();
        Assertions.assertThat(responseBody).isExactlyInstanceOf(ApplicationException.class);
        Assertions.assertThat(responseBody.getCode()).isEqualTo(400);
        Assertions.assertThat(responseBody.getStatus()).isEqualTo("Bad Request");
        Assertions.assertThat(responseBody.getMethod()).isEqualTo("POST");
        Assertions.assertThat(responseBody.getMessage()).isEqualTo(UserExceptionConsts.EMAIL_ALREADY_EXISTS);
    }

    @Test
    void createUserWithCpfThatAlreadyExistsInDBReturnsStatus400() {
        CreateUserDTO userDTO = CreateUserDTO
                .builder()
                .name("user-002")
                .email("user-002@gmail.com")
                .password("123456")
                .cpf("63928498150")
                .birthDay("01/05/1990")
                .cep("55850000")
                .build();

        ApplicationException responseBody = testClient
                .post()
                .uri(createUserUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(userDTO)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(ApplicationException.class)
                .returnResult().getResponseBody();

        Assertions.assertThat(responseBody).isNotNull();
        Assertions.assertThat(responseBody).isExactlyInstanceOf(ApplicationException.class);
        Assertions.assertThat(responseBody.getCode()).isEqualTo(400);
        Assertions.assertThat(responseBody.getStatus()).isEqualTo("Bad Request");
        Assertions.assertThat(responseBody.getMethod()).isEqualTo("POST");
        Assertions.assertThat(responseBody.getMessage()).isEqualTo(UserExceptionConsts.CPF_ALREADY_EXISTS);
    }

    @Test
    void createUserWithEmptyValuesInBodyReturnsStatus400() {
        CreateUserDTO userDTO = CreateUserDTO
                .builder()
                .name("")
                .email("")
                .password("")
                .cpf("")
                .birthDay("")
                .cep("")
                .build();

        ApplicationException responseBody = testClient
                .post()
                .uri(createUserUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(userDTO)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(ApplicationException.class)
                .returnResult().getResponseBody();

        Assertions.assertThat(responseBody).isNotNull();
        Assertions.assertThat(responseBody).isExactlyInstanceOf(ApplicationException.class);
        Assertions.assertThat(responseBody.getCode()).isEqualTo(400);
        Assertions.assertThat(responseBody.getStatus()).isEqualTo("Bad Request");
        Assertions.assertThat(responseBody.getMethod()).isEqualTo("POST");
        Assertions.assertThat(responseBody.getMessage()).isEqualTo(ValidationExceptionConsts.VALIDATION_ERROR);
        Assertions.assertThat(responseBody.getErrors()).hasFieldOrProperty("name");
        Assertions.assertThat(responseBody.getErrors()).hasFieldOrProperty("email");
        Assertions.assertThat(responseBody.getErrors()).hasFieldOrProperty("password");
        Assertions.assertThat(responseBody.getErrors()).hasFieldOrProperty("cpf");
        Assertions.assertThat(responseBody.getErrors()).hasFieldOrProperty("birthDay");
        Assertions.assertThat(responseBody.getErrors()).hasFieldOrProperty("cep");
    }

    @Test
    void createUserWithEmptyNameReturnsStatus400() {
        CreateUserDTO userDTO = CreateUserDTO
                .builder()
                .name("")
                .email("user-003@gmail.com")
                .password("123456")
                .cpf("38322574622")
                .birthDay("01/05/1990")
                .cep("55850000")
                .build();

        ApplicationException responseBody = testClient
                .post()
                .uri(createUserUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(userDTO)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(ApplicationException.class)
                .returnResult().getResponseBody();

        Assertions.assertThat(responseBody).isNotNull();
        Assertions.assertThat(responseBody).isExactlyInstanceOf(ApplicationException.class);
        Assertions.assertThat(responseBody.getCode()).isEqualTo(400);
        Assertions.assertThat(responseBody.getStatus()).isEqualTo("Bad Request");
        Assertions.assertThat(responseBody.getMethod()).isEqualTo("POST");
        Assertions.assertThat(responseBody.getMessage()).isEqualTo(ValidationExceptionConsts.VALIDATION_ERROR);
        Assertions.assertThat(responseBody.getErrors()).hasFieldOrProperty("name");
    }

    @Test
    void createUserWithEmptyEmailReturnsStatus400() {
        CreateUserDTO userDTO = CreateUserDTO
                .builder()
                .name("user-004")
                .email("")
                .password("123456")
                .cpf("38322574622")
                .birthDay("01/05/1990")
                .cep("55850000")
                .build();

        ApplicationException responseBody = testClient
                .post()
                .uri(createUserUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(userDTO)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(ApplicationException.class)
                .returnResult().getResponseBody();

        Assertions.assertThat(responseBody).isNotNull();
        Assertions.assertThat(responseBody).isExactlyInstanceOf(ApplicationException.class);
        Assertions.assertThat(responseBody.getCode()).isEqualTo(400);
        Assertions.assertThat(responseBody.getStatus()).isEqualTo("Bad Request");
        Assertions.assertThat(responseBody.getMethod()).isEqualTo("POST");
        Assertions.assertThat(responseBody.getMessage()).isEqualTo(ValidationExceptionConsts.VALIDATION_ERROR);
        Assertions.assertThat(responseBody.getErrors()).hasFieldOrProperty("email");
    }

    @Test
    void createUserWithEmptyPasswordReturnsStatus400() {
        CreateUserDTO userDTO = CreateUserDTO
                .builder()
                .name("user-004")
                .email("user-004@gmail.com")
                .password("")
                .cpf("38322574622")
                .birthDay("01/05/1990")
                .cep("55850000")
                .build();

        ApplicationException responseBody = testClient
                .post()
                .uri(createUserUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(userDTO)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(ApplicationException.class)
                .returnResult().getResponseBody();

        Assertions.assertThat(responseBody).isNotNull();
        Assertions.assertThat(responseBody).isExactlyInstanceOf(ApplicationException.class);
        Assertions.assertThat(responseBody.getCode()).isEqualTo(400);
        Assertions.assertThat(responseBody.getStatus()).isEqualTo("Bad Request");
        Assertions.assertThat(responseBody.getMethod()).isEqualTo("POST");
        Assertions.assertThat(responseBody.getMessage()).isEqualTo(ValidationExceptionConsts.VALIDATION_ERROR);
        Assertions.assertThat(responseBody.getErrors()).hasFieldOrProperty("password");
    }

    @Test
    void createUserWithEmptyCpfReturnsStatus400() {
        CreateUserDTO userDTO = CreateUserDTO
                .builder()
                .name("user-004")
                .email("user-004@gmail.com")
                .password("123456")
                .cpf("")
                .birthDay("01/05/1990")
                .cep("55850000")
                .build();

        ApplicationException responseBody = testClient
                .post()
                .uri(createUserUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(userDTO)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(ApplicationException.class)
                .returnResult().getResponseBody();

        Assertions.assertThat(responseBody).isNotNull();
        Assertions.assertThat(responseBody).isExactlyInstanceOf(ApplicationException.class);
        Assertions.assertThat(responseBody.getCode()).isEqualTo(400);
        Assertions.assertThat(responseBody.getStatus()).isEqualTo("Bad Request");
        Assertions.assertThat(responseBody.getMethod()).isEqualTo("POST");
        Assertions.assertThat(responseBody.getMessage()).isEqualTo(ValidationExceptionConsts.VALIDATION_ERROR);
        Assertions.assertThat(responseBody.getErrors()).hasFieldOrProperty("cpf");
    }

    @Test
    void createUserWithEmptyBirthDayReturnsStatus400() {
        CreateUserDTO userDTO = CreateUserDTO
                .builder()
                .name("user-004")
                .email("user-004@gmail.com")
                .password("123456")
                .cpf("48832855615")
                .birthDay("")
                .cep("55850000")
                .build();

        ApplicationException responseBody = testClient
                .post()
                .uri(createUserUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(userDTO)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(ApplicationException.class)
                .returnResult().getResponseBody();

        Assertions.assertThat(responseBody).isNotNull();
        Assertions.assertThat(responseBody).isExactlyInstanceOf(ApplicationException.class);
        Assertions.assertThat(responseBody.getCode()).isEqualTo(400);
        Assertions.assertThat(responseBody.getStatus()).isEqualTo("Bad Request");
        Assertions.assertThat(responseBody.getMethod()).isEqualTo("POST");
        Assertions.assertThat(responseBody.getMessage()).isEqualTo(ValidationExceptionConsts.VALIDATION_ERROR);
        Assertions.assertThat(responseBody.getErrors()).hasFieldOrProperty("birthDay");
    }

    @Test
    void createUserWithEmptyCepReturnsStatus400() {
        CreateUserDTO userDTO = CreateUserDTO
                .builder()
                .name("user-004")
                .email("user-004@gmail.com")
                .password("123456")
                .cpf("48832855615")
                .birthDay("02/07/2000")
                .cep("")
                .build();

        ApplicationException responseBody = testClient
                .post()
                .uri(createUserUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(userDTO)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(ApplicationException.class)
                .returnResult().getResponseBody();

        Assertions.assertThat(responseBody).isNotNull();
        Assertions.assertThat(responseBody).isExactlyInstanceOf(ApplicationException.class);
        Assertions.assertThat(responseBody.getCode()).isEqualTo(400);
        Assertions.assertThat(responseBody.getStatus()).isEqualTo("Bad Request");
        Assertions.assertThat(responseBody.getMethod()).isEqualTo("POST");
        Assertions.assertThat(responseBody.getMessage()).isEqualTo(ValidationExceptionConsts.VALIDATION_ERROR);
        Assertions.assertThat(responseBody.getErrors()).hasFieldOrProperty("cep");
    }


}
