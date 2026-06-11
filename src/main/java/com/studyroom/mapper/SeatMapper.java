package com.studyroom.mapper;

import com.studyroom.entity.Seat;
import org.apache.ibatis.annotations.*;

import java.util.List;

public interface SeatMapper {
    @Select("SELECT s.*, r.room_name FROM seat s JOIN study_room r ON s.room_id=r.id " +
            "WHERE s.room_id=#{roomId} ORDER BY s.seat_no")
    List<Seat> findByRoomId(Long roomId);

    @Select("SELECT s.*, r.room_name FROM seat s JOIN study_room r ON s.room_id=r.id ORDER BY r.id,s.seat_no")
    List<Seat> findAll();

    @Select("SELECT * FROM seat WHERE id=#{id}")
    Seat findById(Long id);

    @Insert("INSERT INTO seat(room_id,seat_no,status) VALUES(#{roomId},#{seatNo},#{status})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Seat seat);

    @Update("UPDATE seat SET room_id=#{roomId}, seat_no=#{seatNo}, status=#{status} WHERE id=#{id}")
    int update(Seat seat);

    @Delete("DELETE FROM seat WHERE id=#{id}")
    int delete(Long id);

    @Select("SELECT COUNT(*) FROM seat WHERE room_id=#{roomId} AND seat_no=#{seatNo} AND id<>COALESCE(#{id},0)")
    int countDuplicate(@Param("roomId") Long roomId, @Param("seatNo") String seatNo, @Param("id") Long id);
}
