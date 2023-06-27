package com.electric.component;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.electric.entities.Meter;
import com.electric.repository.MeterRepository;

@Component
public class DatabaseInitializer implements CommandLineRunner {

	private final MeterRepository meterRepository;

	@Autowired
	public DatabaseInitializer(MeterRepository meterRepository) {
		this.meterRepository = meterRepository;
	}

	@Override
	@Transactional
	public void run(String... args) throws Exception {
		if (meterRepository.count() == 0) {
			Meter meter1 = new Meter(1, 500);
			Meter meter2 = new Meter(2, 700);
			Meter meter3 = new Meter(3, 1000);
			Meter meter4 = new Meter(4, 1250);

			meterRepository.save(meter1);
			meterRepository.save(meter2);
			meterRepository.save(meter3);
			meterRepository.save(meter4);
		}
	}
}
