package com.cognizant.hotelmanagement.model.dao.services;

import com.cognizant.hotelmanagement.model.pojo.Hotel;
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
}