package com.electric.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.NoSuchElementException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;

import com.electric.entities.Customer;
import com.electric.service.CustomerService;
import com.electric.service.EmailService;
import com.electric.service.MeterService;

@SpringBootTest
public class CustomerControllerTest {

	@Mock
	private CustomerService customerService;

	@Mock
	private MeterService meterService;
	
	@Mock
	private EmailService emailService;

	@InjectMocks
	private CustomerController customerController;

	@BeforeEach
	public void setUp() {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	public void testCreateCustomer() {

		Customer customer = new Customer();
		customer.setCustomerId(1L);
		customer.setCustomerName("John");
		customer.setCustomerAddress("345 Sant St");

		when(customerService.saveCustomer(customer)).thenReturn(customer);

		ResponseEntity<String> response = customerController.createCustomer(customer);

		assertEquals("The ID of the customer is: 1", response.getBody());

		verify(customerService, times(1)).saveCustomer(any(Customer.class));
	}

	@Test
	public void testCreateCustomer_Exception() {
		Customer customer = new Customer();
		customer.setCustomerId(1L);
		customer.setCustomerName("John Doe");

		RuntimeException exception = new RuntimeException("Internal Server Error");
		doThrow(exception).when(customerService).saveCustomer(customer);

		RuntimeException thrownException = assertThrows(RuntimeException.class, () -> {
			customerController.createCustomer(customer);
		});

		assertEquals(exception.getMessage(), thrownException.getMessage());
		verify(customerService).saveCustomer(customer);
	}

	@Test
	public void testGetCustomer() {

		long customerId = 1;
		Customer sampleCustomer = new Customer();
		sampleCustomer.setCustomerId(customerId);
		sampleCustomer.setCustomerName("John Doe");

		when(customerService.getCustomerById(customerId)).thenReturn(sampleCustomer);

		ResponseEntity<Customer> result = customerController.getCustomer(customerId);

		assertEquals(customerId, result.getBody().getCustomerId());
		assertEquals(sampleCustomer.getCustomerName(), result.getBody().getCustomerName());

		verify(customerService, times(1)).getCustomerById(customerId);

	}

	@Test
	public void testGetCustomer_NotFound() {

		long customerId = 1L;

		when(customerService.getCustomerById(customerId))
				.thenThrow(new NoSuchElementException("No customer exists with given id. " + customerId));

		assertThrows(NoSuchElementException.class, () -> {
			customerController.getCustomer(customerId);
		});

	}

	@Test
	public void testUpdateCustomer_Exception() {
		long customerId = 10L;
		Customer customer = new Customer();
		customer.setCustomerId(customerId);
		customer.setCustomerName("John Doe");
		customer.setCurrentReading(100);

		when(customerService.updateCustomer(customer, customerId))
				.thenThrow(new NoSuchElementException("No customer exists with given id. " + customerId));

		assertThrows(NoSuchElementException.class, () -> {
			customerController.updateCustomer(customer, customerId);
		});

		verify(customerService).updateCustomer(customer, customerId);

	}

	@Test
	public void testUpdateCustomer() {

		Customer customer = new Customer();
		customer.setCustomerName("John Doe");
		customer.setCustomerAddress("345 Sant St");

		Customer updatedCustomer = new Customer();
		updatedCustomer.setCustomerAddress("142 Mark St");

		when(customerService.updateCustomer(updatedCustomer, 1L)).thenReturn(updatedCustomer);

		ResponseEntity<Customer> response = customerController.updateCustomer(updatedCustomer, 1L);

		assertEquals(updatedCustomer.getCustomerAddress(), response.getBody().getCustomerAddress());

	}

	@Test
	public void testCalculateBill() {

		int currentReading = 200;

		Customer customer = new Customer();
		customer.setCustomerId(1L);
		customer.setCustomerName("John Dao");
		customer.setLastReading(100);
		customer.setEmail("john02@gmail.com");

//		String expectedString = "the previous reading was:" + customer.getLastReading() + "\ncurrent reading is "
//				+ currentReading + "\ntotal bill amount is 300";
		String expectedString = "Bill calculated and details sent to your respective email.";

		when(customerService.calculateBillOfCustomer(1L, currentReading)).thenReturn(expectedString);
		when(customerService.getCustomerEmailById(1L)).thenReturn(customer.getEmail());
		


		ResponseEntity<String> response = customerController.calculateBill(1L, 200);

		assertEquals(expectedString, response.getBody());

		verify(customerService, times(1)).calculateBillOfCustomer(1L, 200);
	}

	@Test
	public void testCalculateBill_Exception() {
		long customerId = 1L;
		int currentReading = 100;

		NoSuchElementException exception = new NoSuchElementException(
				"No customer exists with given id. " + customerId);
		when(customerService.calculateBillOfCustomer(customerId, currentReading)).thenThrow(exception);

		NoSuchElementException thrownException = assertThrows(NoSuchElementException.class, () -> {
			customerController.calculateBill(customerId, currentReading);
		});

		assertEquals(exception.getMessage(), thrownException.getMessage());
		verify(customerService).calculateBillOfCustomer(customerId, currentReading);
	}

	@Test
	public void testDeleteCustomer() {

		ResponseEntity<String> response = customerController.deleteCustomer(1L);

		assertEquals("Deleted Successfully", response.getBody());

		verify(customerService, times(1)).deleteCustomerById(1L);
	}

	@Test
	public void testDeleteCustomer_Exception() {
		long customerId = 1L;

		NoSuchElementException exception = new NoSuchElementException(
				"No customer exists with given id. " + customerId);

		doThrow(exception).when(customerService).deleteCustomerById(customerId);

		NoSuchElementException thrownException = assertThrows(NoSuchElementException.class, () -> {
			customerController.deleteCustomer(customerId);
		});

		assertEquals(exception.getMessage(), thrownException.getMessage());
		verify(customerService).deleteCustomerById(customerId);
	}

}
