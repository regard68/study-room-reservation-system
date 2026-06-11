package com.studyroom.service.impl;

import com.studyroom.entity.User;
import com.studyroom.mapper.ReservationMapper;
import com.studyroom.mapper.UserMapper;
import com.studyroom.service.UserService;
import com.studyroom.utils.BusinessException;
import com.studyroom.utils.PasswordUtils;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    private final UserMapper userMapper;
    private final ReservationMapper reservationMapper;

    public UserServiceImpl(UserMapper userMapper, ReservationMapper reservationMapper) {
        this.userMapper = userMapper;
        this.reservationMapper = reservationMapper;
    }

    @Override
    public User login(String username, String password) {
        if (!StringUtils.hasText(username) || !StringUtils.hasText(password)) {
            throw new BusinessException("用户名和密码不能为空");
        }
        username = username.trim();
        User user = userMapper.findByUsername(username);
        if (user == null || !PasswordUtils.matches(password, user.getPassword())) {
            throw new BusinessException("用户名或密码错误");
        }
        return user;
    }

    @Override
    public User register(String username, String password) {
        if (!StringUtils.hasText(username) || !StringUtils.hasText(password)) {
            throw new BusinessException("用户名和密码不能为空");
        }
        username = username.trim();
        if (password.length() < 6) {
            throw new BusinessException("密码长度不能少于 6 位");
        }
        if (userMapper.findByUsername(username) != null) {
            throw new BusinessException("用户名已存在");
        }
        User user = new User();
        user.setUsername(username);
        user.setPassword(PasswordUtils.encode(password));
        user.setRole("STUDENT");
        try {
            userMapper.insert(user);
            return user;
        } catch (DataIntegrityViolationException e) {
            throw new BusinessException("用户名已存在");
        }
    }

    @Override
    public List<User> findStudents() {
        return userMapper.findStudents();
    }

    @Override
    @Transactional
    public void deleteStudent(Long id) {
        if (reservationMapper.countActiveByUser(id) > 0) {
            throw new BusinessException("该用户还有有效预约，不能删除");
        }
        try {
            userMapper.deleteStudent(id);
        } catch (DataIntegrityViolationException e) {
            throw new BusinessException("该用户存在历史预约记录，不能删除");
        }
    }
}
