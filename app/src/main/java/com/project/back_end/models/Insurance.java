package com.project.back_end.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

@Entity
@Table(name = "insurance")
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Insurance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Patient is required")
    @ManyToOne
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @NotNull(message = "Insurance company is required")
    @Size(min = 3, max = 100, message = "Insurance company name must be between 3 and 100 characters")
    @Column(nullable = false)
    private String insuranceCompany;

    @NotNull(message = "Policy number is required")
    @Size(min = 5, max = 50, message = "Policy number must be between 5 and 50 characters")
    @Column(nullable = false, unique = true)
    private String policyNumber;

    @Size(max = 50, message = "Group number cannot exceed 50 characters")
    @Column
    private String groupNumber;

    @NotNull(message = "Plan type is required")
    @Size(min = 2, max = 50, message = "Plan type must be between 2 and 50 characters")
    @Column(nullable = false)
    private String planType;

    @NotNull(message = "Copay amount is required")
    @Min(value = 0, message = "Copay cannot be negative")
    @Column(nullable = false)
    private Double copay;

    @NotNull(message = "Deductible amount is required")
    @Min(value = 0, message = "Deductible cannot be negative")
    @Column(nullable = false)
    private Double deductible;

    @NotNull(message = "Maximum out-of-pocket is required")
    @Min(value = 0, message = "Maximum out-of-pocket cannot be negative")
    @Column(nullable = false)
    private Double maxOutOfPocket;

    @Column(nullable = false, columnDefinition = "BOOLEAN DEFAULT TRUE")
    private boolean activeStatus = true;
}
