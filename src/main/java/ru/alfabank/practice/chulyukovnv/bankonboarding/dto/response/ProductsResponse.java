package ru.alfabank.practice.chulyukovnv.bankonboarding.dto.response;

import ru.alfabank.practice.chulyukovnv.bankonboarding.entity.Product;

import java.util.List;

public record ProductsResponse(List<Product> products) {
}
