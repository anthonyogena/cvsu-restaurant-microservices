package edu.ph.cvsu.imus.restaurant.order;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
public class CvsuRestaurantOrderApplication {

	public static void main(String[] args) {
		SpringApplication.run(CvsuRestaurantOrderApplication.class, args);
	}

	// This is the "Phone" you were missing!
	@Bean
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}

}