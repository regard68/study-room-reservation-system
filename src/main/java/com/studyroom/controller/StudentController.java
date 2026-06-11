package com.studyroom.controller;

import com.studyroom.entity.StudyRoom;
import com.studyroom.entity.User;
import com.studyroom.service.NoticeService;
import com.studyroom.service.ReservationService;
import com.studyroom.service.SeatService;
import com.studyroom.service.StudyRoomService;
import com.studyroom.utils.BusinessException;
import com.studyroom.utils.SessionUtils;
import jakarta.servlet.http.HttpSession;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;

@Controller
@RequestMapping("/student")
public class StudentController {
    private final StudyRoomService roomService;
    private final SeatService seatService;
    private final ReservationService reservationService;
    private final NoticeService noticeService;

    public StudentController(StudyRoomService roomService, SeatService seatService,
                             ReservationService reservationService, NoticeService noticeService) {
        this.roomService = roomService;
        this.seatService = seatService;
        this.reservationService = reservationService;
        this.noticeService = noticeService;
    }

    @GetMapping("/home")
    public String home(Model model, HttpSession session) {
        User user = SessionUtils.getLoginUser(session);
        model.addAttribute("rooms", roomService.findOpenRooms());
        model.addAttribute("reservations", reservationService.findByUserId(user.getId()));
        model.addAttribute("notices", noticeService.findAll());
        return "student/home";
    }

    @GetMapping("/rooms")
    public String rooms(Model model) {
        model.addAttribute("rooms", roomService.findOpenRooms());
        return "student/rooms";
    }

    @GetMapping("/rooms/{roomId}/seats")
    public String seats(@PathVariable Long roomId, Model model) {
        StudyRoom room = roomService.findById(roomId);
        if (room == null || !"OPEN".equals(room.getStatus())) return "redirect:/student/rooms";
        model.addAttribute("room", room);
        model.addAttribute("seats", seatService.findByRoomId(roomId));
        model.addAttribute("today", LocalDate.now());
        return "student/seats";
    }

    @PostMapping("/reserve")
    public String reserve(@RequestParam Long roomId, @RequestParam Long seatId,
                          @RequestParam String reservationName,
                          @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate reserveDate,
                          @RequestParam String timeSlot, HttpSession session,
                          RedirectAttributes redirectAttributes) {
        User user = SessionUtils.getLoginUser(session);
        try {
            reservationService.reserve(user.getId(), reservationName, roomId, seatId, reserveDate, timeSlot);
            redirectAttributes.addFlashAttribute("success", "座位预约成功");
        } catch (BusinessException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/student/rooms/" + roomId + "/seats";
    }

    @GetMapping("/reservations")
    public String reservations(Model model, HttpSession session) {
        User user = SessionUtils.getLoginUser(session);
        model.addAttribute("reservations", reservationService.findByUserId(user.getId()));
        model.addAttribute("today", LocalDate.now());
        return "student/reservations";
    }

    @PostMapping("/reservations/{id}/cancel")
    public String cancel(@PathVariable Long id, HttpSession session, RedirectAttributes redirectAttributes) {
        User user = SessionUtils.getLoginUser(session);
        try {
            reservationService.cancelByStudent(id, user.getId());
            redirectAttributes.addFlashAttribute("success", "预约已取消");
        } catch (BusinessException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/student/reservations";
    }

    @GetMapping("/notices")
    public String notices(Model model) {
        model.addAttribute("notices", noticeService.findAll());
        return "student/notices";
    }
}
