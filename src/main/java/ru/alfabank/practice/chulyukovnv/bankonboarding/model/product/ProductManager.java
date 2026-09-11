package ru.alfabank.practice.chulyukovnv.bankonboarding.model.product;

import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Data
@Component
public class ProductManager {
    private List<Product> products;

    public ProductManager() {
        List<Product> products = new ArrayList<>();
        products.add(new Product(1, "Bread", 70));
        products.add(new Product(2, "Milk", 120));
        products.add(new Product(3, "Sausage", 290));
        this.products = products;
    }
}
