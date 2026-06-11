package com.studyroom.controller;

import com.studyroom.entity.User;
import com.studyroom.service.NoticeService;
import com.studyroom.service.ReservationService;
import com.studyroom.service.SeatService;
import com.studyroom.service.StudyRoomService;
import com.studyroom.utils.BusinessException;
import com.studyroom.utils.SessionUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class StudentControllerTest {
    private ReservationService reservationService;
    private MockMvc mockMvc;
    private MockHttpSession session;

    @BeforeEach
    void setUp() {
        StudyRoomService roomService = mock(StudyRoomService.class);
        SeatService seatService = mock(SeatService.class);
        reservationService = mock(ReservationService.class);
        NoticeService noticeService = mock(NoticeService.class);
        mockMvc = MockMvcBuilders.standaloneSetup(
                new StudentController(roomService, seatService, reservationService, noticeService))
                .build();

        User user = new User();
        user.setId(3L);
        user.setUsername("student");
        user.setRole("STUDENT");
        session = new MockHttpSession();
        session.setAttribute(SessionUtils.LOGIN_USER, user);
    }

    @Test
    void reserveShouldPassReservationNameToService() throws Exception {
        LocalDate date = LocalDate.now().plusDays(1);

        mockMvc.perform(post("/student/reserve")
                        .session(session)
                        .param("roomId", "1")
                        .param("seatId", "2")
                        .param("reservationName", "张三")
                        .param("reserveDate", date.toString())
                        .param("timeSlot", "08:00-10:00"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/student/rooms/1/seats"))
                .andExpect(flash().attribute("success", "座位预约成功"));

        verify(reservationService).reserve(3L, "张三", 1L, 2L, date, "08:00-10:00");
    }

    @Test
    void reserveNameConflictShouldShowFriendlyMessage() throws Exception {
        LocalDate date = LocalDate.now().plusDays(1);
        doThrow(new BusinessException("该姓名在当前时间段已有其他预约"))
                .when(reservationService).reserve(3L, "张三", 1L, 2L, date, "08:00-10:00");

        mockMvc.perform(post("/student/reserve")
                        .session(session)
                        .param("roomId", "1")
                        .param("seatId", "2")
                        .param("reservationName", "张三")
                        .param("reserveDate", date.toString())
                        .param("timeSlot", "08:00-10:00"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/student/rooms/1/seats"))
                .andExpect(flash().attribute("error", "该姓名在当前时间段已有其他预约"));
    }
}
