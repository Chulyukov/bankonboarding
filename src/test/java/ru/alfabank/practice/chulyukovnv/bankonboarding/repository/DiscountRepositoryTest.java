package ru.alfabank.practice.chulyukovnv.bankonboarding.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.mongodb.MongoDBContainer;
import ru.alfabank.practice.chulyukovnv.bankonboarding.entity.Discount;
import ru.alfabank.practice.chulyukovnv.bankonboarding.entity.Product;
import ru.alfabank.practice.chulyukovnv.bankonboarding.service.DadataService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Testcontainers
public class DiscountRepositoryTest {
    @Container
    @ServiceConnection
    static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:7.0");

    @MockitoBean
    private DadataService dadataService;

    @Autowired
    private DiscountRepository discountRepository;

    @Autowired
    private ProductRepository productRepository;

    @AfterEach
    void cleanUp() {
        discountRepository.deleteAll();
        productRepository.deleteAll();
    }

    @Test
    void findAllByIsDiscountAvailableTrueAndProductsContaining_shouldReturnOnlyAvailableDiscountsForGivenProduct() {
        Product targetProduct = new Product(1, "Ноутбук", true, 1000);
        Product secondaryProduct = new Product(2, "Смартфон", true, 5000);
        productRepository.saveAll(List.of(targetProduct, secondaryProduct));

        Discount availableDiscount = new Discount();
        availableDiscount.setIsDiscountAvailable(true);
        availableDiscount.setId(1);
        availableDiscount.setName("Скидка на ноутбуки");
        availableDiscount.setPercentageSale(40);
        availableDiscount.setProducts(List.of(targetProduct));

        Discount unavaliableDiscount = new Discount();
        unavaliableDiscount.setIsDiscountAvailable(false);
        unavaliableDiscount.setId(2);
        unavaliableDiscount.setPercentageSale(30);
        unavaliableDiscount.setProducts(List.of(targetProduct));

        Discount discountWithoutProduct = new Discount();
        discountWithoutProduct.setIsDiscountAvailable(true);
        discountWithoutProduct.setId(3);
        discountWithoutProduct.setPercentageSale(20);
        discountWithoutProduct.setProducts(List.of(new Product(2, "Книга", true, 200)));
        discountRepository.saveAll(List.of(availableDiscount, unavaliableDiscount, discountWithoutProduct));

        List<Discount> result = discountRepository.findAllByIsDiscountAvailableTrueAndProductsContaining(targetProduct);

        assertEquals(1, result.size());
        assertTrue(result.getFirst().getIsDiscountAvailable());
        assertEquals("Скидка на ноутбуки", result.getFirst().getName());
    }
}
