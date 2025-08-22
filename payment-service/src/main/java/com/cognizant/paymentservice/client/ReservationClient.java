package com.cognizant.paymentservice.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class ReservationClient {
    
    @Autowired
    @Qualifier("directRestTemplate")
    private RestTemplate restTemplate;
    
    private static final String RESERVATION_SERVICE_URL = "http://localhost:8083";
    
    public boolean confirmReservation(Long reservationId) {
        try {
            String url = RESERVATION_SERVICE_URL + "/reservations/" + reservationId + "/confirm";
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<String> entity = new HttpEntity<>(headers);
            
            ResponseEntity<String> response = restTemplate.exchange(
                url, 
                HttpMethod.POST, 
                entity, 
                String.class
            );
            
            return response.getStatusCode().is2xxSuccessful();
        } catch (Exception e) {
            System.err.println("Failed to confirm reservation " + reservationId + ": " + e.getMessage());
            return false;
        }
    }
}
