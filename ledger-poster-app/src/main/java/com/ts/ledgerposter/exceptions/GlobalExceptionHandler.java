package com.ts.ledgerposter.exceptions;

import com.ts.ledgerposter.dto.ErrorResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
@RequiredArgsConstructor
@Slf4j
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @Override
    protected ResponseEntity<Object> handleExceptionInternal(Exception ex, Object body, HttpHeaders headers, HttpStatusCode statusCode, WebRequest request) {
        return super.handleExceptionInternal(ex, body, headers, statusCode, request);
    }

    @ExceptionHandler(Exception.class)
    protected ResponseEntity<Object> handleGenericException(Exception ex, WebRequest request) {
        log.error("Unexpected Exception", ex);
        return handleExceptionInternal(ex, new ErrorResponseDTO("LPS_0100", "Unexpected Exception"), new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR, request);
    }

    @ExceptionHandler(LedgerAccountNotFoundException.class)
    protected ResponseEntity<Object> handleAccountNotFoundException(LedgerAccountNotFoundException ex, WebRequest request) {
        return handleExceptionInternal(ex, new ErrorResponseDTO("LPS_0001", String.format("Account specified not found: %s", ex.getAccountNumber())), new HttpHeaders(), HttpStatus.NOT_FOUND, request);
    }

    @ExceptionHandler(InvalidLedgerPostingDataException.class)
    protected ResponseEntity<Object> handleInvalidLedgerPostingDataException(InvalidLedgerPostingDataException ex, WebRequest request) {
        return handleExceptionInternal(ex, new ErrorResponseDTO("LPS_0002", ex.getMessage()), new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }
}
