package ru.alfabank.practice.chulyukovnv.bankonboarding.component;

import org.springframework.stereotype.Component;

import java.util.Random;

@Component
public class RandomGenerator {

    private final Random random = new Random();

    public boolean nextBoolean() {
        return random.nextBoolean();
    }
}
