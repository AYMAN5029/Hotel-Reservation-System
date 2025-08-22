package com.cognizant.hotelservice.repository;

import com.cognizant.hotelservice.model.Hotel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HotelRepository extends JpaRepository<Hotel, Long> {
    
    List<Hotel> findByCityIgnoreCase(String city);
    
    List<Hotel> findByStateIgnoreCase(String state);
    
    List<Hotel> findByCountryIgnoreCase(String country);
    
    List<Hotel> findByHotelNameContainingIgnoreCase(String hotelName);
    
    @Query("SELECT h FROM Hotel h WHERE LOWER(h.city) = LOWER(:city) AND h.acRoomCost <= :maxCost")
    List<Hotel> findByCityAndMaxAcCost(@Param("city") String city, @Param("maxCost") Double maxCost);
    
    @Query("SELECT h FROM Hotel h WHERE LOWER(h.city) = LOWER(:city) AND h.nonAcRoomCost <= :maxCost")
    List<Hotel> findByCityAndMaxNonAcCost(@Param("city") String city, @Param("maxCost") Double maxCost);
}
