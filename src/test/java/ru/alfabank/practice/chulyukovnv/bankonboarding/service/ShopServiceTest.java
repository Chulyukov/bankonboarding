package ru.alfabank.practice.chulyukovnv.bankonboarding.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
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
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ShopServiceTest {

    @Mock
    private ProductService productService;
    @Mock
    private DiscountService discountService;
    @Mock
    private DadataClient dadataClient;

    private ShopService shopService;

    @BeforeEach
    void setUp() {
        shopService = new ShopService(productService, discountService, dadataClient);
        ReflectionTestUtils.setField(shopService, "dadataToken", "test-token");
    }

    @Test
    void welcome_shouldReturnWelcomeMessage() {
        Welcome actual = shopService.welcome();

        assertEquals("Добро пожаловать в наш чудесный магазин", actual.message());
    }

    @Test
    void getProducts_shouldReturnCatalogWithDiscountedProducts() {
        Product product = new Product(1, "Ноутбук", true, 1000);
        Product discountedProduct = new Product(1, "Ноутбук", true, 900);
        when(productService.findAvailableProducts()).thenReturn(List.of(product));
        when(discountService.applyDiscount(product)).thenReturn(discountedProduct);

        ProductCatalog actual = shopService.getProducts();

        assertEquals(1, actual.products().size());
        assertEquals(discountedProduct, actual.products().getFirst());
        verify(discountService).applyDiscount(product);
    }

    @Test
    void getProducts_shouldReturnEmptyCatalogWhenNoProducts() {
        when(productService.findAvailableProducts()).thenReturn(List.of());

        ProductCatalog actual = shopService.getProducts();

        assertNotNull(actual);
        assertEquals(0, actual.products().size());
    }

    @Test
    void calc_shouldReturnInvoiceForValidOrder() {
        OrderedInfo orderedInfo = new OrderedInfo("г Москва, ул Тверская, д 1", List.of(new OrderedProduct(1, 2)));
        Product product = new Product(1, "Ноутбук", true, 1000);
        Product discountedProduct = new Product(1, "Ноутбук", true, 900);

        mockValidAddress();
        when(productService.findAvailableProductIds()).thenReturn(Set.of(1));
        when(productService.findAvailableProductsMap()).thenReturn(Map.of(1, product));
        when(discountService.applyDiscount(product)).thenReturn(discountedProduct);

        Invoice actual = shopService.calc(orderedInfo);

        assertEquals(1800, actual.totalAmount().get());
        assertEquals(1, actual.deliveredProducts().size());
        DeliveredProduct item = actual.deliveredProducts().getFirst();
        assertEquals("Ноутбук", item.name());
        assertEquals(900, item.pricePerUnit());
        assertEquals(2, item.count());
        assertEquals(1800, item.amount());
    }

    @Test
    void calc_shouldSumTotalAmountAcrossMultipleProducts() {
        OrderedInfo orderedInfo = new OrderedInfo("г Москва, ул Тверская, д 1",
                List.of(new OrderedProduct(1, 2), new OrderedProduct(2, 3)));
        Product first = new Product(1, "Ноутбук", true, 1000);
        Product second = new Product(2, "Книга", true, 200);
        Product discountedFirst = new Product(1, "Ноутбук", true, 800);
        Product discountedSecond = new Product(2, "Книга", true, 180);

        mockValidAddress();
        when(productService.findAvailableProductIds()).thenReturn(Set.of(1, 2));
        when(productService.findAvailableProductsMap()).thenReturn(Map.of(1, first, 2, second));
        when(discountService.applyDiscount(first)).thenReturn(discountedFirst);
        when(discountService.applyDiscount(second)).thenReturn(discountedSecond);

        Invoice actual = shopService.calc(orderedInfo);

        assertEquals(2 * 800 + 3 * 180, actual.totalAmount().get());
        assertEquals(2, actual.deliveredProducts().size());
    }

    @Test
    void calc_shouldThrowEmptyDeliveryAddressExceptionWhenAddressIsNull() {
        OrderedInfo orderedInfo = new OrderedInfo(null, List.of(new OrderedProduct(1, 1)));

        assertThrows(EmptyDeliveryAddressException.class, () -> shopService.calc(orderedInfo));
    }

    @Test
    void calc_shouldThrowEmptyDeliveryAddressExceptionWhenAddressIsBlank() {
        OrderedInfo orderedInfo = new OrderedInfo("   ", List.of(new OrderedProduct(1, 1)));

        assertThrows(EmptyDeliveryAddressException.class, () -> shopService.calc(orderedInfo));
    }

    @Test
    void calc_shouldThrowIncorrectDeliveryAddressWhenDadataHasNoMatchingLevel9() {
        OrderedInfo orderedInfo = new OrderedInfo("г Москва, ул Тверская, д 1", List.of(new OrderedProduct(1, 1)));
        DadataAddressResponse response = new DadataAddressResponse();
        response.setSuggestions(List.of(buildSuggestion("8")));

        when(dadataClient.suggestAddress(anyString(), any(DadataAddressRequest.class))).thenReturn(response);

        assertThrows(IncorrectDeliveryAddress.class, () -> shopService.calc(orderedInfo));
    }

    @Test
    void calc_shouldAcceptValidAddressWithLevel9() {
        OrderedInfo orderedInfo = new OrderedInfo("г Москва, ул Тверская, д 1", List.of(new OrderedProduct(1, 1)));
        Product product = new Product(1, "Ноутбук", true, 1000);
        Product discountedProduct = new Product(1, "Ноутбук", true, 900);

        mockValidAddress();
        when(productService.findAvailableProductIds()).thenReturn(Set.of(1));
        when(productService.findAvailableProductsMap()).thenReturn(Map.of(1, product));
        when(discountService.applyDiscount(product)).thenReturn(discountedProduct);

        Invoice actual = shopService.calc(orderedInfo);

        assertEquals(900, actual.totalAmount().get());
    }

    @Test
    void calc_shouldThrowNoSuchProductIdExceptionWhenProductDoesNotExist() {
        OrderedInfo orderedInfo = new OrderedInfo("г Москва, ул Тверская, д 1", List.of(new OrderedProduct(99, 1)));
        mockValidAddress();
        when(productService.findAvailableProductIds()).thenReturn(Set.of(1, 2));

        assertThrows(NoSuchProductIdException.class, () -> shopService.calc(orderedInfo));
    }

    @Test
    void calc_shouldReturnEmptyInvoiceWhenOrderedProductsEmpty() {
        OrderedInfo orderedInfo = new OrderedInfo("г Москва, ул Тверская, д 1", List.of());
        mockValidAddress();

        Invoice actual = shopService.calc(orderedInfo);

        assertEquals(0, actual.totalAmount().get());
        assertEquals(0, actual.deliveredProducts().size());
    }

    @Test
    void calc_shouldCallDiscountServiceForEachOrderedProduct() {
        OrderedInfo orderedInfo = new OrderedInfo("г Москва, ул Тверская, д 1",
                List.of(new OrderedProduct(1, 2), new OrderedProduct(2, 3)));
        Product first = new Product(1, "Ноутбук", true, 1000);
        Product second = new Product(2, "Книга", true, 200);
        Product discountedFirst = new Product(1, "Ноутбук", true, 900);
        Product discountedSecond = new Product(2, "Книга", true, 180);

        mockValidAddress();
        when(productService.findAvailableProductIds()).thenReturn(Set.of(1, 2));
        when(productService.findAvailableProductsMap()).thenReturn(Map.of(1, first, 2, second));
        when(discountService.applyDiscount(first)).thenReturn(discountedFirst);
        when(discountService.applyDiscount(second)).thenReturn(discountedSecond);

        shopService.calc(orderedInfo);

        verify(discountService).applyDiscount(first);
        verify(discountService).applyDiscount(second);
    }

    private void mockValidAddress() {
        when(dadataClient.suggestAddress(anyString(), any(DadataAddressRequest.class))).thenReturn(validAddressResponse());
    }

    private DadataAddressResponse validAddressResponse() {
        DadataAddressResponse response = new DadataAddressResponse();
        response.setSuggestions(List.of(buildSuggestion("9")));
        return response;
    }

    private DadataAddressResponse.Suggestion buildSuggestion(String fiasLevel) {
        DadataAddressResponse.Suggestion suggestion = new DadataAddressResponse.Suggestion();
        DadataAddressResponse.AddressData data = new DadataAddressResponse.AddressData();
        data.setFiasLevel(fiasLevel);
        suggestion.setData(data);
        return suggestion;
    }
}
