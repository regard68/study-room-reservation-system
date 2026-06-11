package com.studyroom.service;

import com.studyroom.entity.Reservation;

import java.time.LocalDate;
import java.util.List;

public interface ReservationService {
    void reserve(Long userId, String reservationName, Long roomId, Long seatId,
                 LocalDate reserveDate, String timeSlot);
    List<Reservation> findByUserId(Long userId);
    List<Reservation> search(String username, Long roomId, LocalDate reserveDate, String status);
    void cancelByStudent(Long id, Long userId);
    void cancelByAdmin(Long id);
}
