package com.project.back_end.services;

import com.project.back_end.DTO.Login;
import com.project.back_end.models.Appointment;
import com.project.back_end.models.Doctor;
import com.project.back_end.repo.AppointmentRepository;
import com.project.back_end.repo.DoctorRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class DoctorService {

    private final DoctorRepository doctorRepository;
    private final AppointmentRepository appointmentRepository;
    private final TokenService tokenService;

    public DoctorService(
            DoctorRepository doctorRepository,
            AppointmentRepository appointmentRepository,
            TokenService tokenService
    ) {
        this.doctorRepository = doctorRepository;
        this.appointmentRepository = appointmentRepository;
        this.tokenService = tokenService;
    }

    @Transactional
    public List<String> getDoctorAvailability(Long doctorId, LocalDate date) {
        Doctor doctor = doctorRepository.findById(doctorId).orElse(null);
        if (doctor == null || doctor.getAvailableTimes() == null) {
            return List.of();
        }

        System.out.println("Doctor ID: " + doctor);

        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end = date.plusDays(1).atStartOfDay();

        List<String> bookedSlots = appointmentRepository
                .findByDoctorIdAndAppointmentTimeBetween(doctorId, start, end)
                .stream()
                .map(Appointment::getAppointmentTime)
                .filter(Objects::nonNull)
                .map(LocalDateTime::toLocalTime)
                .map(time -> time.withSecond(0).withNano(0).toString())
                .toList();
        return doctor.getAvailableTimes().stream()
                .filter(slot -> !bookedSlots.contains(slot))
                .toList();
    }

    public int saveDoctor(Doctor doctor) {
        try {
            Doctor existingDoctor = doctorRepository.findByEmail(doctor.getEmail());
            if (existingDoctor != null) {
                return -1;
            }
            doctorRepository.save(doctor);
            return 1;
        } catch (Exception e) {
            return 0;
        }
    }

    public int updateDoctor(Doctor doctor) {
        try {
            if (doctor.getId() == null || doctorRepository.findById(doctor.getId()).isEmpty()) {
                return -1;
            }
            doctorRepository.save(doctor);
            return 1;
        } catch (Exception e) {
            return 0;
        }
    }

    @Transactional
    public List<Doctor> getDoctors() {
        return doctorRepository.findAll();
    }

    public int deleteDoctor(long id) {
        try {
            if (doctorRepository.findById(id).isEmpty()) {
                return -1;
            }
            appointmentRepository.deleteAllByDoctorId(id);
            doctorRepository.deleteById(id);
            return 1;
        } catch (Exception e) {
            return 0;
        }
    }

    public ResponseEntity<Map<String, String>> validateDoctor(Login login) {
        Map<String, String> response = new HashMap<>();
        System.out.println("Login attempt: " + login.getIdentifier() + ", password: " + login.getPassword());
        try {
            Doctor doctor = doctorRepository.findByEmail(login.getIdentifier());
            System.out.println("Doctor found: " + doctor.getEmail()+", password: "+doctor.getPassword());
            if (doctor == null || !doctor.getPassword().equals(login.getPassword())) {
                response.put("message", "Invalid email or password");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }

            String token = tokenService.generateToken(doctor.getEmail());
            response.put("message", "Login successful");
            response.put("token", token);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("message", "Internal server error");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @Transactional
    public List<Doctor> findDoctorByName(String name) {
        return doctorRepository.findByNameLike(name);
    }

    @Transactional
    public List<Doctor> filterDoctorsByNameSpecilityAndTime(String name, String specialty, String amOrPm) {
        List<Doctor> doctors = doctorRepository.findByNameContainingIgnoreCaseAndSpecialtyIgnoreCase(name, specialty);
        return filterDoctorByTime(doctors, amOrPm);
    }

    @Transactional
    public List<Doctor> filterDoctorByNameAndTime(String name, String amOrPm) {
        List<Doctor> doctors = doctorRepository.findByNameLike(name);
        return filterDoctorByTime(doctors, amOrPm);
    }

    @Transactional
    public List<Doctor> filterDoctorByNameAndSpecility(String name, String specilty) {

        return doctorRepository.findByNameContainingIgnoreCaseAndSpecialtyIgnoreCase(name, specilty);

    }

    @Transactional
    public List<Doctor> filterDoctorByTimeAndSpecility(String specilty, String amOrPm) {
        List<Doctor> doctors = doctorRepository.findBySpecialtyIgnoreCase(specilty);

        return filterDoctorByTime(doctors, amOrPm);

    }

    @Transactional
    public List<Doctor> filterDoctorBySpecility(String specilty) {

        return doctorRepository.findBySpecialtyIgnoreCase(specilty);

    }

    @Transactional
    public List<Doctor> filterDoctorsByTime(String amOrPm) {
        List<Doctor> doctors = doctorRepository.findAll();

        return filterDoctorByTime(doctors, amOrPm);

    }

    public List<Doctor> filterDoctorByTime(List<Doctor> doctors, String amOrPm) {
        if (doctors == null || doctors.isEmpty()) {
            return List.of();
        }

        boolean isAmFilter = "AM".equalsIgnoreCase(amOrPm);
        boolean isPmFilter = "PM".equalsIgnoreCase(amOrPm);

        if (!isAmFilter && !isPmFilter) {
            return doctors;
        }

        return doctors.stream()
                .filter(doctor -> doctor.getAvailableTimes() != null && !doctor.getAvailableTimes().isEmpty())
                .filter(doctor -> doctor.getAvailableTimes().stream().anyMatch(slot -> {
                    LocalTime parsedTime;
                    try {
                        parsedTime = LocalTime.parse(slot);
                    } catch (Exception e) {
                        return false;
                    }
                    return isAmFilter == parsedTime.isBefore(LocalTime.NOON);
                }))
                .collect(Collectors.toList());
    }
}
