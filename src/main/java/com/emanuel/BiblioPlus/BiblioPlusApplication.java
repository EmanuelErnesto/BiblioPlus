package com.emanuel.BiblioPlus;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class BiblioPlusApplication {

	public static void main(String[] args) {
		SpringApplication.run(BiblioPlusApplication.class, args);
	}

}
