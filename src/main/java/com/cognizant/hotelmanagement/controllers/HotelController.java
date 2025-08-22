package com.cognizant.hotelmanagement.controllers;

import com.cognizant.hotelmanagement.model.dao.services.HotelService;
import com.cognizant.hotelmanagement.model.pojo.Hotel;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/hotels")
@CrossOrigin(origins = "*")
public class HotelController {
    
    @Autowired
    private HotelService hotelService;
    
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> addHotel(@Valid @RequestBody Hotel hotel) {
        try {
            Hotel addedHotel = hotelService.addHotel(hotel);
            return new ResponseEntity<>(addedHotel, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
    
    @GetMapping("/{hotelId}")
    public ResponseEntity<?> getHotelById(@PathVariable Long hotelId) {
        Optional<Hotel> hotel = hotelService.getHotelById(hotelId);
        if (hotel.isPresent()) {
            return new ResponseEntity<>(hotel.get(), HttpStatus.OK);
        }
        return new ResponseEntity<>("Hotel not found", HttpStatus.NOT_FOUND);
    }
    
    @GetMapping
    public ResponseEntity<List<Hotel>> getAllHotels() {
        List<Hotel> hotels = hotelService.getAllHotels();
        return new ResponseEntity<>(hotels, HttpStatus.OK);
    }
    
    @GetMapping("/search/city/{city}")
    public ResponseEntity<List<Hotel>> searchHotelsByCity(@PathVariable String city) {
        List<Hotel> hotels = hotelService.searchHotelsByCity(city);
        return new ResponseEntity<>(hotels, HttpStatus.OK);
    }
    
    @GetMapping("/search/state/{state}")
    public ResponseEntity<List<Hotel>> searchHotelsByState(@PathVariable String state) {
        List<Hotel> hotels = hotelService.searchHotelsByState(state);
        return new ResponseEntity<>(hotels, HttpStatus.OK);
    }
    
    @GetMapping("/search/country/{country}")
    public ResponseEntity<List<Hotel>> searchHotelsByCountry(@PathVariable String country) {
        List<Hotel> hotels = hotelService.searchHotelsByCountry(country);
        return new ResponseEntity<>(hotels, HttpStatus.OK);
    }
    
    @GetMapping("/search/name/{hotelName}")
    public ResponseEntity<List<Hotel>> searchHotelsByName(@PathVariable String hotelName) {
        List<Hotel> hotels = hotelService.searchHotelsByName(hotelName);
        return new ResponseEntity<>(hotels, HttpStatus.OK);
    }
    
    @GetMapping("/search/city/{city}/maxac/{maxCost}")
    public ResponseEntity<List<Hotel>> searchHotelsByCityAndMaxAcCost(
            @PathVariable String city, @PathVariable Double maxCost) {
        List<Hotel> hotels = hotelService.searchHotelsByCityAndMaxAcCost(city, maxCost);
        return new ResponseEntity<>(hotels, HttpStatus.OK);
    }
    
    @GetMapping("/search/city/{city}/maxnonac/{maxCost}")
    public ResponseEntity<List<Hotel>> searchHotelsByCityAndMaxNonAcCost(
            @PathVariable String city, @PathVariable Double maxCost) {
        List<Hotel> hotels = hotelService.searchHotelsByCityAndMaxNonAcCost(city, maxCost);
        return new ResponseEntity<>(hotels, HttpStatus.OK);
    }
    
    @PutMapping("/{hotelId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateHotel(@PathVariable Long hotelId, @Valid @RequestBody Hotel hotel) {
        try {
            Hotel updatedHotel = hotelService.updateHotel(hotelId, hotel);
            return new ResponseEntity<>(updatedHotel, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }
    
    @DeleteMapping("/{hotelId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteHotel(@PathVariable Long hotelId) {
        try {
            hotelService.deleteHotel(hotelId);
            return new ResponseEntity<>("Hotel deleted successfully", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }
}