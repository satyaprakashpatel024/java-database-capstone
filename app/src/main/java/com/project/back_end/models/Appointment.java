package com.project.back_end.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;
import java.time.LocalDate;
import java.time.LocalTime;

@Builder
@Entity
@Table(name = "appointments")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Appointment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Doctor is required")
    @ManyToOne
    @JoinColumn(name = "doctor_id", nullable = false)
    private Doctor doctor;

    @NotNull(message = "Patient is required")
    @ManyToOne
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @NotNull(message = "Appointment time is required")
    @Future(message = "Appointment time must be in the future")
    @Column(nullable = false)
    private LocalDateTime appointmentTime;

    @NotNull(message = "Status is required")
    @Column(nullable = false)
    private int status;

    @NotNull(message = "Reason for visit is required")
    @Size(min = 5, max = 200, message = "Reason for visit must be between 5 and 200 characters")
    @Column(nullable = false)
    private String reasonForVisit;

    @Size(max = 500, message = "Notes cannot exceed 500 characters")
    @Column
    private String notes;


    public Appointment(Doctor doctor, Patient patient, LocalDateTime appointmentTime, int status, String reasonForVisit, String notes) {
        this.doctor = doctor;
        this.patient = patient;
        this.appointmentTime = appointmentTime;
        this.status = status;
        this.reasonForVisit = reasonForVisit;
        this.notes = notes;
    }

    public LocalDateTime getEndTime() {
        return appointmentTime.plusHours(1);
    }

    public LocalDate getAppointmentDate() {
        return appointmentTime.toLocalDate();
    }

    public LocalTime getAppointmentTimeOnly() {
        return appointmentTime.toLocalTime();
    }
}
