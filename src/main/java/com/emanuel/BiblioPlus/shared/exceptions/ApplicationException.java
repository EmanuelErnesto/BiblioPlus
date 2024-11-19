package com.emanuel.BiblioPlus.shared.exceptions;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.util.HashMap;
import java.util.Map;


@Getter
@Setter
@ToString
public class ApplicationException {
    private String path;
    private String method;
    private int code;
    private String status;
    private String message;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Map<String, String> errors;

    public ApplicationException(){

    }

    public ApplicationException(HttpServletRequest request, HttpStatus status, String message) {
        this.path = request.getRequestURI();
        this.method = request.getMethod();
        this.code = status.value();
        this.status = status.getReasonPhrase();
        this.message = message;
    }

    public ApplicationException(HttpServletRequest request, HttpStatus status, String message, BindingResult result) {
        this.path = request.getRequestURI();
        this.method = request.getMethod();
        this.code = status.value();
        this.status = status.getReasonPhrase();
        this.message = message;
        stackErrors(result);
    }

    private void stackErrors(BindingResult result) {
        this.errors = new HashMap<>();

        for(FieldError fieldError : result.getFieldErrors()) {
            this.errors.put(fieldError.getField(), fieldError.getDefaultMessage());
        }
    }
}
