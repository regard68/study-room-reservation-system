USE study_room_db;

INSERT INTO `user` (username, password, role) VALUES
('admin', '240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9', 'ADMIN'),
('student', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', 'STUDENT');

INSERT INTO study_room (room_no, room_name, location, open_time, seat_count, status) VALUES
('SR-001', '自习室1', '教学楼 101', '08:00-22:00', 12, 'OPEN'),
('SR-002', '自习室2', '教学楼 102', '08:00-22:00', 8, 'OPEN'),
('SR-003', '自习室3', '教学楼 103', '08:00-22:00', 6, 'OPEN'),
('SR-004', '自习室4', '教学楼 104', '08:00-22:00', 6, 'OPEN');

INSERT INTO seat (room_id, seat_no, status) VALUES
(1,'A01','AVAILABLE'),(1,'A02','AVAILABLE'),(1,'A03','AVAILABLE'),(1,'A04','AVAILABLE'),
(1,'B01','AVAILABLE'),(1,'B02','AVAILABLE'),(1,'B03','AVAILABLE'),(1,'B04','MAINTENANCE'),
(1,'C01','AVAILABLE'),(1,'C02','AVAILABLE'),(1,'C03','AVAILABLE'),(1,'C04','AVAILABLE'),
(2,'A01','AVAILABLE'),(2,'A02','AVAILABLE'),(2,'A03','AVAILABLE'),(2,'A04','AVAILABLE'),
(2,'B01','AVAILABLE'),(2,'B02','AVAILABLE'),(2,'B03','AVAILABLE'),(2,'B04','AVAILABLE'),
(3,'A01','AVAILABLE'),(3,'A02','AVAILABLE'),(3,'A03','AVAILABLE'),
(3,'B01','AVAILABLE'),(3,'B02','AVAILABLE'),(3,'B03','AVAILABLE'),
(4,'A01','AVAILABLE'),(4,'A02','AVAILABLE'),(4,'A03','AVAILABLE'),
(4,'B01','AVAILABLE'),(4,'B02','AVAILABLE'),(4,'B03','AVAILABLE');

INSERT INTO notice (title, content) VALUES
('共享自习室使用须知', '请保持安静，离开座位时带走个人物品。预约成功后请按时使用座位。'),
('系统试运行公告', '共享自习室预约系统现已开放试运行，如遇问题请联系管理员。');
