package com.project.back_end;

import com.project.back_end.models.Doctor;
import com.project.back_end.services.DoctorService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@SpringBootTest
class BackEndApplicationTests {

	@Autowired
	DoctorService doctorService ;
	@Test
	void testMethod() {
		LocalDateTime date = LocalDateTime.parse("2025-05-14T14:00:00");
		LocalDate from = date.toLocalDate();
		List<String> doctorAvailability = doctorService.getDoctorAvailability(3L, from);
		System.out.println(doctorAvailability);
	}

}
