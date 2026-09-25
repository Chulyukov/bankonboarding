package ru.alfabank.practice.chulyukovnv.bankonboarding.service.scheduler;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.alfabank.practice.chulyukovnv.bankonboarding.component.RandomGenerator;
import ru.alfabank.practice.chulyukovnv.bankonboarding.entity.Product;
import ru.alfabank.practice.chulyukovnv.bankonboarding.repository.ProductRepository;
import ru.alfabank.practice.chulyukovnv.bankonboarding.scheduler.StockSupplierScheduler;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StockSupplierSchedulerTest {

    @Mock
    private ProductRepository productRepository;
    @Mock
    private RandomGenerator randomGenerator;

    @InjectMocks
    private StockSupplierScheduler stockSupplierScheduler;

    @Test
    void updateRandomProductStock_shouldToggleStockOnlyForSelectedProducts() {
        Product product1 = new Product(1, "Ноутбук", true, 1000);
        Product product2 = new Product(2, "Книга", true, 200);
        when(productRepository.findAll()).thenReturn(List.of(product1, product2));

        when(randomGenerator.nextBoolean()).thenReturn(true, false);

        stockSupplierScheduler.updateRandomProductStock();

        ArgumentCaptor<List<Product>> captor = ArgumentCaptor.forClass(List.class);
        verify(productRepository).saveAll(captor.capture());
        List<Product> savedProducts = captor.getValue();

        assertEquals(2, savedProducts.size());
        assertFalse(savedProducts.get(0).getInStock());
        assertTrue(savedProducts.get(1).getInStock());
    }
}
