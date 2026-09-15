package ru.alfabank.practice.chulyukovnv.bankonboarding.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ru.alfabank.practice.chulyukovnv.bankonboarding.exception.NoSuchProductIdException;
import ru.alfabank.practice.chulyukovnv.bankonboarding.model.Invoice;
import ru.alfabank.practice.chulyukovnv.bankonboarding.model.Welcome;
import ru.alfabank.practice.chulyukovnv.bankonboarding.model.entity.Discount;
import ru.alfabank.practice.chulyukovnv.bankonboarding.model.entity.Product;
import ru.alfabank.practice.chulyukovnv.bankonboarding.model.product.DeliveredProduct;
import ru.alfabank.practice.chulyukovnv.bankonboarding.model.product.OrderedProduct;
import ru.alfabank.practice.chulyukovnv.bankonboarding.model.product.ProductCatalog;
import ru.alfabank.practice.chulyukovnv.bankonboarding.repository.DiscountRepository;
import ru.alfabank.practice.chulyukovnv.bankonboarding.repository.ProductRepository;

import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ShopService {
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private DiscountRepository discountRepository;

    public Welcome welcome() {
        return new Welcome("Добро пожаловать в наш чудесный магазин");
    }

    public ProductCatalog getProducts() {
        return new ProductCatalog(productRepository.findByInStockTrue().stream().map(this::useDiscountByProduct).toList());
    }

    private Product useDiscountByProduct(Product p) {
        List<Discount> discounts = discountRepository.findAllByIsDiscountAvailableTrueAndProductsContaining(p);
        int finalSale = discounts.stream().mapToInt(Discount::getPercentageSale).sum();
        if (finalSale > 50) {
            finalSale = 50;
        }
        p.setPrice(p.getPrice() * (100 - finalSale) / 100);
        return p;
    }

    public Invoice calc(List<OrderedProduct> orderedProducts) {
        validateProductIds(orderedProducts);
        AtomicInteger totalAmount = new AtomicInteger();
        Map<Integer, Product> existingProductsMap = productRepository.findByInStockTrue().stream()
                .collect(Collectors.toMap(Product::getId, product -> product));
        List<DeliveredProduct> deliveredProducts = orderedProducts.stream().map(orderedProduct -> {
            Product existingProductWithSale = useDiscountByProduct(existingProductsMap.get(orderedProduct.id()));
            DeliveredProduct deliveredProduct = new DeliveredProduct(
                    existingProductWithSale.getName(),
                    existingProductWithSale.getPrice(),
                    orderedProduct.count(),
                    orderedProduct.count() * existingProductWithSale.getPrice()
            );
            totalAmount.addAndGet(deliveredProduct.amount());
            return deliveredProduct;
        }).toList();
        return new Invoice(totalAmount, deliveredProducts);
    }

    @Scheduled(fixedRate = 60000)
    public void updateRandomProductStock() {
        Random r = new Random();
        List<Product> existingProducts = productRepository.findAll();

        existingProducts.forEach(p -> {
            if (r.nextBoolean()) {
                boolean currentStock = Boolean.TRUE.equals(p.getInStock());
                p.setInStock(!currentStock);
                log.info("Product with id={} received getInStock={}", p.getId(), p.getInStock());
            }
        });

        productRepository.saveAll(existingProducts);
    }

    private void validateProductIds(List<OrderedProduct> orderedProducts) {
        Set<Integer> existingIds = productRepository.findByInStockTrue().stream()
                .map(Product::getId)
                .collect(Collectors.toSet());
        orderedProducts.stream().map(OrderedProduct::id)
                .filter(id -> !existingIds.contains(id)).findFirst().ifPresent(invalidId -> {
                    throw new NoSuchProductIdException(invalidId);
                });
    }
}

