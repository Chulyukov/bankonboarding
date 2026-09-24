package ru.alfabank.practice.chulyukovnv.bankonboarding.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.alfabank.practice.chulyukovnv.bankonboarding.aspect.Log;
import ru.alfabank.practice.chulyukovnv.bankonboarding.model.Invoice;
import ru.alfabank.practice.chulyukovnv.bankonboarding.model.OrderedInfo;
import ru.alfabank.practice.chulyukovnv.bankonboarding.model.Welcome;
import ru.alfabank.practice.chulyukovnv.bankonboarding.model.product.ProductCatalog;
import ru.alfabank.practice.chulyukovnv.bankonboarding.service.ShopService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/shop")
public class ShopController {
    private final ShopService shopService;

    @Log
    @GetMapping("/welcome")
    public Welcome welcome() {
        return shopService.welcome();
    }

    @Log
    @GetMapping("/product")
    public ProductCatalog getProducts() {
        return shopService.getProducts();
    }

    @Log
    @PostMapping("/calc")
    public Invoice calc(@Valid @RequestBody OrderedInfo orderedInfo) {
        return shopService.calc(orderedInfo);
    }
}
