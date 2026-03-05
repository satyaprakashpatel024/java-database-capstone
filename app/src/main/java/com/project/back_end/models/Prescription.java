package com.project.back_end.models;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import jakarta.validation.constraints.*;

@Builder
@Document(collection = "prescriptions")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Prescription {

    @Id
    private String id;

    @NotNull(message = "Patient name is required")
    @Size(min = 3, max = 100, message = "Patient name must be between 3 and 100 characters")
    private String patientName;

    @NotNull(message = "Appointment ID is required")
    private Long appointmentId;

    @NotNull(message = "Medication is required")
    @Size(min = 3, max = 100, message = "Medication name must be between 3 and 100 characters")
    private String medication;

    @NotNull(message = "Dosage is required")
    private String dosage;

    @Size(max = 200, message = "Doctor notes cannot exceed 200 characters")
    private String doctorNotes;

    @Min(value = 0, message = "Refill count cannot be negative")
    @Max(value = 12, message = "Refill count cannot exceed 12")
    private int refillCount;

    @Size(min = 3, max = 100, message = "Pharmacy name must be between 3 and 100 characters")
    private String pharmacyName;
}
