package com.electric.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.electric.entities.Meter;

@Repository
public interface MeterRepository extends JpaRepository<Meter, Long> {

	Meter findByLoadb(int load1);

}
