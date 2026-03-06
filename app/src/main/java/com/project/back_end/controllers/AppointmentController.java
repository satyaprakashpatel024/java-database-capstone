package com.project.back_end.controllers;

import com.project.back_end.DTO.AppConstant;
import com.project.back_end.models.Appointment;
import com.project.back_end.services.AppointmentService;
import com.project.back_end.services.CommonService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@RestController
@RequestMapping("/appointments")
public class AppointmentController {

    private final AppointmentService appointmentService;
    private final CommonService commonService;

    @GetMapping("/{date}/{patientName}/{token}")
    public ResponseEntity<Map<String, Object>> getAppointments(@PathVariable LocalDate date, @PathVariable String patientName, @PathVariable String token) {
        ResponseEntity<Map<String, String>> tokenValidation = commonService.validateToken(token, AppConstant.DOCTOR);
        if (tokenValidation != null) {
            Map<String, Object> response = new HashMap<>();
            response.putAll(tokenValidation.getBody());
            return ResponseEntity.status(tokenValidation.getStatusCode()).body(response);
        }
        return ResponseEntity.ok(appointmentService.getAppointment(patientName, date, token));
    }

    @PostMapping("/{token}")
    public ResponseEntity<Map<String, String>> bookAppointment(@PathVariable String token, @RequestBody Appointment appointment) {
        ResponseEntity<Map<String, String>> tokenValidation = commonService.validateToken(token, AppConstant.PATIENT);
        if (tokenValidation != null) {
            return tokenValidation;
        }

        int appointmentValidation = commonService.validateAppointment(appointment);
        Map<String, String> response = new HashMap<>();

        if (appointmentValidation == -1) {
            response.put(AppConstant.MESSAGE, "Invalid doctor id");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        if (appointmentValidation == 0) {
            response.put(AppConstant.MESSAGE, "Appointment time is already booked or unavailable");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        int result = appointmentService.bookAppointment(appointment);
        if (result == 1) {
            response.put(AppConstant.MESSAGE, "Appointment booked successfully");
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        }
        response.put(AppConstant.MESSAGE, "Failed to book appointment");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    @PutMapping("/{token}")
    public ResponseEntity<Map<String, String>> updateAppointment(@PathVariable String token, @RequestBody Appointment appointment) {
        ResponseEntity<Map<String, String>> tokenValidation = commonService.validateToken(token, AppConstant.PATIENT);
        if (tokenValidation != null) {
            return tokenValidation;
        }
        return appointmentService.updateAppointment(appointment);
    }

    @DeleteMapping("/{id}/{token}")
    public ResponseEntity<Map<String, String>> cancelAppointment(@PathVariable long id, @PathVariable String token) {
        ResponseEntity<Map<String, String>> tokenValidation = commonService.validateToken(token, AppConstant.PATIENT);
        if (tokenValidation != null) {
            return tokenValidation;
        }
        return appointmentService.cancelAppointment(id, token);
    }
}
