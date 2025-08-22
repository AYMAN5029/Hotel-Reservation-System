package com.cognizant.hotelservice.service;

import com.cognizant.hotelservice.model.Hotel;
import com.cognizant.hotelservice.repository.HotelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class HotelServiceImpl implements HotelService {
    
    @Autowired
    private HotelRepository hotelRepository;
    
    @Value("${file.upload-dir:uploads}")
    private String uploadDir;
    
    @Override
    public Hotel addHotel(Hotel hotel) {
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
        return hotelRepository.findByCityIgnoreCase(city);
    }
    
    @Override
    public List<Hotel> searchHotelsByState(String state) {
        return hotelRepository.findByStateIgnoreCase(state);
    }
    
    @Override
    public List<Hotel> searchHotelsByCountry(String country) {
        return hotelRepository.findByCountryIgnoreCase(country);
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
    public Hotel updateHotel(Long hotelId, Hotel hotel) {
        Optional<Hotel> existingHotel = hotelRepository.findById(hotelId);
        if (existingHotel.isPresent()) {
            Hotel hotelToUpdate = existingHotel.get();
            hotelToUpdate.setHotelName(hotel.getHotelName());
            hotelToUpdate.setAddress(hotel.getAddress());
            hotelToUpdate.setCity(hotel.getCity());
            hotelToUpdate.setState(hotel.getState());
            hotelToUpdate.setCountry(hotel.getCountry());
            hotelToUpdate.setDescription(hotel.getDescription());
            hotelToUpdate.setAvgRatingByCustomers(hotel.getAvgRatingByCustomers());
            hotelToUpdate.setAcRoomCost(hotel.getAcRoomCost());
            hotelToUpdate.setNonAcRoomCost(hotel.getNonAcRoomCost());
            // Update room inventory
            hotelToUpdate.setTotalAcRooms(hotel.getTotalAcRooms());
            hotelToUpdate.setAvailableAcRooms(hotel.getAvailableAcRooms());
            hotelToUpdate.setTotalNonAcRooms(hotel.getTotalNonAcRooms());
            hotelToUpdate.setAvailableNonAcRooms(hotel.getAvailableNonAcRooms());
            // Update image path if provided
            if (hotel.getImagePath() != null) {
                hotelToUpdate.setImagePath(hotel.getImagePath());
            }
            return hotelRepository.save(hotelToUpdate);
        } else {
            throw new RuntimeException("Hotel not found with id: " + hotelId);
        }
    }
    
    @Override
    public void deleteHotel(Long hotelId) {
        if (hotelRepository.existsById(hotelId)) {
            hotelRepository.deleteById(hotelId);
        } else {
            throw new RuntimeException("Hotel not found with id: " + hotelId);
        }
    }
    
    @Override
    public boolean updateRoomAvailability(Long hotelId, String roomType, Integer numberOfRooms, boolean isReservation) {
        Optional<Hotel> hotelOptional = hotelRepository.findById(hotelId);
        if (hotelOptional.isPresent()) {
            Hotel hotel = hotelOptional.get();
            
            if ("AC".equalsIgnoreCase(roomType)) {
                int currentAvailable = hotel.getAvailableAcRooms();
                if (isReservation) {
                    // Making a reservation - decrease available rooms
                    if (currentAvailable >= numberOfRooms) {
                        hotel.setAvailableAcRooms(currentAvailable - numberOfRooms);
                    } else {
                        return false; // Not enough rooms available
                    }
                } else {
                    // Canceling a reservation - increase available rooms
                    int newAvailable = currentAvailable + numberOfRooms;
                    if (newAvailable <= hotel.getTotalAcRooms()) {
                        hotel.setAvailableAcRooms(newAvailable);
                    } else {
                        hotel.setAvailableAcRooms(hotel.getTotalAcRooms());
                    }
                }
            } else if ("NON_AC".equalsIgnoreCase(roomType)) {
                int currentAvailable = hotel.getAvailableNonAcRooms();
                if (isReservation) {
                    // Making a reservation - decrease available rooms
                    if (currentAvailable >= numberOfRooms) {
                        hotel.setAvailableNonAcRooms(currentAvailable - numberOfRooms);
                    } else {
                        return false; // Not enough rooms available
                    }
                } else {
                    // Canceling a reservation - increase available rooms
                    int newAvailable = currentAvailable + numberOfRooms;
                    if (newAvailable <= hotel.getTotalNonAcRooms()) {
                        hotel.setAvailableNonAcRooms(newAvailable);
                    } else {
                        hotel.setAvailableNonAcRooms(hotel.getTotalNonAcRooms());
                    }
                }
            } else {
                return false; // Invalid room type
            }
            
            hotelRepository.save(hotel);
            return true;
        }
        return false; // Hotel not found
    }
    
    @Override
    public boolean checkRoomAvailability(Long hotelId, String roomType, Integer numberOfRooms) {
        Optional<Hotel> hotelOptional = hotelRepository.findById(hotelId);
        if (hotelOptional.isPresent()) {
            Hotel hotel = hotelOptional.get();
            
            if ("AC".equalsIgnoreCase(roomType)) {
                return hotel.getAvailableAcRooms() >= numberOfRooms;
            } else if ("NON_AC".equalsIgnoreCase(roomType)) {
                return hotel.getAvailableNonAcRooms() >= numberOfRooms;
            }
        }
        return false;
    }
    
    @Override
    public String uploadHotelImage(Long hotelId, MultipartFile image) {
        try {
            // Check if hotel exists
            Optional<Hotel> hotelOptional = hotelRepository.findById(hotelId);
            if (!hotelOptional.isPresent()) {
                throw new RuntimeException("Hotel not found with id: " + hotelId);
            }
            
            // Validate file
            if (image.isEmpty()) {
                throw new RuntimeException("Please select a file to upload");
            }
            
            // Check file type
            String contentType = image.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                throw new RuntimeException("Only image files are allowed");
            }
            
            // Create upload directory if it doesn't exist
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            
            // Generate unique filename
            String originalFilename = image.getOriginalFilename();
            String fileExtension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }
            String uniqueFilename = UUID.randomUUID().toString() + fileExtension;
            
            // Save file
            Path filePath = uploadPath.resolve(uniqueFilename);
            Files.copy(image.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
            
            // Update hotel with image path
            Hotel hotel = hotelOptional.get();
            hotel.setImagePath(uniqueFilename);
            hotelRepository.save(hotel);
            
            return uniqueFilename;
            
        } catch (IOException e) {
            throw new RuntimeException("Failed to store file: " + e.getMessage());
        }
    }
    
    @Override
    public byte[] getHotelImage(Long hotelId) {
        try {
            Optional<Hotel> hotelOptional = hotelRepository.findById(hotelId);
            if (!hotelOptional.isPresent()) {
                throw new RuntimeException("Hotel not found with id: " + hotelId);
            }
            
            Hotel hotel = hotelOptional.get();
            String imagePath = hotel.getImagePath();
            
            if (imagePath == null || imagePath.isEmpty()) {
                throw new RuntimeException("No image found for hotel with id: " + hotelId);
            }
            
            Path filePath = Paths.get(uploadDir).resolve(imagePath);
            if (!Files.exists(filePath)) {
                throw new RuntimeException("Image file not found: " + imagePath);
            }
            
            return Files.readAllBytes(filePath);
            
        } catch (IOException e) {
            throw new RuntimeException("Failed to read image file: " + e.getMessage());
        }
    }
}
