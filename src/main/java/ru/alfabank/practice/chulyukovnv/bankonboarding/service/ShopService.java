package ru.alfabank.practice.chulyukovnv.bankonboarding.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.alfabank.practice.chulyukovnv.bankonboarding.exception.NoSuchProductIdException;
import ru.alfabank.practice.chulyukovnv.bankonboarding.model.Invoice;
import ru.alfabank.practice.chulyukovnv.bankonboarding.model.Welcome;
import ru.alfabank.practice.chulyukovnv.bankonboarding.model.entity.Product;
import ru.alfabank.practice.chulyukovnv.bankonboarding.model.product.DeliveredProduct;
import ru.alfabank.practice.chulyukovnv.bankonboarding.model.product.OrderedProduct;
import ru.alfabank.practice.chulyukovnv.bankonboarding.model.product.ProductCatalog;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@RequiredArgsConstructor
@Slf4j
public class ShopService {
    private final ProductService productService;
    private final DiscountService discountService;

    public Welcome welcome() {
        return new Welcome("Добро пожаловать в наш чудесный магазин");
    }

    public ProductCatalog getProducts() {
        return new ProductCatalog(productService.findAvailableProducts().stream()
                .map(discountService::applyDiscount)
                .toList());
    }

    public Invoice calc(List<OrderedProduct> orderedProducts) {
        validateProductIds(orderedProducts);
        AtomicInteger totalAmount = new AtomicInteger();
        Map<Integer, Product> existingProductsMap = productService.findAvailableProductsMap();
        List<DeliveredProduct> deliveredProducts = orderedProducts.stream().map(orderedProduct -> {
            Product existingProductWithSale = discountService.applyDiscount(existingProductsMap.get(orderedProduct.id()));
            DeliveredProduct deliveredProduct = new DeliveredProduct(existingProductWithSale, orderedProduct.count());
            totalAmount.addAndGet(deliveredProduct.amount());
            return deliveredProduct;
        }).toList();
        return new Invoice(totalAmount, deliveredProducts);
    }

    private void validateProductIds(List<OrderedProduct> orderedProducts) {
        orderedProducts.stream().map(OrderedProduct::id)
                .filter(id -> !productService.findAvailableProductIds().contains(id))
                .findFirst()
                .ifPresent(invalidId -> {
                    throw new NoSuchProductIdException(invalidId);
                });
    }
}

