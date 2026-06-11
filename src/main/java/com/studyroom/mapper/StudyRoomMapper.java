package com.studyroom.mapper;

import com.studyroom.entity.StudyRoom;
import org.apache.ibatis.annotations.*;

import java.util.List;

public interface StudyRoomMapper {
    @Select("SELECT * FROM study_room ORDER BY id DESC")
    List<StudyRoom> findAll();

    @Select("SELECT * FROM study_room WHERE status = 'OPEN' ORDER BY id DESC")
    List<StudyRoom> findOpenRooms();

    @Select("SELECT * FROM study_room WHERE id = #{id}")
    StudyRoom findById(Long id);

    @Insert("INSERT INTO study_room(room_no,room_name,location,open_time,seat_count,status) " +
            "VALUES(#{roomNo},#{roomName},#{location},#{openTime},0,#{status})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(StudyRoom room);

    @Update("UPDATE study_room SET room_no=#{roomNo}, room_name=#{roomName}, location=#{location}, " +
            "open_time=#{openTime}, status=#{status} WHERE id=#{id}")
    int update(StudyRoom room);

    @Delete("DELETE FROM study_room WHERE id = #{id}")
    int delete(Long id);

    @Update("UPDATE study_room SET seat_count=(SELECT COUNT(*) FROM seat WHERE room_id=#{roomId}) WHERE id=#{roomId}")
    int refreshSeatCount(Long roomId);
}
