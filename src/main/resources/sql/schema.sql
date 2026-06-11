DROP DATABASE IF EXISTS study_room_db;
CREATE DATABASE study_room_db DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE study_room_db;

CREATE TABLE `user` (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(64) NOT NULL,
    role VARCHAR(20) NOT NULL DEFAULT 'STUDENT',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

CREATE TABLE study_room (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    room_no VARCHAR(30) NOT NULL UNIQUE,
    room_name VARCHAR(100) NOT NULL,
    location VARCHAR(150) NOT NULL,
    open_time VARCHAR(100) NOT NULL,
    seat_count INT NOT NULL DEFAULT 0,
    status VARCHAR(20) NOT NULL DEFAULT 'OPEN',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

CREATE TABLE seat (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    room_id BIGINT NOT NULL,
    seat_no VARCHAR(30) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'AVAILABLE',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_seat_room FOREIGN KEY (room_id) REFERENCES study_room(id) ON DELETE CASCADE,
    UNIQUE KEY uk_room_seat (room_id, seat_no)
) ENGINE=InnoDB;

CREATE TABLE reservation (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    reservation_name VARCHAR(50) NOT NULL,
    room_id BIGINT NOT NULL,
    seat_id BIGINT NOT NULL,
    reserve_date DATE NOT NULL,
    time_slot VARCHAR(30) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'RESERVED',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_reservation_user FOREIGN KEY (user_id) REFERENCES `user`(id),
    CONSTRAINT fk_reservation_room FOREIGN KEY (room_id) REFERENCES study_room(id),
    CONSTRAINT fk_reservation_seat FOREIGN KEY (seat_id) REFERENCES seat(id),
    INDEX idx_seat_time (seat_id, reserve_date, time_slot, status),
    INDEX idx_name_time (reservation_name, reserve_date, time_slot, status),
    INDEX idx_user_time (user_id, reserve_date, time_slot, status)
) ENGINE=InnoDB;

CREATE TABLE notice (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(150) NOT NULL,
    content TEXT NOT NULL,
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;
