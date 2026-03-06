package com.project.back_end.services;

import com.project.back_end.DTO.AppConstant;
import com.project.back_end.DTO.AppointmentDTO;
import com.project.back_end.models.Patient;
import com.project.back_end.repo.AppointmentRepository;
import com.project.back_end.repo.PatientRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class PatientService {

    private static final Logger logger = LoggerFactory.getLogger(PatientService.class);

    private final PatientRepository patientRepository;
    private final AppointmentRepository appointmentRepository;
    private final TokenService tokenService;

    public int createPatient(Patient patient) {
        try {
            patientRepository.save(patient);
            return 1;
        } catch (Exception e) {
            logger.error("Failed to create patient", e);
            return 0;
        }
    }

    @Transactional(readOnly = true)
    public ResponseEntity<Map<String, Object>> getPatientAppointment(Long id, String token) {
        try {
            String email = tokenService.extractEmail(token);
            Patient patient = patientRepository.findByEmail(email);

            if (patient == null || !patient.getId().equals(id)) {
                return response(HttpStatus.UNAUTHORIZED, "Unauthorized access to patient appointments");
            }

            List<AppointmentDTO> appointments = appointmentRepository.findByPatientId(id)
                    .stream()
                    .map(AppointmentDTO::fromAppointment)
                    .toList();

            Map<String, Object> body = new HashMap<>();
            body.put(AppConstant.APPOINTMENTS, appointments);
            body.put(AppConstant.MESSAGE, "Patient appointments fetched successfully");
            return ResponseEntity.ok(body);
        } catch (Exception e) {
            logger.error(AppConstant.PATIENT_ERROR, e);
            return response(HttpStatus.INTERNAL_SERVER_ERROR, AppConstant.PATIENT_ERROR);
        }
    }

    @Transactional(readOnly = true)
    public ResponseEntity<Map<String, Object>> filterByCondition(String condition, Long patientId) {
        try {
            Integer status = mapConditionToStatus(condition);
            if (status == null) {
                return response(HttpStatus.BAD_REQUEST, "Invalid condition. Use 'past' or 'future'.");
            }

            List<AppointmentDTO> appointments = appointmentRepository
                    .findByPatientIdAndStatusOrderByAppointmentTimeAsc(patientId, status)
                    .stream()
                    .map(AppointmentDTO::fromAppointment)
                    .toList();

            Map<String, Object> body = new HashMap<>();
            body.put(AppConstant.APPOINTMENTS, appointments);
            body.put(AppConstant.MESSAGE, "Appointments filtered by condition successfully");
            return ResponseEntity.ok(body);
        } catch (Exception e) {
            logger.error("Failed to filter appointments by condition", e);
            return response(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to filter appointments by condition");
        }
    }

    @Transactional(readOnly = true)
    public ResponseEntity<Map<String, Object>> filterByDoctor(String drName, Long patientId) {
        try {
            List<AppointmentDTO> appointments = appointmentRepository
                    .filterByDoctorNameAndPatientId(drName, patientId)
                    .stream()
                    .map(AppointmentDTO::fromAppointment)
                    .toList();

            Map<String, Object> body = new HashMap<>();
            body.put(AppConstant.APPOINTMENTS, appointments);
            body.put(AppConstant.MESSAGE, "Appointments filtered by doctor successfully");
            return ResponseEntity.ok(body);
        } catch (Exception e) {
            logger.error("Failed to filter appointments by doctor", e);
            return response(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to filter appointments by doctor");
        }
    }

    @Transactional(readOnly = true)
    public ResponseEntity<Map<String, Object>> filterByDoctorAndCondition(String condition, String name, long patientId) {
        try {
            Integer status = mapConditionToStatus(condition);
            if (status == null) {
                return response(HttpStatus.BAD_REQUEST, "Invalid condition. Use 'past' or 'future'.");
            }

            List<AppointmentDTO> appointments = appointmentRepository
                    .filterByDoctorNameAndPatientIdAndStatus(name, patientId, status)
                    .stream()
                    .map(AppointmentDTO::fromAppointment)
                    .toList();

            Map<String, Object> body = new HashMap<>();
            body.put(AppConstant.APPOINTMENTS, appointments);
            body.put(AppConstant.MESSAGE, "Appointments filtered by doctor and condition successfully");
            return ResponseEntity.ok(body);
        } catch (Exception e) {
            logger.error("Failed to filter appointments by doctor and condition", e);
            return response(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to filter appointments by doctor and condition");
        }
    }

    @Transactional(readOnly = true)
    public ResponseEntity<Map<String, Object>> getPatientDetails(String token) {
        try {
            String email = tokenService.extractEmail(token);
            Patient patient = patientRepository.findByEmail(email);
            if (patient == null) {
                return response(HttpStatus.NOT_FOUND, "Patient not found");
            }

            Map<String, Object> body = new HashMap<>();
            body.put("patient", patient);
            body.put(AppConstant.MESSAGE, "Patient details fetched successfully");
            return ResponseEntity.ok(body);
        } catch (Exception e) {
            logger.error("Failed to fetch patient details", e);
            return response(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to fetch patient details");
        }
    }

    private Integer mapConditionToStatus(String condition) {
        if (condition == null) {
            return null;
        }
        return switch (condition.trim().toLowerCase()) {
            case "past" -> 1;
            case "future" -> 0;
            default -> null;
        };
    }

    private ResponseEntity<Map<String, Object>> response(HttpStatus status, String message) {
        Map<String, Object> body = new HashMap<>();
        body.put(AppConstant.MESSAGE, message);
        return ResponseEntity.status(status).body(body);
    }

    public ResponseEntity<Map<String, Object>> getPatientAppointments(String email) {
        Map<String, Object> body = new HashMap<>();
        try {
            Patient patient = patientRepository.findByEmail(email);
            if (patient == null) {
                return response(HttpStatus.NOT_FOUND, "Patient not found");
            }

            List<AppointmentDTO> appointments = appointmentRepository.findByPatientId(patient.getId())
                    .stream()
                    .map(AppointmentDTO::fromAppointment)
                    .toList();

            body.put(AppConstant.APPOINTMENTS, appointments);
            body.put(AppConstant.MESSAGE, "Patient appointments fetched successfully");
            return ResponseEntity.ok(body);
        } catch (Exception e) {
            logger.error("Failed to fetch patient appointments", e);
            return response(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to fetch patient appointments");
        }
    }
}
