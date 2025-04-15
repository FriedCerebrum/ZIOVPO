package com.example.signatureapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication
@EntityScan(basePackages = "com.example.signatureapp.model")
public class SignatureAppApplication {

    public static void main(String[] args) {
        SpringApplication.run(SignatureAppApplication.class, args);
    }
}
