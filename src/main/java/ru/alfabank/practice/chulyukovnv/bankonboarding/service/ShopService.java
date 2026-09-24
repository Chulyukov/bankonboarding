package ru.alfabank.practice.chulyukovnv.bankonboarding.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.alfabank.practice.chulyukovnv.bankonboarding.client.DadataClient;
import ru.alfabank.practice.chulyukovnv.bankonboarding.exception.EmptyDeliveryAddressException;
import ru.alfabank.practice.chulyukovnv.bankonboarding.exception.IncorrectDeliveryAddress;
import ru.alfabank.practice.chulyukovnv.bankonboarding.exception.NoSuchProductIdException;
import ru.alfabank.practice.chulyukovnv.bankonboarding.model.Invoice;
import ru.alfabank.practice.chulyukovnv.bankonboarding.model.OrderedInfo;
import ru.alfabank.practice.chulyukovnv.bankonboarding.model.Welcome;
import ru.alfabank.practice.chulyukovnv.bankonboarding.model.client.DadataAddressRequest;
import ru.alfabank.practice.chulyukovnv.bankonboarding.model.client.DadataAddressResponse;
import ru.alfabank.practice.chulyukovnv.bankonboarding.model.entity.Product;
import ru.alfabank.practice.chulyukovnv.bankonboarding.model.product.DeliveredProduct;
import ru.alfabank.practice.chulyukovnv.bankonboarding.model.product.OrderedProduct;
import ru.alfabank.practice.chulyukovnv.bankonboarding.model.product.ProductCatalog;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@RequiredArgsConstructor
@Slf4j
public class ShopService {
    private final ProductService productService;
    private final DiscountService discountService;
    private final DadataClient dadataClient;
    @Value("${dadata.api.token}")
    private String dadataToken;

    public Welcome welcome() {
        return new Welcome("Добро пожаловать в наш чудесный магазин");
    }

    public ProductCatalog getProducts() {
        return new ProductCatalog(productService.findAvailableProducts().stream()
                .map(discountService::applyDiscount)
                .toList());
    }

    public Invoice calc(OrderedInfo orderedInfo) {
        validateDeliveryAddress(orderedInfo.deliveryAddress());
        List<OrderedProduct> orderedProducts = orderedInfo.orderedProducts();
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

    private void validateDeliveryAddress(String deliveryAddress) {
        if (deliveryAddress == null || deliveryAddress.isBlank()) {
            throw new EmptyDeliveryAddressException();
        }
        DadataAddressRequest dadataAddressRequest = new DadataAddressRequest(deliveryAddress);
        String authHeader = "Token " + dadataToken;

        DadataAddressResponse dadataAddressResponse = dadataClient.suggestAddress(authHeader, dadataAddressRequest);

        boolean isValidLevel9Found = dadataAddressResponse.getSuggestions() != null && dadataAddressResponse.getSuggestions().stream()
                .map(DadataAddressResponse.Suggestion::getData)
                .filter(Objects::nonNull)
                .anyMatch(data -> "9".equals(data.getFiasLevel()));

        if (!isValidLevel9Found) {
            throw new IncorrectDeliveryAddress();
        }
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

