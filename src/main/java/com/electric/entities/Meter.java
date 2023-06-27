package com.electric.entities;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.validation.constraints.Min;

import com.fasterxml.jackson.annotation.JsonIgnore;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuppressFBWarnings(value = { "EI_EXPOSE_REP", "EI_EXPOSE_REP2" })
public class Meter {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long meterId;

	@Min(value = 0, message = "loadb must be a positive number")
	private int loadb;

	@Min(value = 0, message = "Minimum bill amount must be a positive number")
	private int minimumBillAmount;

	@OneToMany(mappedBy = "meter")
	@JsonIgnore
	private List<Customer> customers;

	public Meter(int loadb, int minimumBillAmount) {
		this.loadb = loadb;
		this.minimumBillAmount = minimumBillAmount;
	}

	public Meter(Long meterId, @Min(value = 0, message = "loadb must be a positive number") int loadb,
			@Min(value = 0, message = "Minimum bill amount must be a positive number") int minimumBillAmount) {
		super();
		this.meterId = meterId;
		this.loadb = loadb;
		this.minimumBillAmount = minimumBillAmount;
	}

}
