package ru.alfabank.practice.chulyukovnv.bankonboarding.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.alfabank.practice.chulyukovnv.bankonboarding.entity.Discount;
import ru.alfabank.practice.chulyukovnv.bankonboarding.entity.Product;
import ru.alfabank.practice.chulyukovnv.bankonboarding.repository.DiscountRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DiscountService {
    private final DiscountRepository discountRepository;

    public Product applyDiscount(Product product) {
        List<Discount> discounts = discountRepository.findAllByIsDiscountAvailableTrueAndProductsContaining(product);
        int finalSale = Math.min(discounts.stream().mapToInt(Discount::getPercentageSale).sum(), 50);
        product.setPrice(product.getPrice() * (100 - finalSale) / 100);
        return product;
    }
}
