package com.electric.serviceImpl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
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

import com.electric.entities.Meter;
import com.electric.repository.MeterRepository;

@SpringBootTest
public class MeterServiceImplTest {

	@Mock
	private MeterRepository meterRepository;

	@InjectMocks
	private MeterServiceImpl meterService;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	void testSaveMeter() {
		Meter meter = new Meter();
		meter.setMeterId(1L);
		meter.setLoadb(100);
		meter.setMinimumBillAmount(1000);

		when(meterRepository.save(meter)).thenReturn(meter);

		Meter savedMeter = meterService.saveMeter(meter);

		assertNotNull(savedMeter);
		assertEquals(1L, savedMeter.getMeterId());
		assertEquals(100, savedMeter.getLoadb());
		assertEquals(1000, savedMeter.getMinimumBillAmount());

		verify(meterRepository, times(1)).save(meter);
	}

	@Test
	public void testSaveMeter_ExceptionOccurred() {
		Meter meter = new Meter();
		when(meterRepository.save(meter)).thenThrow(new RuntimeException("Error saving meter"));

		assertThrows(RuntimeException.class, () -> meterService.saveMeter(meter));

		verify(meterRepository, times(1)).save(meter);
	}

	@Test
	void testGetAllMeters() {
		List<Meter> meters = new ArrayList<>();
		meters.add(new Meter(1L, 100, 1000 , null));
		meters.add(new Meter(2L, 200, 2000 , null));

		when(meterRepository.findAll()).thenReturn(meters);

		List<Meter> allMeters = meterService.getAllMeters();

		assertNotNull(allMeters);
		assertEquals(2, allMeters.size());

		verify(meterRepository, times(1)).findAll();
	}

	@Test
	void testGetMeterById() {
		Meter meter = new Meter(1L, 100, 1000);

		when(meterRepository.findById(1L)).thenReturn(Optional.of(meter));

		Meter foundMeter = meterService.getMeterById(1L);

		assertNotNull(foundMeter);
		assertEquals(1L, foundMeter.getMeterId());
		assertEquals(100, foundMeter.getLoadb());
		assertEquals(1000, foundMeter.getMinimumBillAmount());

		verify(meterRepository, times(1)).findById(1L);
	}

	@Test
    void testGetMeterByIdNotFound() {
        when(meterRepository.findById(1L)).thenReturn(Optional.empty());

        try {
            meterService.getMeterById(1L);
        } catch (NoSuchElementException exception) {
            assertEquals("Meter not found with ID: 1", exception.getMessage());
        }

        verify(meterRepository, times(1)).findById(1L);
    }

	@Test
	void testUpdateMeter() {
		Meter existingMeter = new Meter(1L, 100, 1000);
		Meter updatedMeter = new Meter(1L, 200, 2000);

		when(meterRepository.findById(1L)).thenReturn(Optional.of(existingMeter));
		when(meterRepository.save(existingMeter)).thenReturn(updatedMeter);

		Meter result = meterService.updateMeter(1L, updatedMeter);

		assertNotNull(result);
		assertEquals(1L, result.getMeterId());
		assertEquals(200, result.getLoadb());
		assertEquals(2000, result.getMinimumBillAmount());

		verify(meterRepository, times(1)).findById(1L);
		verify(meterRepository, times(1)).save(existingMeter);
	}

	@Test
	void testUpdateMeterNotFound() {
		Meter updatedMeter = new Meter(1L, 200, 2000);

		when(meterRepository.findById(1L)).thenReturn(Optional.empty());

		try {
			meterService.updateMeter(1L, updatedMeter);
		} catch (NoSuchElementException exception) {
			assertEquals("Meter not found with ID: 1", exception.getMessage());
		}

		verify(meterRepository, times(1)).findById(1L);
		verify(meterRepository, never()).save(any());
	}

	@Test
	void testDeleteMeter() {
		long meterId = 1L;

		meterService.deleteMeter(meterId);

		verify(meterRepository, times(1)).deleteById(meterId);
	}

}
