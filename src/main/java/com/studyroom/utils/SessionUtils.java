package com.studyroom.utils;

import com.studyroom.entity.User;
import jakarta.servlet.http.HttpSession;

public final class SessionUtils {
    public static final String LOGIN_USER = "loginUser";

    private SessionUtils() {}

    public static User getLoginUser(HttpSession session) {
        return (User) session.getAttribute(LOGIN_USER);
    }
}
