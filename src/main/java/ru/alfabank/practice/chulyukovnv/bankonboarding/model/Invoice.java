package ru.alfabank.practice.chulyukovnv.bankonboarding.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.alfabank.practice.chulyukovnv.bankonboarding.model.product.DeliveredProduct;

import java.util.List;

@Data
@AllArgsConstructor
public class Invoice {
    private Integer totalAmount;
    private List<DeliveredProduct> deliveredProducts;
}
