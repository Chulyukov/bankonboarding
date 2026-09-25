package ru.alfabank.practice.chulyukovnv.bankonboarding.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import ru.alfabank.practice.chulyukovnv.bankonboarding.entity.Discount;
import ru.alfabank.practice.chulyukovnv.bankonboarding.entity.Product;

import java.util.List;

public interface DiscountRepository extends MongoRepository<Discount, Integer> {
    List<Discount> findAllByIsDiscountAvailableTrueAndProductsContaining(Product product);
}
