package com.cognizant.hotelservice.config;

import com.cognizant.hotelservice.model.Hotel;
import com.cognizant.hotelservice.repository.HotelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private HotelRepository hotelRepository;

    @Override
    public void run(String... args) throws Exception {
        // Add sample hotels if database is empty
        if (hotelRepository.count() == 0) {
            Hotel hotel1 = new Hotel();
            hotel1.setHotelName("Grand Plaza Hotel");
            hotel1.setAddress("123 Marine Drive");
            hotel1.setCity("Mumbai");
            hotel1.setState("Maharashtra");
            hotel1.setCountry("India");
            hotel1.setDescription("Luxury hotel in the heart of Mumbai");
            hotel1.setAvgRatingByCustomers(4.5);
            hotel1.setAcRoomCost(5000.0);
            hotel1.setNonAcRoomCost(3000.0);
            hotelRepository.save(hotel1);

            Hotel hotel2 = new Hotel();
            hotel2.setHotelName("Ocean View Resort");
            hotel2.setAddress("456 Beach Road");
            hotel2.setCity("Goa");
            hotel2.setState("Goa");
            hotel2.setCountry("India");
            hotel2.setDescription("Beautiful beachside resort");
            hotel2.setAvgRatingByCustomers(4.2);
            hotel2.setAcRoomCost(4000.0);
            hotel2.setNonAcRoomCost(2500.0);
            hotelRepository.save(hotel2);

            Hotel hotel3 = new Hotel();
            hotel3.setHotelName("Mountain View Lodge");
            hotel3.setAddress("789 Hill Station Road");
            hotel3.setCity("Shimla");
            hotel3.setState("Himachal Pradesh");
            hotel3.setCountry("India");
            hotel3.setDescription("Cozy lodge with mountain views");
            hotel3.setAvgRatingByCustomers(4.0);
            hotel3.setAcRoomCost(3500.0);
            hotel3.setNonAcRoomCost(2000.0);
            hotelRepository.save(hotel3);

            System.out.println("Sample hotels added to database");
        }
    }
}
