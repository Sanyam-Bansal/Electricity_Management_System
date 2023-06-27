package com.electric.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
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

import com.electric.entities.Meter;
import com.electric.service.MeterService;

@SpringBootTest
public class MeterControllerTest {

	@Mock
	private MeterService meterService;

	@InjectMocks
	private MeterController meterController;

	@BeforeEach
	void setup() {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	void testGetAllMeters() {
		Meter meter1 = new Meter(1L, 1, 500);
		Meter meter2 = new Meter(2L, 2, 700);
		Meter meter3 = new Meter(3L, 3, 1000);
		List<Meter> meters = Arrays.asList(meter1, meter2, meter3);

		when(meterService.getAllMeters()).thenReturn(meters);

		ResponseEntity<List<Meter>> response = meterController.allMeters();

		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals(meters, response.getBody());
	}

	@Test
	void testAllMeters_NotFound() {
		List<Meter> emptyMeterList = new ArrayList<>();
		ResponseEntity<List<Meter>> expectedResponse = new ResponseEntity<>(HttpStatus.NOT_FOUND);

		when(meterService.getAllMeters()).thenReturn(emptyMeterList);

		ResponseEntity<List<Meter>> actualResponse = meterController.allMeters();

		assertEquals(expectedResponse.getStatusCode(), actualResponse.getStatusCode());
		assertEquals(expectedResponse.getBody(), actualResponse.getBody());
		verify(meterService, times(1)).getAllMeters();
	}

	@Test
	void testCreateMeter() {
		Meter meter = new Meter(1, 500);

		when(meterService.saveMeter(meter)).thenReturn(meter);

		Meter createdMeter = meterController.createMeter(meter);

		assertNotNull(createdMeter);
		assertEquals(meter.getMeterId(), createdMeter.getMeterId());
		assertEquals(meter.getMinimumBillAmount(), createdMeter.getMinimumBillAmount());
	}

	@Test
	void testUpdateMeter_Success() throws NoSuchElementException {
		long meterId = 1L;
//		@SuppressWarnings("unused")
//		Meter existingMeter = new Meter(meterId, 1, 500);
		Meter updatedMeter = new Meter(meterId, 1, 700);

		when(meterService.updateMeter(meterId, updatedMeter)).thenReturn(updatedMeter);

		Meter updated = meterController.updateMeter(meterId, updatedMeter);

		assertNotNull(updated);
		assertEquals(updatedMeter.getMinimumBillAmount(), updated.getMinimumBillAmount());
		assertEquals(updatedMeter.getLoadb(), updated.getLoadb());
	}

	@Test
	void testUpdateMeter_NotFound() throws NoSuchElementException {
		long meterId = 1L;
		Meter updatedMeter = new Meter(meterId, 1, 700);

		when(meterService.updateMeter(meterId, updatedMeter))
				.thenThrow(new NoSuchElementException("No meter exists with given id. " + meterId));

		assertThrows(NoSuchElementException.class, () -> {
			meterController.updateMeter(meterId, updatedMeter);
		});
	}

	@Test
	void testDeleteMeter() {
		long meterId = 1L;

		String expectedMessage = "Meter with id: " + meterId + " is successfully deleted";

		String actualMessage = meterController.deleteMeter(meterId);

		assertEquals(expectedMessage, actualMessage);
		verify(meterService, times(1)).deleteMeter(meterId);
	}

}
