package ru.alfabank.practice.chulyukovnv.bankonboarding.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.alfabank.practice.chulyukovnv.bankonboarding.dto.product.DeliveredProduct;
import ru.alfabank.practice.chulyukovnv.bankonboarding.dto.product.OrderedProduct;
import ru.alfabank.practice.chulyukovnv.bankonboarding.dto.request.OrderedInfoRequest;
import ru.alfabank.practice.chulyukovnv.bankonboarding.dto.response.OrderedInfoResponse;
import ru.alfabank.practice.chulyukovnv.bankonboarding.dto.response.ProductsResponse;
import ru.alfabank.practice.chulyukovnv.bankonboarding.dto.response.WelcomeResponse;
import ru.alfabank.practice.chulyukovnv.bankonboarding.entity.Product;
import ru.alfabank.practice.chulyukovnv.bankonboarding.exception.NoSuchProductIdException;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@RequiredArgsConstructor
@Slf4j
public class ShopService {
    private final ProductService productService;
    private final DiscountService discountService;
    private final DadataService dadataService;

    public WelcomeResponse welcome() {
        return new WelcomeResponse("Добро пожаловать в наш чудесный магазин");
    }

    public ProductsResponse getProducts() {
        return new ProductsResponse(productService.findAvailableProducts().stream()
                .map(discountService::applyDiscount)
                .toList());
    }

    public OrderedInfoResponse calc(OrderedInfoRequest orderedInfoRequest) {
        dadataService.validateDeliveryAddress(orderedInfoRequest.deliveryAddress());
        List<OrderedProduct> orderedProducts = orderedInfoRequest.orderedProducts();
        validateProductIds(orderedProducts);
        AtomicInteger totalAmount = new AtomicInteger();
        Map<Integer, Product> existingProductsMap = productService.findAvailableProductsMap();
        List<DeliveredProduct> deliveredProducts = orderedProducts.stream().map(orderedProduct -> {
            Product existingProductWithSale = discountService.applyDiscount(existingProductsMap.get(orderedProduct.id()));
            DeliveredProduct deliveredProduct = new DeliveredProduct(existingProductWithSale, orderedProduct.count());
            totalAmount.addAndGet(deliveredProduct.amount());
            return deliveredProduct;
        }).toList();
        return new OrderedInfoResponse(totalAmount, deliveredProducts);
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

