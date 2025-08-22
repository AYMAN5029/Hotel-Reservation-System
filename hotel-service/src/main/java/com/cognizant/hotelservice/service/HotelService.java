package com.cognizant.hotelservice.service;

import com.cognizant.hotelservice.model.Hotel;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;
import java.util.Optional;

public interface HotelService {
    
    Hotel addHotel(Hotel hotel);
    
    Optional<Hotel> getHotelById(Long hotelId);
    
    List<Hotel> getAllHotels();
    
    List<Hotel> searchHotelsByCity(String city);
    
    List<Hotel> searchHotelsByState(String state);
    
    List<Hotel> searchHotelsByCountry(String country);
    
    List<Hotel> searchHotelsByName(String hotelName);
    
    List<Hotel> searchHotelsByCityAndMaxAcCost(String city, Double maxCost);
    
    List<Hotel> searchHotelsByCityAndMaxNonAcCost(String city, Double maxCost);
    
    Hotel updateHotel(Long hotelId, Hotel hotel);
    
    void deleteHotel(Long hotelId);
    
    // Room availability management methods
    boolean updateRoomAvailability(Long hotelId, String roomType, Integer numberOfRooms, boolean isReservation);
    
    boolean checkRoomAvailability(Long hotelId, String roomType, Integer numberOfRooms);
    
    // Image management methods
    String uploadHotelImage(Long hotelId, MultipartFile image);
    
    byte[] getHotelImage(Long hotelId);
}
