package ru.alfabank.practice.chulyukovnv.bankonboarding.model;

import ru.alfabank.practice.chulyukovnv.bankonboarding.model.product.DeliveredProduct;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public record Invoice(AtomicInteger totalAmount, List<DeliveredProduct> deliveredProducts) {
}
