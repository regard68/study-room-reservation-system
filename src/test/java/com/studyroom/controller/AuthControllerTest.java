package com.studyroom.controller;

import com.studyroom.entity.User;
import com.studyroom.service.UserService;
import com.studyroom.utils.BusinessException;
import com.studyroom.utils.SessionUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.springframework.dao.DataAccessResourceFailureException;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class AuthControllerTest {
    private UserService userService;
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        userService = mock(UserService.class);
        InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
        viewResolver.setPrefix("/WEB-INF/views/");
        viewResolver.setSuffix(".html");
        mockMvc = MockMvcBuilders.standaloneSetup(new AuthController(userService))
                .setViewResolvers(viewResolver)
                .build();
    }

    @Test
    void registerShouldAutoLoginAndRedirectToStudentHome() throws Exception {
        User user = new User();
        user.setId(10L);
        user.setUsername("newstudent");
        user.setRole("STUDENT");
        when(userService.register("newstudent", "123456")).thenReturn(user);

        mockMvc.perform(post("/register")
                        .param("username", "newstudent")
                        .param("password", "123456"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/student/home"))
                .andExpect(request().sessionAttribute(SessionUtils.LOGIN_USER, user));
    }

    @Test
    void registerFailureShouldStayOnRegisterPage() throws Exception {
        when(userService.register("student", "123456"))
                .thenThrow(new BusinessException("用户名已存在"));

        mockMvc.perform(post("/register")
                        .param("username", "student")
                        .param("password", "123456"))
                .andExpect(status().isOk())
                .andExpect(view().name("register"))
                .andExpect(model().attribute("error", "用户名已存在"))
                .andExpect(model().attribute("username", "student"));
    }

    @Test
    void registerDatabaseFailureShouldShowFriendlyMessage() throws Exception {
        when(userService.register("newstudent", "123456"))
                .thenThrow(new DataAccessResourceFailureException("connection failed"));

        mockMvc.perform(post("/register")
                        .param("username", "newstudent")
                        .param("password", "123456"))
                .andExpect(status().isOk())
                .andExpect(view().name("register"))
                .andExpect(model().attribute("error",
                        "数据库连接失败，请检查 MySQL 是否启动以及数据库账号密码是否正确"));
    }
}
