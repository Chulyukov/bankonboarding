package ru.alfabank.practice.chulyukovnv.bankonboarding.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
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
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ShopServiceTest {

    @Mock
    private ProductService productService;
    @Mock
    private DiscountService discountService;
    @Mock
    private DadataService dadataService;

    private ShopService shopService;

    @BeforeEach
    void setUp() {
        shopService = new ShopService(productService, discountService, dadataService);
    }

    @Test
    void welcome_shouldReturnWelcomeMessage() {
        WelcomeResponse actual = shopService.welcome();

        assertEquals("Добро пожаловать в наш чудесный магазин", actual.message());
    }

    @Test
    void getProducts_shouldReturnCatalogWithDiscountedProducts() {
        Product product = new Product(1, "Ноутбук", true, 1000);
        Product discountedProduct = new Product(1, "Ноутбук", true, 900);
        when(productService.findAvailableProducts()).thenReturn(List.of(product));
        when(discountService.applyDiscount(product)).thenReturn(discountedProduct);

        ProductsResponse actual = shopService.getProducts();

        assertEquals(1, actual.products().size());
        assertEquals(discountedProduct, actual.products().getFirst());
        verify(discountService).applyDiscount(product);
    }

    @Test
    void getProducts_shouldReturnEmptyCatalogWhenNoProducts() {
        when(productService.findAvailableProducts()).thenReturn(List.of());

        ProductsResponse actual = shopService.getProducts();

        assertNotNull(actual);
        assertEquals(0, actual.products().size());
    }

    @Test
    void calc_shouldReturnInvoiceForValidOrder() {
        OrderedInfoRequest orderedInfoRequest = new OrderedInfoRequest("г Москва, ул Тверская, д 1", List.of(new OrderedProduct(1, 2)));
        Product product = new Product(1, "Ноутбук", true, 1000);
        Product discountedProduct = new Product(1, "Ноутбук", true, 900);

        when(productService.findAvailableProductIds()).thenReturn(Set.of(1));
        when(productService.findAvailableProductsMap()).thenReturn(Map.of(1, product));
        when(discountService.applyDiscount(product)).thenReturn(discountedProduct);

        OrderedInfoResponse actual = shopService.calc(orderedInfoRequest);

        assertEquals(1800, actual.totalAmount().get());
        assertEquals(1, actual.deliveredProducts().size());
        DeliveredProduct item = actual.deliveredProducts().getFirst();
        assertEquals("Ноутбук", item.name());
        assertEquals(900, item.pricePerUnit());
        assertEquals(2, item.count());
        assertEquals(1800, item.amount());
        verify(dadataService).validateDeliveryAddress("г Москва, ул Тверская, д 1");
    }

    @Test
    void calc_shouldSumTotalAmountAcrossMultipleProducts() {
        OrderedInfoRequest orderedInfoRequest = new OrderedInfoRequest("г Москва, ул Тверская, д 1",
                List.of(new OrderedProduct(1, 2), new OrderedProduct(2, 3)));
        Product first = new Product(1, "Ноутбук", true, 1000);
        Product second = new Product(2, "Книга", true, 200);
        Product discountedFirst = new Product(1, "Ноутбук", true, 800);
        Product discountedSecond = new Product(2, "Книга", true, 180);

        when(productService.findAvailableProductIds()).thenReturn(Set.of(1, 2));
        when(productService.findAvailableProductsMap()).thenReturn(Map.of(1, first, 2, second));
        when(discountService.applyDiscount(first)).thenReturn(discountedFirst);
        when(discountService.applyDiscount(second)).thenReturn(discountedSecond);

        OrderedInfoResponse actual = shopService.calc(orderedInfoRequest);

        assertEquals(2 * 800 + 3 * 180, actual.totalAmount().get());
        assertEquals(2, actual.deliveredProducts().size());
    }

    @Test
    void calc_shouldThrowNoSuchProductIdExceptionWhenProductDoesNotExist() {
        OrderedInfoRequest orderedInfoRequest = new OrderedInfoRequest("г Москва, ул Тверская, д 1", List.of(new OrderedProduct(99, 1)));
        when(productService.findAvailableProductIds()).thenReturn(Set.of(1, 2));

        assertThrows(NoSuchProductIdException.class, () -> shopService.calc(orderedInfoRequest));
    }

    @Test
    void calc_shouldReturnEmptyInvoiceWhenOrderedProductsEmpty() {
        OrderedInfoRequest orderedInfoRequest = new OrderedInfoRequest("г Москва, ул Тверская, д 1", List.of());

        OrderedInfoResponse actual = shopService.calc(orderedInfoRequest);

        assertEquals(0, actual.totalAmount().get());
        assertEquals(0, actual.deliveredProducts().size());
    }

    @Test
    void calc_shouldCallDiscountServiceForEachOrderedProduct() {
        OrderedInfoRequest orderedInfoRequest = new OrderedInfoRequest("г Москва, ул Тверская, д 1",
                List.of(new OrderedProduct(1, 2), new OrderedProduct(2, 3)));
        Product first = new Product(1, "Ноутбук", true, 1000);
        Product second = new Product(2, "Книга", true, 200);
        Product discountedFirst = new Product(1, "Ноутбук", true, 900);
        Product discountedSecond = new Product(2, "Книга", true, 180);

        when(productService.findAvailableProductIds()).thenReturn(Set.of(1, 2));
        when(productService.findAvailableProductsMap()).thenReturn(Map.of(1, first, 2, second));
        when(discountService.applyDiscount(first)).thenReturn(discountedFirst);
        when(discountService.applyDiscount(second)).thenReturn(discountedSecond);

        shopService.calc(orderedInfoRequest);

        verify(discountService).applyDiscount(first);
        verify(discountService).applyDiscount(second);
    }
}
