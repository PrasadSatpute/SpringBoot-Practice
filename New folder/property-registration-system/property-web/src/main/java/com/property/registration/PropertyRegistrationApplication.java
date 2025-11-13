package com.property.registration;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = "com.property.registration")
@EnableJpaRepositories(basePackages = "com.property.registration.repository")
@EntityScan(basePackages = "com.property.registration.entity")
public class PropertyRegistrationApplication {

    public static void main(String[] args) {
        SpringApplication.run(PropertyRegistrationApplication.class, args);
    }
}