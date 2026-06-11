package com.studyroom.service.impl;

import com.studyroom.entity.StudyRoom;
import com.studyroom.mapper.StudyRoomMapper;
import com.studyroom.service.StudyRoomService;
import com.studyroom.utils.BusinessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StudyRoomServiceImpl implements StudyRoomService {
    private final StudyRoomMapper roomMapper;

    public StudyRoomServiceImpl(StudyRoomMapper roomMapper) {
        this.roomMapper = roomMapper;
    }

    public List<StudyRoom> findAll() { return roomMapper.findAll(); }
    public List<StudyRoom> findOpenRooms() { return roomMapper.findOpenRooms(); }
    public StudyRoom findById(Long id) { return roomMapper.findById(id); }

    @Override
    public void save(StudyRoom room) {
        if (room.getStatus() == null) room.setStatus("OPEN");
        try {
            if (room.getId() == null) roomMapper.insert(room); else roomMapper.update(room);
        } catch (DataIntegrityViolationException e) {
            throw new BusinessException("自习室编号已存在");
        }
    }

    @Override
    public void delete(Long id) {
        try {
            roomMapper.delete(id);
        } catch (DataIntegrityViolationException e) {
            throw new BusinessException("该自习室已有预约记录，不能删除");
        }
    }
}
