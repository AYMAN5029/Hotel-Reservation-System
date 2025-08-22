package com.cognizant.hotelmanagement.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/demo")
public class DemoController {
    
    @GetMapping("/health")
    public String healthCheck() {
        return "Hotel Management System is running successfully!";
    }
    
    @GetMapping("/version")
    public String version() {
        return "Hotel Management System v1.0.0";
    }
}