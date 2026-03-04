package com.project.back_end.mvc;

import com.project.back_end.services.CommonService;

import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class DashboardController {

    @Autowired
    private CommonService commonService;

    @GetMapping("/adminDashboard/{token}")
    public String adminDashboard(@PathVariable String token) {
        ResponseEntity<Map<String, String>> validateResponse = commonService.validateToken(token, "admin");
        if (validateResponse!=null) {
            return "redirect:http://localhost:8080";
        }
        return "admin/adminDashboard";
    }

    @GetMapping("/doctorDashboard/{token}")
    public String doctorDashboard(@PathVariable String token) {
        ResponseEntity<Map<String, String>> validateResponse = commonService.validateToken(token, "doctor");
        if (validateResponse!= null) {
            return "redirect:http://localhost:8080";
        }
        return "doctor/doctorDashboard";
    }
}
