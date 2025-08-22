package com.cognizant.hotelmanagement.model.dao.repositories;

import com.cognizant.hotelmanagement.model.pojo.Reservation;
import com.cognizant.hotelmanagement.model.pojo.User;
import com.cognizant.hotelmanagement.model.pojo.Hotel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    List<Reservation> findByUser(User user);
    List<Reservation> findByHotel(Hotel hotel);
    List<Reservation> findByUserUserId(Long userId);
    List<Reservation> findByHotelHotelId(Long hotelId);
    List<Reservation> findByStatus(Reservation.ReservationStatus status);
}