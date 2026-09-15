package ru.alfabank.practice.chulyukovnv.bankonboarding.model.product;

public record DeliveredProduct(
        String name,
        Integer pricePerUnit,
        Integer count,
        Integer amount
) {
}