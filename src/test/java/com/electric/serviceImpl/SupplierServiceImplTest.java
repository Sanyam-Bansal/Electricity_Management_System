package com.electric.serviceImpl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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

import com.electric.entities.Supplier;
import com.electric.repository.SupplierRepository;

@SpringBootTest
public class SupplierServiceImplTest {

	@Mock
	private SupplierRepository supplierRepository;

	@InjectMocks
	private SupplierServiceImpl supplierService;

	@BeforeEach
	public void setup() {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	public void testSaveSupplier() {
		Supplier supplier = new Supplier(1L , "Supplier A" , "Urban", null);
//		supplier.setSupplierId(1L);
//		supplier.setSupplierName("Supplier A");
//		supplier.setUrbanRural("Urban");

		when(supplierRepository.save(supplier)).thenReturn(supplier);

		Supplier savedSupplier = supplierService.saveSupplier(supplier);

		assertNotNull(savedSupplier);
		assertEquals("Supplier A", savedSupplier.getSupplierName());
		assertEquals("Urban", savedSupplier.getUrbanRural());

		verify(supplierRepository, times(1)).save(supplier);
	}
	
	 @Test
	    public void testSaveSupplier_Exception() {
	        Supplier supplier = new Supplier();
	        when(supplierRepository.save(supplier)).thenThrow(new RuntimeException("Error saving supplier"));

	        assertThrows(RuntimeException.class, () -> supplierService.saveSupplier(supplier));

	        verify(supplierRepository, times(1)).save(supplier);
	    }

	@Test
	public void testGetAllSuppliers() {
		List<Supplier> suppliers = new ArrayList<>();
		suppliers.add(new Supplier("Supplier A", "Urban"));
		suppliers.add(new Supplier("Supplier B", "Rural"));

		when(supplierRepository.findAll()).thenReturn(suppliers);

		List<Supplier> result = supplierService.getAllSuppliers();

		assertEquals(suppliers.size(), result.size());
		assertEquals("Supplier A", result.get(0).getSupplierName());
		assertEquals("Urban", result.get(0).getUrbanRural());
		assertEquals("Supplier B", result.get(1).getSupplierName());
		assertEquals("Rural", result.get(1).getUrbanRural());

		verify(supplierRepository, times(1)).findAll();
	}

	@Test
	public void testGetSupplierById() {
		Supplier supplier = new Supplier("Supplier A", "Urban");

		when(supplierRepository.findById(1L)).thenReturn(Optional.of(supplier));

		Supplier result = supplierService.getSupplierById(1L);

		assertNotNull(result);
		assertEquals("Supplier A", result.getSupplierName());
		assertEquals("Urban", result.getUrbanRural());

		verify(supplierRepository, times(1)).findById(1L);
	}

	@Test
    public void testGetSupplierById_NotFound() {
        when(supplierRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> supplierService.getSupplierById(1L));

        verify(supplierRepository, times(1)).findById(1L);
    }

	@Test
	public void testDeleteSupplierById() {
		supplierService.deleteSupplierById(1L);

		verify(supplierRepository, times(1)).deleteById(1L);
	}

	@Test
	public void testUpdateSupplier() {
		Supplier existingSupplier = new Supplier("Supplier A", "Urban");
		Supplier updatedSupplier = new Supplier("Updated Supplier", "Rural");

		when(supplierRepository.findById(1L)).thenReturn(Optional.of(existingSupplier));
		when(supplierRepository.save(existingSupplier)).thenReturn(existingSupplier);

		Supplier result = supplierService.updateSupplier(1L, updatedSupplier);

		assertNotNull(result);
		assertEquals("Updated Supplier", result.getSupplierName());
		assertEquals("Rural", result.getUrbanRural());

		verify(supplierRepository, times(1)).findById(1L);
		verify(supplierRepository, times(1)).save(existingSupplier);
	}

	@Test
	public void testUpdateSupplier_NotFound() {
		Supplier updatedSupplier = new Supplier("Updated Supplier", "Rural");

		when(supplierRepository.findById(1L)).thenReturn(Optional.empty());

		assertThrows(NoSuchElementException.class, () -> supplierService.updateSupplier(1L, updatedSupplier));

		verify(supplierRepository, times(1)).findById(1L);
	}
}
