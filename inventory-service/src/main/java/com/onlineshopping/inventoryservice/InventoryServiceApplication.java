package com.onlineshopping.inventoryservice;

import com.onlineshopping.inventoryservice.model.Inventory;
import com.onlineshopping.inventoryservice.repository.InventoryRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@EnableDiscoveryClient
public class InventoryServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(InventoryServiceApplication.class, args);
	}

	@Bean
	public CommandLineRunner loadData(InventoryRepository inventoryRepository) {
		return args -> {
			Inventory inventoryOne = new Inventory();
			inventoryOne.setSkuCode("Iphone 15 Pro");
			inventoryOne.setQuantity(100);

			Inventory inventoryTwo = new Inventory();
			inventoryTwo.setSkuCode("Iphone 15");
			inventoryTwo.setQuantity(0);

			inventoryRepository.save(inventoryOne);
			inventoryRepository.save(inventoryTwo);
		};
	}
}
