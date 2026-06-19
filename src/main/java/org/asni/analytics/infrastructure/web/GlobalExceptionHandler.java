package org.asni.analytics.infrastructure.web;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@RestControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(WebClientResponseException.NotFound.class)
    ProblemDetail handleNotFound(WebClientResponseException.NotFound ex) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, "Survey not found");
    }

    @ExceptionHandler(WebClientResponseException.class)
    ProblemDetail handleWebClient(WebClientResponseException ex) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_GATEWAY, "survey-service error: " + ex.getStatusCode());
    }

    @ExceptionHandler(Exception.class)
    ProblemDetail handleGeneric(Exception ex) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error");
    }
}
