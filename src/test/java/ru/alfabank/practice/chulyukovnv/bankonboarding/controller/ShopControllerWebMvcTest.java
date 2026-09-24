package ru.alfabank.practice.chulyukovnv.bankonboarding.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import tools.jackson.databind.ObjectMapper;
import ru.alfabank.practice.chulyukovnv.bankonboarding.exception.GlobalExceptionHandler;
import ru.alfabank.practice.chulyukovnv.bankonboarding.exception.NoSuchProductIdException;
import ru.alfabank.practice.chulyukovnv.bankonboarding.model.Invoice;
import ru.alfabank.practice.chulyukovnv.bankonboarding.model.OrderedInfo;
import ru.alfabank.practice.chulyukovnv.bankonboarding.model.Welcome;
import ru.alfabank.practice.chulyukovnv.bankonboarding.model.entity.Product;
import ru.alfabank.practice.chulyukovnv.bankonboarding.model.product.DeliveredProduct;
import ru.alfabank.practice.chulyukovnv.bankonboarding.model.product.OrderedProduct;
import ru.alfabank.practice.chulyukovnv.bankonboarding.model.product.ProductCatalog;
import ru.alfabank.practice.chulyukovnv.bankonboarding.service.ShopService;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ShopControllerWebMvcTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private ShopService shopService;

    @BeforeEach
    void setUp() {
        shopService = mock(ShopService.class);
        objectMapper = new ObjectMapper();
        mockMvc = MockMvcBuilders.standaloneSetup(new ShopController(shopService))
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void welcome_shouldReturnWelcomeMessage() throws Exception {
        when(shopService.welcome()).thenReturn(new Welcome("Добро пожаловать в наш чудесный магазин"));

        mockMvc.perform(get("/shop/welcome"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Добро пожаловать в наш чудесный магазин"));
    }

    @Test
    void getProducts_shouldReturnProductCatalog() throws Exception {
        Product product = new Product(1, "Ноутбук", true, 1000);
        when(shopService.getProducts()).thenReturn(new ProductCatalog(List.of(product)));

        mockMvc.perform(get("/shop/product"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.products[0].id").value(1))
                .andExpect(jsonPath("$.products[0].name").value("Ноутбук"));
    }

    @Test
    void calc_shouldAcceptValidJsonAndReturnInvoice() throws Exception {
        OrderedInfo request = new OrderedInfo("г Москва, ул Тверская, д 1",
                List.of(new OrderedProduct(1, 2)));
        Invoice invoice = new Invoice(new AtomicInteger(1800),
                List.of(new DeliveredProduct("Ноутбук", 900, 2, 1800)));
        when(shopService.calc(request)).thenReturn(invoice);

        mockMvc.perform(post("/shop/calc")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalAmount").value(1800))
                .andExpect(jsonPath("$.deliveredProducts[0].name").value("Ноутбук"))
                .andExpect(jsonPath("$.deliveredProducts[0].pricePerUnit").value(900))
                .andExpect(jsonPath("$.deliveredProducts[0].count").value(2));
    }

    @Test
    void calc_shouldReturnBadRequest_WhenDeliveryAddressIsBlank() throws Exception {
        String invalidJson = "{\"deliveryAddress\":\"   \",\"orderedProducts\":[{\"id\":1,\"count\":2}]}";

        mockMvc.perform(post("/shop/calc")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("deliveryAddress must not be blank"));
    }

    @Test
    void calc_shouldReturnBadRequest_WhenOrderedProductsEmpty() throws Exception {
        String invalidJson = "{\"deliveryAddress\":\"г Москва, ул Тверская, д 1\",\"orderedProducts\":[]}";

        mockMvc.perform(post("/shop/calc")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("orderedProducts must not be empty"));
    }

    @Test
    void calc_shouldReturnBadRequest_WhenCountIsNegative() throws Exception {
        String invalidJson = "{\"deliveryAddress\":\"г Москва, ул Тверская, д 1\",\"orderedProducts\":[{\"id\":1,\"count\":0}]}";

        mockMvc.perform(post("/shop/calc")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("count must be positive"));
    }

    @Test
    void calc_shouldReturnNotFound_WhenServiceThrowsApplicationException() throws Exception {
        OrderedInfo request = new OrderedInfo("г Москва, ул Тверская, д 1",
                List.of(new OrderedProduct(99, 1)));
        when(shopService.calc(request)).thenThrow(new NoSuchProductIdException(99));

        mockMvc.perform(post("/shop/calc")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("There is no product with id = 99"));
    }
}
