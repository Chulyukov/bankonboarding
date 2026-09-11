package ru.alfabank.practice.chulyukovnv.bankonboarding.model.product;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class OrderedProduct {
    private Integer id;
    private Integer count;
}
