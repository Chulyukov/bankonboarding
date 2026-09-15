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

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class ShopService {
    @Autowired
    private ProductManager productManager;

    public Welcome welcome() {
        return new Welcome("Добро пожаловать в наш чудесный магазин");
    }

    public ProductManager getProductManager() {
        return productManager;
    }

    public Invoice calc(List<OrderedProduct> orderedProducts) {
        validateProductIds(orderedProducts);
        AtomicInteger totalAmount = new AtomicInteger();
        List<DeliveredProduct> deliveredProducts = orderedProducts.stream().map(orderedProduct -> {
            Product existingProduct = productManager.getProducts().get(orderedProduct.getId());
            DeliveredProduct deliveredProduct = new DeliveredProduct(
                    existingProduct.getName(),
                    existingProduct.getPrice(),
                    orderedProduct.getCount(),
                    orderedProduct.getCount() * existingProduct.getPrice()
            );
            totalAmount.addAndGet(orderedProduct.getCount() * existingProduct.getPrice());
            return deliveredProduct;
        }).toList();
        return new Invoice(totalAmount, deliveredProducts);
    }

    private void validateProductIds(List<OrderedProduct> orderedProducts) {
        Set<Integer> existingIds = new HashSet<>(productManager.getProducts().keySet());
        orderedProducts.stream().map(OrderedProduct::getId)
                .filter(id -> !existingIds.contains(id)).findFirst().ifPresent(invalidId -> {
                    throw new NoSuchProductIdException(invalidId);
                });
    }
}

