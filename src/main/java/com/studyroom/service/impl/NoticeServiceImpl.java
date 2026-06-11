package com.studyroom.service.impl;

import com.studyroom.entity.Notice;
import com.studyroom.mapper.NoticeMapper;
import com.studyroom.service.NoticeService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NoticeServiceImpl implements NoticeService {
    private final NoticeMapper noticeMapper;

    public NoticeServiceImpl(NoticeMapper noticeMapper) { this.noticeMapper = noticeMapper; }
    public List<Notice> findAll() { return noticeMapper.findAll(); }
    public Notice findById(Long id) { return noticeMapper.findById(id); }
    public void save(Notice notice) {
        if (notice.getId() == null) noticeMapper.insert(notice); else noticeMapper.update(notice);
    }
    public void delete(Long id) { noticeMapper.delete(id); }
}
