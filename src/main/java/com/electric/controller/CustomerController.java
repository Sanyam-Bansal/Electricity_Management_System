package com.electric.controller;

import java.util.NoSuchElementException;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.electric.entities.Customer;
import com.electric.service.CustomerService;
import com.electric.service.EmailService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

/**
 * Controller class that handles HTTP requests related to customer management.
 */
@RestController
@Api(tags = "Customer Management")
public class CustomerController {
	private static final Logger logger = LoggerFactory.getLogger(CustomerController.class);

	@Autowired
	private CustomerService customerService;

	@Autowired
	private EmailService emailService;

	/**
	 * Creates a new customer with the provided details.
	 *
	 * @param customer The customer object to be created.
	 * @return A string indicating the ID of the created customer.
	 */
	@PostMapping("/customers")
	@ApiOperation(value = "Create a new customer", notes = "Creates a new customer with the provided details.")
	@ApiResponses(value = { @ApiResponse(code = 201, message = "Customer created successfully"),
			@ApiResponse(code = 400, message = "Invalid request") })
	public ResponseEntity<String> createCustomer(@Valid @RequestBody Customer customer) {
		try {
			customerService.saveCustomer(customer);
			logger.info("Created customer with ID: {}", customer.getCustomerId());
			return new ResponseEntity<>("The ID of the customer is: " + customer.getCustomerId(), HttpStatus.OK);
		} catch (Exception e) {
			logger.error("Error creating customer: {}", e.getMessage(), e);
			throw e;
		}
	}

	/**
	 * Retrieves a customer based on the provided ID.
	 *
	 * @param cId The ID of the customer to retrieve.
	 * @return The customer object with the specified ID.
	 * @throws NoSuchElementException If no customer is found with the specified ID.
	 */
	@GetMapping("/customers/{cId}")
	@ApiOperation(value = "Get customer by ID", notes = "Retrieves a customer based on the provided ID.")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Customer found"),
			@ApiResponse(code = 404, message = "Customer not found") })
	public ResponseEntity<Customer> getCustomer(@PathVariable long cId) throws NoSuchElementException {
		try {
			Customer customer = customerService.getCustomerById(cId);
			logger.info("Retrieved customer by ID: {}", cId);
			return new ResponseEntity<>(customer, HttpStatus.OK);
		} catch (NoSuchElementException e) {
			logger.error("Customer not found with ID: {}", e);
			throw e;
		}
	}

	/**
	 * Updates an existing customer with the provided details.
	 *
	 * @param customer The updated customer object.
	 * @param cId      The ID of the customer to update.
	 * @return The updated customer object.
	 */
	@PatchMapping("/customers/{cId}")
	@ApiOperation(value = "Update customer by ID", notes = "Updates an existing customer with the provided details.")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Customer updated successfully"),
			@ApiResponse(code = 404, message = "Customer not found") })
	public ResponseEntity<Customer> updateCustomer(@RequestBody Customer customer, @PathVariable long cId) {
		try {
			Customer updatedCustomer = customerService.updateCustomer(customer, cId);
			logger.info("Updated customer with ID: {}", cId);
			return new ResponseEntity<>(updatedCustomer, HttpStatus.OK);
		} catch (NoSuchElementException e) {
			logger.error("Customer not found with ID: {}", cId);
			throw e;
		}
	}

	/**
	 * Calculates the bill for a customer based on their ID and current meter
	 * reading.
	 *
	 * @param cId            The ID of the customer.
	 * @param currentReading The current meter reading.
	 * @return A string containing the details of the calculated bill.
	 * @throws NoSuchElementException If no customer is found with the specified ID.
	 */
	@GetMapping("customerBill/{cId}/{currentReading}")
	@ApiOperation(value = "Calculate customer's bill", notes = "Calculates the bill for a customer based on their ID and current meter reading.")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Bill calculated successfully"),
			@ApiResponse(code = 404, message = "Customer not found") })
	public ResponseEntity<String> calculateBill(@PathVariable long cId, @PathVariable int currentReading)
			throws NoSuchElementException {
		try {
			String billDetails = customerService.calculateBillOfCustomer(cId, currentReading);
			logger.info("Calculated bill for customer with ID: {}", cId);
			String email = customerService.getCustomerEmailById(cId);
			String subject = "Electricity Bill";
			String body = "Dear customer, your electricity bill details: \n" + billDetails;
			emailService.sendMail(email, subject, body);
			String responseMessage = "Bill calculated and details sent to your respective email.";
			return new ResponseEntity<>(responseMessage, HttpStatus.OK);
		} catch (NoSuchElementException e) {
			logger.warn("Customer not found with ID: {}", cId);
			throw e;
		}
	}

	/**
	 * Deletes a customer based on the provided ID.
	 *
	 * @param cId The ID of the customer to delete.
	 * @return A string indicating the success of the deletion operation.
	 */
	@DeleteMapping("/customers/{cId}")
	@ApiOperation(value = "Delete customer by ID", notes = "Deletes a customer based on the provided ID.")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Customer deleted successfully"),
			@ApiResponse(code = 404, message = "Customer not found") })
	public ResponseEntity<String> deleteCustomer(@PathVariable long cId) {
		try {
			customerService.deleteCustomerById(cId);
			logger.info("Deleted customer with ID: {}", cId);
			return new ResponseEntity<>("Deleted Successfully", HttpStatus.OK);
		} catch (NoSuchElementException e) {
			logger.warn("Customer not found with ID: {}", cId);
			throw e;
		}
	}

//	@GetMapping("/customers")
//	public List<Customer> getAllCustomers(){
//		return customerService.getAllCustomers();
//	}

}
