package com.electric.service;

import java.util.List;

import javax.validation.Valid;

import com.electric.entities.Supplier;

public interface SupplierService {

	Supplier saveSupplier(@Valid Supplier supplier);

	List<Supplier> getAllSuppliers();

	Supplier getSupplierById(Long supplierId);

	void deleteSupplierById(Long supplierId);

	Supplier updateSupplier(Long supplierId, Supplier updatedSupplier);

}
