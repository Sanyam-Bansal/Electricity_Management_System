package com.electric.serviceImpl;

import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.electric.entities.Customer;
import com.electric.entities.Meter;
import com.electric.entities.Supplier;
import com.electric.repository.CustomerRepository;
import com.electric.repository.MeterRepository;
import com.electric.repository.SupplierRepository;
import com.electric.service.CustomerService;

/**
 * Implementation of the CustomerService interface that provides CRUD operations
 * and bill calculation for customers.
 */

@Service
public class CustomerServiceImpl implements CustomerService {
	private static final Logger logger = LoggerFactory.getLogger(CustomerServiceImpl.class);
	@Autowired
	private CustomerRepository customerRepository;
	@Autowired
	private MeterRepository meterRepository;
	@Autowired
	private SupplierRepository supplierRepository;

	/**
	 * Saves a new customer with the provided details.
	 *
	 * @param customer The customer object to be saved.
	 * @return The saved customer object.
	 */
	public Customer saveCustomer(@Valid Customer customer) {

		int load1 = customer.getMeter().getLoadb();
		Meter meter1 = meterRepository.findByLoadb(load1);

		if (meter1 != null) {
			customer.setMeter(meter1);
		} else {
			Meter meter = customer.getMeter();
			if (meter != null && meter.getMeterId() == null) {
				meter.setLoadb(customer.getMeter().getLoadb());
				meter.setMinimumBillAmount(1250);

				meterRepository.save(meter);
				customer.setMeter(meter);
			}
		}
		logger.info("Customer saved successfully. Customer ID: {}", customer.getCustomerId());
		return customerRepository.save(customer);
	}

	/**
	 * Retrieves all customers.
	 *
	 * @return A list of all customers.
	 */
	public List<Customer> getAllCustomers() {
//		return customerRepository.findAll();
		List<Customer> customers = customerRepository.findAll();
		logger.info("Retrieving all customers. Total count: {}", customers.size());
		return customers;
	}

	/**
	 * Retrieves a customer by their ID.
	 *
	 * @param customerId The ID of the customer to retrieve.
	 * @return The customer object with the specified ID.
	 * @throws NoSuchElementException If no customer is found with the specified ID.
	 */
	public Customer getCustomerById(Long customerId) throws NoSuchElementException {
//		return customerRepository.findById(customerId)
//				.orElseThrow(() -> new NoSuchElementException("Customer not found with ID: " + customerId));
		try {
			Customer customer = customerRepository.findById(customerId)
					.orElseThrow(() -> new NoSuchElementException("Customer not found with ID: " + customerId));
			logger.info("Retrieving customer by ID: {}", customerId);
			return customer;
		} catch (NoSuchElementException e) {
			logger.error("Error occurred while retrieving customer by ID: {}", e.getMessage());
			throw e;
		}
	}

	/**
	 * Deletes a customer by their ID.
	 *
	 * @param customerId The ID of the customer to delete.
	 * @throws NoSuchElementException If no customer is found with the specified ID.
	 */
	public void deleteCustomerById(Long customerId) {
		customerRepository.findById(customerId)
				.orElseThrow(() -> new NoSuchElementException("Customer not found with ID: " + customerId));
		customerRepository.deleteById(customerId);
	}

	/**
	 * Updates a customer with the provided details.
	 *
	 * @param customer The updated customer object.
	 * @param cId      The ID of the customer to update.
	 * @return The updated customer object.
	 * @throws NoSuchElementException If no customer is found with the specified ID.
	 */
	@Override
	public Customer updateCustomer(Customer customer, long cId) {
		try {
			customerRepository.findById(cId)
					.orElseThrow(() -> new NoSuchElementException("Customer not found with ID: " + cId));

			Customer dbCustomer = customerRepository.findById(cId).get();
			if (customer.getCurrentReading() != 0) {
				dbCustomer.setCurrentReading(customer.getCurrentReading());
			}
			if (customer.getCustomerAddress() != null) {
				dbCustomer.setCustomerAddress(customer.getCustomerAddress());
			}
			if (customer.getMeter() != null) {
				dbCustomer.setMeter(customer.getMeter());
			}
			if (customer.getCustomerName() != null) {
				dbCustomer.setCustomerName(customer.getCustomerName());
			}
			if (customer.getConnectionDate() != null) {
				dbCustomer.setConnectionDate(customer.getConnectionDate());
			}
			if (customer.getSupplier() != null) {
				dbCustomer.setSupplier(customer.getSupplier());
			}
			logger.info("Updating customer. Customer ID: {}", cId);
			return customerRepository.save(dbCustomer);
		} catch (NoSuchElementException e) {
			logger.error("Error occurred while updating customer: {}", e.getMessage());
			throw e;
		}
	}

	/**
	 * Calculates the bill amount for a customer based on the current reading.
	 *
	 * @param cId            The ID of the customer.
	 * @param currentReading The current reading of the customer's meter.
	 * @return A string containing the bill details.
	 * @throws NoSuchElementException If no customer is found with the specified ID.
	 */
	@Override
	public String calculateBillOfCustomer(long cId, int currentReading) {
		try {
			Customer dbCustomer = customerRepository.findById(cId)
					.orElseThrow(() -> new NoSuchElementException("Customer not found with ID: " + cId));
			int lastReading = dbCustomer.getCurrentReading();
			int unitsConsumed = currentReading - lastReading;
			double billAmount = 0;
			if (unitsConsumed <= 100) {
				billAmount = unitsConsumed * 3;
			} else if (unitsConsumed <= 200) {
				billAmount = 100 * 3 + (unitsConsumed - 100) * 5;
			} else if (unitsConsumed <= 300) {
				billAmount = 100 * 3 + 100 * 5 + (unitsConsumed - 200) * 6;
			} else if (unitsConsumed <= 400) {
				billAmount = 100 * 3 + 100 * 5 + 100 * 6 + (unitsConsumed - 300) * 7;
			} else if (unitsConsumed <= 500) {
				billAmount = 100 * 3 + 100 * 5 + 100 * 6 + 100 * 7 + (unitsConsumed - 400) * 7.5;
			} else {
				billAmount = 100 * 3 + 100 * 5 + 100 * 6 + 100 * 7 + 100 * 7.5 + (unitsConsumed - 500) * 8;
			}

			if (dbCustomer.getMeter().getMinimumBillAmount() > billAmount) {
				billAmount = dbCustomer.getMeter().getMinimumBillAmount();
			}
			dbCustomer.setLastReading(dbCustomer.getCurrentReading());
			dbCustomer.setCurrentReading(currentReading);
			dbCustomer.setBillAmount(billAmount);
			customerRepository.save(dbCustomer);
			logger.info("Calculating bill for customer. Customer ID: {}, Bill Amount: {}", cId, billAmount);
			return "the previous reading was:" + lastReading + "\ncurrent reading is " + currentReading
					+ "\ntotal bill amount is " + billAmount;
		} catch (NoSuchElementException e) {
			logger.error("Error occurred while calculating bill of the customer: {}", e.getMessage());
			throw e;
		}

	}

	/**
	 * Retrieves all customers associated with a specific supplier.
	 *
	 * @param supplierId The ID of the supplier.
	 * @return A list of customers associated with the specified supplier.
	 * @throws NoSuchElementException If no supplier is found with the specified ID.
	 */
	public List<Customer> getCustomersBySupplierId(Long supplierId) {

		Supplier supplier = supplierRepository.findById(supplierId)
				.orElseThrow(() -> new NoSuchElementException("Supplier not found with ID: " + supplierId));
		if (supplier != null) {
			return customerRepository.findBySupplier(supplier);
		}
		return Collections.emptyList();
	}

}
