package ru.alfabank.practice.chulyukovnv.bankonboarding.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import ru.alfabank.practice.chulyukovnv.bankonboarding.model.entity.Product;

import java.util.List;

public interface ProductRepository extends MongoRepository<Product, Integer> {
    List<Product> findByInStockTrue();
}
