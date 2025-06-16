/*
 Navicat Premium Dump SQL

 Source Server         : mysql8-docker
 Source Server Type    : MySQL
 Source Server Version : 80023 (8.0.23)
 Source Host           : localhost:3307
 Source Schema         : school

 Target Server Type    : MySQL
 Target Server Version : 80023 (8.0.23)
 File Encoding         : 65001

 Date: 16/06/2025 09:18:03
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for chat_messages
-- ----------------------------
DROP TABLE IF EXISTS `chat_messages`;
CREATE TABLE `chat_messages` (
  `message_id` int NOT NULL AUTO_INCREMENT,
  `message_type` varchar(20) COLLATE utf8mb4_bin NOT NULL,
  `sender_id` int NOT NULL,
  `target_id` int DEFAULT NULL,
  `group_id` varchar(50) COLLATE utf8mb4_bin DEFAULT NULL,
  `content` text COLLATE utf8mb4_bin NOT NULL,
  `sent_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`message_id`),
  KEY `idx_messages_sender` (`sender_id`),
  KEY `idx_messages_target` (`target_id`),
  KEY `idx_messages_group` (`group_id`),
  KEY `idx_messages_sent_at` (`sent_at`)
) ENGINE=InnoDB AUTO_INCREMENT=61 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

-- ----------------------------
-- Records of chat_messages
-- ----------------------------
BEGIN;
INSERT INTO `chat_messages` (`message_id`, `message_type`, `sender_id`, `target_id`, `group_id`, `content`, `sent_at`) VALUES (58, 'PRIVATE', 7, 6, NULL, '32323', '2025-06-16 01:16:43');
INSERT INTO `chat_messages` (`message_id`, `message_type`, `sender_id`, `target_id`, `group_id`, `content`, `sent_at`) VALUES (59, 'PRIVATE', 6, 7, NULL, '好嘛', '2025-06-16 01:16:58');
INSERT INTO `chat_messages` (`message_id`, `message_type`, `sender_id`, `target_id`, `group_id`, `content`, `sent_at`) VALUES (60, 'PRIVATE', 7, 6, NULL, '111', '2025-06-16 01:17:06');
COMMIT;

-- ----------------------------
-- Table structure for courses
-- ----------------------------
DROP TABLE IF EXISTS `courses`;
CREATE TABLE `courses` (
  `course_id` int NOT NULL AUTO_INCREMENT,
  `course_name` varchar(100) COLLATE utf8mb4_bin NOT NULL,
  `course_code` varchar(20) COLLATE utf8mb4_bin NOT NULL,
  `teacher_id` int DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`course_id`),
  UNIQUE KEY `courses_course_code_key` (`course_code`),
  KEY `fk_teacher` (`teacher_id`),
  CONSTRAINT `fk_teacher` FOREIGN KEY (`teacher_id`) REFERENCES `teachers` (`teacher_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

-- ----------------------------
-- Records of courses
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for enrollments
-- ----------------------------
DROP TABLE IF EXISTS `enrollments`;
CREATE TABLE `enrollments` (
  `enrollment_id` int NOT NULL AUTO_INCREMENT,
  `student_id` int DEFAULT NULL,
  `course_id` int DEFAULT NULL,
  `enrollment_date` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`enrollment_id`),
  KEY `fk_course` (`course_id`),
  KEY `fk_student` (`student_id`),
  CONSTRAINT `fk_course` FOREIGN KEY (`course_id`) REFERENCES `courses` (`course_id`),
  CONSTRAINT `fk_student` FOREIGN KEY (`student_id`) REFERENCES `students` (`student_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

-- ----------------------------
-- Records of enrollments
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for group_members
-- ----------------------------
DROP TABLE IF EXISTS `group_members`;
CREATE TABLE `group_members` (
  `id` int NOT NULL AUTO_INCREMENT,
  `group_id` varchar(50) COLLATE utf8mb4_bin NOT NULL,
  `student_id` int NOT NULL,
  `joined_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `group_members_group_id_student_id_key` (`group_id`,`student_id`),
  KEY `idx_group_members_group` (`group_id`),
  KEY `idx_group_members_student` (`student_id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

-- ----------------------------
-- Records of group_members
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for students
-- ----------------------------
DROP TABLE IF EXISTS `students`;
CREATE TABLE `students` (
  `student_id` int NOT NULL AUTO_INCREMENT,
  `first_name` varchar(50) COLLATE utf8mb4_bin NOT NULL,
  `last_name` varchar(50) COLLATE utf8mb4_bin NOT NULL,
  `birthdate` date DEFAULT NULL,
  `email` varchar(100) COLLATE utf8mb4_bin NOT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`student_id`),
  UNIQUE KEY `students_email_key` (`email`)
) ENGINE=InnoDB AUTO_INCREMENT=26 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

-- ----------------------------
-- Records of students
-- ----------------------------
BEGIN;
INSERT INTO `students` (`student_id`, `first_name`, `last_name`, `birthdate`, `email`, `created_at`, `updated_at`) VALUES (6, '张', '三', '2000-01-15', 'zhangsan@example.com', '2025-06-16 01:09:35', '2025-06-16 01:09:35');
INSERT INTO `students` (`student_id`, `first_name`, `last_name`, `birthdate`, `email`, `created_at`, `updated_at`) VALUES (7, '李', '四', '1999-05-20', 'lisi@example.com', '2025-06-16 01:09:35', '2025-06-16 01:09:35');
INSERT INTO `students` (`student_id`, `first_name`, `last_name`, `birthdate`, `email`, `created_at`, `updated_at`) VALUES (8, '王', '五', '2001-03-10', 'wangwu@example.com', '2025-06-16 01:09:35', '2025-06-16 01:09:35');
INSERT INTO `students` (`student_id`, `first_name`, `last_name`, `birthdate`, `email`, `created_at`, `updated_at`) VALUES (9, '赵', '六', '2000-08-25', 'zhaoliu@example.com', '2025-06-16 01:09:35', '2025-06-16 01:09:35');
INSERT INTO `students` (`student_id`, `first_name`, `last_name`, `birthdate`, `email`, `created_at`, `updated_at`) VALUES (10, '孙', '七', '1998-12-03', 'sunqi@example.com', '2025-06-16 01:09:35', '2025-06-16 01:09:35');
INSERT INTO `students` (`student_id`, `first_name`, `last_name`, `birthdate`, `email`, `created_at`, `updated_at`) VALUES (11, '周', '八', '2001-07-18', 'zhouba@example.com', '2025-06-16 01:09:35', '2025-06-16 01:09:35');
INSERT INTO `students` (`student_id`, `first_name`, `last_name`, `birthdate`, `email`, `created_at`, `updated_at`) VALUES (12, '吴', '九', '1999-11-28', 'wujiu@example.com', '2025-06-16 01:09:35', '2025-06-16 01:09:35');
INSERT INTO `students` (`student_id`, `first_name`, `last_name`, `birthdate`, `email`, `created_at`, `updated_at`) VALUES (13, '郑', '十', '2000-04-12', 'zhengshi@example.com', '2025-06-16 01:09:35', '2025-06-16 01:09:35');
INSERT INTO `students` (`student_id`, `first_name`, `last_name`, `birthdate`, `email`, `created_at`, `updated_at`) VALUES (14, '冯', '十一', '2001-09-05', 'fengshiyi@example.com', '2025-06-16 01:09:35', '2025-06-16 01:09:35');
INSERT INTO `students` (`student_id`, `first_name`, `last_name`, `birthdate`, `email`, `created_at`, `updated_at`) VALUES (15, '陈', '十二', '1999-02-14', 'chenshier@example.com', '2025-06-16 01:09:35', '2025-06-16 01:09:35');
COMMIT;

-- ----------------------------
-- Table structure for teachers
-- ----------------------------
DROP TABLE IF EXISTS `teachers`;
CREATE TABLE `teachers` (
  `teacher_id` int NOT NULL AUTO_INCREMENT,
  `first_name` varchar(50) COLLATE utf8mb4_bin NOT NULL,
  `last_name` varchar(50) COLLATE utf8mb4_bin NOT NULL,
  `hire_date` date NOT NULL,
  `email` varchar(100) COLLATE utf8mb4_bin NOT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`teacher_id`),
  UNIQUE KEY `teachers_email_key` (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

-- ----------------------------
-- Records of teachers
-- ----------------------------
BEGIN;
COMMIT;

SET FOREIGN_KEY_CHECKS = 1;
