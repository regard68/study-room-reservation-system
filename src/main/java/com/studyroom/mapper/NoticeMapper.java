package com.studyroom.mapper;

import com.studyroom.entity.Notice;
import org.apache.ibatis.annotations.*;

import java.util.List;

public interface NoticeMapper {
    @Select("SELECT * FROM notice ORDER BY create_time DESC")
    List<Notice> findAll();

    @Select("SELECT * FROM notice WHERE id=#{id}")
    Notice findById(Long id);

    @Insert("INSERT INTO notice(title,content) VALUES(#{title},#{content})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Notice notice);

    @Update("UPDATE notice SET title=#{title},content=#{content} WHERE id=#{id}")
    int update(Notice notice);

    @Delete("DELETE FROM notice WHERE id=#{id}")
    int delete(Long id);
}
