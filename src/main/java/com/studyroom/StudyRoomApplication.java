package com.studyroom;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.studyroom.mapper")
public class StudyRoomApplication {
    public static void main(String[] args) {
        SpringApplication.run(StudyRoomApplication.class, args);
    }
}
