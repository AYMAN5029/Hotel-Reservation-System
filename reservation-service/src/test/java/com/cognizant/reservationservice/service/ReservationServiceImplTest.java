package com.cognizant.reservationservice.service;

import com.cognizant.reservationservice.client.HotelServiceClient;
import com.cognizant.reservationservice.model.Reservation;
import com.cognizant.reservationservice.model.ReservationStatus;
import com.cognizant.reservationservice.repository.ReservationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReservationServiceImplTest {

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private HotelServiceClient hotelServiceClient;

    @InjectMocks
    private ReservationServiceImpl reservationService;

    private Reservation testReservation;

    @BeforeEach
    void setUp() {
        testReservation = new Reservation();
        testReservation.setReservationId(1L);
        testReservation.setUserId(1L);
        testReservation.setHotelId(1L);
        testReservation.setRoomType("AC");
        testReservation.setNumberOfRooms(2);
        testReservation.setCheckInDate(LocalDate.now().plusDays(5));
        testReservation.setCheckOutDate(LocalDate.now().plusDays(7));
        testReservation.setNumberOfGuests(2);
        testReservation.setTotalCost(4000.0);
        testReservation.setStatus(ReservationStatus.PENDING);
        testReservation.setCreatedAt(LocalDateTime.now());
        testReservation.setRefundedAmount(0.0);
    }

    @Test
    void createReservation_Success() {
        // Given
        when(hotelServiceClient.checkRoomAvailability(1L, "AC", 2)).thenReturn(true);
        when(hotelServiceClient.updateRoomAvailability(1L, "AC", 2, true)).thenReturn(true);
        when(reservationRepository.save(any(Reservation.class))).thenReturn(testReservation);

        // When
        Reservation result = reservationService.createReservation(testReservation);

        // Then
        assertNotNull(result);
        assertEquals(ReservationStatus.PENDING, result.getStatus());
        assertEquals(1L, result.getHotelId());
        verify(hotelServiceClient).checkRoomAvailability(1L, "AC", 2);
        verify(hotelServiceClient).updateRoomAvailability(1L, "AC", 2, true);
        verify(reservationRepository).save(any(Reservation.class));
    }

    @Test
    void createReservation_InsufficientRooms() {
        // Given
        when(hotelServiceClient.checkRoomAvailability(1L, "AC", 2)).thenReturn(false);

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> reservationService.createReservation(testReservation));
        assertEquals("Room availability check failed: Insufficient rooms available for the requested reservation", exception.getMessage());
        verify(hotelServiceClient).checkRoomAvailability(1L, "AC", 2);
        verify(hotelServiceClient, never()).updateRoomAvailability(anyLong(), anyString(), anyInt(), anyBoolean());
        verify(reservationRepository, never()).save(any(Reservation.class));
    }

    @Test
    void createReservation_UpdateRoomAvailabilityFails() {
        // Given
        when(hotelServiceClient.checkRoomAvailability(1L, "AC", 2)).thenReturn(true);
        when(hotelServiceClient.updateRoomAvailability(1L, "AC", 2, true)).thenReturn(false);

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> reservationService.createReservation(testReservation));
        assertEquals("Room availability check failed: Failed to update room availability", exception.getMessage());
        verify(hotelServiceClient).checkRoomAvailability(1L, "AC", 2);
        verify(hotelServiceClient).updateRoomAvailability(1L, "AC", 2, true);
        verify(reservationRepository, never()).save(any(Reservation.class));
    }

    @Test
    void getReservationById_Success() {
        // Given
        when(reservationRepository.findById(1L)).thenReturn(Optional.of(testReservation));

        // When
        Optional<Reservation> result = reservationService.getReservationById(1L);

        // Then
        assertTrue(result.isPresent());
        assertEquals(1L, result.get().getReservationId());
        verify(reservationRepository).findById(1L);
    }

    @Test
    void getReservationsByUserId_Success() {
        // Given
        List<Reservation> reservations = Arrays.asList(testReservation);
        when(reservationRepository.findByUserId(1L)).thenReturn(reservations);

        // When
        List<Reservation> result = reservationService.getReservationsByUserId(1L);

        // Then
        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getUserId());
        verify(reservationRepository).findByUserId(1L);
    }

    @Test
    void getReservationsByHotelId_Success() {
        // Given
        List<Reservation> reservations = Arrays.asList(testReservation);
        when(reservationRepository.findByHotelId(1L)).thenReturn(reservations);

        // When
        List<Reservation> result = reservationService.getReservationsByHotelId(1L);

        // Then
        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getHotelId());
        verify(reservationRepository).findByHotelId(1L);
    }

    @Test
    void cancelReservation_Success_FullRefund() {
        // Given - 10 days before check-in (100% refund)
        testReservation.setCheckInDate(LocalDate.now().plusDays(10));
        testReservation.setTotalCost(4000.0);
        when(reservationRepository.findById(1L)).thenReturn(Optional.of(testReservation));
        when(hotelServiceClient.updateRoomAvailability(1L, "AC", 2, false)).thenReturn(true);
        when(reservationRepository.save(any(Reservation.class))).thenReturn(testReservation);

        // When
        Reservation result = reservationService.cancelReservation(1L);

        // Then
        assertNotNull(result);
        assertEquals(ReservationStatus.CANCELLED, result.getStatus());
        assertEquals(4000.0, result.getRefundedAmount()); // 100% refund
        verify(reservationRepository).findById(1L);
        verify(hotelServiceClient).updateRoomAvailability(1L, "AC", 2, false);
        verify(reservationRepository).save(any(Reservation.class));
    }

    @Test
    void cancelReservation_Success_PartialRefund() {
        // Given - 4 days before check-in (75% refund)
        testReservation.setCheckInDate(LocalDate.now().plusDays(4));
        testReservation.setTotalCost(4000.0);
        when(reservationRepository.findById(1L)).thenReturn(Optional.of(testReservation));
        when(hotelServiceClient.updateRoomAvailability(1L, "AC", 2, false)).thenReturn(true);
        when(reservationRepository.save(any(Reservation.class))).thenReturn(testReservation);

        // When
        Reservation result = reservationService.cancelReservation(1L);

        // Then
        assertNotNull(result);
        assertEquals(ReservationStatus.CANCELLED, result.getStatus());
        assertEquals(3000.0, result.getRefundedAmount()); // 75% refund
        verify(reservationRepository).findById(1L);
        verify(hotelServiceClient).updateRoomAvailability(1L, "AC", 2, false);
        verify(reservationRepository).save(any(Reservation.class));
    }

    @Test
    void cancelReservation_Success_NoRefund() {
        // Given - same day cancellation (0% refund)
        testReservation.setCheckInDate(LocalDate.now());
        testReservation.setTotalCost(4000.0);
        when(reservationRepository.findById(1L)).thenReturn(Optional.of(testReservation));
        when(hotelServiceClient.updateRoomAvailability(1L, "AC", 2, false)).thenReturn(true);
        when(reservationRepository.save(any(Reservation.class))).thenReturn(testReservation);

        // When
        Reservation result = reservationService.cancelReservation(1L);

        // Then
        assertNotNull(result);
        assertEquals(ReservationStatus.CANCELLED, result.getStatus());
        assertEquals(0.0, result.getRefundedAmount()); // 0% refund
        verify(reservationRepository).findById(1L);
        verify(hotelServiceClient).updateRoomAvailability(1L, "AC", 2, false);
        verify(reservationRepository).save(any(Reservation.class));
    }

    @Test
    void cancelReservation_NotFound() {
        // Given
        when(reservationRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> reservationService.cancelReservation(1L));
        assertEquals("Reservation not found with id: 1", exception.getMessage());
        verify(reservationRepository).findById(1L);
        verify(hotelServiceClient, never()).updateRoomAvailability(anyLong(), anyString(), anyInt(), anyBoolean());
    }

    @Test
    void confirmReservation_Success() {
        // Given
        when(reservationRepository.findById(1L)).thenReturn(Optional.of(testReservation));
        when(reservationRepository.save(any(Reservation.class))).thenReturn(testReservation);

        // When
        Reservation result = reservationService.confirmReservation(1L);

        // Then
        assertNotNull(result);
        assertEquals(ReservationStatus.CONFIRMED, result.getStatus());
        verify(reservationRepository).findById(1L);
        verify(reservationRepository).save(any(Reservation.class));
    }

    @Test
    void confirmReservation_AlreadyConfirmed() {
        // Given
        testReservation.setStatus(ReservationStatus.CONFIRMED);
        when(reservationRepository.findById(1L)).thenReturn(Optional.of(testReservation));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> reservationService.confirmReservation(1L));
        assertEquals("Cannot confirm reservation with status: CONFIRMED", exception.getMessage());
        verify(reservationRepository).findById(1L);
        verify(reservationRepository, never()).save(any(Reservation.class));
    }

    @Test
    void confirmReservation_NotFound() {
        // Given
        when(reservationRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> reservationService.confirmReservation(1L));
        assertEquals("Reservation not found with id: 1", exception.getMessage());
        verify(reservationRepository).findById(1L);
    }

    @Test
    void getReservationsByStatus_Success() {
        // Given
        List<Reservation> reservations = Arrays.asList(testReservation);
        when(reservationRepository.findByStatus(ReservationStatus.PENDING)).thenReturn(reservations);

        // When
        List<Reservation> result = reservationService.getReservationsByStatus(ReservationStatus.PENDING);

        // Then
        assertEquals(1, result.size());
        assertEquals(ReservationStatus.PENDING, result.get(0).getStatus());
        verify(reservationRepository).findByStatus(ReservationStatus.PENDING);
    }

    @Test
    void deleteReservation_Success() {
        // Given
        when(reservationRepository.existsById(1L)).thenReturn(true);

        // When
        reservationService.deleteReservation(1L);

        // Then
        verify(reservationRepository).existsById(1L);
        verify(reservationRepository).deleteById(1L);
    }

    @Test
    void deleteReservation_NotFound() {
        // Given
        when(reservationRepository.existsById(1L)).thenReturn(false);

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> reservationService.deleteReservation(1L));
        assertEquals("Reservation not found with id: 1", exception.getMessage());
        verify(reservationRepository).existsById(1L);
        verify(reservationRepository, never()).deleteById(anyLong());
    }

    @Test
    void deleteReservationsByHotelId_Success() {
        // Given
        Reservation reservation1 = new Reservation();
        reservation1.setReservationId(1L);
        reservation1.setHotelId(1L);
        reservation1.setRoomType("AC");
        reservation1.setNumberOfRooms(2);
        reservation1.setStatus(ReservationStatus.CONFIRMED);

        Reservation reservation2 = new Reservation();
        reservation2.setReservationId(2L);
        reservation2.setHotelId(1L);
        reservation2.setRoomType("NON_AC");
        reservation2.setNumberOfRooms(1);
        reservation2.setStatus(ReservationStatus.PENDING);

        List<Reservation> reservations = Arrays.asList(reservation1, reservation2);
        when(reservationRepository.findByHotelId(1L)).thenReturn(reservations);
        when(hotelServiceClient.updateRoomAvailability(anyLong(), anyString(), anyInt(), eq(false))).thenReturn(true);

        // When
        reservationService.deleteReservationsByHotelId(1L);

        // Then
        verify(reservationRepository).findByHotelId(1L);
        verify(hotelServiceClient, times(2)).updateRoomAvailability(anyLong(), anyString(), anyInt(), eq(false));
        verify(reservationRepository).deleteAll(reservations);
    }

    @Test
    void deleteReservationsByHotelId_NoReservations() {
        // Given
        when(reservationRepository.findByHotelId(1L)).thenReturn(Arrays.asList());

        // When
        reservationService.deleteReservationsByHotelId(1L);

        // Then
        verify(reservationRepository).findByHotelId(1L);
        verify(hotelServiceClient, never()).updateRoomAvailability(anyLong(), anyString(), anyInt(), anyBoolean());
        verify(reservationRepository, never()).deleteAll(any());
    }

    @Test
    void updateReservation_Success() {
        // Given
        Reservation updatedReservation = new Reservation();
        updatedReservation.setUserId(2L);
        updatedReservation.setHotelId(2L);
        updatedReservation.setRoomType("NON_AC");
        updatedReservation.setNumberOfRooms(1);
        updatedReservation.setTotalCost(3000.0);

        when(reservationRepository.findById(1L)).thenReturn(Optional.of(testReservation));
        when(reservationRepository.save(any(Reservation.class))).thenReturn(testReservation);

        // When
        Reservation result = reservationService.updateReservation(1L, updatedReservation);

        // Then
        assertNotNull(result);
        verify(reservationRepository).findById(1L);
        verify(reservationRepository).save(any(Reservation.class));
    }

    @Test
    void updateReservation_NotFound() {
        // Given
        when(reservationRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> reservationService.updateReservation(1L, testReservation));
        assertEquals("Reservation not found with id: 1", exception.getMessage());
        verify(reservationRepository).findById(1L);
        verify(reservationRepository, never()).save(any(Reservation.class));
    }
}
