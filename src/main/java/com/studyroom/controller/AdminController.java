package com.studyroom.controller;

import com.studyroom.entity.Notice;
import com.studyroom.entity.Seat;
import com.studyroom.entity.StudyRoom;
import com.studyroom.service.*;
import com.studyroom.utils.BusinessException;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;

@Controller
@RequestMapping("/admin")
public class AdminController {
    private final UserService userService;
    private final StudyRoomService roomService;
    private final SeatService seatService;
    private final ReservationService reservationService;
    private final NoticeService noticeService;

    public AdminController(UserService userService, StudyRoomService roomService, SeatService seatService,
                           ReservationService reservationService, NoticeService noticeService) {
        this.userService = userService;
        this.roomService = roomService;
        this.seatService = seatService;
        this.reservationService = reservationService;
        this.noticeService = noticeService;
    }

    @GetMapping("/home")
    public String home(Model model) {
        model.addAttribute("userCount", userService.findStudents().size());
        model.addAttribute("roomCount", roomService.findAll().size());
        model.addAttribute("seatCount", seatService.findAll().size());
        model.addAttribute("reservationCount", reservationService.search(null, null, null, null).size());
        model.addAttribute("recentReservations", reservationService.search(null, null, null, null));
        return "admin/home";
    }

    @GetMapping("/users")
    public String users(Model model) {
        model.addAttribute("users", userService.findStudents());
        return "admin/users";
    }

    @PostMapping("/users/{id}/delete")
    public String deleteUser(@PathVariable Long id, RedirectAttributes ra) {
        return execute(() -> userService.deleteStudent(id), "用户已删除", "redirect:/admin/users", ra);
    }

    @GetMapping("/rooms")
    public String rooms(@RequestParam(required = false) Long editId, Model model) {
        model.addAttribute("rooms", roomService.findAll());
        model.addAttribute("roomForm", editId == null ? new StudyRoom() : roomService.findById(editId));
        return "admin/rooms";
    }

    @PostMapping("/rooms/save")
    public String saveRoom(StudyRoom room, RedirectAttributes ra) {
        return execute(() -> roomService.save(room), "自习室信息已保存", "redirect:/admin/rooms", ra);
    }

    @PostMapping("/rooms/{id}/delete")
    public String deleteRoom(@PathVariable Long id, RedirectAttributes ra) {
        return execute(() -> roomService.delete(id), "自习室已删除", "redirect:/admin/rooms", ra);
    }

    @GetMapping("/seats")
    public String seats(@RequestParam(required = false) Long roomId,
                        @RequestParam(required = false) Long editId, Model model) {
        model.addAttribute("rooms", roomService.findAll());
        model.addAttribute("selectedRoomId", roomId);
        model.addAttribute("seats", roomId == null ? seatService.findAll() : seatService.findByRoomId(roomId));
        Seat form = editId == null ? new Seat() : seatService.findById(editId);
        if (form != null && form.getRoomId() == null) form.setRoomId(roomId);
        model.addAttribute("seatForm", form);
        return "admin/seats";
    }

    @PostMapping("/seats/save")
    public String saveSeat(Seat seat, RedirectAttributes ra) {
        return execute(() -> seatService.save(seat), "座位信息已保存",
                "redirect:/admin/seats?roomId=" + seat.getRoomId(), ra);
    }

    @PostMapping("/seats/{id}/delete")
    public String deleteSeat(@PathVariable Long id, @RequestParam(required = false) Long roomId,
                             RedirectAttributes ra) {
        String url = roomId == null ? "redirect:/admin/seats" : "redirect:/admin/seats?roomId=" + roomId;
        return execute(() -> seatService.delete(id), "座位已删除", url, ra);
    }

    @GetMapping("/reservations")
    public String reservations(@RequestParam(required = false) String username,
                               @RequestParam(required = false) Long roomId,
                               @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate reserveDate,
                               @RequestParam(required = false) String status, Model model) {
        model.addAttribute("reservations", reservationService.search(username, roomId, reserveDate, status));
        model.addAttribute("rooms", roomService.findAll());
        model.addAttribute("username", username);
        model.addAttribute("roomId", roomId);
        model.addAttribute("reserveDate", reserveDate);
        model.addAttribute("status", status);
        return "admin/reservations";
    }

    @PostMapping("/reservations/{id}/cancel")
    public String cancelReservation(@PathVariable Long id, RedirectAttributes ra) {
        reservationService.cancelByAdmin(id);
        ra.addFlashAttribute("success", "预约已取消");
        return "redirect:/admin/reservations";
    }

    @GetMapping("/notices")
    public String notices(@RequestParam(required = false) Long editId, Model model) {
        model.addAttribute("notices", noticeService.findAll());
        model.addAttribute("noticeForm", editId == null ? new Notice() : noticeService.findById(editId));
        return "admin/notices";
    }

    @PostMapping("/notices/save")
    public String saveNotice(Notice notice, RedirectAttributes ra) {
        noticeService.save(notice);
        ra.addFlashAttribute("success", "公告已保存");
        return "redirect:/admin/notices";
    }

    @PostMapping("/notices/{id}/delete")
    public String deleteNotice(@PathVariable Long id, RedirectAttributes ra) {
        noticeService.delete(id);
        ra.addFlashAttribute("success", "公告已删除");
        return "redirect:/admin/notices";
    }

    private String execute(Runnable action, String success, String redirect, RedirectAttributes ra) {
        try {
            action.run();
            ra.addFlashAttribute("success", success);
        } catch (BusinessException e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return redirect;
    }
}
