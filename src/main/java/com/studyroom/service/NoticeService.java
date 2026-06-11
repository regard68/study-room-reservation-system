package com.studyroom.service;

import com.studyroom.entity.Notice;

import java.util.List;

public interface NoticeService {
    List<Notice> findAll();
    Notice findById(Long id);
    void save(Notice notice);
    void delete(Long id);
}
