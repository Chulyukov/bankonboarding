package ru.alfabank.practice.chulyukovnv.bankonboarding.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.alfabank.practice.chulyukovnv.bankonboarding.entity.Discount;
import ru.alfabank.practice.chulyukovnv.bankonboarding.entity.Product;
import ru.alfabank.practice.chulyukovnv.bankonboarding.repository.DiscountRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DiscountServiceTest {

    @Mock
    private DiscountRepository discountRepository;

    @InjectMocks
    private DiscountService discountService;

    @Test
    void applyDiscount_shouldReturnSameProductWithDiscountedPrice() {
        Product product = new Product(1, "Ноутбук", true, 1000);
        Discount discount = new Discount();
        discount.setPercentageSale(10);
        discount.setProducts(List.of(product));

        when(discountRepository.findAllByIsDiscountAvailableTrueAndProductsContaining(product))
                .thenReturn(List.of(discount));

        Product actual = discountService.applyDiscount(product);

        assertSame(product, actual);
        assertEquals(900, actual.getPrice());
    }

    @Test
    void applyDiscount_shouldSumSeveralDiscountsAndCapAt50Percent() {
        Product product = new Product(1, "Ноутбук", true, 1000);

        Discount discount1 = new Discount();
        discount1.setPercentageSale(20);
        discount1.setProducts(List.of(product));

        Discount discount2 = new Discount();
        discount2.setPercentageSale(35);
        discount2.setProducts(List.of(product));

        when(discountRepository.findAllByIsDiscountAvailableTrueAndProductsContaining(product))
                .thenReturn(List.of(discount1, discount2));

        Product actual = discountService.applyDiscount(product);

        assertEquals(500, actual.getPrice());
    }

    @Test
    void applyDiscount_shouldCapDiscountAt50Percent() {
        Product product = new Product(1, "Ноутбук", true, 1000);

        Discount discount1 = new Discount();
        discount1.setPercentageSale(40);
        discount1.setProducts(List.of(product));

        Discount discount2 = new Discount();
        discount2.setPercentageSale(30);
        discount2.setProducts(List.of(product));

        when(discountRepository.findAllByIsDiscountAvailableTrueAndProductsContaining(product))
                .thenReturn(List.of(discount1, discount2));

        Product actual = discountService.applyDiscount(product);

        assertEquals(500, actual.getPrice());
    }

    @Test
    void applyDiscount_shouldReturnOriginalPriceWhenNoDiscountsFound() {
        Product product = new Product(1, "Ноутбук", true, 1000);

        when(discountRepository.findAllByIsDiscountAvailableTrueAndProductsContaining(product))
                .thenReturn(List.of());

        Product actual = discountService.applyDiscount(product);

        assertEquals(1000, actual.getPrice());
    }
}
