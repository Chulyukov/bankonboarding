package ru.alfabank.practice.chulyukovnv.bankonboarding;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@EnableAspectJAutoProxy
@EnableFeignClients
@SpringBootApplication
public class BankonboardingApplication {

    public static void main(String[] args) {
        SpringApplication.run(BankonboardingApplication.class, args);
    }

}
