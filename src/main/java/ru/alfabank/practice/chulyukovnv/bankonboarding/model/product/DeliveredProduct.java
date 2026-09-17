package ru.alfabank.practice.chulyukovnv.bankonboarding.model.product;

import ru.alfabank.practice.chulyukovnv.bankonboarding.model.entity.Product;

public record DeliveredProduct(
        String name,
        Integer pricePerUnit,
        Integer count,
        Integer amount
) {
    public DeliveredProduct(Product product, Integer count) {
        this(product.getName(), product.getPrice(), count, product.getPrice() * count);
    }
}