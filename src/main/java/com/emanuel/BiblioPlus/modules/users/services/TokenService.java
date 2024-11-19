package com.emanuel.BiblioPlus.modules.users.services;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.Claim;
import com.emanuel.BiblioPlus.modules.users.domain.dtos.request.RefreshTokenRequestDTO;
import com.emanuel.BiblioPlus.modules.users.domain.dtos.response.TokenResponseDTO;
import com.emanuel.BiblioPlus.modules.users.domain.dtos.response.RefreshTokenResponseDTO;
import com.emanuel.BiblioPlus.modules.users.infra.database.entities.TokenModel;
import com.emanuel.BiblioPlus.modules.users.infra.database.entities.UserModel;
import com.emanuel.BiblioPlus.modules.users.infra.database.repositories.TokenRepository;
import com.emanuel.BiblioPlus.modules.users.infra.database.repositories.UserRepository;
import com.emanuel.BiblioPlus.shared.consts.AuthenticationExceptionConsts;
import com.emanuel.BiblioPlus.shared.exceptions.HttpNotFoundException;
import com.emanuel.BiblioPlus.shared.exceptions.HttpUnauthorizedException;
import io.github.cdimascio.dotenv.Dotenv;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Optional;

@Service
public class TokenService {

    private final Logger logger = LoggerFactory.getLogger(TokenService.class);

    private final Dotenv dotenv;

    private final String secret;

    @Value("${api.security.token.access-token.expiration-hour}")
    private int expirationHour;

    @Value("${api.security.token.refresh-token.expiration-hour}")
    private int refreshTokenExpiration;

    private final UserRepository userRepository;

    private final TokenRepository tokenRepository;


    public TokenService(Dotenv dotenv, UserRepository userRepository, TokenRepository tokenRepository){
        this.dotenv = dotenv;
        this.userRepository = userRepository;
        this.tokenRepository = tokenRepository;
        this.secret = dotenv.get("API_SECRET");
    }

    public String generateToken(UserModel user, Integer expiration) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            return JWT.create()
                    .withIssuer("BiblioPlus-App")
                    .withSubject(user.getEmail())
                    .withClaim("type", "access")
                    .withExpiresAt(getExpirationInst(expiration))
                    .sign(algorithm);
        }
        catch (JWTCreationException e){
            throw new JWTCreationException(String.format("Token generation failed. Reason: %s", e.getMessage()), e.getCause());
        }
    }

    public String generateRefreshToken(UserModel user, Integer expiration) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            String token = JWT.create()
                    .withIssuer("BiblioPlus-App")
                    .withSubject(user.getEmail())
                    .withClaim("type", "refresh")
                    .withExpiresAt(getExpirationInst(expiration))
                    .sign(algorithm);

            saveRefreshToken(user, token);

            return token;
        } catch (JWTCreationException e){
            throw new JWTCreationException(String.format("Refresh token generation failed. Reason: %s", e.getMessage()), e.getCause());

        }
    }

    public String validateToken(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            return JWT.require(algorithm)
                    .withIssuer("BiblioPlus-App")
                    .build()
                    .verify(token)
                    .getSubject();
        } catch (JWTVerificationException e){
            throw new JWTVerificationException(String.format("Token validation failed. Reason: %s", e.getMessage()), e.getCause());
        }
    }


    public Instant getExpirationInst(Integer expiration){
        return LocalDateTime.now().plusHours(expiration).toInstant(ZoneOffset.of("-03:00"));
    }

    public Claim getTokenType(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            return JWT.require(algorithm)
                    .withIssuer("BiblioPlus-App")
                    .build()
                    .verify(token)
                    .getClaim("type");
        } catch (JWTVerificationException e){
            throw new JWTVerificationException(String.format("Token type retrieval failed. Reason: %s", e.getMessage()), e.getCause());
        }
    }

    public void validateRefreshToken(RefreshTokenRequestDTO tokenResponseDTO) {

        Claim tokenTypeClaim = getTokenType(tokenResponseDTO.getRefreshToken());

        boolean result = "refresh".equals(tokenTypeClaim.asString());

        if(!result) throw new HttpUnauthorizedException(AuthenticationExceptionConsts.INVALID_REFRESH_TOKEN);
    }

    public void validateAccessToken(String token) {
        Claim tokenTypeClaim = getTokenType(token);

        boolean result = "access".equals(tokenTypeClaim.asString());

        if(!result) throw new HttpUnauthorizedException(AuthenticationExceptionConsts.INVALID_ACCESS_TOKEN);
    }


    public TokenResponseDTO getRefreshToken(RefreshTokenRequestDTO refreshToken) {

        validateRefreshToken(refreshToken);

        String email = validateToken(refreshToken.getRefreshToken());

        UserDetails userDetails = userRepository.findByEmail(email);

        if(userDetails == null) {
            throw new HttpUnauthorizedException(AuthenticationExceptionConsts.INVALID_USER);
        }

        var authenticate = new UsernamePasswordAuthenticationToken((UserModel)userDetails, null, userDetails.getAuthorities());

        SecurityContextHolder.getContext().setAuthentication(authenticate);

        invalidateRefreshToken(refreshToken.getRefreshToken(), (UserModel)authenticate.getPrincipal());


        String newAccessToken = generateToken((UserModel) authenticate.getPrincipal(), expirationHour);
        String newRefreshToken = generateRefreshToken((UserModel) authenticate.getPrincipal(), refreshTokenExpiration);

        saveRefreshToken((UserModel) authenticate.getPrincipal(), newRefreshToken);

        return TokenResponseDTO
                .builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .build();

    }

    private void invalidateRefreshToken(String refreshToken, UserModel user) {
        Optional<TokenModel> existingToken = tokenRepository.findByTokenAndUser(refreshToken, user);

        if (existingToken.isEmpty()) throw new HttpNotFoundException(AuthenticationExceptionConsts.REFRESH_TOKEN_NOT_FOUND);

        TokenModel token = existingToken.get();
        validateIfTokenIsAlreadyRevoked(token);

        token.setRevoked(true);
        tokenRepository.save(token);
    }

    private void validateIfTokenIsAlreadyRevoked (TokenModel token) {
        if(token.isRevoked()) throw new HttpUnauthorizedException(AuthenticationExceptionConsts.REFRESH_TOKEN_REVOKED);
    }

    private void saveRefreshToken(UserModel user, String newRefreshToken) {
        TokenModel newToken = new TokenModel();
        newToken.setUser(user);
        newToken.setToken(newRefreshToken);
        newToken.setExpiresAt(LocalDateTime.now().plusHours(refreshTokenExpiration));
        tokenRepository.save(newToken);
    }
}
