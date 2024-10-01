package com.onlineshopping.productservice;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.onlineshopping.productservice.dto.ProductRequest;
import com.onlineshopping.productservice.model.Product;
import com.onlineshopping.productservice.repository.ProductRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.shaded.org.hamcrest.Matcher;
import org.testcontainers.shaded.org.hamcrest.Matchers;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc
class ProductServiceApplicationTests {

	@Container
	static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:4.0.10");

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;


	@Autowired
	private ProductRepository productRepository;


	@DynamicPropertySource
	static void setProperties(DynamicPropertyRegistry dynamicPropertyRegistry){
		// Set mongodb uri
		dynamicPropertyRegistry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
	}

	@BeforeEach
	void setUp() {
		productRepository.deleteAll(); // Clean up existing data before each test
	}

	@Test
	void shouldCreateProduct() throws Exception {
		// Create Product Request
		ProductRequest productRequest = getProductRequest();

		// Serialize ProductRequest object
		String productRequestString = objectMapper.writeValueAsString(productRequest);

		// Invoke Rest Controller
		mockMvc.perform(MockMvcRequestBuilders.post("/api/product")
				.contentType(MediaType.APPLICATION_JSON)
				.content(productRequestString))
				.andExpect(status().isCreated());
		Assertions.assertEquals(productRepository.findAll().size(), 1);
	}

	// subroutine to create Product Request object
	private ProductRequest getProductRequest(){
		return ProductRequest.builder()
			.name("Iphone 15 PRO")
			.description("New Iphone 15 PRO 256GB")
			.price(BigDecimal.valueOf(1100))
			.build();
	}

	@Test
	void shouldGetAllProducts() throws Exception {
		// create product
		Product savedProduct = productRepository.save(new Product("1L", "Iphone 15 PRO", "Refurbished Iphone 15 PRO 256GB", BigDecimal.valueOf(1100)));


		mockMvc.perform(MockMvcRequestBuilders.get("/api/product")
				.contentType(MediaType.APPLICATION_JSON))
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.[0].name").value(savedProduct.getName()))
				.andExpect(jsonPath("$.[0].description").value(savedProduct.getDescription()));
	}

}
