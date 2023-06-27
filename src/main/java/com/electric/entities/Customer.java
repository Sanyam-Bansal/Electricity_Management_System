package com.electric.entities;
import java.time.LocalDate;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuppressFBWarnings(value = {"EI_EXPOSE_REP", "EI_EXPOSE_REP2"})
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long customerId;

    @NotBlank(message = "Customer name is required")
    @Size(max = 100, message = "Customer name must be at most 100 characters")
    private String customerName;

    @NotBlank(message = "Customer address is required")
    @Size(max = 200, message = "Customer address must be at most 200 characters")
    private String customerAddress;

    @NotNull(message = "Connection date is required")
    private LocalDate connectionDate;

    @Min(value = 0, message = "Last reading must be a non-negative number")
    private int lastReading;

    @Min(value = 0, message = "Current reading must be a non-negative number")
    private int currentReading;

    @Min(value = 0, message = "Bill amount must be a non-negative number")
    private double billAmount;

    @ManyToOne
    @JoinColumn(name = "meter_id")
    @NotNull(message = "Meter ID is required")
    private Meter meter;

    @ManyToOne
    @JoinColumn(name = "supplier_id")
    @NotNull(message = "Supplier ID is required")
    private Supplier supplier; }
