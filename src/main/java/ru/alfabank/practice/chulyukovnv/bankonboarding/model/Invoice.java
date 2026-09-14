package ru.alfabank.practice.chulyukovnv.bankonboarding.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.alfabank.practice.chulyukovnv.bankonboarding.model.product.DeliveredProduct;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Data
@AllArgsConstructor
public class Invoice {
    private AtomicInteger totalAmount;
    private List<DeliveredProduct> deliveredProducts;
}
