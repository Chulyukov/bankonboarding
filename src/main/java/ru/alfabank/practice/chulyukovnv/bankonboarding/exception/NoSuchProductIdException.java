package ru.alfabank.practice.chulyukovnv.bankonboarding.exception;

public class NoSuchProductIdException extends ApplicationException {
    public NoSuchProductIdException(Integer id) {
        super("There is no product with id = " + id);
    }
}
