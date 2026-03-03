package com.example.crm;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class SgCustomerHubApplication {

    public static void main(String[] args) {
        SpringApplication.run(SgCustomerHubApplication.class, args);
    }
}
