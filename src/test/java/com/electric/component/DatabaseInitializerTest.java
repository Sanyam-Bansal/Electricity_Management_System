package com.electric.component;

import static org.mockito.ArgumentMatchers.any;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;

import com.electric.entities.Meter;
import com.electric.repository.MeterRepository;

@SpringBootTest
public class DatabaseInitializerTest {

	@Mock
	private MeterRepository meterRepository;

	private DatabaseInitializer databaseInitializer;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
		databaseInitializer = new DatabaseInitializer(meterRepository);
	}

	@Test
    void testRun_WithEmptyDatabase() throws Exception {
        when(meterRepository.count()).thenReturn(0L);

        Meter meter1 = new Meter(1, 500);
        Meter meter2 = new Meter(2, 700);
        Meter meter3 = new Meter(3, 1000);
        Meter meter4 = new Meter(4, 1250);

        databaseInitializer.run();

        verify(meterRepository, times(1)).count();
        verify(meterRepository, times(1)).save(meter1);
        verify(meterRepository, times(1)).save(meter2);
        verify(meterRepository, times(1)).save(meter3);
        verify(meterRepository, times(1)).save(meter4);
    }

	@Test
    void testRun_WithExistingData() throws Exception {
        when(meterRepository.count()).thenReturn(4L);

        databaseInitializer.run();

        verify(meterRepository, times(1)).count();
        verify(meterRepository, never()).save(any(Meter.class));
    }
}
