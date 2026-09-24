package ru.alfabank.practice.chulyukovnv.bankonboarding.model;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import ru.alfabank.practice.chulyukovnv.bankonboarding.model.product.OrderedProduct;

import java.util.List;

public record OrderedInfo(
        @NotBlank(message = "deliveryAddress must not be blank")
        String deliveryAddress,
        @NotNull(message = "orderedProducts must not be null")
        @NotEmpty(message = "orderedProducts must not be empty")
        @Valid
        List<OrderedProduct> orderedProducts
) {
}
