package com.electric.controller;

import java.util.List;
import java.util.NoSuchElementException;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.electric.entities.Meter;
import com.electric.service.MeterService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

/**
 * Controller class that handles HTTP requests related to meter management.
 */
@RestController
@RequestMapping("/meters")
@Api(value = "Meter Controller", tags = "Meter API")
public class MeterController {
	private static final Logger logger = LoggerFactory.getLogger(MeterController.class);
	@Autowired
	private MeterService meterService;

	/**
	 * Creates a new meter entity.
	 *
	 * @param meter The meter object to be created.
	 * @return The created meter object.
	 */
	@PostMapping
	@ApiOperation(value = "Create a new meter", notes = "Creates a new meter entity.")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Meter created successfully") })
	public Meter createMeter(@Valid @RequestBody Meter meter) {
//		return meterService.saveMeter(meter);
		Meter createdMeter = meterService.saveMeter(meter);
		logger.info("Created meter with ID: {}", createdMeter.getMeterId());
		return createdMeter;

	}

	/**
	 * Retrieves a list of all meters.
	 *
	 * @return A ResponseEntity containing the list of meters.
	 */
	@GetMapping
	@ApiOperation(value = "Get all meters", notes = "Retrieves a list of all meters.")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Meters retrieved successfully"),
			@ApiResponse(code = 404, message = "No meters found") })
	public ResponseEntity<List<Meter>> allMeters() {
		List<Meter> meters = meterService.getAllMeters();
		if (!meters.isEmpty()) {
			logger.info("Retrieved all meters. Count: {}", meters.size());
			return new ResponseEntity<>(meters, HttpStatus.OK);
		} else {
			logger.warn("No meters found");
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}

	/**
	 * Updates an existing meter entity.
	 *
	 * @param meterId The ID of the meter to update.
	 * @param meter   The updated meter object.
	 * @return The updated meter object.
	 */
	@PatchMapping("/{meterId}")
	@ApiOperation(value = "Update a meter", notes = "Updates an existing meter entity.")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Meter updated successfully"),
			@ApiResponse(code = 404, message = "Meter not found") })
	public Meter updateMeter(@PathVariable long meterId, @RequestBody Meter meter) {
		try {
			Meter updatedMeter = meterService.updateMeter(meterId, meter);
			logger.info("Updated meter with ID: {}", meterId);
			return updatedMeter;
		} catch (NoSuchElementException e) {
			logger.error("Meter not found with ID: {}", meterId);
			throw e;
		}
	}

	/**
	 * Deletes a meter entity by its ID.
	 *
	 * @param meterId The ID of the meter to delete.
	 * @return A string indicating the success of the deletion operation.
	 */
	@DeleteMapping("/{meterId}")
	@ApiOperation(value = "Delete a meter", notes = "Deletes a meter entity by its ID.")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Meter deleted successfully"),
			@ApiResponse(code = 404, message = "Meter not found") })
	public String deleteMeter(@PathVariable long meterId) {
		meterService.deleteMeter(meterId);
		logger.info("Deleted meter with ID: {}", meterId);
		return "Meter with id: " + meterId + " is successfully deleted";
	}

}
