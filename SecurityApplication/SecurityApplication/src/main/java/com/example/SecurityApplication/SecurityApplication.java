package com.example.SecurityApplication;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SecurityApplication {

	public static void main(String[] args) {
		SpringApplication.run(SecurityApplication.class, args);

		System.out.println("==============================================");
		System.out.println("Spring Security Application Started");
		System.out.println("Security Filter Chain is now active");
		System.out.println("==============================================");
	}

}
