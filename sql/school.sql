/*
 Navicat Premium Dump SQL

 Source Server         : 127.0.0.1
 Source Server Type    : PostgreSQL
 Source Server Version : 170002 (170002)
 Source Host           : localhost:5432
 Source Catalog        : school
 Source Schema         : public

 Target Server Type    : PostgreSQL
 Target Server Version : 170002 (170002)
 File Encoding         : 65001

 Date: 15/06/2025 16:28:02
*/


-- ----------------------------
-- Sequence structure for chat_messages_message_id_seq
-- ----------------------------
DROP SEQUENCE IF EXISTS "public"."chat_messages_message_id_seq";
CREATE SEQUENCE "public"."chat_messages_message_id_seq" 
INCREMENT 1
MINVALUE  1
MAXVALUE 2147483647
START 1
CACHE 1;
ALTER SEQUENCE "public"."chat_messages_message_id_seq" OWNER TO "myuser";

-- ----------------------------
-- Sequence structure for courses_course_id_seq
-- ----------------------------
DROP SEQUENCE IF EXISTS "public"."courses_course_id_seq";
CREATE SEQUENCE "public"."courses_course_id_seq" 
INCREMENT 1
MINVALUE  1
MAXVALUE 2147483647
START 1
CACHE 1;
ALTER SEQUENCE "public"."courses_course_id_seq" OWNER TO "myuser";

-- ----------------------------
-- Sequence structure for enrollments_enrollment_id_seq
-- ----------------------------
DROP SEQUENCE IF EXISTS "public"."enrollments_enrollment_id_seq";
CREATE SEQUENCE "public"."enrollments_enrollment_id_seq" 
INCREMENT 1
MINVALUE  1
MAXVALUE 2147483647
START 1
CACHE 1;
ALTER SEQUENCE "public"."enrollments_enrollment_id_seq" OWNER TO "myuser";

-- ----------------------------
-- Sequence structure for group_members_id_seq
-- ----------------------------
DROP SEQUENCE IF EXISTS "public"."group_members_id_seq";
CREATE SEQUENCE "public"."group_members_id_seq" 
INCREMENT 1
MINVALUE  1
MAXVALUE 2147483647
START 1
CACHE 1;
ALTER SEQUENCE "public"."group_members_id_seq" OWNER TO "myuser";

-- ----------------------------
-- Sequence structure for students_student_id_seq
-- ----------------------------
DROP SEQUENCE IF EXISTS "public"."students_student_id_seq";
CREATE SEQUENCE "public"."students_student_id_seq" 
INCREMENT 1
MINVALUE  1
MAXVALUE 2147483647
START 1
CACHE 1;
ALTER SEQUENCE "public"."students_student_id_seq" OWNER TO "myuser";

-- ----------------------------
-- Sequence structure for teachers_teacher_id_seq
-- ----------------------------
DROP SEQUENCE IF EXISTS "public"."teachers_teacher_id_seq";
CREATE SEQUENCE "public"."teachers_teacher_id_seq" 
INCREMENT 1
MINVALUE  1
MAXVALUE 2147483647
START 1
CACHE 1;
ALTER SEQUENCE "public"."teachers_teacher_id_seq" OWNER TO "myuser";

-- ----------------------------
-- Table structure for chat_messages
-- ----------------------------
DROP TABLE IF EXISTS "public"."chat_messages";
CREATE TABLE "public"."chat_messages" (
  "message_id" int4 NOT NULL DEFAULT nextval('chat_messages_message_id_seq'::regclass),
  "message_type" varchar(20) COLLATE "pg_catalog"."default" NOT NULL,
  "sender_id" int4 NOT NULL,
  "target_id" int4,
  "group_id" varchar(50) COLLATE "pg_catalog"."default",
  "content" text COLLATE "pg_catalog"."default" NOT NULL,
  "sent_at" timestamp(6) DEFAULT CURRENT_TIMESTAMP
)
;
ALTER TABLE "public"."chat_messages" OWNER TO "myuser";

-- ----------------------------
-- Records of chat_messages
-- ----------------------------
BEGIN;
INSERT INTO "public"."chat_messages" ("message_id", "message_type", "sender_id", "target_id", "group_id", "content", "sent_at") VALUES (1, 'PRIVATE', 2, 1, NULL, '你好', '2025-06-15 15:31:03.831');
INSERT INTO "public"."chat_messages" ("message_id", "message_type", "sender_id", "target_id", "group_id", "content", "sent_at") VALUES (2, 'PRIVATE', 2, 1, NULL, '111', '2025-06-15 15:31:16.444');
INSERT INTO "public"."chat_messages" ("message_id", "message_type", "sender_id", "target_id", "group_id", "content", "sent_at") VALUES (3, 'PRIVATE', 1, 2, NULL, '你好啊', '2025-06-15 15:31:25.207');
INSERT INTO "public"."chat_messages" ("message_id", "message_type", "sender_id", "target_id", "group_id", "content", "sent_at") VALUES (4, 'PRIVATE', 2, 1, NULL, '我收到你的问好啦', '2025-06-15 15:31:56.118');
INSERT INTO "public"."chat_messages" ("message_id", "message_type", "sender_id", "target_id", "group_id", "content", "sent_at") VALUES (5, 'PRIVATE', 1, 2, NULL, '我也收到啦', '2025-06-15 15:32:04.746');
INSERT INTO "public"."chat_messages" ("message_id", "message_type", "sender_id", "target_id", "group_id", "content", "sent_at") VALUES (6, 'PRIVATE', 2, 1, NULL, '哈哈哈', '2025-06-15 15:32:07.926');
INSERT INTO "public"."chat_messages" ("message_id", "message_type", "sender_id", "target_id", "group_id", "content", "sent_at") VALUES (7, 'PRIVATE', 1, 2, NULL, '你好', '2025-06-15 15:34:37.391');
INSERT INTO "public"."chat_messages" ("message_id", "message_type", "sender_id", "target_id", "group_id", "content", "sent_at") VALUES (8, 'PRIVATE', 2, 1, NULL, '我很好', '2025-06-15 15:34:51.299');
INSERT INTO "public"."chat_messages" ("message_id", "message_type", "sender_id", "target_id", "group_id", "content", "sent_at") VALUES (9, 'GROUP', 2, NULL, 'group_123', '你好 我是 李四', '2025-06-15 15:35:28.732');
INSERT INTO "public"."chat_messages" ("message_id", "message_type", "sender_id", "target_id", "group_id", "content", "sent_at") VALUES (10, 'GROUP', 1, NULL, 'group_123', '李四你在做什么', '2025-06-15 15:35:45.159');
INSERT INTO "public"."chat_messages" ("message_id", "message_type", "sender_id", "target_id", "group_id", "content", "sent_at") VALUES (11, 'GROUP', 1, NULL, 'group_123', '大家好', '2025-06-15 15:36:43.466');
INSERT INTO "public"."chat_messages" ("message_id", "message_type", "sender_id", "target_id", "group_id", "content", "sent_at") VALUES (12, 'GROUP', 2, NULL, 'group_123', '你好啊', '2025-06-15 15:36:50.16');
INSERT INTO "public"."chat_messages" ("message_id", "message_type", "sender_id", "target_id", "group_id", "content", "sent_at") VALUES (13, 'GROUP', 1, NULL, 'group_123', '你们好', '2025-06-15 15:39:43.774');
INSERT INTO "public"."chat_messages" ("message_id", "message_type", "sender_id", "target_id", "group_id", "content", "sent_at") VALUES (14, 'GROUP', 2, NULL, 'group_123', '你好', '2025-06-15 15:39:54.85');
INSERT INTO "public"."chat_messages" ("message_id", "message_type", "sender_id", "target_id", "group_id", "content", "sent_at") VALUES (15, 'GROUP', 1, NULL, 'group_123', '你们好', '2025-06-15 15:43:46.142');
INSERT INTO "public"."chat_messages" ("message_id", "message_type", "sender_id", "target_id", "group_id", "content", "sent_at") VALUES (16, 'GROUP', 2, NULL, 'group_123', '你好', '2025-06-15 15:44:06.538');
INSERT INTO "public"."chat_messages" ("message_id", "message_type", "sender_id", "target_id", "group_id", "content", "sent_at") VALUES (17, 'GROUP', 1, NULL, 'group_123', '大家好', '2025-06-15 15:44:34.002');
INSERT INTO "public"."chat_messages" ("message_id", "message_type", "sender_id", "target_id", "group_id", "content", "sent_at") VALUES (18, 'GROUP', 2, NULL, 'group_123', '你们好', '2025-06-15 15:49:38.881');
INSERT INTO "public"."chat_messages" ("message_id", "message_type", "sender_id", "target_id", "group_id", "content", "sent_at") VALUES (19, 'GROUP', 1, NULL, 'group_123', '你好啊', '2025-06-15 15:50:00.889');
INSERT INTO "public"."chat_messages" ("message_id", "message_type", "sender_id", "target_id", "group_id", "content", "sent_at") VALUES (20, 'GROUP', 1, NULL, 'group_123', '大家好啊', '2025-06-15 15:54:22.859');
INSERT INTO "public"."chat_messages" ("message_id", "message_type", "sender_id", "target_id", "group_id", "content", "sent_at") VALUES (21, 'GROUP', 2, NULL, 'group_123', '你们好啊', '2025-06-15 15:54:53.398');
INSERT INTO "public"."chat_messages" ("message_id", "message_type", "sender_id", "target_id", "group_id", "content", "sent_at") VALUES (22, 'GROUP', 1, NULL, 'group_123', '卧槽', '2025-06-15 15:55:03.442');
INSERT INTO "public"."chat_messages" ("message_id", "message_type", "sender_id", "target_id", "group_id", "content", "sent_at") VALUES (23, 'GROUP', 2, NULL, 'group_123', '呵哈哈', '2025-06-15 15:55:06.678');
INSERT INTO "public"."chat_messages" ("message_id", "message_type", "sender_id", "target_id", "group_id", "content", "sent_at") VALUES (24, 'PRIVATE', 2, 1, NULL, '你好 张三', '2025-06-15 15:55:26.653');
INSERT INTO "public"."chat_messages" ("message_id", "message_type", "sender_id", "target_id", "group_id", "content", "sent_at") VALUES (25, 'GROUP', 1, NULL, 'group_123', '你好啊', '2025-06-15 15:55:34.525');
INSERT INTO "public"."chat_messages" ("message_id", "message_type", "sender_id", "target_id", "group_id", "content", "sent_at") VALUES (26, 'PRIVATE', 1, 2, NULL, '111', '2025-06-15 15:56:22.859');
INSERT INTO "public"."chat_messages" ("message_id", "message_type", "sender_id", "target_id", "group_id", "content", "sent_at") VALUES (27, 'PRIVATE', 1, 2, NULL, '111', '2025-06-15 15:56:31.597');
INSERT INTO "public"."chat_messages" ("message_id", "message_type", "sender_id", "target_id", "group_id", "content", "sent_at") VALUES (28, 'PRIVATE', 2, 1, NULL, '121212', '2025-06-15 15:56:45.562');
INSERT INTO "public"."chat_messages" ("message_id", "message_type", "sender_id", "target_id", "group_id", "content", "sent_at") VALUES (29, 'PRIVATE', 1, 2, NULL, '21332323', '2025-06-15 15:56:53.43');
INSERT INTO "public"."chat_messages" ("message_id", "message_type", "sender_id", "target_id", "group_id", "content", "sent_at") VALUES (30, 'PRIVATE', 2, 1, NULL, '3232323', '2025-06-15 15:56:56.763');
INSERT INTO "public"."chat_messages" ("message_id", "message_type", "sender_id", "target_id", "group_id", "content", "sent_at") VALUES (31, 'PRIVATE', 1, 2, NULL, '11121212', '2025-06-15 15:57:26.398');
INSERT INTO "public"."chat_messages" ("message_id", "message_type", "sender_id", "target_id", "group_id", "content", "sent_at") VALUES (32, 'PRIVATE', 2, 1, NULL, '1212', '2025-06-15 16:02:20.777');
INSERT INTO "public"."chat_messages" ("message_id", "message_type", "sender_id", "target_id", "group_id", "content", "sent_at") VALUES (33, 'PRIVATE', 1, 2, NULL, '2', '2025-06-15 16:02:51.828');
INSERT INTO "public"."chat_messages" ("message_id", "message_type", "sender_id", "target_id", "group_id", "content", "sent_at") VALUES (34, 'PRIVATE', 2, 1, NULL, '你好', '2025-06-15 16:12:00.409');
INSERT INTO "public"."chat_messages" ("message_id", "message_type", "sender_id", "target_id", "group_id", "content", "sent_at") VALUES (35, 'PRIVATE', 2, 1, NULL, '你好 张三', '2025-06-15 16:15:28.803');
INSERT INTO "public"."chat_messages" ("message_id", "message_type", "sender_id", "target_id", "group_id", "content", "sent_at") VALUES (36, 'PRIVATE', 1, 2, NULL, '你好我是张三', '2025-06-15 16:15:40.377');
INSERT INTO "public"."chat_messages" ("message_id", "message_type", "sender_id", "target_id", "group_id", "content", "sent_at") VALUES (37, 'PRIVATE', 2, 1, NULL, '我知道你是张三 我是李四', '2025-06-15 16:16:01.842');
INSERT INTO "public"."chat_messages" ("message_id", "message_type", "sender_id", "target_id", "group_id", "content", "sent_at") VALUES (38, 'PRIVATE', 1, 2, NULL, '哈哈哈', '2025-06-15 16:16:07.138');
INSERT INTO "public"."chat_messages" ("message_id", "message_type", "sender_id", "target_id", "group_id", "content", "sent_at") VALUES (39, 'PRIVATE', 2, 1, NULL, '哈哈哈', '2025-06-15 16:16:09.759');
INSERT INTO "public"."chat_messages" ("message_id", "message_type", "sender_id", "target_id", "group_id", "content", "sent_at") VALUES (40, 'PRIVATE', 1, 2, NULL, '你好 我是张三', '2025-06-15 16:19:29.049');
INSERT INTO "public"."chat_messages" ("message_id", "message_type", "sender_id", "target_id", "group_id", "content", "sent_at") VALUES (41, 'PRIVATE', 2, 1, NULL, '你好啊', '2025-06-15 16:19:53.672');
INSERT INTO "public"."chat_messages" ("message_id", "message_type", "sender_id", "target_id", "group_id", "content", "sent_at") VALUES (42, 'PRIVATE', 1, 2, NULL, '哈哈哈', '2025-06-15 16:20:06.42');
INSERT INTO "public"."chat_messages" ("message_id", "message_type", "sender_id", "target_id", "group_id", "content", "sent_at") VALUES (43, 'GROUP', 1, NULL, 'group_123', '大家好', '2025-06-15 16:20:32.047');
INSERT INTO "public"."chat_messages" ("message_id", "message_type", "sender_id", "target_id", "group_id", "content", "sent_at") VALUES (44, 'PRIVATE', 1, 2, NULL, '李四', '2025-06-15 16:20:43.445');
INSERT INTO "public"."chat_messages" ("message_id", "message_type", "sender_id", "target_id", "group_id", "content", "sent_at") VALUES (45, 'PRIVATE', 2, 1, NULL, '李四你在做什么', '2025-06-15 16:20:56.245');
INSERT INTO "public"."chat_messages" ("message_id", "message_type", "sender_id", "target_id", "group_id", "content", "sent_at") VALUES (46, 'PRIVATE', 2, 1, NULL, '张三你在做什么？', '2025-06-15 16:21:02.646');
INSERT INTO "public"."chat_messages" ("message_id", "message_type", "sender_id", "target_id", "group_id", "content", "sent_at") VALUES (47, 'PRIVATE', 4, 3, NULL, '大家好', '2025-06-15 16:21:34.141');
INSERT INTO "public"."chat_messages" ("message_id", "message_type", "sender_id", "target_id", "group_id", "content", "sent_at") VALUES (48, 'PRIVATE', 1, 4, NULL, '你好', '2025-06-15 16:21:51.71');
INSERT INTO "public"."chat_messages" ("message_id", "message_type", "sender_id", "target_id", "group_id", "content", "sent_at") VALUES (49, 'PRIVATE', 3, 4, NULL, '你哈', '2025-06-15 16:22:10.191');
INSERT INTO "public"."chat_messages" ("message_id", "message_type", "sender_id", "target_id", "group_id", "content", "sent_at") VALUES (50, 'PRIVATE', 3, 4, NULL, '我是王五', '2025-06-15 16:22:32.822');
INSERT INTO "public"."chat_messages" ("message_id", "message_type", "sender_id", "target_id", "group_id", "content", "sent_at") VALUES (51, 'PRIVATE', 4, 3, NULL, '你猜猜我是谁', '2025-06-15 16:22:43.322');
INSERT INTO "public"."chat_messages" ("message_id", "message_type", "sender_id", "target_id", "group_id", "content", "sent_at") VALUES (52, 'PRIVATE', 3, 4, NULL, '你是老六', '2025-06-15 16:22:47.722');
INSERT INTO "public"."chat_messages" ("message_id", "message_type", "sender_id", "target_id", "group_id", "content", "sent_at") VALUES (53, 'PRIVATE', 4, 3, NULL, '哈哈哈', '2025-06-15 16:22:50.875');
COMMIT;

-- ----------------------------
-- Table structure for courses
-- ----------------------------
DROP TABLE IF EXISTS "public"."courses";
CREATE TABLE "public"."courses" (
  "course_id" int4 NOT NULL DEFAULT nextval('courses_course_id_seq'::regclass),
  "course_name" varchar(100) COLLATE "pg_catalog"."default" NOT NULL,
  "course_code" varchar(20) COLLATE "pg_catalog"."default" NOT NULL,
  "teacher_id" int4,
  "created_at" timestamp(6) DEFAULT CURRENT_TIMESTAMP,
  "updated_at" timestamp(6) DEFAULT CURRENT_TIMESTAMP
)
;
ALTER TABLE "public"."courses" OWNER TO "myuser";

-- ----------------------------
-- Records of courses
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for enrollments
-- ----------------------------
DROP TABLE IF EXISTS "public"."enrollments";
CREATE TABLE "public"."enrollments" (
  "enrollment_id" int4 NOT NULL DEFAULT nextval('enrollments_enrollment_id_seq'::regclass),
  "student_id" int4,
  "course_id" int4,
  "enrollment_date" timestamp(6) DEFAULT CURRENT_TIMESTAMP
)
;
ALTER TABLE "public"."enrollments" OWNER TO "myuser";

-- ----------------------------
-- Records of enrollments
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for group_members
-- ----------------------------
DROP TABLE IF EXISTS "public"."group_members";
CREATE TABLE "public"."group_members" (
  "id" int4 NOT NULL DEFAULT nextval('group_members_id_seq'::regclass),
  "group_id" varchar(50) COLLATE "pg_catalog"."default" NOT NULL,
  "student_id" int4 NOT NULL,
  "joined_at" timestamp(6) DEFAULT CURRENT_TIMESTAMP
)
;
ALTER TABLE "public"."group_members" OWNER TO "myuser";

-- ----------------------------
-- Records of group_members
-- ----------------------------
BEGIN;
INSERT INTO "public"."group_members" ("id", "group_id", "student_id", "joined_at") VALUES (1, 'group_123', 1, '2025-06-15 15:35:06.682');
INSERT INTO "public"."group_members" ("id", "group_id", "student_id", "joined_at") VALUES (2, 'group_123', 2, '2025-06-15 15:35:12.226');
INSERT INTO "public"."group_members" ("id", "group_id", "student_id", "joined_at") VALUES (3, 'group_123', 4, '2025-06-15 16:23:10.59');
COMMIT;

-- ----------------------------
-- Table structure for students
-- ----------------------------
DROP TABLE IF EXISTS "public"."students";
CREATE TABLE "public"."students" (
  "student_id" int4 NOT NULL DEFAULT nextval('students_student_id_seq'::regclass),
  "first_name" varchar(50) COLLATE "pg_catalog"."default" NOT NULL,
  "last_name" varchar(50) COLLATE "pg_catalog"."default" NOT NULL,
  "birthdate" date,
  "email" varchar(100) COLLATE "pg_catalog"."default" NOT NULL,
  "created_at" timestamp(6) DEFAULT CURRENT_TIMESTAMP,
  "updated_at" timestamp(6) DEFAULT CURRENT_TIMESTAMP
)
;
ALTER TABLE "public"."students" OWNER TO "myuser";

-- ----------------------------
-- Records of students
-- ----------------------------
BEGIN;
INSERT INTO "public"."students" ("student_id", "first_name", "last_name", "birthdate", "email", "created_at", "updated_at") VALUES (1, '张', '三', '2000-05-15', 'john.doe@example.com', '2024-12-24 15:00:04.356464', '2024-12-24 15:00:04.356464');
INSERT INTO "public"."students" ("student_id", "first_name", "last_name", "birthdate", "email", "created_at", "updated_at") VALUES (2, '李', '四', '1999-12-20', 'jane.smith@example.com', '2024-12-24 15:00:04.356464', '2024-12-24 15:00:04.356464');
INSERT INTO "public"."students" ("student_id", "first_name", "last_name", "birthdate", "email", "created_at", "updated_at") VALUES (3, '王', '五', '2001-03-10', 'alice.johnson@example.com', '2024-12-24 15:00:04.356464', '2024-12-24 15:00:04.356464');
INSERT INTO "public"."students" ("student_id", "first_name", "last_name", "birthdate", "email", "created_at", "updated_at") VALUES (4, '赵', '六', '1998-07-25', 'bob.brown@example.com', '2024-12-24 15:00:04.356464', '2024-12-24 15:00:04.356464');
INSERT INTO "public"."students" ("student_id", "first_name", "last_name", "birthdate", "email", "created_at", "updated_at") VALUES (5, '钱', '七', '2002-11-05', 'charlie.davis@example.com', '2024-12-24 15:00:04.356464', '2024-12-24 15:00:04.356464');
COMMIT;

-- ----------------------------
-- Table structure for teachers
-- ----------------------------
DROP TABLE IF EXISTS "public"."teachers";
CREATE TABLE "public"."teachers" (
  "teacher_id" int4 NOT NULL DEFAULT nextval('teachers_teacher_id_seq'::regclass),
  "first_name" varchar(50) COLLATE "pg_catalog"."default" NOT NULL,
  "last_name" varchar(50) COLLATE "pg_catalog"."default" NOT NULL,
  "hire_date" date NOT NULL,
  "email" varchar(100) COLLATE "pg_catalog"."default" NOT NULL,
  "created_at" timestamp(6) DEFAULT CURRENT_TIMESTAMP,
  "updated_at" timestamp(6) DEFAULT CURRENT_TIMESTAMP
)
;
ALTER TABLE "public"."teachers" OWNER TO "myuser";

-- ----------------------------
-- Records of teachers
-- ----------------------------
BEGIN;
INSERT INTO "public"."teachers" ("teacher_id", "first_name", "last_name", "hire_date", "email", "created_at", "updated_at") VALUES (1, '张', '伟', '2015-08-15', 'zhangwei@example.com', '2025-03-29 14:47:15.9351', '2025-03-29 14:47:15.9351');
INSERT INTO "public"."teachers" ("teacher_id", "first_name", "last_name", "hire_date", "email", "created_at", "updated_at") VALUES (2, '李', '娜', '2018-03-20', 'linna@example.com', '2025-03-29 14:47:15.9351', '2025-03-29 14:47:15.9351');
INSERT INTO "public"."teachers" ("teacher_id", "first_name", "last_name", "hire_date", "email", "created_at", "updated_at") VALUES (3, '王', '强', '2012-07-10', 'wangqiang@example.com', '2025-03-29 14:47:15.9351', '2025-03-29 14:47:15.9351');
INSERT INTO "public"."teachers" ("teacher_id", "first_name", "last_name", "hire_date", "email", "created_at", "updated_at") VALUES (4, '赵', '敏', '2020-11-05', 'zhaomin@example.com', '2025-03-29 14:47:15.9351', '2025-03-29 14:47:15.9351');
INSERT INTO "public"."teachers" ("teacher_id", "first_name", "last_name", "hire_date", "email", "created_at", "updated_at") VALUES (5, '孙', '杰', '2017-09-30', 'sunjie@example.com', '2025-03-29 14:47:15.9351', '2025-03-29 14:47:15.9351');
COMMIT;

-- ----------------------------
-- Alter sequences owned by
-- ----------------------------
ALTER SEQUENCE "public"."chat_messages_message_id_seq"
OWNED BY "public"."chat_messages"."message_id";
SELECT setval('"public"."chat_messages_message_id_seq"', 53, true);

-- ----------------------------
-- Alter sequences owned by
-- ----------------------------
ALTER SEQUENCE "public"."courses_course_id_seq"
OWNED BY "public"."courses"."course_id";
SELECT setval('"public"."courses_course_id_seq"', 1, false);

-- ----------------------------
-- Alter sequences owned by
-- ----------------------------
ALTER SEQUENCE "public"."enrollments_enrollment_id_seq"
OWNED BY "public"."enrollments"."enrollment_id";
SELECT setval('"public"."enrollments_enrollment_id_seq"', 1, false);

-- ----------------------------
-- Alter sequences owned by
-- ----------------------------
ALTER SEQUENCE "public"."group_members_id_seq"
OWNED BY "public"."group_members"."id";
SELECT setval('"public"."group_members_id_seq"', 3, true);

-- ----------------------------
-- Alter sequences owned by
-- ----------------------------
ALTER SEQUENCE "public"."students_student_id_seq"
OWNED BY "public"."students"."student_id";
SELECT setval('"public"."students_student_id_seq"', 5, true);

-- ----------------------------
-- Alter sequences owned by
-- ----------------------------
ALTER SEQUENCE "public"."teachers_teacher_id_seq"
OWNED BY "public"."teachers"."teacher_id";
SELECT setval('"public"."teachers_teacher_id_seq"', 1, false);

-- ----------------------------
-- Indexes structure for table chat_messages
-- ----------------------------
CREATE INDEX "idx_messages_group" ON "public"."chat_messages" USING btree (
  "group_id" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST
);
CREATE INDEX "idx_messages_sender" ON "public"."chat_messages" USING btree (
  "sender_id" "pg_catalog"."int4_ops" ASC NULLS LAST
);
CREATE INDEX "idx_messages_sent_at" ON "public"."chat_messages" USING btree (
  "sent_at" "pg_catalog"."timestamp_ops" ASC NULLS LAST
);
CREATE INDEX "idx_messages_target" ON "public"."chat_messages" USING btree (
  "target_id" "pg_catalog"."int4_ops" ASC NULLS LAST
);

-- ----------------------------
-- Primary Key structure for table chat_messages
-- ----------------------------
ALTER TABLE "public"."chat_messages" ADD CONSTRAINT "chat_messages_pkey" PRIMARY KEY ("message_id");

-- ----------------------------
-- Uniques structure for table courses
-- ----------------------------
ALTER TABLE "public"."courses" ADD CONSTRAINT "courses_course_code_key" UNIQUE ("course_code");

-- ----------------------------
-- Primary Key structure for table courses
-- ----------------------------
ALTER TABLE "public"."courses" ADD CONSTRAINT "courses_pkey" PRIMARY KEY ("course_id");

-- ----------------------------
-- Primary Key structure for table enrollments
-- ----------------------------
ALTER TABLE "public"."enrollments" ADD CONSTRAINT "enrollments_pkey" PRIMARY KEY ("enrollment_id");

-- ----------------------------
-- Indexes structure for table group_members
-- ----------------------------
CREATE INDEX "idx_group_members_group" ON "public"."group_members" USING btree (
  "group_id" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST
);
CREATE INDEX "idx_group_members_student" ON "public"."group_members" USING btree (
  "student_id" "pg_catalog"."int4_ops" ASC NULLS LAST
);

-- ----------------------------
-- Uniques structure for table group_members
-- ----------------------------
ALTER TABLE "public"."group_members" ADD CONSTRAINT "group_members_group_id_student_id_key" UNIQUE ("group_id", "student_id");

-- ----------------------------
-- Primary Key structure for table group_members
-- ----------------------------
ALTER TABLE "public"."group_members" ADD CONSTRAINT "group_members_pkey" PRIMARY KEY ("id");

-- ----------------------------
-- Uniques structure for table students
-- ----------------------------
ALTER TABLE "public"."students" ADD CONSTRAINT "students_email_key" UNIQUE ("email");

-- ----------------------------
-- Primary Key structure for table students
-- ----------------------------
ALTER TABLE "public"."students" ADD CONSTRAINT "students_pkey" PRIMARY KEY ("student_id");

-- ----------------------------
-- Uniques structure for table teachers
-- ----------------------------
ALTER TABLE "public"."teachers" ADD CONSTRAINT "teachers_email_key" UNIQUE ("email");

-- ----------------------------
-- Primary Key structure for table teachers
-- ----------------------------
ALTER TABLE "public"."teachers" ADD CONSTRAINT "teachers_pkey" PRIMARY KEY ("teacher_id");

-- ----------------------------
-- Foreign Keys structure for table courses
-- ----------------------------
ALTER TABLE "public"."courses" ADD CONSTRAINT "fk_teacher" FOREIGN KEY ("teacher_id") REFERENCES "public"."teachers" ("teacher_id") ON DELETE NO ACTION ON UPDATE NO ACTION;

-- ----------------------------
-- Foreign Keys structure for table enrollments
-- ----------------------------
ALTER TABLE "public"."enrollments" ADD CONSTRAINT "fk_course" FOREIGN KEY ("course_id") REFERENCES "public"."courses" ("course_id") ON DELETE NO ACTION ON UPDATE NO ACTION;
ALTER TABLE "public"."enrollments" ADD CONSTRAINT "fk_student" FOREIGN KEY ("student_id") REFERENCES "public"."students" ("student_id") ON DELETE NO ACTION ON UPDATE NO ACTION;
