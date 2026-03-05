package com.project.back_end.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "doctors")
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Doctor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Name is required")
    @Size(min = 3, max = 100, message = "Name must be between 3 and 100 characters")
    @Column(nullable = false)
    private String name;

    @NotNull(message = "Specialty is required")
    @Size(min = 3, max = 50, message = "Specialty must be between 3 and 50 characters")
    @Column(nullable = false)
    private String specialty;

    @NotNull(message = "Email is required")
    @Email(message = "Email should be valid")
    @Column(nullable = false, unique = true)
    private String email;

    @NotNull(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Column(nullable = false)
    private String password;

    @NotNull(message = "Phone number is required")
    @Pattern(regexp = "^[0-9]{10}$", message = "Phone number must be exactly 10 digits")
    @Column(nullable = false)
    private String phone;

    @ElementCollection
    @CollectionTable(name = "doctor_available_times", joinColumns = @JoinColumn(name = "doctor_id"))
    @Column(name = "available_time")
    private List<String> availableTimes;

    @Min(value = 0, message = "Years of experience cannot be negative")
    @Max(value = 70, message = "Years of experience cannot exceed 70")
    @Column(nullable = false)
    private int yearsOfExperience;

    @NotNull(message = "Clinic address is required")
    @Size(min = 10, max = 255, message = "Clinic address must be between 10 and 255 characters")
    @Column(nullable = false)
    private String clinicAddress;

    @Min(value = 0, message = "Rating cannot be less than 0")
    @Max(value = 5, message = "Rating cannot exceed 5")
    @Column
    private Double rating;
}
