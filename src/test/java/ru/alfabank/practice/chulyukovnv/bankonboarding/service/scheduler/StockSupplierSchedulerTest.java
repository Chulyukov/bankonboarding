package ru.alfabank.practice.chulyukovnv.bankonboarding.service.scheduler;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.alfabank.practice.chulyukovnv.bankonboarding.model.entity.Product;
import ru.alfabank.practice.chulyukovnv.bankonboarding.repository.ProductRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StockSupplierSchedulerTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private StockSupplierScheduler stockSupplierScheduler;

    @Test
    void updateRandomProductStock_shouldFetchAllProductsAndSaveThemBack() {
        Product product1 = new Product(1, "Ноутбук", true, 1000);
        Product product2 = new Product(2, "Книга", true, 200);
        when(productRepository.findAll()).thenReturn(List.of(product1, product2));

        stockSupplierScheduler.updateRandomProductStock();

        ArgumentCaptor<List<Product>> captor = ArgumentCaptor.forClass(List.class);
        verify(productRepository).saveAll(captor.capture());
        assertEquals(List.of(product1, product2), captor.getValue());
    }

    @Test
    void updateRandomProductStock_shouldSaveEmptyListWhenThereAreNoProducts() {
        when(productRepository.findAll()).thenReturn(List.of());

        stockSupplierScheduler.updateRandomProductStock();

        ArgumentCaptor<List<Product>> captor = ArgumentCaptor.forClass(List.class);
        verify(productRepository).saveAll(captor.capture());
        assertEquals(List.of(), captor.getValue());
    }
}
