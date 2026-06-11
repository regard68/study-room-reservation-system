package com.studyroom.service;

import com.studyroom.entity.User;
import com.studyroom.mapper.ReservationMapper;
import com.studyroom.mapper.UserMapper;
import com.studyroom.service.impl.UserServiceImpl;
import com.studyroom.utils.BusinessException;
import com.studyroom.utils.PasswordUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class UserServiceImplTest {
    private UserMapper userMapper;
    private UserServiceImpl service;

    @BeforeEach
    void setUp() {
        userMapper = mock(UserMapper.class);
        service = new UserServiceImpl(userMapper, mock(ReservationMapper.class));
    }

    @Test
    void registerShouldCreateStudentWithoutPhone() {
        User user = service.register(" newstudent ", "123456");

        assertEquals("newstudent", user.getUsername());
        assertEquals("STUDENT", user.getRole());
        assertTrue(PasswordUtils.matches("123456", user.getPassword()));
        verify(userMapper).insert(any(User.class));
    }

    @Test
    void loginShouldTrimUsername() {
        User stored = new User();
        stored.setUsername("student");
        stored.setPassword(PasswordUtils.encode("123456"));
        when(userMapper.findByUsername("student")).thenReturn(stored);

        assertSame(stored, service.login(" student ", "123456"));
    }

    @Test
    void registerShouldRejectShortPassword() {
        BusinessException exception = assertThrows(BusinessException.class,
                () -> service.register("student2", "123"));
        assertEquals("密码长度不能少于 6 位", exception.getMessage());
        verify(userMapper, never()).insert(any());
    }
}
