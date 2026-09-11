package ru.alfabank.practice.chulyukovnv.bankonboarding.model.product;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
    @AllArgsConstructor
    public class DeliveredProduct {
        private String name;
        private Integer pricePerUnit;
        private Integer count;
        private Integer amount;
    }