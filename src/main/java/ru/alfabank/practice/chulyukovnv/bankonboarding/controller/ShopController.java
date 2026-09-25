package ru.alfabank.practice.chulyukovnv.bankonboarding.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.alfabank.practice.chulyukovnv.bankonboarding.aspect.Log;
import ru.alfabank.practice.chulyukovnv.bankonboarding.dto.request.OrderedInfoRequest;
import ru.alfabank.practice.chulyukovnv.bankonboarding.dto.response.OrderedInfoResponse;
import ru.alfabank.practice.chulyukovnv.bankonboarding.dto.response.ProductsResponse;
import ru.alfabank.practice.chulyukovnv.bankonboarding.dto.response.WelcomeResponse;
import ru.alfabank.practice.chulyukovnv.bankonboarding.service.ShopService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/shop")
public class ShopController {
    private final ShopService shopService;

    @Log
    @GetMapping("/welcome")
    public WelcomeResponse welcome() {
        return shopService.welcome();
    }

    @Log
    @GetMapping("/product")
    public ProductsResponse getProducts() {
        return shopService.getProducts();
    }

    @Log
    @PostMapping("/calc")
    public OrderedInfoResponse calc(@Valid @RequestBody OrderedInfoRequest orderedInfoRequest) {
        return shopService.calc(orderedInfoRequest);
    }
}
