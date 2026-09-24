package ru.alfabank.practice.chulyukovnv.bankonboarding.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.alfabank.practice.chulyukovnv.bankonboarding.model.entity.Product;
import ru.alfabank.practice.chulyukovnv.bankonboarding.repository.ProductRepository;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    @Test
    void findAvailableProducts_shouldReturnProductsFromRepository() {
        Product first = new Product(1, "Ноутбук", true, 1000);
        Product second = new Product(2, "Книга", true, 200);
        when(productRepository.findByInStockTrue()).thenReturn(List.of(first, second));

        List<Product> actual = productService.findAvailableProducts();

        assertEquals(List.of(first, second), actual);
    }

    @Test
    void findAvailableProducts_shouldReturnEmptyListWhenRepositoryHasNoAvailableProducts() {
        when(productRepository.findByInStockTrue()).thenReturn(List.of());

        List<Product> actual = productService.findAvailableProducts();

        assertTrue(actual.isEmpty());
    }

    @Test
    void findAvailableProductsMap_shouldBuildMapByProductId() {
        Product first = new Product(1, "Ноутбук", true, 1000);
        Product second = new Product(2, "Книга", true, 200);
        when(productRepository.findByInStockTrue()).thenReturn(List.of(first, second));

        Map<Integer, Product> actual = productService.findAvailableProductsMap();

        assertEquals(Map.of(1, first, 2, second), actual);
    }

    @Test
    void findAvailableProductsMap_shouldReturnEmptyMapWhenNoProducts() {
        when(productRepository.findByInStockTrue()).thenReturn(List.of());

        Map<Integer, Product> actual = productService.findAvailableProductsMap();

        assertTrue(actual.isEmpty());
    }

    @Test
    void findAvailableProductIds_shouldCollectAllAvailableProductIds() {
        Product first = new Product(1, "Ноутбук", true, 1000);
        Product second = new Product(2, "Книга", true, 200);
        when(productRepository.findByInStockTrue()).thenReturn(List.of(first, second));

        Set<Integer> actual = productService.findAvailableProductIds();

        assertEquals(Set.of(1, 2), actual);
    }

    @Test
    void findAvailableProductIds_shouldReturnEmptySetWhenNoProducts() {
        when(productRepository.findByInStockTrue()).thenReturn(List.of());

        Set<Integer> actual = productService.findAvailableProductIds();

        assertTrue(actual.isEmpty());
    }
}
