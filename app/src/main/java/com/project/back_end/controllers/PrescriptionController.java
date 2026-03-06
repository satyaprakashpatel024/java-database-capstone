package com.project.back_end.controllers;

import com.project.back_end.DTO.AppConstant;
import com.project.back_end.models.Prescription;
import com.project.back_end.services.CommonService;
import com.project.back_end.services.PrescriptionService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@AllArgsConstructor
@RestController
@RequestMapping("${api.path}" + "prescription")
public class PrescriptionController {

    private final PrescriptionService prescriptionService;
    private final CommonService commonService;

    @PostMapping("/{token}")
    public ResponseEntity<Map<String, String>> savePrescription(
            @PathVariable String token,
            @Valid @RequestBody Prescription prescription
    ) {
        ResponseEntity<Map<String, String>> tokenValidation = commonService.validateToken(token, AppConstant.DOCTOR);
        if (tokenValidation != null) {
            return tokenValidation;
        }

        return prescriptionService.savePrescription(prescription);
    }

    @GetMapping("/{appointmentId}/{token}")
    public ResponseEntity<Map<String, Object>> getPrescription(
            @PathVariable Long appointmentId,
            @PathVariable String token
    ) {
        ResponseEntity<Map<String, String>> tokenValidation = commonService.validateToken(token, AppConstant.DOCTOR);
        if (tokenValidation != null) {
            Map<String, Object> response = new java.util.HashMap<>();
            if (tokenValidation.getBody() != null) {
                response.putAll(tokenValidation.getBody());
            }
            return ResponseEntity.status(tokenValidation.getStatusCode()).body(response);
        }

        return prescriptionService.getPrescription(appointmentId);
    }
}
