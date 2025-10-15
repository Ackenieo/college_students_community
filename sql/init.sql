-- 创建用户数据库
CREATE DATABASE IF NOT EXISTS college_students;
USE college_students;

-- 创建用户表
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    full_name VARCHAR(100),
    phone VARCHAR(20),
    role ENUM('ADMIN', 'TEACHER', 'STUDENT') NOT NULL DEFAULT 'STUDENT',
    status ENUM('ACTIVE', 'INACTIVE', 'SUSPENDED') NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    last_login TIMESTAMP NULL
);

-- 插入示例数据
INSERT INTO users (username, email, password, full_name, role) VALUES
('admin', 'admin@college.edu', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi', '系统管理员', 'ADMIN'),
('teacher1', 'teacher1@college.edu', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi', '张老师', 'TEACHER'),
('student1', 'student1@college.edu', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi', '李同学', 'STUDENT');

-- 创建课程数据库
CREATE DATABASE IF NOT EXISTS college_courses;
USE college_courses;

-- 创建Nacos数据库
CREATE DATABASE IF NOT EXISTS nacos;
USE nacos;

-- 创建课程表
CREATE TABLE IF NOT EXISTS courses (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    course_name VARCHAR(100) NOT NULL,
    course_code VARCHAR(20) NOT NULL UNIQUE,
    description TEXT,
    teacher_id BIGINT NOT NULL,
    credits INT NOT NULL DEFAULT 3,
    max_students INT NOT NULL DEFAULT 50,
    current_students INT NOT NULL DEFAULT 0,
    status ENUM('ACTIVE', 'INACTIVE', 'COMPLETED') NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 创建学生选课表
CREATE TABLE IF NOT EXISTS student_courses (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    student_id BIGINT NOT NULL,
    course_id BIGINT NOT NULL,
    enrolled_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    status ENUM('ENROLLED', 'DROPPED', 'COMPLETED') NOT NULL DEFAULT 'ENROLLED',
    grade DECIMAL(3,2),
    UNIQUE KEY unique_student_course (student_id, course_id)
);

-- 插入示例课程数据
INSERT INTO courses (course_name, course_code, description, teacher_id, credits) VALUES
('Java程序设计', 'CS101', 'Java编程基础课程', 2, 4),
('数据结构与算法', 'CS102', '计算机科学基础课程', 2, 3),
('数据库系统', 'CS201', '数据库设计与实现', 2, 3);
