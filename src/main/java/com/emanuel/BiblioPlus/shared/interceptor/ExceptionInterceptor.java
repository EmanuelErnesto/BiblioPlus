package com.emanuel.BiblioPlus.shared.interceptor;

import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.emanuel.BiblioPlus.shared.exceptions.ApplicationException;
import com.emanuel.BiblioPlus.shared.exceptions.HttpBadRequestException;
import com.emanuel.BiblioPlus.shared.exceptions.HttpNotFoundException;
import com.emanuel.BiblioPlus.shared.exceptions.HttpUnauthorizedException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class ExceptionInterceptor {

    private Logger logger = LoggerFactory.getLogger(ExceptionInterceptor.class);

    @ExceptionHandler(HttpBadRequestException.class)
    public ResponseEntity<ApplicationException> httpApplicationBadRequestException(HttpServletRequest request, RuntimeException ex) {
        logger.error("Bad Request error occurred. Error: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .contentType(MediaType.APPLICATION_JSON)
                .body(new ApplicationException(request, HttpStatus.BAD_REQUEST, ex.getMessage()));
    }

    @ExceptionHandler(HttpNotFoundException.class)
    public ResponseEntity<ApplicationException> httpApplicationNotFoundException(HttpServletRequest request, RuntimeException ex) {
        logger.error("Not Found error occurred. Error: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .contentType(MediaType.APPLICATION_JSON)
                .body(new ApplicationException(request, HttpStatus.NOT_FOUND, ex.getMessage()));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApplicationException> dataIntegrityViolationException(HttpServletRequest request, DataIntegrityViolationException ex) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .contentType(MediaType.APPLICATION_JSON)
                .body(
                        new ApplicationException(request, HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage())
                );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApplicationException> methodArgumentNotValidException(
            HttpServletRequest request,
            MethodArgumentNotValidException ex) {
        logger.error("Validation error occurred. Error: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .contentType(MediaType.APPLICATION_JSON)
                .body(
                        new ApplicationException(
                                request,
                                HttpStatus.BAD_REQUEST,
                                "Validation error",
                                ex.getBindingResult()
                        )
                );
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApplicationException> constraintViolationException(
            HttpServletRequest request,
            ConstraintViolationException ex
    ) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .contentType(MediaType.APPLICATION_JSON)
                .body(
                        new ApplicationException(
                                request,
                                HttpStatus.BAD_REQUEST,
                                ex.getMessage()
                        )
                );
    }

    @ExceptionHandler(JWTCreationException.class)
    public ResponseEntity<ApplicationException> jwtCreationException(
            HttpServletRequest request,
            JWTCreationException ex
    ) {
        log.error("Error while creating jwt token {}", ex.getMessage());

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .contentType(MediaType.APPLICATION_JSON)
                .body(new ApplicationException(
                        request,
                        HttpStatus.INTERNAL_SERVER_ERROR,
                        ex.getMessage()
                ));
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApplicationException> authenticationException(
            HttpServletRequest request,
            AuthenticationException ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .contentType(MediaType.APPLICATION_JSON)
                .body(new ApplicationException(
                        request,
                        HttpStatus.BAD_REQUEST,
                        ex.getMessage()
                ));
    }

    @ExceptionHandler(JWTVerificationException.class)
    public ResponseEntity<ApplicationException> jwtVerificationException(
            HttpServletRequest request,
            JWTVerificationException ex
    ) {
        log.error("Error while validate jwt token {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .contentType(MediaType.APPLICATION_JSON)
                .body(new ApplicationException(
                        request,
                        HttpStatus.UNAUTHORIZED,
                        ex.getMessage()
                ));
    }

    @ExceptionHandler(HttpUnauthorizedException.class)
    public ResponseEntity<ApplicationException> httpUnauthorizedException(
            HttpServletRequest request,
            HttpUnauthorizedException ex
    ) {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .contentType(MediaType.APPLICATION_JSON)
                .body(new ApplicationException(
                        request,
                        HttpStatus.UNAUTHORIZED,
                        ex.getMessage()
                ));
    }
}
