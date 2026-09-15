package ru.alfabank.practice.chulyukovnv.bankonboarding.exception;

public abstract class ApplicationException extends RuntimeException {
    public ApplicationException(String message) {
        super(message);
    }
}
