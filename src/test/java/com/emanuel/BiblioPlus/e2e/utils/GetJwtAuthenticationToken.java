package com.emanuel.BiblioPlus.e2e.utils;


import com.emanuel.BiblioPlus.modules.users.domain.dtos.request.AuthenticationDTO;
import com.emanuel.BiblioPlus.modules.users.domain.dtos.response.TokenResponseDTO;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;


import java.util.Objects;
import java.util.function.Consumer;

public class GetJwtAuthenticationToken {

    public static Consumer<HttpHeaders> getHeaderJwtAccessToken(WebTestClient client, String email, String password) {
        String accessToken = Objects.requireNonNull(client
                .post()
                .uri("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new AuthenticationDTO(email, password))
                .exchange()
                .expectStatus().isOk()
                .expectBody(TokenResponseDTO.class)
                .returnResult().getResponseBody()).accessToken();

        return headers -> headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
    }
}
