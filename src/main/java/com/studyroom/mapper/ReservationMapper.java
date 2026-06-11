package com.studyroom.mapper;

import com.studyroom.entity.Reservation;
import org.apache.ibatis.annotations.*;

import java.time.LocalDate;
import java.util.List;

public interface ReservationMapper {
    @Select("SELECT COUNT(*) FROM reservation WHERE seat_id=#{seatId} AND reserve_date=#{reserveDate} " +
            "AND time_slot=#{timeSlot} AND status='RESERVED'")
    int countSeatConflict(@Param("seatId") Long seatId, @Param("reserveDate") LocalDate reserveDate,
                          @Param("timeSlot") String timeSlot);

    @Select("SELECT COUNT(*) FROM reservation WHERE reservation_name=#{reservationName} AND reserve_date=#{reserveDate} " +
            "AND time_slot=#{timeSlot} AND status='RESERVED'")
    int countNameConflict(@Param("reservationName") String reservationName,
                          @Param("reserveDate") LocalDate reserveDate, @Param("timeSlot") String timeSlot);

    @Insert("INSERT INTO reservation(user_id,reservation_name,room_id,seat_id,reserve_date,time_slot,status) " +
            "VALUES(#{userId},#{reservationName},#{roomId},#{seatId},#{reserveDate},#{timeSlot},#{status})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Reservation reservation);

    @Select("SELECT rv.*,u.username,r.room_name,s.seat_no FROM reservation rv " +
            "JOIN `user` u ON rv.user_id=u.id JOIN study_room r ON rv.room_id=r.id " +
            "JOIN seat s ON rv.seat_id=s.id WHERE rv.user_id=#{userId} ORDER BY rv.reserve_date DESC,rv.create_time DESC")
    List<Reservation> findByUserId(Long userId);

    @Select("<script>SELECT rv.*,u.username,r.room_name,s.seat_no FROM reservation rv " +
            "JOIN `user` u ON rv.user_id=u.id JOIN study_room r ON rv.room_id=r.id JOIN seat s ON rv.seat_id=s.id " +
            "<where><if test='username != null and username != \"\"'>" +
            "AND (rv.reservation_name LIKE CONCAT('%',#{username},'%') OR u.username LIKE CONCAT('%',#{username},'%'))</if>" +
            "<if test='roomId != null'>AND rv.room_id=#{roomId}</if>" +
            "<if test='reserveDate != null'>AND rv.reserve_date=#{reserveDate}</if>" +
            "<if test='status != null and status != \"\"'>AND rv.status=#{status}</if></where>" +
            " ORDER BY rv.create_time DESC</script>")
    List<Reservation> search(@Param("username") String username, @Param("roomId") Long roomId,
                             @Param("reserveDate") LocalDate reserveDate, @Param("status") String status);

    @Select("SELECT * FROM reservation WHERE id=#{id}")
    Reservation findById(Long id);

    @Update("UPDATE reservation SET status='CANCELLED' WHERE id=#{id} AND status='RESERVED'")
    int cancel(Long id);

    @Select("SELECT COUNT(*) FROM reservation WHERE user_id=#{userId} AND status='RESERVED'")
    int countActiveByUser(Long userId);
}
