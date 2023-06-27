package com.electric.entities;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

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
public class Supplier {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long supplierId;

	@NotBlank(message = "Supplier name is required")
	@Size(max = 100, message = "Supplier name must be at most 100 characters")
	private String supplierName;

	@NotBlank(message = "Urban/Rural is required")
	@Size(max = 50, message = "Urban/Rural must be at most 50 characters")
	private String urbanRural;

	@OneToMany(mappedBy = "supplier")
	@JsonIgnore
	private List<Customer> customers;

	public Supplier(String supplierName, String urbanRural) {
		super();
		this.supplierName = supplierName;
		this.urbanRural = urbanRural;
	}

}
