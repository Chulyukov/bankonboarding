package ru.alfabank.practice.chulyukovnv.bankonboarding.dto.response;

import ru.alfabank.practice.chulyukovnv.bankonboarding.dto.product.DeliveredProduct;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public record OrderedInfoResponse(AtomicInteger totalAmount, List<DeliveredProduct> deliveredProducts) {
}
