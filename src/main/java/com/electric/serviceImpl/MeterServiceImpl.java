package com.electric.serviceImpl;

import java.util.List;
import java.util.NoSuchElementException;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.electric.entities.Meter;
import com.electric.repository.MeterRepository;
import com.electric.service.MeterService;

/**
 * Implementation of the MeterService interface that provides CRUD operations
 * for meters.
 */
@Service
public class MeterServiceImpl implements MeterService {
	private static final Logger logger = LoggerFactory.getLogger(MeterServiceImpl.class);
	@Autowired
	private MeterRepository meterRepository;

	/**
	 * Saves a new meter with the provided details.
	 *
	 * @param meter The meter object to be saved.
	 * @return The saved meter object.
	 */
	public Meter saveMeter(@Valid Meter meter) {
//		return meterRepository.save(meter);
		try {
			logger.info("Saving the meter");
			return meterRepository.save(meter);
		} catch (Exception e) {
			logger.error("Error occurred while saving meter: {}", e.getMessage());
			throw e;
		}
	}

	/**
	 * Retrieves all meters.
	 *
	 * @return A list of all meters.
	 */
	public List<Meter> getAllMeters() {
		logger.info("Retrieving all meters.");
		return meterRepository.findAll();
	}

	/**
	 * Retrieves a meter by its ID.
	 *
	 * @param meterId The ID of the meter to retrieve.
	 * @return The meter object with the specified ID.
	 * @throws NoSuchElementException If no meter is found with the specified ID.
	 */
	public Meter getMeterById(Long meterId) {
//		return meterRepository.findById(meterId)
//				.orElseThrow(() -> new NoSuchElementException("Meter not found with ID: " + meterId));

		try {
			return meterRepository.findById(meterId)
					.orElseThrow(() -> new NoSuchElementException("Meter not found with ID: " + meterId));
		} catch (NoSuchElementException e) {
			logger.error("Error occurred while retrieving meter by ID: {}", e.getMessage());
			throw e;
		}
	}

	/**
	 * Updates a meter with the provided details.
	 *
	 * @param meterId The ID of the meter to update.
	 * @param meter   The updated meter object.
	 * @return The updated meter object.
	 * @throws NoSuchElementException If no meter is found with the specified ID.
	 */
	@Override
	public Meter updateMeter(long meterId, Meter meter) {
		try {
			Meter existingMeter = meterRepository.findById(meterId)
					.orElseThrow(() -> new NoSuchElementException("Meter not found with ID: " + meterId));
			if (existingMeter != null) {
				logger.info("Updating meter with ID: {}", meterId);
				existingMeter.setLoadb(meter.getLoadb());
				existingMeter.setMinimumBillAmount(meter.getMinimumBillAmount());
				return meterRepository.save(existingMeter);
			}
			return existingMeter;
		} catch (NoSuchElementException e) {
			logger.error("Error occurred while updating meter: {}", e.getMessage());
			throw e;
		}
	}

	/**
	 * Deletes a meter by its ID.
	 *
	 * @param meterId The ID of the meter to delete.
	 */
	@Override
	public void deleteMeter(long meterId) {
		logger.info("Deleting meter with ID: {}", meterId);
		meterRepository.deleteById(meterId);
	}

}
