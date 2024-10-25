package com.chensoul.bookstore.product;

import org.springframework.boot.SpringApplication;

public class TestProductServiceApplication {

    public static void main(String[] args) {
        SpringApplication.from(ProductServiceApplication::main)
                .with(ContainersConfig.class)
                .run(args);
    }
}
