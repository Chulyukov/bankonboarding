package ru.alfabank.practice.chulyukovnv.bankonboarding.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.alfabank.practice.chulyukovnv.bankonboarding.dto.product.DeliveredProduct;
import ru.alfabank.practice.chulyukovnv.bankonboarding.dto.product.OrderedProduct;
import ru.alfabank.practice.chulyukovnv.bankonboarding.dto.request.OrderedInfoRequest;
import ru.alfabank.practice.chulyukovnv.bankonboarding.dto.response.OrderedInfoResponse;
import ru.alfabank.practice.chulyukovnv.bankonboarding.dto.response.ProductsResponse;
import ru.alfabank.practice.chulyukovnv.bankonboarding.dto.response.WelcomeResponse;
import ru.alfabank.practice.chulyukovnv.bankonboarding.entity.Product;
import ru.alfabank.practice.chulyukovnv.bankonboarding.exception.GlobalExceptionHandler;
import ru.alfabank.practice.chulyukovnv.bankonboarding.exception.NoSuchProductIdException;
import ru.alfabank.practice.chulyukovnv.bankonboarding.service.ShopService;
import tools.jackson.databind.ObjectMapper;

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
        when(shopService.welcome()).thenReturn(new WelcomeResponse("Добро пожаловать в наш чудесный магазин"));

        mockMvc.perform(get("/shop/welcome"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Добро пожаловать в наш чудесный магазин"));
    }

    @Test
    void getProducts_shouldReturnProductCatalog() throws Exception {
        Product product = new Product(1, "Ноутбук", true, 1000);
        when(shopService.getProducts()).thenReturn(new ProductsResponse(List.of(product)));

        mockMvc.perform(get("/shop/product"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.products[0].id").value(1))
                .andExpect(jsonPath("$.products[0].name").value("Ноутбук"));
    }

    @Test
    void calc_shouldAcceptValidJsonAndReturnInvoice() throws Exception {
        OrderedInfoRequest request = new OrderedInfoRequest("г Москва, ул Тверская, д 1",
                List.of(new OrderedProduct(1, 2)));
        OrderedInfoResponse orderedInfoResponse = new OrderedInfoResponse(new AtomicInteger(1800),
                List.of(new DeliveredProduct("Ноутбук", 900, 2, 1800)));
        when(shopService.calc(request)).thenReturn(orderedInfoResponse);

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
        OrderedInfoRequest request = new OrderedInfoRequest("г Москва, ул Тверская, д 1",
                List.of(new OrderedProduct(99, 1)));
        when(shopService.calc(request)).thenThrow(new NoSuchProductIdException(99));

        mockMvc.perform(post("/shop/calc")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("There is no product with id = 99"));
    }
}
