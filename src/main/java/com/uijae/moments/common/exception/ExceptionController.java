package com.uijae.moments.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class ExceptionController {

  @ExceptionHandler(CustomException.class)
  public ResponseEntity<ExceptionResponse> handleCustomException(CustomException e) {
    log.warn("API Exception -> {}", e.getErrorCode());

    return ResponseEntity
        .status(e.getErrorCode().getStatus())
        .body(new ExceptionResponse(e.getMessage(), e.getErrorCode()));
  }

  @Getter
  @ToString
  @AllArgsConstructor
  public static class ExceptionResponse {
    private String message;
    private ErrorCode errorCode;
  }
}
