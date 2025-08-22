package com.cognizant.hotelservice.service;

import com.cognizant.hotelservice.model.Hotel;
import com.cognizant.hotelservice.repository.HotelRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HotelServiceImplTest {

    @Mock
    private HotelRepository hotelRepository;

    @InjectMocks
    private HotelServiceImpl hotelService;

    private Hotel testHotel;

    @BeforeEach
    void setUp() {
        testHotel = new Hotel();
        testHotel.setHotelId(1L);
        testHotel.setHotelName("Test Hotel");
        testHotel.setCity("Mumbai");
        testHotel.setState("Maharashtra");
        testHotel.setCountry("India");
        testHotel.setDescription("Test hotel description");
        testHotel.setAcRoomCost(2000.0);
        testHotel.setNonAcRoomCost(1500.0);
        testHotel.setTotalAcRooms(10);
        testHotel.setAvailableAcRooms(8);
        testHotel.setTotalNonAcRooms(15);
        testHotel.setAvailableNonAcRooms(12);
        
        // Set upload directory for testing
        ReflectionTestUtils.setField(hotelService, "uploadDir", "test-uploads");
    }

    @Test
    void addHotel_Success() {
        // Given
        when(hotelRepository.save(any(Hotel.class))).thenReturn(testHotel);

        // When
        Hotel result = hotelService.addHotel(testHotel);

        // Then
        assertNotNull(result);
        assertEquals("Test Hotel", result.getHotelName());
        assertEquals("Mumbai", result.getCity());
        verify(hotelRepository).save(testHotel);
    }

    @Test
    void getHotelById_Success() {
        // Given
        when(hotelRepository.findById(1L)).thenReturn(Optional.of(testHotel));

        // When
        Optional<Hotel> result = hotelService.getHotelById(1L);

        // Then
        assertTrue(result.isPresent());
        assertEquals("Test Hotel", result.get().getHotelName());
        verify(hotelRepository).findById(1L);
    }

    @Test
    void getHotelById_NotFound() {
        // Given
        when(hotelRepository.findById(1L)).thenReturn(Optional.empty());

        // When
        Optional<Hotel> result = hotelService.getHotelById(1L);

        // Then
        assertFalse(result.isPresent());
        verify(hotelRepository).findById(1L);
    }

    @Test
    void getAllHotels_Success() {
        // Given
        List<Hotel> hotels = Arrays.asList(testHotel);
        when(hotelRepository.findAll()).thenReturn(hotels);

        // When
        List<Hotel> result = hotelService.getAllHotels();

        // Then
        assertEquals(1, result.size());
        assertEquals("Test Hotel", result.get(0).getHotelName());
        verify(hotelRepository).findAll();
    }

    @Test
    void searchHotelsByCity_Success() {
        // Given
        List<Hotel> hotels = Arrays.asList(testHotel);
        when(hotelRepository.findByCityIgnoreCase("Mumbai")).thenReturn(hotels);

        // When
        List<Hotel> result = hotelService.searchHotelsByCity("Mumbai");

        // Then
        assertEquals(1, result.size());
        assertEquals("Mumbai", result.get(0).getCity());
        verify(hotelRepository).findByCityIgnoreCase("Mumbai");
    }

    @Test
    void searchHotelsByName_Success() {
        // Given
        List<Hotel> hotels = Arrays.asList(testHotel);
        when(hotelRepository.findByHotelNameContainingIgnoreCase("Test")).thenReturn(hotels);

        // When
        List<Hotel> result = hotelService.searchHotelsByName("Test");

        // Then
        assertEquals(1, result.size());
        assertEquals("Test Hotel", result.get(0).getHotelName());
        verify(hotelRepository).findByHotelNameContainingIgnoreCase("Test");
    }

    @Test
    void updateHotel_Success() {
        // Given
        Hotel updatedHotel = new Hotel();
        updatedHotel.setHotelName("Updated Hotel");
        updatedHotel.setCity("Delhi");
        updatedHotel.setState("Delhi");
        updatedHotel.setCountry("India");
        updatedHotel.setDescription("Updated description");
        updatedHotel.setAcRoomCost(2500.0);
        updatedHotel.setNonAcRoomCost(1800.0);
        updatedHotel.setTotalAcRooms(12);
        updatedHotel.setAvailableAcRooms(10);
        updatedHotel.setTotalNonAcRooms(18);
        updatedHotel.setAvailableNonAcRooms(15);

        when(hotelRepository.findById(1L)).thenReturn(Optional.of(testHotel));
        when(hotelRepository.save(any(Hotel.class))).thenReturn(testHotel);

        // When
        Hotel result = hotelService.updateHotel(1L, updatedHotel);

        // Then
        assertNotNull(result);
        verify(hotelRepository).findById(1L);
        verify(hotelRepository).save(any(Hotel.class));
    }

    @Test
    void updateHotel_NotFound() {
        // Given
        when(hotelRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> hotelService.updateHotel(1L, testHotel));
        assertEquals("Hotel not found with id: 1", exception.getMessage());
        verify(hotelRepository).findById(1L);
        verify(hotelRepository, never()).save(any(Hotel.class));
    }

    @Test
    void deleteHotel_Success() {
        // Given
        when(hotelRepository.existsById(1L)).thenReturn(true);

        // When
        hotelService.deleteHotel(1L);

        // Then
        verify(hotelRepository).existsById(1L);
        verify(hotelRepository).deleteById(1L);
    }

    @Test
    void deleteHotel_NotFound() {
        // Given
        when(hotelRepository.existsById(1L)).thenReturn(false);

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> hotelService.deleteHotel(1L));
        assertEquals("Hotel not found with id: 1", exception.getMessage());
        verify(hotelRepository).existsById(1L);
        verify(hotelRepository, never()).deleteById(anyLong());
    }

    @Test
    void updateRoomAvailability_ACRoom_Reservation_Success() {
        // Given
        when(hotelRepository.findById(1L)).thenReturn(Optional.of(testHotel));
        when(hotelRepository.save(any(Hotel.class))).thenReturn(testHotel);

        // When
        boolean result = hotelService.updateRoomAvailability(1L, "AC", 2, true);

        // Then
        assertTrue(result);
        verify(hotelRepository).findById(1L);
        verify(hotelRepository).save(any(Hotel.class));
    }

    @Test
    void updateRoomAvailability_ACRoom_InsufficientRooms() {
        // Given
        when(hotelRepository.findById(1L)).thenReturn(Optional.of(testHotel));

        // When
        boolean result = hotelService.updateRoomAvailability(1L, "AC", 10, true);

        // Then
        assertFalse(result);
        verify(hotelRepository).findById(1L);
        verify(hotelRepository, never()).save(any(Hotel.class));
    }

    @Test
    void updateRoomAvailability_NonACRoom_Cancellation_Success() {
        // Given
        when(hotelRepository.findById(1L)).thenReturn(Optional.of(testHotel));
        when(hotelRepository.save(any(Hotel.class))).thenReturn(testHotel);

        // When
        boolean result = hotelService.updateRoomAvailability(1L, "NON_AC", 2, false);

        // Then
        assertTrue(result);
        verify(hotelRepository).findById(1L);
        verify(hotelRepository).save(any(Hotel.class));
    }

    @Test
    void updateRoomAvailability_InvalidRoomType() {
        // Given
        when(hotelRepository.findById(1L)).thenReturn(Optional.of(testHotel));

        // When
        boolean result = hotelService.updateRoomAvailability(1L, "INVALID", 2, true);

        // Then
        assertFalse(result);
        verify(hotelRepository).findById(1L);
        verify(hotelRepository, never()).save(any(Hotel.class));
    }

    @Test
    void checkRoomAvailability_ACRoom_Available() {
        // Given
        when(hotelRepository.findById(1L)).thenReturn(Optional.of(testHotel));

        // When
        boolean result = hotelService.checkRoomAvailability(1L, "AC", 5);

        // Then
        assertTrue(result);
        verify(hotelRepository).findById(1L);
    }

    @Test
    void checkRoomAvailability_NonACRoom_NotAvailable() {
        // Given
        when(hotelRepository.findById(1L)).thenReturn(Optional.of(testHotel));

        // When
        boolean result = hotelService.checkRoomAvailability(1L, "NON_AC", 15);

        // Then
        assertFalse(result);
        verify(hotelRepository).findById(1L);
    }

    @Test
    void uploadHotelImage_HotelNotFound() {
        // Given
        MockMultipartFile file = new MockMultipartFile("image", "test.jpg", "image/jpeg", "test data".getBytes());
        when(hotelRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> hotelService.uploadHotelImage(1L, file));
        assertEquals("Hotel not found with id: 1", exception.getMessage());
        verify(hotelRepository).findById(1L);
    }

    @Test
    void uploadHotelImage_EmptyFile() {
        // Given
        MockMultipartFile file = new MockMultipartFile("image", "test.jpg", "image/jpeg", new byte[0]);
        when(hotelRepository.findById(1L)).thenReturn(Optional.of(testHotel));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> hotelService.uploadHotelImage(1L, file));
        assertEquals("Please select a file to upload", exception.getMessage());
        verify(hotelRepository).findById(1L);
    }

    @Test
    void uploadHotelImage_InvalidFileType() {
        // Given
        MockMultipartFile file = new MockMultipartFile("file", "test.txt", "text/plain", "test data".getBytes());
        when(hotelRepository.findById(1L)).thenReturn(Optional.of(testHotel));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> hotelService.uploadHotelImage(1L, file));
        assertEquals("Only image files are allowed", exception.getMessage());
        verify(hotelRepository).findById(1L);
    }

    @Test
    void getHotelImage_HotelNotFound() {
        // Given
        when(hotelRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> hotelService.getHotelImage(1L));
        assertEquals("Hotel not found with id: 1", exception.getMessage());
        verify(hotelRepository).findById(1L);
    }

    @Test
    void getHotelImage_NoImagePath() {
        // Given
        testHotel.setImagePath(null);
        when(hotelRepository.findById(1L)).thenReturn(Optional.of(testHotel));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> hotelService.getHotelImage(1L));
        assertEquals("No image found for hotel with id: 1", exception.getMessage());
        verify(hotelRepository).findById(1L);
    }
}
