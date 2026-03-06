package com.project.back_end.services;

import com.project.back_end.DTO.AppConstant;
import com.project.back_end.DTO.Login;
import com.project.back_end.models.Admin;
import com.project.back_end.models.Appointment;
import com.project.back_end.models.Doctor;
import com.project.back_end.models.Patient;
import com.project.back_end.repo.AdminRepository;
import com.project.back_end.repo.DoctorRepository;
import com.project.back_end.repo.PatientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class CommonService {

    private final TokenService tokenService;
    private final AdminRepository adminRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;
    private final DoctorService doctorService;
    private final PatientService patientService;


    public ResponseEntity<Map<String, String>> validateToken(String token, String user) {
        Map<String, String> response = new HashMap<>();
        if (!tokenService.validateToken(token, user)) {
            response.put(AppConstant.MESSAGE, "Invalid or expired token");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
        return null;
    }

    public ResponseEntity<Map<String, String>> validateAdmin(Admin receivedAdmin) {
        Map<String, String> response = new HashMap<>();
        try {
            Admin storedAdmin = adminRepository.findByUsername(receivedAdmin.getUsername());
            if (storedAdmin == null || !storedAdmin.getPassword().equals(receivedAdmin.getPassword())) {
                response.put(AppConstant.MESSAGE, "Invalid username or password");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }

            response.put("token", tokenService.generateToken(storedAdmin.getUsername()));
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put(AppConstant.MESSAGE, "An error occurred while validating admin");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    public Map<String, Object> filterDoctor(String name, String specialty, String time) {
        Map<String, Object> response = new HashMap<>();

        List<Doctor> doctors;
        boolean hasName = name != null && !name.isBlank();
        boolean hasSpecialty = specialty != null && !specialty.isBlank();
        boolean hasTime = time != null && !time.isBlank();

        if (hasName && hasSpecialty && hasTime) {
            doctors = doctorService.filterDoctorsByNameSpecilityAndTime(name, specialty, time);
        } else if (hasName && hasSpecialty) {
            doctors = doctorService.filterDoctorByNameAndSpecility(name, specialty);
        } else if (hasName && hasTime) {
            doctors = doctorService.filterDoctorByNameAndTime(name, time);
        } else if (hasSpecialty && hasTime) {
            doctors = doctorService.filterDoctorByTimeAndSpecility(specialty, time);
        } else if (hasName) {
            doctors = doctorService.findDoctorByName(name);
        } else if (hasSpecialty) {
            doctors = doctorService.filterDoctorBySpecility(specialty);
        } else if (hasTime) {
            doctors = doctorService.filterDoctorByTime(doctorService.getDoctors(), time);
        } else {
            doctors = doctorService.getDoctors();
        }

        response.put("doctors", doctors);
        return response;
    }

    public int validateAppointment(Appointment appointment) {
        Optional<Doctor> doctorOptional = doctorRepository.findById(appointment.getDoctor().getId());
        if (doctorOptional.isEmpty()) {
            return -1;
        }

        LocalDate appointmentDate = appointment.getAppointmentTime().toLocalDate();
        LocalTime appointmentTime = appointment.getAppointmentTime().toLocalTime();
        List<String> availableSlots = doctorService.getDoctorAvailability(appointment.getDoctor().getId(), appointmentDate);

        for (String slot : availableSlots) {
            String startTime = slot.split("-")[0].trim();
            if (LocalTime.parse(startTime).equals(appointmentTime)) {
                return 1;
            }
        }
        return 0;
    }

    public boolean validatePatient(Patient patient) {
        Patient found = patientRepository.findByEmailOrPhone(patient.getEmail(), patient.getPhone());
        return found == null;
    }

    public ResponseEntity<Map<String, String>> validatePatientLogin(Login login) {
        Map<String, String> response = new HashMap<>();
        try {
            Patient patient = patientRepository.findByEmail(login.getIdentifier());
            if (patient == null || !patient.getPassword().equals(login.getPassword())) {
                response.put(AppConstant.MESSAGE, "Invalid email or password");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }

            response.put("token", tokenService.generateToken(patient.getEmail()));
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put(AppConstant.MESSAGE, "An error occurred while validating patient login");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    public ResponseEntity<Map<String, Object>> filterPatient(String condition, String pName, String token) {
        Map<String, Object> response = new HashMap<>();
        try {
            String email = tokenService.extractEmail(token);
            boolean hasCondition = condition != null && !condition.isBlank();
            Patient patient = patientRepository.findByEmail(email);
            if (hasCondition) {
                return patientService.filterByCondition(condition, patient.getId());
            } else {
                return patientService.getPatientAppointments(email);
            }
        } catch (Exception e) {
            response.put(AppConstant.MESSAGE, "An error occurred while filtering patient appointments");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    public int validateAppointment(Long doctorId, LocalDateTime appointmentDateTime) {
        if (doctorId == null) {
            throw new IllegalArgumentException("Invalid doctor id");
        }
        Doctor doctor = doctorRepository.findById(doctorId).orElse(null);
        if (doctor == null) {
            return -1;
        }
        List<String> doctorAvailability = doctorService.getDoctorAvailability(doctorId, appointmentDateTime.toLocalDate());
        LocalTime appointmentTime = appointmentDateTime.toLocalTime();

        boolean isAvailable = doctorAvailability.stream()
                .anyMatch(availability -> {
                    String[] times = availability.split("-");
                    LocalTime start = LocalTime.parse(times[0].trim());
                    LocalTime end = LocalTime.parse(times[1].trim());

                    return !appointmentTime.isBefore(start)
                            && !appointmentTime.isAfter(end);
                });
        return isAvailable ? 1 : 0;
    }
}
