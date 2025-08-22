package com.cognizant.reservationservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "hotel-service", url = "http://localhost:8082")
public interface HotelServiceClient {
    
    @GetMapping("/hotels/{hotelId}")
    HotelDto getHotelById(@PathVariable("hotelId") Long hotelId);
    
    @GetMapping("/hotels/{hotelId}/availability")
    boolean checkAvailability(@PathVariable("hotelId") Long hotelId);
    
    @GetMapping("/hotels/{hotelId}/room-availability")
    boolean checkRoomAvailability(@PathVariable("hotelId") Long hotelId, 
                                 @RequestParam("roomType") String roomType, 
                                 @RequestParam("numberOfRooms") Integer numberOfRooms);
    
    @PostMapping("/hotels/{hotelId}/update-room-availability")
    boolean updateRoomAvailability(@PathVariable("hotelId") Long hotelId,
                                  @RequestParam("roomType") String roomType,
                                  @RequestParam("numberOfRooms") Integer numberOfRooms,
                                  @RequestParam("isReservation") boolean isReservation);
    
    // DTO class for Hotel data transfer
    class HotelDto {
        private Long hotelId;
        private String hotelName;
        private String address;
        private String city;
        private String state;
        private String country;
        private String description;
        private Double avgRatingByCustomers;
        private Double acRoomCost;
        private Double nonAcRoomCost;
        
        // Constructors
        public HotelDto() {}
        
        public HotelDto(Long hotelId, String hotelName, String address, String city, String state, 
                       String country, String description, Double avgRatingByCustomers, 
                       Double acRoomCost, Double nonAcRoomCost) {
            this.hotelId = hotelId;
            this.hotelName = hotelName;
            this.address = address;
            this.city = city;
            this.state = state;
            this.country = country;
            this.description = description;
            this.avgRatingByCustomers = avgRatingByCustomers;
            this.acRoomCost = acRoomCost;
            this.nonAcRoomCost = nonAcRoomCost;
        }
        
        // Getters and Setters
        public Long getHotelId() { return hotelId; }
        public void setHotelId(Long hotelId) { this.hotelId = hotelId; }
        
        public String getHotelName() { return hotelName; }
        public void setHotelName(String hotelName) { this.hotelName = hotelName; }
        
        public String getAddress() { return address; }
        public void setAddress(String address) { this.address = address; }
        
        public String getCity() { return city; }
        public void setCity(String city) { this.city = city; }
        
        public String getState() { return state; }
        public void setState(String state) { this.state = state; }
        
        public String getCountry() { return country; }
        public void setCountry(String country) { this.country = country; }
        
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        
        public Double getAvgRatingByCustomers() { return avgRatingByCustomers; }
        public void setAvgRatingByCustomers(Double avgRatingByCustomers) { this.avgRatingByCustomers = avgRatingByCustomers; }
        
        public Double getAcRoomCost() { return acRoomCost; }
        public void setAcRoomCost(Double acRoomCost) { this.acRoomCost = acRoomCost; }
        
        public Double getNonAcRoomCost() { return nonAcRoomCost; }
        public void setNonAcRoomCost(Double nonAcRoomCost) { this.nonAcRoomCost = nonAcRoomCost; }
    }
}
