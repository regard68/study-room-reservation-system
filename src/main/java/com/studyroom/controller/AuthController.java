package com.studyroom.controller;

import com.studyroom.entity.User;
import com.studyroom.service.UserService;
import com.studyroom.utils.BusinessException;
import com.studyroom.utils.SessionUtils;
import jakarta.servlet.http.HttpSession;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AuthController {
    private final UserService userService;

    public AuthController(UserService userService) { this.userService = userService; }

    @GetMapping("/")
    public String index(HttpSession session) {
        User user = SessionUtils.getLoginUser(session);
        if (user == null) return "redirect:/login";
        return "ADMIN".equals(user.getRole()) ? "redirect:/admin/home" : "redirect:/student/home";
    }

    @GetMapping("/login")
    public String loginPage() { return "login"; }

    @PostMapping("/login")
    public String login(@RequestParam String username, @RequestParam String password,
                        HttpSession session, Model model) {
        try {
            User user = userService.login(username, password);
            session.setAttribute(SessionUtils.LOGIN_USER, user);
            return "ADMIN".equals(user.getRole()) ? "redirect:/admin/home" : "redirect:/student/home";
        } catch (BusinessException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("username", username);
            return "login";
        } catch (DataAccessException e) {
            model.addAttribute("error", "数据库连接失败，请检查 MySQL 是否启动以及数据库账号密码是否正确");
            model.addAttribute("username", username);
            return "login";
        }
    }

    @GetMapping("/register")
    public String registerPage() { return "register"; }

    @PostMapping("/register")
    public String register(@RequestParam String username, @RequestParam String password,
                           HttpSession session, Model model) {
        try {
            User user = userService.register(username, password);
            session.setAttribute(SessionUtils.LOGIN_USER, user);
            return "redirect:/student/home";
        } catch (BusinessException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("username", username);
            return "register";
        } catch (DataAccessException e) {
            model.addAttribute("error", "数据库连接失败，请检查 MySQL 是否启动以及数据库账号密码是否正确");
            model.addAttribute("username", username);
            return "register";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }
}
