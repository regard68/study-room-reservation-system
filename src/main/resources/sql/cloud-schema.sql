CREATE TABLE IF NOT EXISTS `user` (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(64) NOT NULL,
    role VARCHAR(20) NOT NULL DEFAULT 'STUDENT',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS study_room (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    room_no VARCHAR(30) NOT NULL UNIQUE,
    room_name VARCHAR(100) NOT NULL,
    location VARCHAR(150) NOT NULL,
    open_time VARCHAR(100) NOT NULL,
    seat_count INT NOT NULL DEFAULT 0,
    status VARCHAR(20) NOT NULL DEFAULT 'OPEN',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS seat (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    room_id BIGINT NOT NULL,
    seat_no VARCHAR(30) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'AVAILABLE',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_cloud_seat_room FOREIGN KEY (room_id) REFERENCES study_room(id) ON DELETE CASCADE,
    UNIQUE KEY uk_cloud_room_seat (room_id, seat_no)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS reservation (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    reservation_name VARCHAR(50) NOT NULL,
    room_id BIGINT NOT NULL,
    seat_id BIGINT NOT NULL,
    reserve_date DATE NOT NULL,
    time_slot VARCHAR(30) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'RESERVED',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_cloud_reservation_user FOREIGN KEY (user_id) REFERENCES `user`(id),
    CONSTRAINT fk_cloud_reservation_room FOREIGN KEY (room_id) REFERENCES study_room(id),
    CONSTRAINT fk_cloud_reservation_seat FOREIGN KEY (seat_id) REFERENCES seat(id),
    INDEX idx_cloud_seat_time (seat_id, reserve_date, time_slot, status),
    INDEX idx_cloud_name_time (reservation_name, reserve_date, time_slot, status),
    INDEX idx_cloud_user_time (user_id, reserve_date, time_slot, status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS notice (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(150) NOT NULL,
    content TEXT NOT NULL,
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT IGNORE INTO `user` (username, password, role) VALUES
('admin', '240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9', 'ADMIN'),
('student', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', 'STUDENT');

INSERT IGNORE INTO study_room (room_no, room_name, location, open_time, seat_count, status) VALUES
('SR-001', '自习室1', '教学楼 101', '08:00-22:00', 12, 'OPEN'),
('SR-002', '自习室2', '教学楼 102', '08:00-22:00', 8, 'OPEN'),
('SR-003', '自习室3', '教学楼 103', '08:00-22:00', 6, 'OPEN'),
('SR-004', '自习室4', '教学楼 104', '08:00-22:00', 6, 'OPEN');

INSERT IGNORE INTO seat (room_id, seat_no, status)
SELECT id, 'A01', 'AVAILABLE' FROM study_room WHERE room_no = 'SR-001';
INSERT IGNORE INTO seat (room_id, seat_no, status)
SELECT id, 'A02', 'AVAILABLE' FROM study_room WHERE room_no = 'SR-001';
INSERT IGNORE INTO seat (room_id, seat_no, status)
SELECT id, 'A03', 'AVAILABLE' FROM study_room WHERE room_no = 'SR-001';
INSERT IGNORE INTO seat (room_id, seat_no, status)
SELECT id, 'A04', 'AVAILABLE' FROM study_room WHERE room_no = 'SR-001';
INSERT IGNORE INTO seat (room_id, seat_no, status)
SELECT id, 'B01', 'AVAILABLE' FROM study_room WHERE room_no = 'SR-001';
INSERT IGNORE INTO seat (room_id, seat_no, status)
SELECT id, 'B02', 'AVAILABLE' FROM study_room WHERE room_no = 'SR-001';
INSERT IGNORE INTO seat (room_id, seat_no, status)
SELECT id, 'B03', 'AVAILABLE' FROM study_room WHERE room_no = 'SR-001';
INSERT IGNORE INTO seat (room_id, seat_no, status)
SELECT id, 'B04', 'MAINTENANCE' FROM study_room WHERE room_no = 'SR-001';
INSERT IGNORE INTO seat (room_id, seat_no, status)
SELECT id, 'C01', 'AVAILABLE' FROM study_room WHERE room_no = 'SR-001';
INSERT IGNORE INTO seat (room_id, seat_no, status)
SELECT id, 'C02', 'AVAILABLE' FROM study_room WHERE room_no = 'SR-001';
INSERT IGNORE INTO seat (room_id, seat_no, status)
SELECT id, 'C03', 'AVAILABLE' FROM study_room WHERE room_no = 'SR-001';
INSERT IGNORE INTO seat (room_id, seat_no, status)
SELECT id, 'C04', 'AVAILABLE' FROM study_room WHERE room_no = 'SR-001';

INSERT IGNORE INTO seat (room_id, seat_no, status)
SELECT id, 'A01', 'AVAILABLE' FROM study_room WHERE room_no = 'SR-002';
INSERT IGNORE INTO seat (room_id, seat_no, status)
SELECT id, 'A02', 'AVAILABLE' FROM study_room WHERE room_no = 'SR-002';
INSERT IGNORE INTO seat (room_id, seat_no, status)
SELECT id, 'A03', 'AVAILABLE' FROM study_room WHERE room_no = 'SR-002';
INSERT IGNORE INTO seat (room_id, seat_no, status)
SELECT id, 'A04', 'AVAILABLE' FROM study_room WHERE room_no = 'SR-002';
INSERT IGNORE INTO seat (room_id, seat_no, status)
SELECT id, 'B01', 'AVAILABLE' FROM study_room WHERE room_no = 'SR-002';
INSERT IGNORE INTO seat (room_id, seat_no, status)
SELECT id, 'B02', 'AVAILABLE' FROM study_room WHERE room_no = 'SR-002';
INSERT IGNORE INTO seat (room_id, seat_no, status)
SELECT id, 'B03', 'AVAILABLE' FROM study_room WHERE room_no = 'SR-002';
INSERT IGNORE INTO seat (room_id, seat_no, status)
SELECT id, 'B04', 'AVAILABLE' FROM study_room WHERE room_no = 'SR-002';

INSERT IGNORE INTO seat (room_id, seat_no, status)
SELECT id, 'A01', 'AVAILABLE' FROM study_room WHERE room_no = 'SR-003';
INSERT IGNORE INTO seat (room_id, seat_no, status)
SELECT id, 'A02', 'AVAILABLE' FROM study_room WHERE room_no = 'SR-003';
INSERT IGNORE INTO seat (room_id, seat_no, status)
SELECT id, 'A03', 'AVAILABLE' FROM study_room WHERE room_no = 'SR-003';
INSERT IGNORE INTO seat (room_id, seat_no, status)
SELECT id, 'B01', 'AVAILABLE' FROM study_room WHERE room_no = 'SR-003';
INSERT IGNORE INTO seat (room_id, seat_no, status)
SELECT id, 'B02', 'AVAILABLE' FROM study_room WHERE room_no = 'SR-003';
INSERT IGNORE INTO seat (room_id, seat_no, status)
SELECT id, 'B03', 'AVAILABLE' FROM study_room WHERE room_no = 'SR-003';

INSERT IGNORE INTO seat (room_id, seat_no, status)
SELECT id, 'A01', 'AVAILABLE' FROM study_room WHERE room_no = 'SR-004';
INSERT IGNORE INTO seat (room_id, seat_no, status)
SELECT id, 'A02', 'AVAILABLE' FROM study_room WHERE room_no = 'SR-004';
INSERT IGNORE INTO seat (room_id, seat_no, status)
SELECT id, 'A03', 'AVAILABLE' FROM study_room WHERE room_no = 'SR-004';
INSERT IGNORE INTO seat (room_id, seat_no, status)
SELECT id, 'B01', 'AVAILABLE' FROM study_room WHERE room_no = 'SR-004';
INSERT IGNORE INTO seat (room_id, seat_no, status)
SELECT id, 'B02', 'AVAILABLE' FROM study_room WHERE room_no = 'SR-004';
INSERT IGNORE INTO seat (room_id, seat_no, status)
SELECT id, 'B03', 'AVAILABLE' FROM study_room WHERE room_no = 'SR-004';

INSERT INTO notice (title, content)
SELECT '共享自习室使用须知', '请保持安静，离开座位时带走个人物品。预约成功后请按时使用座位。'
WHERE NOT EXISTS (SELECT 1 FROM notice WHERE title = '共享自习室使用须知');

INSERT INTO notice (title, content)
SELECT '系统试运行公告', '共享自习室预约系统现已开放试运行，如遇问题请联系管理员。'
WHERE NOT EXISTS (SELECT 1 FROM notice WHERE title = '系统试运行公告');
