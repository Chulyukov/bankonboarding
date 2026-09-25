package ru.alfabank.practice.chulyukovnv.bankonboarding.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.alfabank.practice.chulyukovnv.bankonboarding.entity.Product;
import ru.alfabank.practice.chulyukovnv.bankonboarding.repository.ProductRepository;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;

    public List<Product> findAvailableProducts() {
        return productRepository.findByInStockTrue();
    }

    public Map<Integer, Product> findAvailableProductsMap() {
        return findAvailableProducts().stream()
                .collect(Collectors.toMap(Product::getId, product -> product));
    }

    public Set<Integer> findAvailableProductIds() {
        return findAvailableProducts().stream()
                .map(Product::getId)
                .collect(Collectors.toSet());
    }


}
