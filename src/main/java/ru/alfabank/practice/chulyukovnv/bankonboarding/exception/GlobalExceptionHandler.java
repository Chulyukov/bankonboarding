package ru.alfabank.practice.chulyukovnv.bankonboarding.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler
    public ResponseEntity<InfoIncorrectData> handleException(NoSuchProductIdException e) {
        InfoIncorrectData data = new InfoIncorrectData();
        data.setMessage(e.getMessage());
        return new ResponseEntity<>(data, NOT_FOUND);
    }
}
