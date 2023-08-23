
package com.electric.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.electric.entities.Customer;
import com.electric.entities.Supplier;
import com.electric.service.CustomerService;
import com.electric.service.SupplierService;

@SpringBootTest
public class SupplierControllerTest {

	@Mock
	private SupplierService supplierService;

	@Mock
	private CustomerService customerService;

	@InjectMocks
	private SupplierController supplierController;

	@BeforeEach
	void setup() {
		MockitoAnnotations.openMocks(this);

	}

	@Test
	void testCreateSupplier() {
		Supplier supplier = new Supplier();
		supplier.setSupplierId(1L);
		supplier.setSupplierName("Supplier 1");
		supplier.setUrbanRural("Urban");

		when(supplierService.saveSupplier(supplier)).thenReturn(supplier);

		ResponseEntity<Supplier> createdSupplier = supplierController.createSupplier(supplier);

		assertEquals(supplier.getSupplierId(), createdSupplier.getBody().getSupplierId());
		assertEquals(supplier.getSupplierName(), createdSupplier.getBody().getSupplierName());
		assertEquals(supplier.getUrbanRural(), createdSupplier.getBody().getUrbanRural());

		verify(supplierService, times(1)).saveSupplier(supplier);
	}

	@Test
	void testUpdateSupplier() {
		Long supplierId = 1L;
		Supplier updatedSupplier = new Supplier();
		updatedSupplier.setSupplierId(supplierId);
		updatedSupplier.setSupplierName("Updated Supplier");

		when(supplierService.updateSupplier(supplierId, updatedSupplier)).thenReturn(updatedSupplier);

		ResponseEntity<Supplier> updated = supplierController.updateSupplier(supplierId, updatedSupplier);

		assertEquals(updatedSupplier.getSupplierId(), updated.getBody().getSupplierId());
		assertEquals(updatedSupplier.getSupplierName(), updated.getBody().getSupplierName());

		verify(supplierService, times(1)).updateSupplier(supplierId, updatedSupplier);
	}

	@Test
	void testUpdateSupplier_InvalidSupplierId() {
		Long supplierId = 100L;
		Supplier updatedSupplier = new Supplier();
		updatedSupplier.setSupplierId(supplierId);
		updatedSupplier.setSupplierName("Updated Supplier");

		when(supplierService.updateSupplier(supplierId, updatedSupplier)).thenThrow(NoSuchElementException.class);

		assertThrows(NoSuchElementException.class,
				() -> supplierController.updateSupplier(supplierId, updatedSupplier));

		verify(supplierService, times(1)).updateSupplier(supplierId, updatedSupplier);
	}

	@Test
	void testGetCustomersBySupplierId_Success() {
		Long supplierId = 1L;
		Customer customer1 = new Customer();
		customer1.setCustomerId(1L);
		customer1.setCustomerName("Customer 1");
		Customer customer2 = new Customer();
		customer2.setCustomerId(2L);
		customer2.setCustomerName("Customer 2");
		List<Customer> customers = new ArrayList<>();
		customers.add(customer1);
		customers.add(customer2);

		when(customerService.getCustomersBySupplierId(supplierId)).thenReturn(customers);

		ResponseEntity<List<Customer>> response = supplierController.getCustomersBySupplierId(supplierId);

		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals(customers, response.getBody());

		verify(customerService, times(1)).getCustomersBySupplierId(supplierId);
	}

	@Test
	void testGetCustomersBySupplierId_NotFound() {
		Long supplierId = 1L;

		when(customerService.getCustomersBySupplierId(supplierId)).thenReturn(new ArrayList<>());

		ResponseEntity<List<Customer>> response = supplierController.getCustomersBySupplierId(supplierId);

		assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
	}

	@Test
	void testAllSuppliers_Success() {
		Supplier supplier1 = new Supplier();
		supplier1.setSupplierId(1L);
		supplier1.setSupplierName("Supplier 1");
		Supplier supplier2 = new Supplier();
		supplier2.setSupplierId(2L);
		supplier2.setSupplierName("Supplier 2");
		List<Supplier> suppliers = new ArrayList<>();
		suppliers.add(supplier1);
		suppliers.add(supplier2);

		when(supplierService.getAllSuppliers()).thenReturn(suppliers);

		ResponseEntity<List<Supplier>> response = supplierController.allSuppliers();

		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals(suppliers, response.getBody());

		verify(supplierService, times(1)).getAllSuppliers();
	}

	@Test
    void testAllSuppliers_NotFound() {
        when(supplierService.getAllSuppliers()).thenReturn(new ArrayList<>());

        ResponseEntity<List<Supplier>> response = supplierController.allSuppliers();

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());

        verify(supplierService, times(1)).getAllSuppliers();
    }

	@Test
	void testGetBySupplierId_Success() {
		long supplierId = 1L;
		Supplier supplier = new Supplier();
		supplier.setSupplierId(supplierId);
		supplier.setSupplierName("Supplier 1");

		when(supplierService.getSupplierById(supplierId)).thenReturn(supplier);

		ResponseEntity<Supplier> result = supplierController.getBySupplierId(supplierId);

		assertEquals(supplier, result.getBody());

		verify(supplierService, times(1)).getSupplierById(supplierId);
	}

	@Test
	void testGetBySupplierId_NotFound() {
		long supplierId = 1L;

		when(supplierService.getSupplierById(supplierId))
				.thenThrow(new NoSuchElementException("No supplier exists with given id. " + supplierId));

		assertThrows(NoSuchElementException.class, () -> {
			supplierController.getBySupplierId(supplierId);
		});
	}

}
