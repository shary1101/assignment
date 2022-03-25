package com.shary.assignment;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class CarRentalApplication {

	@GetMapping("/message")
	public String message(String message) {
		return "Congrats! Your app is deployed successfully in Azure!";
	}

	public static void main(String[] args) {
		SpringApplication.run(CarRentalApplication.class, args);
	}

}
