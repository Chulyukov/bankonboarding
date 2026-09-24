package ru.alfabank.practice.chulyukovnv.bankonboarding.exception;

public class IncorrectDeliveryAddress extends ApplicationException {
    public IncorrectDeliveryAddress() {
        super("The delivery address was not found or the house number was not specified.");
    }
}
