package ru.alfabank.practice.chulyukovnv.bankonboarding.service.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.alfabank.practice.chulyukovnv.bankonboarding.model.entity.Product;
import ru.alfabank.practice.chulyukovnv.bankonboarding.repository.ProductRepository;

import java.util.List;
import java.util.Random;

@Slf4j
@Component
@RequiredArgsConstructor
public class StockSupplierScheduler {
    private final ProductRepository productRepository;

    @Scheduled(fixedRate = 60000)
    public void updateRandomProductStock() {
        Random r = new Random();
        List<Product> existingProducts = productRepository.findAll();

        existingProducts.forEach(p -> {
            if (r.nextBoolean()) {
                boolean currentStock = Boolean.TRUE.equals(p.getInStock());
                p.setInStock(!currentStock);
                log.info("Product with id={} received getInStock={}", p.getId(), p.getInStock());
            }
        });

        productRepository.saveAll(existingProducts);
    }
}
