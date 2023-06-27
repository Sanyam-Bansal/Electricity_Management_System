package com.electric.service;

import java.util.List;

import javax.validation.Valid;

import com.electric.entities.Customer;

public interface CustomerService {

	Customer saveCustomer(@Valid Customer customer);

	List<Customer> getAllCustomers();

	Customer getCustomerById(Long customerId);

	void deleteCustomerById(Long customerId);

	Customer updateCustomer(Customer customer, long cId);

	String calculateBillOfCustomer(long cId, int currentReading);

	List<Customer> getCustomersBySupplierId(Long supplierId);

}
