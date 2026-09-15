package ru.alfabank.practice.chulyukovnv.bankonboarding.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.alfabank.practice.chulyukovnv.bankonboarding.model.Invoice;
import ru.alfabank.practice.chulyukovnv.bankonboarding.model.Welcome;
import ru.alfabank.practice.chulyukovnv.bankonboarding.model.product.OrderedProduct;
import ru.alfabank.practice.chulyukovnv.bankonboarding.model.product.ProductCatalog;
import ru.alfabank.practice.chulyukovnv.bankonboarding.service.ShopService;

import java.util.List;

@RestController
@RequestMapping("/shop")
public class ShopController {
    @Autowired
    private ShopService shopService;

    @GetMapping("/welcome")
    public Welcome welcome() {
        return shopService.welcome();
    }

    @GetMapping("/product")
    public ProductCatalog getProducts() {
        return shopService.getProducts();
    }

    @PostMapping("/calc")
    public Invoice calc(@RequestBody List<OrderedProduct> orderedProducts) {
        return shopService.calc(orderedProducts);
    }
}
