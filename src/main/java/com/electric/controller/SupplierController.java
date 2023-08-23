package com.electric.controller;

import java.util.List;
import java.util.NoSuchElementException;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.electric.entities.Customer;
import com.electric.entities.Supplier;
import com.electric.service.CustomerService;
import com.electric.service.SupplierService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@RestController
@RequestMapping("/suppliers")
@Api(value = "Supplier Controller", tags = "Supplier API")
public class SupplierController {
	private static final Logger logger = LoggerFactory.getLogger(SupplierController.class);
	@Autowired
	private SupplierService supplierService;
	@Autowired
	private CustomerService customerService;

	/**
	 * Creates a new supplier entity.
	 *
	 * @param supplier The supplier object to be created.
	 * @return The created supplier object.
	 */
	@PostMapping
	@ApiOperation(value = "Create a new supplier", notes = "Creates a new supplier entity.")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Supplier created successfully") })
	public ResponseEntity<Supplier> createSupplier(@Valid @RequestBody Supplier supplier) {
		logger.info("Creating a new supplier");
		Supplier supplier1 = supplierService.saveSupplier(supplier);
		return new ResponseEntity<>(supplier1, HttpStatus.OK);
	}

	/**
	 * Updates an existing supplier entity.
	 *
	 * @param supplierId      The ID of the supplier to update.
	 * @param updatedSupplier The updated supplier object.
	 * @return The updated supplier object.
	 * @throws NoSuchElementException If no supplier exists with the given ID.
	 */
	@PatchMapping("/{supplierId}")
	@ApiOperation(value = "Update a supplier", notes = "Updates an existing supplier entity.")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Supplier updated successfully"),
			@ApiResponse(code = 404, message = "Supplier not found") })
	public ResponseEntity<Supplier> updateSupplier(@PathVariable Long supplierId, @RequestBody Supplier updatedSupplier)
			throws NoSuchElementException {
		try {
			logger.info("Updating supplier with ID: {}", supplierId);
			Supplier updaSupplier = supplierService.updateSupplier(supplierId, updatedSupplier);
			return new ResponseEntity<>(updaSupplier, HttpStatus.OK);
		} catch (NoSuchElementException e) {
			logger.error("Supplier not found with ID: {}", supplierId);
			throw e;
		}
	}

	/**
	 * Retrieves a list of customers associated with a specific supplier.
	 *
	 * @param supplierId The ID of the supplier.
	 * @return A ResponseEntity containing the list of customers.
	 */
	@GetMapping("/{supplierId}")
	@ApiOperation(value = "Get customers by supplier ID", notes = "Retrieves a list of customers associated with a specific supplier.")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Customers retrieved successfully"),
			@ApiResponse(code = 404, message = "No customers found for the supplier") })
	public ResponseEntity<List<Customer>> getCustomersBySupplierId(@PathVariable Long supplierId) {
		logger.info("Retrieving customers for supplier with ID: {}", supplierId);
		List<Customer> customers = customerService.getCustomersBySupplierId(supplierId);
		if (!customers.isEmpty()) {
			return new ResponseEntity<>(customers, HttpStatus.OK);
		}
		logger.warn("No customer found with associated supplier");
		return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	}

	/**
	 * Retrieves a list of all suppliers.
	 *
	 * @return A ResponseEntity containing the list of suppliers.
	 */
	@GetMapping
	@ApiOperation(value = "Get all suppliers", notes = "Retrieves a list of all suppliers.")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Suppliers retrieved successfully"),
			@ApiResponse(code = 404, message = "No suppliers found") })
	public ResponseEntity<List<Supplier>> allSuppliers() {
		List<Supplier> suppliers = supplierService.getAllSuppliers();
		if (!suppliers.isEmpty()) {
			logger.info("Retrieved all suppliers. Count: {}", suppliers.size());
			return new ResponseEntity<>(suppliers, HttpStatus.OK);
		}
		logger.warn("No suppliers found");
		return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	}

	/**
	 * Retrieves a specific supplier by its ID.
	 *
	 * @param supplierId The ID of the supplier.
	 * @return The supplier object.
	 * @throws NoSuchElementException If no supplier exists with the given ID.
	 */
	@GetMapping("/supplier/{supplierId}")
	@ApiOperation(value = "Get supplier by ID", notes = "Retrieves a specific supplier by its ID.")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Supplier retrieved successfully"),
			@ApiResponse(code = 404, message = "Supplier not found") })
	public ResponseEntity<Supplier> getBySupplierId(@PathVariable Long supplierId) throws NoSuchElementException {
		try {
			logger.info("Retrieving supplier by ID: {}", supplierId);
			Supplier supplier = supplierService.getSupplierById(supplierId);
			return new ResponseEntity<>(supplier, HttpStatus.OK);
		} catch (NoSuchElementException e) {
			logger.error("Supplier not found with ID: {}", supplierId);
			throw e;
		}
	}

}
