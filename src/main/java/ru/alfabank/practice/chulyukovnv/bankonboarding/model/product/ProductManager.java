package ru.alfabank.practice.chulyukovnv.bankonboarding.model.product;

import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Data
@Component
public class ProductManager {
    private Map<Integer, Product> products;

    public ProductManager() {
        Map<Integer, Product> products = new HashMap<>();
        products.put(1, new Product(1, "Bread", 70));
        products.put(2, new Product(2, "Milk", 120));
        products.put(3, new Product(3, "Sausage", 290));
        this.products = products;
    }
}
