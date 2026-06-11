package com.studyroom.service;

import com.studyroom.entity.StudyRoom;

import java.util.List;

public interface StudyRoomService {
    List<StudyRoom> findAll();
    List<StudyRoom> findOpenRooms();
    StudyRoom findById(Long id);
    void save(StudyRoom room);
    void delete(Long id);
}
