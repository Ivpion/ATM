package com.atm.controller;

import com.atm.exception.AuthenticationException;
import com.atm.exception.ValidationException;
import com.atm.security.JwtTokenRepository;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.web.csrf.InvalidCsrfTokenException;
import org.springframework.security.web.csrf.MissingCsrfTokenException;
import org.springframework.security.web.util.UrlUtils;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@ControllerAdvice
@AllArgsConstructor
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {


    private final JwtTokenRepository tokenRepository;


    @ExceptionHandler({AuthenticationException.class, MissingCsrfTokenException.class, InvalidCsrfTokenException.class, InvalidDataAccessApiUsageException.class})
    public ResponseEntity<ErrorInfo> handleAuthenticationException(Exception ex, HttpServletRequest request, HttpServletResponse response) {
        this.tokenRepository.clearToken(response);
        return ResponseEntity.status(HttpServletResponse.SC_UNAUTHORIZED).body(new ErrorInfo(UrlUtils.buildFullRequestUrl(request), ex.getMessage()));
    }

    @ExceptionHandler({ValidationException.class})
    public ResponseEntity<ErrorInfo> handleValidationException(ValidationException ex, HttpServletRequest request, HttpServletResponse response){
        this.tokenRepository.clearToken(response);
        return ResponseEntity.status(422).body(new ErrorInfo(UrlUtils.buildFullRequestUrl(request), ex.getMessage()));
    }

    @Getter
    public static class ErrorInfo {
        private final String url;
        private final String info;

        ErrorInfo(String url, String info) {
            this.url = url;
            this.info = info;
        }
    }
}
