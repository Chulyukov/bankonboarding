package ru.alfabank.practice.chulyukovnv.bankonboarding.model.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document("products")
public class Product {
    @Id
    private Integer id;
    private String name;
    private Boolean inStock;
    private Integer price;
}
