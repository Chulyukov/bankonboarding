package ru.alfabank.practice.chulyukovnv.bankonboarding.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import ru.alfabank.practice.chulyukovnv.bankonboarding.model.InfoIncorrectData;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(ApplicationException.class)
    public ResponseEntity<InfoIncorrectData> handleException(ApplicationException e) {
        return buildResponse(e.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(EmptyDeliveryAddressException.class)
    public ResponseEntity<InfoIncorrectData> handleException(EmptyDeliveryAddressException e) {
        return buildResponse(e.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(IncorrectDeliveryAddress.class)
    public ResponseEntity<InfoIncorrectData> handleException(IncorrectDeliveryAddress e) {
        return buildResponse(e.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<InfoIncorrectData> handleValidationException(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(FieldError::getDefaultMessage)
                .orElse("Validation error");
        return buildResponse(message, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<InfoIncorrectData> handleInvalidBodyException(HttpMessageNotReadableException e) {
        return buildResponse("Request body is invalid", HttpStatus.BAD_REQUEST);
    }

    private ResponseEntity<InfoIncorrectData> buildResponse(String message, HttpStatus status) {
        InfoIncorrectData data = new InfoIncorrectData();
        data.setMessage(message);
        return new ResponseEntity<>(data, status);
    }
}
