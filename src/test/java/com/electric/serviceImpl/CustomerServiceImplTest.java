package com.electric.serviceImpl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;

import com.electric.entities.Customer;
import com.electric.entities.Meter;
import com.electric.entities.Supplier;
import com.electric.repository.CustomerRepository;
import com.electric.repository.MeterRepository;
import com.electric.repository.SupplierRepository;

@SpringBootTest
public class CustomerServiceImplTest {

	@Mock
	private CustomerRepository customerRepository;

	@Mock
	private MeterRepository meterRepository;

	@Mock
	private SupplierRepository supplierRepository;

	@InjectMocks
	private CustomerServiceImpl customerService;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	void testGetCustomerById() {
		long customerId = 1L;
		Customer customer = new Customer();
		customer.setCustomerId(customerId);
		customer.setCustomerName("John Doe");
		customer.setCustomerAddress("123 Main St");

		when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));

		Customer foundCustomer = customerService.getCustomerById(customerId);

		assertNotNull(foundCustomer);
		assertEquals(customerId, foundCustomer.getCustomerId());
		assertEquals("John Doe", foundCustomer.getCustomerName());
		assertEquals("123 Main St", foundCustomer.getCustomerAddress());
	}

	@Test
	void testSaveCustomer() {
		Customer customer = new Customer();
		customer.setCustomerName("John Doe");
		customer.setCustomerAddress("123 Main St");
		customer.setMeter(new Meter());
		customer.getMeter().setMeterId(1L);
		customer.setSupplier(new Supplier());
		customer.getSupplier().setSupplierId(1L);

		Meter existingMeter = new Meter();
		existingMeter.setMeterId(1L);
		when(meterRepository.findById(1L)).thenReturn(Optional.of(existingMeter));

		Supplier existingSupplier = new Supplier();
		existingSupplier.setSupplierId(1L);
		when(supplierRepository.findById(1L)).thenReturn(Optional.of(existingSupplier));

		when(customerRepository.save(customer)).thenReturn(customer);

		Customer savedCustomer = customerService.saveCustomer(customer);
		assertEquals(customer, savedCustomer);
		assertEquals(customer.getCustomerName(), savedCustomer.getCustomerName());
		assertEquals(existingMeter, savedCustomer.getMeter());
	}

	@Test
	void testSaveCustomer_WithExistingMeter() {
		Customer customer = new Customer();
		customer.setCustomerId(1L);
		customer.setCustomerName("John Doe");
		customer.setMeter(new Meter());
		customer.getMeter().setLoadb(100);

		Meter existingMeter = new Meter();
		existingMeter.setMeterId(1L);
		existingMeter.setLoadb(100);

		when(meterRepository.findByLoadb(100)).thenReturn(existingMeter);
		when(customerRepository.save(customer)).thenReturn(customer);

		Customer savedCustomer = customerService.saveCustomer(customer);

		assertNotNull(savedCustomer);
		assertEquals(existingMeter, savedCustomer.getMeter());

		verify(meterRepository, never()).save(existingMeter);
		verify(customerRepository).save(customer);
	}

	@Test
	void saveCustomer_CreatesNewMeterAndUpdatesCustomer() {

		Customer customer = new Customer();
		customer.setMeter(new Meter());
		customer.getMeter().setLoadb(456);

		when(meterRepository.findByLoadb(customer.getMeter().getLoadb())).thenReturn(null);

		Meter meter = new Meter();
		meter.setLoadb(customer.getMeter().getLoadb());
		meter.setMinimumBillAmount(1250);

		when(meterRepository.save(meter)).thenReturn(meter);
		when(customerRepository.save(customer)).thenReturn(customer);

		Customer result = customerService.saveCustomer(customer);

		assertEquals(customer.getMeter().getLoadb(), result.getMeter().getLoadb());
		assertEquals(1250, result.getMeter().getMinimumBillAmount());
	}

	@Test
	void testSaveCustomer_NotValid() {
		Customer customer = new Customer();
		customer.setCustomerId(1L);
		customer.setCustomerName("John Doe");
		customer.setCurrentReading(-100);

		when(customerRepository.save(customer)).thenReturn(null);

		verify(customerRepository, never()).save(customer);

	}

	@Test
	void testGetCustomerById_NotFound() {
		long customerId = 1L;

		when(customerRepository.findById(customerId)).thenReturn(Optional.empty());

		assertThrows(NoSuchElementException.class, () -> customerService.getCustomerById(customerId));
	}

	@Test
	void testGetAllCustomers() {
		List<Customer> customers = new ArrayList<>();
		Customer customer = new Customer();
		customer.setCustomerId(1L);
		customer.setCustomerName("John Doe");
		customer.setCustomerAddress("123 Main St");
		customer.setEmail("john02@gmail.com");
		Meter meter = new Meter(1L, 100, 1000, null);
		Supplier supplier = new Supplier(1L, "ABC Power", "Urban", null);
		Customer customer2 = new Customer(2L, "Jane Smith", "456 Elm St", LocalDate.parse("2023-09-09"), 0, 100, 0,
				"jane02@gmail.com", meter, supplier);
		customers.add(customer);
		customers.add(customer2);

		when(customerRepository.findByNameSorted()).thenReturn(customers);

		List<Customer> result = customerService.getAllCustomers();

		assertNotNull(result);
		assertEquals(2, result.size());
		assertEquals("John Doe", result.get(0).getCustomerName());
		assertEquals("123 Main St", result.get(0).getCustomerAddress());
		assertEquals("Jane Smith", result.get(1).getCustomerName());
		assertEquals("456 Elm St", result.get(1).getCustomerAddress());
	}

	@Test
	public void testGetCustomerEmailById_Found() {
		long customerId = 1L;
		String expectedEmail = "test@example.com";

		Customer customer = new Customer();
		customer.setEmail(expectedEmail);

		when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));

		String actualEmail = customerService.getCustomerEmailById(customerId);

		assertEquals(expectedEmail, actualEmail);
	}

	@Test
	public void testGetCustomerEmailById_NotFound() {
		long customerId = 1L;

		when(customerRepository.findById(customerId)).thenReturn(Optional.empty());

		assertThrows(NoSuchElementException.class, () -> {
			customerService.getCustomerEmailById(customerId);
		});

	}

	@Test
	void testDeleteCustomerById() {
		long customerId = 1L;
		Customer customer = new Customer();
		customer.setCustomerId(customerId);

		when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));

		customerService.deleteCustomerById(customerId);

		verify(customerRepository).deleteById(customerId);
	}

	@Test
	void testDeleteCustomerById_NotFound() {
		long customerId = 1L;

		when(customerRepository.findById(customerId)).thenReturn(Optional.empty());

		assertThrows(NoSuchElementException.class, () -> customerService.deleteCustomerById(customerId));

		verify(customerRepository, never()).deleteById(customerId);
	}

	@Test
	void testUpdateCustomer() {
		long customerId = 1L;
		Customer existingCustomer = new Customer();
		existingCustomer.setCustomerId(customerId);
		existingCustomer.setCustomerName("John Doe");
		existingCustomer.setCustomerAddress("123 Main St");
		existingCustomer.setConnectionDate(LocalDate.parse("2023-09-02"));
		existingCustomer.setCurrentReading(100);
		existingCustomer.setMeter(new Meter());
		existingCustomer.getMeter().setLoadb(1);
		existingCustomer.setSupplier(new Supplier());
		existingCustomer.getSupplier().setSupplierId(1L);

		Customer updatedCustomer = new Customer();
		updatedCustomer.setCustomerName("Jane Smith");
		updatedCustomer.setCustomerAddress("345 Sant st");
		updatedCustomer.setConnectionDate(LocalDate.parse("2023-09-10"));
		updatedCustomer.setCurrentReading(110);
		updatedCustomer.setMeter(new Meter());
		updatedCustomer.getMeter().setLoadb(2);
		updatedCustomer.setSupplier(new Supplier());
		updatedCustomer.getSupplier().setSupplierId(2L);

		when(customerRepository.findById(customerId)).thenReturn(Optional.of(existingCustomer));
		when(customerRepository.save(existingCustomer)).thenReturn(existingCustomer);

		Customer result = customerService.updateCustomer(updatedCustomer, customerId);

		assertNotNull(result);
		assertEquals(customerId, result.getCustomerId());
		assertEquals("Jane Smith", result.getCustomerName());
		assertEquals("345 Sant st", result.getCustomerAddress());
		assertEquals(LocalDate.parse("2023-09-10"), result.getConnectionDate());
		assertEquals(110, result.getCurrentReading());
		assertEquals(2, result.getMeter().getLoadb());
		assertEquals(2, result.getSupplier().getSupplierId());
	}

	@Test
	void testUpdateCustomer_NotFound() {
		long customerId = 1L;
		Customer updatedCustomer = new Customer();
		updatedCustomer.setCustomerName("Jane Smith");

		when(customerRepository.findById(customerId)).thenReturn(Optional.empty());

		assertThrows(NoSuchElementException.class, () -> customerService.updateCustomer(updatedCustomer, customerId));
	}

	@Test
	void testCalculateBillOfCustomer() {
		long customerId = 1L;
		int currentReading = 1000;

		Customer customer = new Customer();
		customer.setCustomerId(customerId);
		customer.setCustomerName("John Doe");
		customer.setCustomerAddress("123 Main St");
		customer.setMeter(new Meter());
		customer.getMeter().setMinimumBillAmount(700);
		customer.setCurrentReading(500);

		when(customerRepository.findById(customerId)).thenReturn(java.util.Optional.of(customer));

		String billDetails = customerService.calculateBillOfCustomer(customerId, currentReading);

		verify(customerRepository).save(customer);

		String expectedBillDetails = "the previous reading was:500\ncurrent reading is 1000\ntotal bill amount is 2850.0";
		assertEquals(expectedBillDetails, billDetails);
		assertEquals(1000, customer.getCurrentReading());
		assertEquals(2850.0, customer.getBillAmount());
	}

	@Test
	void testCalculateBillOfCustomer_NotFound() {
		long customerId = 1L;
		int currentReading = 300;

		when(customerRepository.findById(customerId)).thenReturn(Optional.empty());

		assertThrows(NoSuchElementException.class,
				() -> customerService.calculateBillOfCustomer(customerId, currentReading));
	}

	@Test
	void testGetCustomersBySupplierId() {
		long supplierId = 1L;
		Supplier supplier = new Supplier();
		supplier.setSupplierId(supplierId);

		Customer customer1 = new Customer();
		customer1.setCustomerName("John Doe");

		Customer customer2 = new Customer();
		customer2.setCustomerName("Jane Smith");

		List<Customer> customers = new ArrayList<>();
		customers.add(customer1);
		customers.add(customer2);

		when(supplierRepository.findById(supplierId)).thenReturn(Optional.of(supplier));
		when(customerRepository.findBySupplier(supplier)).thenReturn(customers);

		List<Customer> foundCustomers = customerService.getCustomersBySupplierId(supplierId);

		assertNotNull(foundCustomers);
		assertEquals(2, foundCustomers.size());
		assertEquals("John Doe", foundCustomers.get(0).getCustomerName());
		assertEquals("Jane Smith", foundCustomers.get(1).getCustomerName());
	}

	@Test
	void testGetCustomersBySupplierId_SupplierNotFound() {
		long supplierId = 1L;

		when(supplierRepository.findById(supplierId)).thenReturn(Optional.empty());

		assertThrows(NoSuchElementException.class, () -> customerService.getCustomersBySupplierId(supplierId));
	}
}
