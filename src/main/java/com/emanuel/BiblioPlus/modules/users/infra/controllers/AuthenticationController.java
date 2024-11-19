package com.emanuel.BiblioPlus.modules.users.infra.controllers;


import com.emanuel.BiblioPlus.modules.users.domain.dtos.request.AuthenticationDTO;
import com.emanuel.BiblioPlus.modules.users.domain.dtos.request.RefreshTokenRequestDTO;
import com.emanuel.BiblioPlus.modules.users.domain.dtos.response.TokenResponseDTO;
import com.emanuel.BiblioPlus.modules.users.infra.database.entities.UserModel;
import com.emanuel.BiblioPlus.modules.users.services.TokenService;
import com.emanuel.BiblioPlus.shared.exceptions.ApplicationException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "authentication", description = "Resource that provides authentication in API.")
@RestController
@RequestMapping("/auth")
public class AuthenticationController {

    @Autowired
    private TokenService tokenService;

    @Autowired
    private AuthenticationManager authenticationManager;


    @Operation(summary = "Authenticate in API", description = "Resource for authenticate in API.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "authentication created successfully.",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = TokenResponseDTO.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid credentials.",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApplicationException.class))),
                    @ApiResponse(responseCode = "500", description = "Error while creating token",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApplicationException.class))),

            })
    @PostMapping("/login")
    public ResponseEntity<TokenResponseDTO> login(@RequestBody @Valid AuthenticationDTO authenticationDTO){
        var credentials =  new UsernamePasswordAuthenticationToken(authenticationDTO.getEmail(), authenticationDTO.getPassword());
        Authentication authenticate = authenticationManager.authenticate(credentials);

        var tokens = TokenResponseDTO
                .builder()
                .accessToken(tokenService.generateToken((UserModel)authenticate.getPrincipal(), 1))
                .refreshToken(tokenService.generateRefreshToken((UserModel)authenticate.getPrincipal(), 12))
                .build();

        return ResponseEntity.ok(tokens);
    }


    @Operation(summary = "Retrieve new refresh token", description = "Resource for retrieve new refresh token and invalidate old refresh token.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "authentication retrieve successfully.",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = TokenResponseDTO.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid credentials.",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApplicationException.class))),
                    @ApiResponse(responseCode = "401", description = "Error while trying to retrieve a user by email.",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApplicationException.class))),
                    @ApiResponse(responseCode = "500", description = "Error while creating token",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApplicationException.class))),
                    @ApiResponse(responseCode = "401", description = "Refresh token already revoked.",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApplicationException.class))),
                    @ApiResponse(responseCode = "404", description = "Refresh token don't found.",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApplicationException.class))),

            })
    @PostMapping("/refresh-token")
    public ResponseEntity<TokenResponseDTO> revalidateToken(@RequestBody @Valid RefreshTokenRequestDTO refreshTokenRequestDTO) {
        TokenResponseDTO tokens = tokenService.getRefreshToken(refreshTokenRequestDTO);

        return ResponseEntity.ok(tokens);
    }
}
