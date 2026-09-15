package ru.alfabank.practice.chulyukovnv.bankonboarding.model.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

import java.time.Instant;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document("discounts")
public class Discount {
    @Id
    private Integer id;
    private String name;
    private Integer percentageSale;
    private Boolean isDiscountAvailable;
    private Instant createdAt;

    @DocumentReference
    private List<Product> products;
}