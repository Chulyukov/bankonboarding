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
import ru.alfabank.practice.chulyukovnv.bankonboarding.entity.Product;
import ru.alfabank.practice.chulyukovnv.bankonboarding.service.DadataService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Testcontainers
class ProductRepositoryTest {
    @Container
    @ServiceConnection
    static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:7.0");

    @MockitoBean
    private DadataService dadataService;

    @Autowired
    private ProductRepository productRepository;

    @AfterEach
    void cleanUp() {
        productRepository.deleteAll();
    }

    @Test
    void findByInStockTrue_shouldReturnOnlyInStockProducts() {
        Product inStockProduct = new Product(1, "Ноутбук", true, 1000);
        Product outOfStockProduct = new Product(2, "Книга", false, 200);
        productRepository.saveAll(List.of(inStockProduct, outOfStockProduct));

        List<Product> result = productRepository.findByInStockTrue();

        assertEquals(1, result.size());
        assertTrue(result.getFirst().getInStock());
        assertEquals("Ноутбук", result.getFirst().getName());
    }
}
