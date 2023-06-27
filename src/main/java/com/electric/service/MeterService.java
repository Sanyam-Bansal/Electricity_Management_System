package com.electric.service;

import java.util.List;

import javax.validation.Valid;

import com.electric.entities.Meter;

public interface MeterService {

	Meter saveMeter(@Valid Meter meter);

	List<Meter> getAllMeters();

	Meter getMeterById(Long meterId);

	Meter updateMeter(long meterId, Meter meter);

	void deleteMeter(long meterId);

}
