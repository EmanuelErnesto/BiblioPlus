package com.emanuel.BiblioPlus.e2e.modules.books;

import com.emanuel.BiblioPlus.e2e.utils.GetJwtAuthenticationToken;
import com.emanuel.BiblioPlus.modules.books.domain.dtos.request.CreateBookDTO;
import com.emanuel.BiblioPlus.modules.books.domain.dtos.response.BookResponseDTO;
import com.emanuel.BiblioPlus.modules.users.domain.dtos.request.AuthenticationDTO;
import com.emanuel.BiblioPlus.shared.consts.BookExceptionConsts;
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
@Sql(scripts = "/sql/books/insert-books.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "/sql/books/clear-books-table.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public class UpdateBookE2ETest {

    @Autowired
    private WebTestClient testClient;

    private String updateBookUri = "/books";

    @Test
    void updateBookWithValidDataReturnStatus200(){
        AuthenticationDTO authenticationDTO = new AuthenticationDTO("maria@gmail.com", "senha123");
        String bookId = "d8f426fe-71f9-46ed-8d0b-b39a4f88db42";

        CreateBookDTO createBookDTO = CreateBookDTO
                .builder()
                .name("update-book-001")
                .releaseDate("02/05/2010")
                .description("Description of the book 001 that will be updated")
                .quantityInStock(35L)
                .publisher("publisher-book-001")
                .genre("action")
                .build();

        BookResponseDTO responseBody = testClient
                .put()
                .uri(updateBookUri + "/" + bookId)
                .contentType(MediaType.APPLICATION_JSON)
                .headers(GetJwtAuthenticationToken.getHeaderJwtAccessToken(testClient, authenticationDTO.getEmail(), authenticationDTO.getPassword()))
                .bodyValue(createBookDTO)
                .exchange()
                .expectStatus().isOk()
                .expectBody(BookResponseDTO.class)
                .returnResult().getResponseBody();

        Assertions.assertThat(responseBody).isNotNull();
        Assertions.assertThat(responseBody).isExactlyInstanceOf(BookResponseDTO.class);
        Assertions.assertThat(responseBody).hasFieldOrProperty("id");
        Assertions.assertThat(responseBody.getName()).isEqualTo(createBookDTO.getName());
        Assertions.assertThat(responseBody.getDescription()).isEqualTo(createBookDTO.getDescription());
        Assertions.assertThat(responseBody.getQuantityInStock()).isEqualTo(createBookDTO.getQuantityInStock());
        Assertions.assertThat(responseBody.getPublisher()).isEqualTo(createBookDTO.getPublisher());
        Assertions.assertThat(responseBody.getGenre()).isEqualTo(createBookDTO.getGenre());
    }

    @Test
    void updateBookWithIdThatNotExistsInDatabaseReturnStatus404() {
        String bookId = "ea50d691-3f3a-4b5d-aba9-45fda65547c5";
        AuthenticationDTO authenticationDTO = new AuthenticationDTO("maria@gmail.com", "senha123");

        CreateBookDTO createBookDTO = CreateBookDTO
                .builder()
                .name("update-book-002")
                .releaseDate("02/05/2010")
                .description("Description of the book 002 that will be updated")
                .quantityInStock(35L)
                .publisher("publisher-book-002")
                .genre("action")
                .build();

        ApplicationException responseBody = testClient
                .put()
                .uri(updateBookUri + "/" + bookId)
                .contentType(MediaType.APPLICATION_JSON)
                .headers(GetJwtAuthenticationToken.getHeaderJwtAccessToken(testClient, authenticationDTO.getEmail(), authenticationDTO.getPassword()))
                .bodyValue(createBookDTO)                .exchange()
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
    void updateBookWithNameAndPublisherOfABookThatAlreadyExistsReturnStatus400(){
        AuthenticationDTO authenticationDTO = new AuthenticationDTO("maria@gmail.com", "senha123");
        String bookId = "d8f426fe-71f9-46ed-8d0b-b39a4f88db42";


        CreateBookDTO createBookDTO = CreateBookDTO
                .builder()
                .name("Misterios do Passado")
                .releaseDate("02/05/2010")
                .description("Description of the book 001 that will be created")
                .quantityInStock(35L)
                .publisher("Editora Arqueologia")
                .genre("action")
                .build();


        ApplicationException responseBody = testClient
                .put()
                .uri(updateBookUri + "/" + bookId)
                .contentType(MediaType.APPLICATION_JSON)
                .headers(GetJwtAuthenticationToken.getHeaderJwtAccessToken(testClient, authenticationDTO.getEmail(), authenticationDTO.getPassword()))
                .bodyValue(createBookDTO)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(ApplicationException.class)
                .returnResult().getResponseBody();

        Assertions.assertThat(responseBody).isNotNull();
        Assertions.assertThat(responseBody).isExactlyInstanceOf(ApplicationException.class);
        Assertions.assertThat(responseBody.getCode()).isEqualTo(400);
        Assertions.assertThat(responseBody.getStatus()).isEqualTo("Bad Request");
        Assertions.assertThat(responseBody.getMethod()).isEqualTo("PUT");
        Assertions.assertThat(responseBody.getMessage()).isEqualTo(BookExceptionConsts.BOOK_ALREADY_EXISTS);
    }

    @Test
    void updateBookWithUserThatHaveClientRoleReturnStatus403(){
        AuthenticationDTO authenticationDTO = new AuthenticationDTO("joao@gmail.com", "senha123");
        String bookId = "b1e72f2f-d5c8-44bc-a7fe-eeb62d0cf54";

        CreateBookDTO createBookDTO = CreateBookDTO
                .builder()
                .name("update-book-002")
                .releaseDate("02/05/2010")
                .description("Description of the book 001 that will be created")
                .quantityInStock(35L)
                .publisher("publisher-book-001")
                .genre("action")
                .build();

        testClient
                .put()
                .uri(updateBookUri + "/" + bookId)
                .contentType(MediaType.APPLICATION_JSON)
                .headers(GetJwtAuthenticationToken.getHeaderJwtAccessToken(testClient, authenticationDTO.getEmail(), authenticationDTO.getPassword()))
                .bodyValue(createBookDTO)
                .exchange()
                .expectStatus().isForbidden()
                .expectBody().isEmpty();
    }

    @Test
    void updateBookWithEmptyResponseBodyValuesReturnStatus400(){
        AuthenticationDTO authenticationDTO = new AuthenticationDTO("maria@gmail.com", "senha123");
        String bookId = "d8f426fe-71f9-46ed-8d0b-b39a4f88db42";

        CreateBookDTO createBookDTO = CreateBookDTO
                .builder()
                .name("")
                .releaseDate("")
                .description("")
                .quantityInStock(null)
                .publisher("")
                .genre("")
                .build();


        ApplicationException responseBody = testClient
                .put()
                .uri(updateBookUri + "/" + bookId)
                .contentType(MediaType.APPLICATION_JSON)
                .headers(GetJwtAuthenticationToken.getHeaderJwtAccessToken(testClient, authenticationDTO.getEmail(), authenticationDTO.getPassword()))
                .bodyValue(createBookDTO)
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
        Assertions.assertThat(responseBody.getErrors()).hasFieldOrProperty("releaseDate");
        Assertions.assertThat(responseBody.getErrors()).hasFieldOrProperty("description");
        Assertions.assertThat(responseBody.getErrors()).hasFieldOrProperty("quantityInStock");
        Assertions.assertThat(responseBody.getErrors()).hasFieldOrProperty("publisher");
        Assertions.assertThat(responseBody.getErrors()).hasFieldOrProperty("genre");
    }


    @Test
    void updateBookWithEmptyNameValuesReturnStatus400(){
        AuthenticationDTO authenticationDTO = new AuthenticationDTO("maria@gmail.com", "senha123");
        String bookId = "d8f426fe-71f9-46ed-8d0b-b39a4f88db42";

        CreateBookDTO createBookDTO = CreateBookDTO
                .builder()
                .name("")
                .releaseDate("02/05/2010")
                .description("Description of the book 001 that will be created")
                .quantityInStock(35L)
                .publisher("publisher-book-001")
                .genre("action")
                .build();


        ApplicationException responseBody = testClient
                .put()
                .uri(updateBookUri + "/" + bookId)
                .contentType(MediaType.APPLICATION_JSON)
                .headers(GetJwtAuthenticationToken.getHeaderJwtAccessToken(testClient, authenticationDTO.getEmail(), authenticationDTO.getPassword()))
                .bodyValue(createBookDTO)
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
    void updateBookWithNameWithLessThan5CharactersFormatValuesReturnStatus400(){
        AuthenticationDTO authenticationDTO = new AuthenticationDTO("maria@gmail.com", "senha123");
        String bookId = "d8f426fe-71f9-46ed-8d0b-b39a4f88db42";

        CreateBookDTO createBookDTO = CreateBookDTO
                .builder()
                .name("rand")
                .releaseDate("02/05/2010")
                .description("Description of the book 001 that will be created")
                .quantityInStock(35L)
                .publisher("publisher-book-001")
                .genre("action")
                .build();


        ApplicationException responseBody = testClient
                .put()
                .uri(updateBookUri + "/" + bookId)
                .contentType(MediaType.APPLICATION_JSON)
                .headers(GetJwtAuthenticationToken.getHeaderJwtAccessToken(testClient, authenticationDTO.getEmail(), authenticationDTO.getPassword()))
                .bodyValue(createBookDTO)
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
    void updateBookWithEmptyReleaseDateValuesReturnStatus400(){
        AuthenticationDTO authenticationDTO = new AuthenticationDTO("maria@gmail.com", "senha123");
        String bookId = "d8f426fe-71f9-46ed-8d0b-b39a4f88db42";

        CreateBookDTO createBookDTO = CreateBookDTO
                .builder()
                .name("create-book-02")
                .releaseDate("")
                .description("Description of the book 001 that will be created")
                .quantityInStock(35L)
                .publisher("publisher-book-001")
                .genre("action")
                .build();


        ApplicationException responseBody = testClient
                .put()
                .uri(updateBookUri + "/" + bookId)
                .contentType(MediaType.APPLICATION_JSON)
                .headers(GetJwtAuthenticationToken.getHeaderJwtAccessToken(testClient, authenticationDTO.getEmail(), authenticationDTO.getPassword()))
                .bodyValue(createBookDTO)
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
        Assertions.assertThat(responseBody.getErrors()).hasFieldOrProperty("releaseDate");
    }

    @Test
    void updateBookWithReleaseDateInInvalidFormatValuesReturnStatus400(){
        AuthenticationDTO authenticationDTO = new AuthenticationDTO("maria@gmail.com", "senha123");
        String bookId = "d8f426fe-71f9-46ed-8d0b-b39a4f88db42";

        CreateBookDTO createBookDTO = CreateBookDTO
                .builder()
                .name("create-book-02")
                .releaseDate("dsadasdslkdansak")
                .description("Description of the book 001 that will be created")
                .quantityInStock(35L)
                .publisher("publisher-book-001")
                .genre("action")
                .build();


        ApplicationException responseBody = testClient
                .put()
                .uri(updateBookUri + "/" + bookId)
                .contentType(MediaType.APPLICATION_JSON)
                .headers(GetJwtAuthenticationToken.getHeaderJwtAccessToken(testClient, authenticationDTO.getEmail(), authenticationDTO.getPassword()))
                .bodyValue(createBookDTO)
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
        Assertions.assertThat(responseBody.getErrors()).hasFieldOrProperty("releaseDate");
    }

    @Test
    void updateBookWithEmptyDescriptionValueReturnStatus400(){
        AuthenticationDTO authenticationDTO = new AuthenticationDTO("maria@gmail.com", "senha123");
        String bookId = "d8f426fe-71f9-46ed-8d0b-b39a4f88db42";

        CreateBookDTO createBookDTO = CreateBookDTO
                .builder()
                .name("update-book")
                .releaseDate("02/05/2010")
                .description("")
                .quantityInStock(35L)
                .publisher("publisher-book-001")
                .genre("action")
                .build();

        ApplicationException responseBody = testClient
                .put()
                .uri(updateBookUri + "/" + bookId)
                .contentType(MediaType.APPLICATION_JSON)
                .headers(GetJwtAuthenticationToken.getHeaderJwtAccessToken(testClient, authenticationDTO.getEmail(), authenticationDTO.getPassword()))
                .bodyValue(createBookDTO)
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
        Assertions.assertThat(responseBody.getErrors()).hasFieldOrProperty("description");
    }

    @Test
    void updateBookWitEmptyDescriptionValueReturnStatus400(){
        AuthenticationDTO authenticationDTO = new AuthenticationDTO("maria@gmail.com", "senha123");
        String bookId = "d8f426fe-71f9-46ed-8d0b-b39a4f88db42";

        CreateBookDTO createBookDTO = CreateBookDTO
                .builder()
                .name("update-book")
                .releaseDate("02/05/2010")
                .description("")
                .quantityInStock(35L)
                .publisher("publisher-book-001")
                .genre("action")
                .build();


        ApplicationException responseBody = testClient
                .put()
                .uri(updateBookUri + "/" + bookId)
                .contentType(MediaType.APPLICATION_JSON)
                .headers(GetJwtAuthenticationToken.getHeaderJwtAccessToken(testClient, authenticationDTO.getEmail(), authenticationDTO.getPassword()))
                .bodyValue(createBookDTO)
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
        Assertions.assertThat(responseBody.getErrors()).hasFieldOrProperty("description");
    }

    @Test
    void updateBookWithEmptyPublisherValueReturnStatus400(){
        AuthenticationDTO authenticationDTO = new AuthenticationDTO("maria@gmail.com", "senha123");
        String bookId = "d8f426fe-71f9-46ed-8d0b-b39a4f88db42";

        CreateBookDTO createBookDTO = CreateBookDTO
                .builder()
                .name("update-book")
                .releaseDate("02/05/2010")
                .description("This is the description of the book 3")
                .quantityInStock(35L)
                .publisher("")
                .genre("action")
                .build();

        ApplicationException responseBody = testClient
                .put()
                .uri(updateBookUri + "/" + bookId)
                .contentType(MediaType.APPLICATION_JSON)
                .headers(GetJwtAuthenticationToken.getHeaderJwtAccessToken(testClient, authenticationDTO.getEmail(), authenticationDTO.getPassword()))
                .bodyValue(createBookDTO)
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
        Assertions.assertThat(responseBody.getErrors()).hasFieldOrProperty("publisher");
    }

    @Test
    void updateBookWitDescriptionThatHaveLessThan10CharactersValueReturnStatus400(){
        AuthenticationDTO authenticationDTO = new AuthenticationDTO("maria@gmail.com", "senha123");
        String bookId = "d8f426fe-71f9-46ed-8d0b-b39a4f88db42";
        CreateBookDTO createBookDTO = CreateBookDTO
                .builder()
                .name("update-book-003")
                .releaseDate("02/05/2010")
                .description("invalid")
                .quantityInStock(35L)
                .publisher("publisher-book-001")
                .genre("action")
                .build();

        ApplicationException responseBody = testClient
                .put()
                .uri(updateBookUri + "/" + bookId)
                .contentType(MediaType.APPLICATION_JSON)
                .headers(GetJwtAuthenticationToken.getHeaderJwtAccessToken(testClient, authenticationDTO.getEmail(), authenticationDTO.getPassword()))
                .bodyValue(createBookDTO)
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
        Assertions.assertThat(responseBody.getErrors()).hasFieldOrProperty("description");
    }

    @Test
    void updateBookWithEmptyGenreValueReturnStatus400(){
        AuthenticationDTO authenticationDTO = new AuthenticationDTO("maria@gmail.com", "senha123");
        String bookId = "d8f426fe-71f9-46ed-8d0b-b39a4f88db42";
        CreateBookDTO createBookDTO = CreateBookDTO
                .builder()
                .name("update-book-003")
                .releaseDate("02/05/2010")
                .description("This is the description of the book 3")
                .quantityInStock(35L)
                .publisher("pb-book-003")
                .genre("")
                .build();

        ApplicationException responseBody = testClient
                .put()
                .uri(updateBookUri + "/" + bookId)
                .contentType(MediaType.APPLICATION_JSON)
                .headers(GetJwtAuthenticationToken.getHeaderJwtAccessToken(testClient, authenticationDTO.getEmail(), authenticationDTO.getPassword()))
                .bodyValue(createBookDTO)
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
        Assertions.assertThat(responseBody.getErrors()).hasFieldOrProperty("genre");
    }

    @Test
    void updateBookWithGenreThatHaveLessThan3CharactersValueReturnStatus400(){
        AuthenticationDTO authenticationDTO = new AuthenticationDTO("maria@gmail.com", "senha123");
        String bookId = "d8f426fe-71f9-46ed-8d0b-b39a4f88db42";
        CreateBookDTO createBookDTO = CreateBookDTO
                .builder()
                .name("update-book")
                .releaseDate("02/05/2010")
                .description("This is the description of the book 3")
                .quantityInStock(35L)
                .publisher("pb-book-003")
                .genre("ac")
                .build();

        ApplicationException responseBody = testClient
                .put()
                .uri(updateBookUri + "/" + bookId)
                .contentType(MediaType.APPLICATION_JSON)
                .headers(GetJwtAuthenticationToken.getHeaderJwtAccessToken(testClient, authenticationDTO.getEmail(), authenticationDTO.getPassword()))
                .bodyValue(createBookDTO)
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
        Assertions.assertThat(responseBody.getErrors()).hasFieldOrProperty("genre");
    }


}
