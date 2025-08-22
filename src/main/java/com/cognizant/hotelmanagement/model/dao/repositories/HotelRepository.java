package com.cognizant.hotelmanagement.model.dao.repositories;

import com.cognizant.hotelmanagement.model.pojo.Hotel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface HotelRepository extends JpaRepository<Hotel, Long> {
    List<Hotel> findByCity(String city);
    List<Hotel> findByState(String state);
    List<Hotel> findByCountry(String country);
    List<Hotel> findByHotelNameContainingIgnoreCase(String hotelName);
    
    @Query("SELECT h FROM Hotel h WHERE h.city = :city AND h.acRoomCost <= :maxCost")
    List<Hotel> findByCityAndMaxAcCost(@Param("city") String city, @Param("maxCost") Double maxCost);
    
    @Query("SELECT h FROM Hotel h WHERE h.city = :city AND h.nonAcRoomCost <= :maxCost")
    List<Hotel> findByCityAndMaxNonAcCost(@Param("city") String city, @Param("maxCost") Double maxCost);
}