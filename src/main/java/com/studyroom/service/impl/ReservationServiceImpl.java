package com.studyroom.service.impl;

import com.studyroom.entity.Reservation;
import com.studyroom.entity.Seat;
import com.studyroom.entity.StudyRoom;
import com.studyroom.mapper.ReservationMapper;
import com.studyroom.mapper.SeatMapper;
import com.studyroom.mapper.StudyRoomMapper;
import com.studyroom.service.ReservationService;
import com.studyroom.utils.BusinessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
public class ReservationServiceImpl implements ReservationService {
    private final ReservationMapper reservationMapper;
    private final StudyRoomMapper roomMapper;
    private final SeatMapper seatMapper;

    public ReservationServiceImpl(ReservationMapper reservationMapper, StudyRoomMapper roomMapper,
                                  SeatMapper seatMapper) {
        this.reservationMapper = reservationMapper;
        this.roomMapper = roomMapper;
        this.seatMapper = seatMapper;
    }

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void reserve(Long userId, String reservationName, Long roomId, Long seatId,
                        LocalDate reserveDate, String timeSlot) {
        String normalizedName = normalizeName(reservationName);
        if (reserveDate == null || reserveDate.isBefore(LocalDate.now())) {
            throw new BusinessException("预约日期不能早于今天");
        }
        StudyRoom room = roomMapper.findById(roomId);
        Seat seat = seatMapper.findById(seatId);
        if (room == null || !"OPEN".equals(room.getStatus())) {
            throw new BusinessException("该自习室当前未开放");
        }
        if (seat == null || !roomId.equals(seat.getRoomId()) || !"AVAILABLE".equals(seat.getStatus())) {
            throw new BusinessException("该座位当前不可预约");
        }
        if (reservationMapper.countSeatConflict(seatId, reserveDate, timeSlot) > 0) {
            throw new BusinessException("该座位当前时间段已被预约");
        }
        if (reservationMapper.countNameConflict(normalizedName, reserveDate, timeSlot) > 0) {
            throw new BusinessException("该姓名在当前时间段已有其他预约");
        }
        Reservation reservation = new Reservation();
        reservation.setUserId(userId);
        reservation.setReservationName(normalizedName);
        reservation.setRoomId(roomId);
        reservation.setSeatId(seatId);
        reservation.setReserveDate(reserveDate);
        reservation.setTimeSlot(timeSlot);
        reservation.setStatus("RESERVED");
        reservationMapper.insert(reservation);
    }

    private String normalizeName(String reservationName) {
        if (reservationName == null || reservationName.isBlank()) {
            throw new BusinessException("请填写预约人姓名");
        }
        String normalizedName = reservationName.trim().replaceAll("\\s+", " ");
        if (normalizedName.length() > 50) {
            throw new BusinessException("预约人姓名不能超过50个字符");
        }
        return normalizedName;
    }

    public List<Reservation> findByUserId(Long userId) { return reservationMapper.findByUserId(userId); }

    public List<Reservation> search(String username, Long roomId, LocalDate reserveDate, String status) {
        return reservationMapper.search(username, roomId, reserveDate, status);
    }

    @Override
    public void cancelByStudent(Long id, Long userId) {
        Reservation reservation = reservationMapper.findById(id);
        if (reservation == null || !userId.equals(reservation.getUserId())) {
            throw new BusinessException("无权取消该预约");
        }
        if (hasEnded(reservation)) {
            throw new BusinessException("已结束的预约不能取消");
        }
        reservationMapper.cancel(id);
    }

    public void cancelByAdmin(Long id) { reservationMapper.cancel(id); }

    private boolean hasEnded(Reservation reservation) {
        if (reservation.getReserveDate().isBefore(LocalDate.now())) return true;
        if (reservation.getReserveDate().isAfter(LocalDate.now())) return false;
        String[] times = reservation.getTimeSlot().split("-");
        LocalTime endTime = LocalTime.parse(times[1]);
        return LocalDateTime.now().isAfter(reservation.getReserveDate().atTime(endTime));
    }
}
