package com.electric.serviceImpl;

import java.util.List;
import java.util.NoSuchElementException;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.electric.entities.Supplier;
import com.electric.repository.SupplierRepository;
import com.electric.service.SupplierService;

/**
 * Implementation of the SupplierService interface that provides CRUD operations
 * for suppliers.
 */
@Service
public class SupplierServiceImpl implements SupplierService {
	private static final Logger logger = LoggerFactory.getLogger(MeterServiceImpl.class);

	@Autowired
	private SupplierRepository supplierRepository;

	/**
	 * Saves a new supplier with the provided details.
	 *
	 * @param supplier The supplier object to be saved.
	 * @return The saved supplier object.
	 */
	public Supplier saveSupplier(@Valid Supplier supplier) {
//		return supplierRepository.save(supplier);
		try {
			logger.info("Saving the Supplier");
			return supplierRepository.save(supplier);
		} catch (Exception e) {
			logger.error("Error occurred while saving supplier: {}", e.getMessage());
			throw e;
		}
	}

	/**
	 * Retrieves all suppliers.
	 *
	 * @return A list of all suppliers.
	 */
	public List<Supplier> getAllSuppliers() {
		logger.info("Retrieving all Suppliers.");
		return supplierRepository.findAll();
	}

	/**
	 * Retrieves a supplier by its ID.
	 *
	 * @param supplierId The ID of the supplier to retrieve.
	 * @return The supplier object with the specified ID.
	 * @throws NoSuchElementException If no supplier is found with the specified ID.
	 */
	public Supplier getSupplierById(Long supplierId) throws NoSuchElementException {
		logger.info("Retrieving supplier with ID: {}", supplierId);
		Supplier supplier = supplierRepository.findById(supplierId).orElse(null);
		if (supplier == null) {
			logger.error("Error occurred while retrieving supplier by ID: {}",supplierId);
			throw new NoSuchElementException("No supplier exists with given id. " + supplierId);
		}
		return supplier;

	}

	/**
	 * Deletes a supplier by its ID.
	 *
	 * @param supplierId The ID of the supplier to delete.
	 */
	public void deleteSupplierById(Long supplierId) {
		logger.info("Deleting the supplier th ID : {}" , supplierId);
		supplierRepository.deleteById(supplierId);
	}

	/**
	 * Updates a supplier with the provided details.
	 *
	 * @param supplierId      The ID of the supplier to update.
	 * @param updatedSupplier The updated supplier object.
	 * @return The updated supplier object.
	 * @throws NoSuchElementException If no supplier is found with the specified ID.
	 */
	public Supplier updateSupplier(Long supplierId, Supplier updatedSupplier) throws NoSuchElementException {
		
		Supplier existingSupplier = supplierRepository.findById(supplierId).orElse(null);
		if (existingSupplier == null) {
			logger.error("Error occurred while retrieving supplier by ID: {}",supplierId);
			throw new NoSuchElementException("No supplier exists with given id. " + supplierId);
		} else {
			logger.info("Updating supplier with ID: {}", supplierId);
			existingSupplier.setSupplierName(updatedSupplier.getSupplierName());
			existingSupplier.setUrbanRural(updatedSupplier.getUrbanRural());

			return supplierRepository.save(existingSupplier);
		}
	}

}
