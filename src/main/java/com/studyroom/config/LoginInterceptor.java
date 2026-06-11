package com.studyroom.config;

import com.studyroom.entity.User;
import com.studyroom.utils.SessionUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class LoginInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        User user = SessionUtils.getLoginUser(request.getSession());
        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return false;
        }
        String path = request.getRequestURI();
        if (path.startsWith(request.getContextPath() + "/admin") && !"ADMIN".equals(user.getRole())) {
            response.sendRedirect(request.getContextPath() + "/student/home");
            return false;
        }
        if (path.startsWith(request.getContextPath() + "/student") && !"STUDENT".equals(user.getRole())) {
            response.sendRedirect(request.getContextPath() + "/admin/home");
            return false;
        }
        return true;
    }
}
