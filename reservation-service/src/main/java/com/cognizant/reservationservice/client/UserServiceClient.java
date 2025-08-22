package com.cognizant.reservationservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "user-service")
public interface UserServiceClient {
    
    @GetMapping("/api/users/{userId}")
    UserDto getUserById(@PathVariable("userId") Long userId);
    
    @GetMapping("/api/users/validate/{username}")
    boolean validateUser(@PathVariable("username") String username);
    
    // DTO class for User data transfer
    class UserDto {
        private Long userId;
        private String username;
        private String email;
        private String fullName;
        private String phoneNumber;
        private String role;
        
        // Constructors
        public UserDto() {}
        
        public UserDto(Long userId, String username, String email, String fullName, String phoneNumber, String role) {
            this.userId = userId;
            this.username = username;
            this.email = email;
            this.fullName = fullName;
            this.phoneNumber = phoneNumber;
            this.role = role;
        }
        
        // Getters and Setters
        public Long getUserId() { return userId; }
        public void setUserId(Long userId) { this.userId = userId; }
        
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        
        public String getFullName() { return fullName; }
        public void setFullName(String fullName) { this.fullName = fullName; }
        
        public String getPhoneNumber() { return phoneNumber; }
        public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
        
        public String getRole() { return role; }
        public void setRole(String role) { this.role = role; }
    }
}
