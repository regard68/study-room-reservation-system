package com.studyroom.mapper;

import com.studyroom.entity.User;
import org.apache.ibatis.annotations.*;

import java.util.List;

public interface UserMapper {
    @Select("SELECT * FROM `user` WHERE username = #{username}")
    User findByUsername(String username);

    @Select("SELECT * FROM `user` WHERE id = #{id}")
    User findById(Long id);

    @Select("SELECT * FROM `user` WHERE role = 'STUDENT' ORDER BY create_time DESC")
    List<User> findStudents();

    @Insert("INSERT INTO `user`(username,password,role) VALUES(#{username},#{password},#{role})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(User user);

    @Delete("DELETE FROM `user` WHERE id = #{id} AND role = 'STUDENT'")
    int deleteStudent(Long id);
}
