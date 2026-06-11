package com.studyroom.service;

import com.studyroom.entity.User;

import java.util.List;

public interface UserService {
    User login(String username, String password);
    User register(String username, String password);
    List<User> findStudents();
    void deleteStudent(Long id);
}
