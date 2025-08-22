package com.cognizant.hotelmanagement.model.dao.serviceimplementations;

import com.cognizant.hotelmanagement.model.dao.repositories.HotelRepository;
import com.cognizant.hotelmanagement.model.dao.services.HotelService;
import com.cognizant.hotelmanagement.model.exceptions.HotelNotFoundException;
import com.cognizant.hotelmanagement.model.pojo.Hotel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class HotelServiceImpl implements HotelService {
    
    @Autowired
    private HotelRepository hotelRepository;
    
    @Override
    public Hotel addHotel(Hotel hotel) {
        hotel.setCreatedAt(LocalDateTime.now());
        hotel.setUpdatedAt(LocalDateTime.now());
        return hotelRepository.save(hotel);
    }
    
    @Override
    public Optional<Hotel> getHotelById(Long hotelId) {
        return hotelRepository.findById(hotelId);
    }
    
    @Override
    public List<Hotel> getAllHotels() {
        return hotelRepository.findAll();
    }
    
    @Override
    public List<Hotel> searchHotelsByCity(String city) {
        return hotelRepository.findByCity(city);
    }
    
    @Override
    public List<Hotel> searchHotelsByState(String state) {
        return hotelRepository.findByState(state);
    }
    
    @Override
    public List<Hotel> searchHotelsByCountry(String country) {
        return hotelRepository.findByCountry(country);
    }
    
    @Override
    public List<Hotel> searchHotelsByName(String hotelName) {
        return hotelRepository.findByHotelNameContainingIgnoreCase(hotelName);
    }
    
    @Override
    public List<Hotel> searchHotelsByCityAndMaxAcCost(String city, Double maxCost) {
        return hotelRepository.findByCityAndMaxAcCost(city, maxCost);
    }
    
    @Override
    public List<Hotel> searchHotelsByCityAndMaxNonAcCost(String city, Double maxCost) {
        return hotelRepository.findByCityAndMaxNonAcCost(city, maxCost);
    }
    
    @Override
    public Hotel updateHotel(Long hotelId, Hotel updatedHotel) {
        Hotel hotel = hotelRepository.findById(hotelId)
                .orElseThrow(() -> new HotelNotFoundException("Hotel not found with id: " + hotelId));
        
        hotel.setHotelName(updatedHotel.getHotelName());
        hotel.setAddress(updatedHotel.getAddress());
        hotel.setCity(updatedHotel.getCity());
        hotel.setState(updatedHotel.getState());
        hotel.setCountry(updatedHotel.getCountry());
        hotel.setAcRooms(updatedHotel.getAcRooms());
        hotel.setNonAcRooms(updatedHotel.getNonAcRooms());
        hotel.setAcRoomCost(updatedHotel.getAcRoomCost());
        hotel.setNonAcRoomCost(updatedHotel.getNonAcRoomCost());
        hotel.setDescription(updatedHotel.getDescription());
        hotel.setAmenities(updatedHotel.getAmenities());
        hotel.setRating(updatedHotel.getRating());
        hotel.setUpdatedAt(LocalDateTime.now());
        
        return hotelRepository.save(hotel);
    }
    
    @Override
    public void deleteHotel(Long hotelId) {
        if (!hotelRepository.existsById(hotelId)) {
            throw new HotelNotFoundException("Hotel not found with id: " + hotelId);
        }
        hotelRepository.deleteById(hotelId);
    }
}