package com.cognizant.hotelservice.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

@Entity
@Table(name = "hotels")
public class Hotel {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long hotelId;
    
    @NotBlank(message = "Hotel name is required")
    @Size(max = 100, message = "Hotel name must be less than 100 characters")
    private String hotelName;
    
    @NotBlank(message = "Address is required")
    @Size(max = 255, message = "Address must be less than 255 characters")
    private String address;
    
    @NotBlank(message = "City is required")
    @Size(max = 50, message = "City must be less than 50 characters")
    private String city;
    
    @NotBlank(message = "State is required")
    @Size(max = 50, message = "State must be less than 50 characters")
    private String state;
    
    @NotBlank(message = "Country is required")
    @Size(max = 50, message = "Country must be less than 50 characters")
    private String country;
    
    @Size(max = 1000, message = "Description must be less than 1000 characters")
    @Column(length = 1000)
    private String description;
    
    @DecimalMin(value = "0.0", message = "Rating must be at least 0")
    @DecimalMax(value = "5.0", message = "Rating must be at most 5")
    private Double avgRatingByCustomers = 0.0;
    
    @NotNull(message = "AC room cost is required")
    @DecimalMin(value = "0.01", message = "AC room cost must be greater than 0")
    private Double acRoomCost;
    
    @NotNull(message = "Non-AC room cost is required")
    @DecimalMin(value = "0.01", message = "Non-AC room cost must be greater than 0")
    private Double nonAcRoomCost;
    
    @NotNull(message = "Total AC rooms is required")
    @Min(value = 0, message = "Total AC rooms must be at least 0")
    private Integer totalAcRooms = 0;
    
    @NotNull(message = "Available AC rooms is required")
    @Min(value = 0, message = "Available AC rooms must be at least 0")
    private Integer availableAcRooms = 0;
    
    @NotNull(message = "Total Non-AC rooms is required")
    @Min(value = 0, message = "Total Non-AC rooms must be at least 0")
    private Integer totalNonAcRooms = 0;
    
    @NotNull(message = "Available Non-AC rooms is required")
    @Min(value = 0, message = "Available Non-AC rooms must be at least 0")
    private Integer availableNonAcRooms = 0;
    
    @Size(max = 255, message = "Image path must be less than 255 characters")
    private String imagePath;
    
    // Default constructor
    public Hotel() {}
    
    // Constructor with all fields
    public Hotel(String hotelName, String address, String city, String state, String country, 
                 String description, Double avgRatingByCustomers, Double acRoomCost, Double nonAcRoomCost,
                 Integer totalAcRooms, Integer totalNonAcRooms) {
        this.hotelName = hotelName;
        this.address = address;
        this.city = city;
        this.state = state;
        this.country = country;
        this.description = description;
        this.avgRatingByCustomers = avgRatingByCustomers;
        this.acRoomCost = acRoomCost;
        this.nonAcRoomCost = nonAcRoomCost;
        this.totalAcRooms = totalAcRooms;
        this.availableAcRooms = totalAcRooms; // Initially all rooms are available
        this.totalNonAcRooms = totalNonAcRooms;
        this.availableNonAcRooms = totalNonAcRooms; // Initially all rooms are available
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
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public Double getAvgRatingByCustomers() {
        return avgRatingByCustomers;
    }
    
    public void setAvgRatingByCustomers(Double avgRatingByCustomers) {
        this.avgRatingByCustomers = avgRatingByCustomers;
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
    
    public Integer getTotalAcRooms() {
        return totalAcRooms;
    }
    
    public void setTotalAcRooms(Integer totalAcRooms) {
        this.totalAcRooms = totalAcRooms;
        // When setting total rooms, ensure available rooms don't exceed total
        if (this.availableAcRooms > totalAcRooms) {
            this.availableAcRooms = totalAcRooms;
        }
    }
    
    public Integer getAvailableAcRooms() {
        return availableAcRooms;
    }
    
    public void setAvailableAcRooms(Integer availableAcRooms) {
        this.availableAcRooms = availableAcRooms;
    }
    
    public Integer getTotalNonAcRooms() {
        return totalNonAcRooms;
    }
    
    public void setTotalNonAcRooms(Integer totalNonAcRooms) {
        this.totalNonAcRooms = totalNonAcRooms;
        // When setting total rooms, ensure available rooms don't exceed total
        if (this.availableNonAcRooms > totalNonAcRooms) {
            this.availableNonAcRooms = totalNonAcRooms;
        }
    }
    
    public Integer getAvailableNonAcRooms() {
        return availableNonAcRooms;
    }
    
    public void setAvailableNonAcRooms(Integer availableNonAcRooms) {
        this.availableNonAcRooms = availableNonAcRooms;
    }
    
    public String getImagePath() {
        return imagePath;
    }
    
    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }
    
    @Override
    public String toString() {
        return "Hotel{" +
                "hotelId=" + hotelId +
                ", hotelName='" + hotelName + '\'' +
                ", address='" + address + '\'' +
                ", city='" + city + '\'' +
                ", state='" + state + '\'' +
                ", country='" + country + '\'' +
                ", description='" + description + '\'' +
                ", avgRatingByCustomers=" + avgRatingByCustomers +
                ", acRoomCost=" + acRoomCost +
                ", nonAcRoomCost=" + nonAcRoomCost +
                ", totalAcRooms=" + totalAcRooms +
                ", availableAcRooms=" + availableAcRooms +
                ", totalNonAcRooms=" + totalNonAcRooms +
                ", availableNonAcRooms=" + availableNonAcRooms +
                ", imagePath='" + imagePath + '\'' +
                '}';
    }
}
