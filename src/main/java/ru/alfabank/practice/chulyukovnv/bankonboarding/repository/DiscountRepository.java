package ru.alfabank.practice.chulyukovnv.bankonboarding.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import ru.alfabank.practice.chulyukovnv.bankonboarding.model.entity.Discount;
import ru.alfabank.practice.chulyukovnv.bankonboarding.model.entity.Product;

import java.util.List;

public interface DiscountRepository extends MongoRepository<Discount, Integer> {
    List<Discount> findAllByIsDiscountAvailableTrueAndProductsContaining(Product product);
}
