package ru.alfabank.practice.chulyukovnv.bankonboarding;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = {
        "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration,org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration",
        "spring.docker.compose.enabled=false",
        "dadata.api.token=test-token",
        "dadata.api.url=http://localhost"
})
class BankonboardingApplicationTests {

    @Test
    void contextLoads() {
    }

}
