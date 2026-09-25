package ru.alfabank.practice.chulyukovnv.bankonboarding.exception;

public class EmptyDeliveryAddressException extends ApplicationException {
    public EmptyDeliveryAddressException() {
        super("The delivery address can not be empty or = null");
    }
}
