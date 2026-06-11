package com.studyroom.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Reservation {
    private Long id;
    private Long userId;
    private String reservationName;
    private Long roomId;
    private Long seatId;
    private LocalDate reserveDate;
    private String timeSlot;
    private String status;
    private LocalDateTime createTime;
    private String username;
    private String roomName;
    private String seatNo;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getReservationName() { return reservationName; }
    public void setReservationName(String reservationName) { this.reservationName = reservationName; }
    public Long getRoomId() { return roomId; }
    public void setRoomId(Long roomId) { this.roomId = roomId; }
    public Long getSeatId() { return seatId; }
    public void setSeatId(Long seatId) { this.seatId = seatId; }
    public LocalDate getReserveDate() { return reserveDate; }
    public void setReserveDate(LocalDate reserveDate) { this.reserveDate = reserveDate; }
    public String getTimeSlot() { return timeSlot; }
    public void setTimeSlot(String timeSlot) { this.timeSlot = timeSlot; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getRoomName() { return roomName; }
    public void setRoomName(String roomName) { this.roomName = roomName; }
    public String getSeatNo() { return seatNo; }
    public void setSeatNo(String seatNo) { this.seatNo = seatNo; }
}
