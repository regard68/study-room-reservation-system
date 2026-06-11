package com.studyroom.service;

import com.studyroom.entity.Seat;
import com.studyroom.entity.Reservation;
import com.studyroom.entity.StudyRoom;
import com.studyroom.mapper.ReservationMapper;
import com.studyroom.mapper.SeatMapper;
import com.studyroom.mapper.StudyRoomMapper;
import com.studyroom.service.impl.ReservationServiceImpl;
import com.studyroom.utils.BusinessException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ReservationServiceImplTest {
    private ReservationMapper reservationMapper;
    private StudyRoomMapper roomMapper;
    private SeatMapper seatMapper;
    private ReservationServiceImpl service;

    @BeforeEach
    void setUp() {
        reservationMapper = mock(ReservationMapper.class);
        roomMapper = mock(StudyRoomMapper.class);
        seatMapper = mock(SeatMapper.class);
        service = new ReservationServiceImpl(reservationMapper, roomMapper, seatMapper);

        StudyRoom room = new StudyRoom();
        room.setId(1L);
        room.setStatus("OPEN");
        Seat seat = new Seat();
        seat.setId(2L);
        seat.setRoomId(1L);
        seat.setStatus("AVAILABLE");
        when(roomMapper.findById(1L)).thenReturn(room);
        when(seatMapper.findById(2L)).thenReturn(seat);
    }

    @Test
    void shouldCreateReservationWhenNoConflict() {
        service.reserve(3L, " 张三 ", 1L, 2L, LocalDate.now().plusDays(1), "08:00-10:00");
        var captor = org.mockito.ArgumentCaptor.forClass(Reservation.class);
        verify(reservationMapper).insert(captor.capture());
        assertEquals("张三", captor.getValue().getReservationName());
    }

    @Test
    void shouldRejectSeatConflict() {
        LocalDate date = LocalDate.now().plusDays(1);
        when(reservationMapper.countSeatConflict(2L, date, "08:00-10:00")).thenReturn(1);
        BusinessException exception = assertThrows(BusinessException.class,
                () -> service.reserve(3L, "张三", 1L, 2L, date, "08:00-10:00"));
        assertEquals("该座位当前时间段已被预约", exception.getMessage());
        verify(reservationMapper, never()).insert(any());
    }

    @Test
    void shouldRejectNameConflict() {
        LocalDate date = LocalDate.now().plusDays(1);
        when(reservationMapper.countNameConflict("张三", date, "08:00-10:00")).thenReturn(1);
        BusinessException exception = assertThrows(BusinessException.class,
                () -> service.reserve(3L, "张三", 1L, 2L, date, "08:00-10:00"));
        assertEquals("该姓名在当前时间段已有其他预约", exception.getMessage());
    }

    @Test
    void shouldAllowSameAccountToReserveWithDifferentNames() {
        LocalDate date = LocalDate.now().plusDays(1);
        service.reserve(3L, "张三", 1L, 2L, date, "08:00-10:00");
        service.reserve(3L, "李四", 1L, 2L, date, "08:00-10:00");
        verify(reservationMapper, times(2)).insert(any());
    }

    @Test
    void shouldAllowSameNameInDifferentTimeSlots() {
        LocalDate date = LocalDate.now().plusDays(1);
        service.reserve(3L, "张三", 1L, 2L, date, "08:00-10:00");
        service.reserve(3L, "张三", 1L, 2L, date, "10:00-12:00");
        verify(reservationMapper, times(2)).insert(any());
    }

    @Test
    void shouldRejectBlankReservationName() {
        BusinessException exception = assertThrows(BusinessException.class,
                () -> service.reserve(3L, "  ", 1L, 2L, LocalDate.now().plusDays(1), "08:00-10:00"));
        assertEquals("请填写预约人姓名", exception.getMessage());
        verifyNoInteractions(roomMapper, seatMapper, reservationMapper);
    }

    @Test
    void shouldRejectPastDate() {
        assertThrows(BusinessException.class,
                () -> service.reserve(3L, "张三", 1L, 2L, LocalDate.now().minusDays(1), "08:00-10:00"));
        verifyNoInteractions(roomMapper, seatMapper);
    }
}
