package com.cognizant.hotelservice.controller;

import com.cognizant.hotelservice.model.Hotel;
import com.cognizant.hotelservice.service.HotelService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/hotels")
@CrossOrigin(origins = "*")
public class HotelController {
    
    @Autowired
    private HotelService hotelService;
    
    @PostMapping
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
    public ResponseEntity<?> updateHotel(@PathVariable Long hotelId, @Valid @RequestBody Hotel hotel) {
        try {
            Hotel updatedHotel = hotelService.updateHotel(hotelId, hotel);
            return new ResponseEntity<>(updatedHotel, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }
    
    @DeleteMapping("/{hotelId}")
    public ResponseEntity<?> deleteHotel(@PathVariable Long hotelId) {
        try {
            hotelService.deleteHotel(hotelId);
            return new ResponseEntity<>("Hotel deleted successfully", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }
    
    @GetMapping("/{hotelId}/room-availability")
    public ResponseEntity<Boolean> checkRoomAvailability(@PathVariable Long hotelId,
                                                        @RequestParam String roomType,
                                                        @RequestParam Integer numberOfRooms) {
        try {
            boolean isAvailable = hotelService.checkRoomAvailability(hotelId, roomType, numberOfRooms);
            return new ResponseEntity<>(isAvailable, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(false, HttpStatus.BAD_REQUEST);
        }
    }
    
    @PostMapping("/{hotelId}/update-room-availability")
    public ResponseEntity<Boolean> updateRoomAvailability(@PathVariable Long hotelId,
                                                         @RequestParam String roomType,
                                                         @RequestParam Integer numberOfRooms,
                                                         @RequestParam boolean isReservation) {
        try {
            boolean success = hotelService.updateRoomAvailability(hotelId, roomType, numberOfRooms, isReservation);
            return new ResponseEntity<>(success, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(false, HttpStatus.BAD_REQUEST);
        }
    }
    
    @PostMapping("/{hotelId}/upload-image")
    public ResponseEntity<?> uploadHotelImage(@PathVariable Long hotelId, 
                                            @RequestParam("image") MultipartFile image) {
        try {
            String imagePath = hotelService.uploadHotelImage(hotelId, image);
            return new ResponseEntity<>(imagePath, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
    
    @GetMapping("/{hotelId}/image")
    public ResponseEntity<byte[]> getHotelImage(@PathVariable Long hotelId) {
        try {
            byte[] imageData = hotelService.getHotelImage(hotelId);
            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_JPEG)
                    .body(imageData);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
