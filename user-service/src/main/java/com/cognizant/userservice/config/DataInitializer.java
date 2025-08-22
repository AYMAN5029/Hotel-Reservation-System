package com.cognizant.userservice.config;

import com.cognizant.userservice.entity.User;
import com.cognizant.userservice.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // Create admin user if not exists
        if (!userRepository.existsByUsername("admin")) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setFullName("Admin User");
            admin.setEmail("admin@hotel.com");
            admin.setPhoneNumber("1234567890");
            admin.setRole(User.UserRole.ADMIN);
            userRepository.save(admin);
            System.out.println("Admin user created: username=admin, password=admin123");
        }

        // Create a test customer user if not exists
        if (!userRepository.existsByUsername("customer")) {
            User customer = new User();
            customer.setUsername("customer");
            customer.setPassword(passwordEncoder.encode("customer123"));
            customer.setFullName("John Doe");
            customer.setEmail("customer@hotel.com");
            customer.setPhoneNumber("9876543210");
            customer.setRole(User.UserRole.CUSTOMER);
            userRepository.save(customer);
            System.out.println("Customer user created: username=customer, password=customer123");
        }
    }
}
