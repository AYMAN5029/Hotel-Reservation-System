package com.cognizant.hotelmanagement.model.pojo;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "hotels")
public class Hotel {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long hotelId;
    
    @NotBlank(message = "Hotel name is required")
    private String hotelName;
    
    @NotBlank(message = "Address is required")
    private String address;
    
    @NotBlank(message = "City is required")
    private String city;
    
    @NotBlank(message = "State is required")
    private String state;
    
    @NotBlank(message = "Country is required")
    private String country;
    
    @NotNull(message = "Number of AC rooms is required")
    @Positive(message = "Number of AC rooms must be positive")
    private Integer acRooms;
    
    @NotNull(message = "Number of Non-AC rooms is required")
    @Positive(message = "Number of Non-AC rooms must be positive")
    private Integer nonAcRooms;
    
    @NotNull(message = "AC room cost is required")
    @Positive(message = "AC room cost must be positive")
    private Double acRoomCost;
    
    @NotNull(message = "Non-AC room cost is required")
    @Positive(message = "Non-AC room cost must be positive")
    private Double nonAcRoomCost;
    
    private String description;
    private String amenities;
    private Double rating = 0.0;
    
    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime updatedAt = LocalDateTime.now();
    
    @OneToMany(mappedBy = "hotel", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<Reservation> reservations;
    
    // Constructors
    public Hotel() {}
    
    public Hotel(String hotelName, String address, String city, String state, String country,
                 Integer acRooms, Integer nonAcRooms, Double acRoomCost, Double nonAcRoomCost) {
        this.hotelName = hotelName;
        this.address = address;
        this.city = city;
        this.state = state;
        this.country = country;
        this.acRooms = acRooms;
        this.nonAcRooms = nonAcRooms;
        this.acRoomCost = acRoomCost;
        this.nonAcRoomCost = nonAcRoomCost;
    }
    
    // Getters and Setters
    public Long getHotelId() {
        return hotelId;
    }
    
    public void setHotelId(Long hotelId) {
        this.hotelId = hotelId;
    }
    
    public String getHotelName() {
        return hotelName;
    }
    
    public void setHotelName(String hotelName) {
        this.hotelName = hotelName;
    }
    
    public String getAddress() {
        return address;
    }
    
    public void setAddress(String address) {
        this.address = address;
    }
    
    public String getCity() {
        return city;
    }
    
    public void setCity(String city) {
        this.city = city;
    }
    
    public String getState() {
        return state;
    }
    
    public void setState(String state) {
        this.state = state;
    }
    
    public String getCountry() {
        return country;
    }
    
    public void setCountry(String country) {
        this.country = country;
    }
    
    public Integer getAcRooms() {
        return acRooms;
    }
    
    public void setAcRooms(Integer acRooms) {
        this.acRooms = acRooms;
    }
    
    public Integer getNonAcRooms() {
        return nonAcRooms;
    }
    
    public void setNonAcRooms(Integer nonAcRooms) {
        this.nonAcRooms = nonAcRooms;
    }
    
    public Double getAcRoomCost() {
        return acRoomCost;
    }
    
    public void setAcRoomCost(Double acRoomCost) {
        this.acRoomCost = acRoomCost;
    }
    
    public Double getNonAcRoomCost() {
        return nonAcRoomCost;
    }
    
    public void setNonAcRoomCost(Double nonAcRoomCost) {
        this.nonAcRoomCost = nonAcRoomCost;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getAmenities() {
        return amenities;
    }
    
    public void setAmenities(String amenities) {
        this.amenities = amenities;
    }
    
    public Double getRating() {
        return rating;
    }
    
    public void setRating(Double rating) {
        this.rating = rating;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    public List<Reservation> getReservations() {
        return reservations;
    }
    
    public void setReservations(List<Reservation> reservations) {
        this.reservations = reservations;
    }
}