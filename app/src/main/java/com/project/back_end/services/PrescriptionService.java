package com.project.back_end.services;

import com.project.back_end.DTO.AppConstant;
import com.project.back_end.models.Prescription;
import com.project.back_end.repo.PrescriptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Service
public class PrescriptionService {

    private final PrescriptionRepository prescriptionRepository;

    public ResponseEntity<Map<String, String>> savePrescription(Prescription prescription) {
        Map<String, String> response = new HashMap<>();
        try {
            List<Prescription> existingPrescription = prescriptionRepository.findByAppointmentId(prescription.getAppointmentId());
            if (existingPrescription != null) {
                response.put(AppConstant.MESSAGE, "Prescription already exists for this appointment");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }

            prescriptionRepository.save(prescription);
            response.put(AppConstant.MESSAGE, "Prescription saved");
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            response.put(AppConstant.MESSAGE, "An error occurred while saving prescription");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    public ResponseEntity<Map<String, Object>> getPrescription(Long appointmentId) {
        Map<String, Object> response = new HashMap<>();
        try {
            List<Prescription> prescription = prescriptionRepository.findByAppointmentId(appointmentId);
            if (prescription == null) {
                response.put(AppConstant.MESSAGE, "Prescription not found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

            response.put("prescription", prescription);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put(AppConstant.MESSAGE, "An error occurred while retrieving prescription");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
