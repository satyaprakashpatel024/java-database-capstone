package com.project.back_end.controllers;

import com.project.back_end.DTO.AppConstant;
import com.project.back_end.DTO.Login;
import com.project.back_end.models.Patient;
import com.project.back_end.services.PatientService;
import com.project.back_end.services.CommonService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@RestController
@RequestMapping("/patient")
public class PatientController {

    private final PatientService patientService;
    private final CommonService commonService;

    @GetMapping("/{token}")
    public ResponseEntity<Map<String, Object>> getPatient(@PathVariable String token) {
        ResponseEntity<Map<String, String>> tokenValidation = commonService.validateToken(token, AppConstant.PATIENT);
        if (tokenValidation != null) {
            Map<String, Object> response = new HashMap<>();
            response.putAll(tokenValidation.getBody());
            return ResponseEntity.status(tokenValidation.getStatusCode()).body(response);
        }

        return patientService.getPatientDetails(token);
    }

    @PostMapping
    public ResponseEntity<Map<String, String>> createPatient(@Validated @RequestBody Patient patient) {
        Map<String, String> response = new HashMap<>();

        boolean isValid = commonService.validatePatient(patient);
        if (!isValid) {
            response.put(AppConstant.MESSAGE, "Patient with email id or phone no already exist");
            return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
        }

        int result = patientService.createPatient(patient);
        if (result == 1) {
            response.put(AppConstant.MESSAGE, "Signup successful");
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        }

        response.put(AppConstant.MESSAGE, "Internal server error");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody Login login) {
        return commonService.validatePatientLogin(login);
    }

    @GetMapping("/{id}/{token}")
    public ResponseEntity<Map<String, Object>> getPatientAppointment(
            @PathVariable Long id,
            @PathVariable String token
    ) {
        ResponseEntity<Map<String, String>> tokenValidation = commonService.validateToken(token, AppConstant.PATIENT);
        if (tokenValidation != null) {
            Map<String, Object> response = new HashMap<>();
            response.putAll(tokenValidation.getBody());
            return ResponseEntity.status(tokenValidation.getStatusCode()).body(response);
        }

        return patientService.getPatientAppointment(id, token);
    }

    @GetMapping("/filter/{condition}/{name}/{token}")
    public ResponseEntity<Map<String, Object>> filterPatientAppointment(
            @PathVariable String condition,
            @PathVariable String name,
            @PathVariable String token
    ) {
        ResponseEntity<Map<String, String>> tokenValidation = commonService.validateToken(token, AppConstant.PATIENT);
        if (tokenValidation != null) {
            Map<String, Object> response = new HashMap<>();
            response.putAll(tokenValidation.getBody());
            return ResponseEntity.status(tokenValidation.getStatusCode()).body(response);
        }
        return commonService.filterPatient(condition, name, token);
    }
}
