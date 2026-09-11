package ru.alfabank.practice.chulyukovnv.bankonboarding.model.product;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Product {
    private Integer id;
    private String name;
    private Integer price;
}
