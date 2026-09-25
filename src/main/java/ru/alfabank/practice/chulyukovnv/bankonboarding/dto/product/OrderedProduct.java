package ru.alfabank.practice.chulyukovnv.bankonboarding.dto.product;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record OrderedProduct(
        @NotNull(message = "id must not be null")
        @Positive(message = "id must be positive")
        Integer id,
        @NotNull(message = "count must not be null")
        @Positive(message = "count must be positive")
        Integer count
) {
}
