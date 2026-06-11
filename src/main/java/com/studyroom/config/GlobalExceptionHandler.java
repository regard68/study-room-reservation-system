package com.studyroom.config;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(DataAccessException.class)
    public String handleDatabaseError(DataAccessException exception, HttpServletRequest request, Model model) {
        log.error("Database operation failed for {}", request.getRequestURI(), exception);
        model.addAttribute("title", "数据库连接失败");
        model.addAttribute("message", "请确认 MySQL 已启动，并检查 DB_USERNAME 和 DB_PASSWORD 配置。");
        return "error/system-error";
    }

    @ExceptionHandler(Exception.class)
    public String handleUnexpectedError(Exception exception, HttpServletRequest request, Model model) {
        log.error("Unexpected error for {}", request.getRequestURI(), exception);
        model.addAttribute("title", "系统暂时无法完成操作");
        model.addAttribute("message", "请返回上一页重试；如果问题持续出现，请查看启动终端中的错误信息。");
        return "error/system-error";
    }
}
