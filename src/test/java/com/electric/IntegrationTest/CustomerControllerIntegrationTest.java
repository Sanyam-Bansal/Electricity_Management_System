package com.electric.IntegrationTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.time.LocalDate;
import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.electric.entities.Customer;
import com.electric.entities.Meter;
import com.electric.entities.Supplier;
import com.electric.repository.CustomerRepository;

public class CustomerControllerIntegrationTest extends BaseIntegrationTest{
//	@Autowired
//	private RestTemplate restTemplate;
//	
	@LocalServerPort
	private int port;

	@Autowired
	private CustomerRepository customerRepository;

	@BeforeEach
	public void setUp() {
		Customer customer = new Customer();
		customer.setCustomerName("Test Customer");
		customer.setConnectionDate(LocalDate.parse("2023-09-09"));
		customer.setEmail("bansalsanyam09@gmail.com");
		customer.setCustomerAddress("Test Address");
		customer.setMeter(new Meter());
		customer.getMeter().setMeterId(1L);
		customer.setSupplier(new Supplier());
		customer.getSupplier().setSupplierId(1L);
		ResponseEntity<String> response = restTemplate.postForEntity("http://localhost:" + port + "/customers",
				customer, String.class);
		assertEquals(HttpStatus.OK, response.getStatusCode());
	}

	@AfterEach
	public void tearDown() {
		Optional<Customer> matchingCustomer = Optional.of(customerRepository.findByCustomerName("Test Customer"));

		matchingCustomer.ifPresent(customer -> {
			ResponseEntity<String> response = restTemplate.exchange(
					"http://localhost:" + port + "/customers/" + customer.getCustomerId(), HttpMethod.DELETE, null,
					String.class);

			assertEquals(HttpStatus.OK, response.getStatusCode());
			assertEquals("Deleted Successfully", response.getBody());
		});

	}

	@Test
	public void testGetCustomer_Success() {
		Customer customer = customerRepository.findByCustomerName("Test Customer");
		long customerId = customer.getCustomerId();

		ResponseEntity<Customer> response = restTemplate
				.getForEntity("http://localhost:" + port + "/customers/" + customerId, Customer.class);

		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertNotNull(response.getBody());
	}

	@Test
	public void testCalculateBill_Success() {
		Customer customer = customerRepository.findByCustomerName("Test Customer");
		long customerId = customer.getCustomerId();
		int currentReading = 100;

		ResponseEntity<String> response = restTemplate.getForEntity(
				"http://localhost:" + port + "/customerBill/" + customerId + "/" + currentReading, String.class);

		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals("Bill calculated and details sent to your respective email.", response.getBody());
	}

	@Test
	public void testUpdateCustomer_Success() {
		Customer customer = customerRepository.findByCustomerName("Test Customer");
		long customerId = customer.getCustomerId();
		
		Customer updatedCustomer = new Customer();
		updatedCustomer.setCustomerAddress("Updated Address");
		
		HttpHeaders headers = new HttpHeaders();

		
		HttpEntity<Customer> requestEntity = new HttpEntity<>(updatedCustomer , headers);

		ResponseEntity<Customer> responseEntity = restTemplate.exchange(
				"http://localhost:" + port + "/customers/" + customerId, HttpMethod.PATCH, requestEntity,
				Customer.class);

		assertEquals(HttpStatus.OK, responseEntity.getStatusCode());

		Customer updatedResponseCustomer = responseEntity.getBody();
		assertEquals(updatedCustomer.getCustomerAddress(), updatedResponseCustomer.getCustomerAddress());
		
	}

//	@Test
//	public void testDeleteCustomer_Success() {
//		Customer customer = customerRepository.findByCustomerName("Test Customer");
//		long customerId = customer.getCustomerId();
//
//		ResponseEntity<String> response = restTemplate.exchange("http://localhost:" + port + "/customers/" + customerId,
//				HttpMethod.DELETE, null, String.class);
//
//		assertEquals(HttpStatus.OK, response.getStatusCode());
//		assertEquals("Deleted Successfully", response.getBody());
//	}

}

