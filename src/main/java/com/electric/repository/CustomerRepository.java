package com.electric.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.electric.entities.Customer;
import com.electric.entities.Supplier;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {

	List<Customer> findBySupplier(Supplier supplier);
	
	@Query("SELECT c FROM Customer c ORDER BY c.customerName")
    List<Customer> findByNameSorted();
	
	Customer findByCustomerName(String customerName);
}
