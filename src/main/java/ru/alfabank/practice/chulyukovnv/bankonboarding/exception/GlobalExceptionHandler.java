package ru.alfabank.practice.chulyukovnv.bankonboarding.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import ru.alfabank.practice.chulyukovnv.bankonboarding.model.InfoIncorrectData;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(ApplicationException.class)
    public ResponseEntity<InfoIncorrectData> handleException(ApplicationException e) {
        InfoIncorrectData data = new InfoIncorrectData();
        data.setMessage(e.getMessage());
        return new ResponseEntity<>(data, HttpStatus.NOT_FOUND);
    }
}
