package com.studyroom.service;

import com.studyroom.entity.Seat;

import java.util.List;

public interface SeatService {
    List<Seat> findAll();
    List<Seat> findByRoomId(Long roomId);
    Seat findById(Long id);
    void save(Seat seat);
    void delete(Long id);
}
