package ru.alfabank.practice.chulyukovnv.bankonboarding.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.alfabank.practice.chulyukovnv.bankonboarding.exception.NoSuchProductIdException;
import ru.alfabank.practice.chulyukovnv.bankonboarding.model.Invoice;
import ru.alfabank.practice.chulyukovnv.bankonboarding.model.Welcome;
import ru.alfabank.practice.chulyukovnv.bankonboarding.model.product.DeliveredProduct;
import ru.alfabank.practice.chulyukovnv.bankonboarding.model.product.OrderedProduct;
import ru.alfabank.practice.chulyukovnv.bankonboarding.model.product.Product;
import ru.alfabank.practice.chulyukovnv.bankonboarding.model.product.ProductManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ShopService {
    @Autowired
    private ProductManager productManager;

    public Welcome welcome() {
        return new Welcome("Добро пожаловать в наш чудесный магазин");
    }

    public List<Product> getProducts() {
        return productManager.getProducts();
    }

    public Invoice calc(List<OrderedProduct> orderedProducts) {
        validateProductIds(orderedProducts);
        int totalAmount = 0;
        List<DeliveredProduct> deliveredProducts = new ArrayList<>();
        for (OrderedProduct orderedProduct : orderedProducts) {
            for (Product existingProduct : productManager.getProducts()) {
                if (Objects.equals(orderedProduct.getId(), existingProduct.getId())) {
                    DeliveredProduct deliveredProduct = new DeliveredProduct(
                            existingProduct.getName(),
                            existingProduct.getPrice(),
                            orderedProduct.getCount(),
                            orderedProduct.getCount() * existingProduct.getPrice()
                    );
                    totalAmount += orderedProduct.getCount() * existingProduct.getPrice();
                    deliveredProducts.add(deliveredProduct);
                }
            }
        }
        return new Invoice(totalAmount, deliveredProducts);
    }

    private void validateProductIds(List<OrderedProduct> orderedProducts) {
        Set<Integer> existingIds = productManager.getProducts().stream()
                .map(Product::getId)
                .collect(Collectors.toSet());
        orderedProducts.stream().map(OrderedProduct::getId)
                .filter(id -> !existingIds.contains(id)).findFirst().ifPresent(invalidId -> {
                    throw new NoSuchProductIdException(invalidId);
                });
    }
}

