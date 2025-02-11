-- init.sql (mariadb/init.sql)
CREATE DATABASE IF NOT EXISTS bangflix;

CREATE USER IF NOT EXISTS 'varc'@'%' IDENTIFIED BY 'varcpw!';

GRANT ALL PRIVILEGES ON bangflix.* TO 'varc'@'%';

FLUSH PRIVILEGES;

USE bangflix;

DROP TABLE IF EXISTS member;
CREATE TABLE member (
  member_code int(11) NOT NULL AUTO_INCREMENT,
  id varchar(255) NOT NULL,
  password varchar(1024) NOT NULL,
  nickname varchar(255) NOT NULL,
  email varchar(512) NOT NULL,
  is_admin tinyint(1) NOT NULL,
  image varchar(1024) DEFAULT NULL,
  point int(11) NOT NULL COMMENT '기본 0값',
  created_at datetime NOT NULL,
  active tinyint(1) NOT NULL,
  PRIMARY KEY (member_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

DROP TABLE IF EXISTS member_ranking;
CREATE TABLE member_ranking (
  ranking_code int(11) NOT NULL AUTO_INCREMENT,
  created_at datetime NOT NULL,
  active tinyint(1) NOT NULL,
  member_code int(11) NOT NULL,
  PRIMARY KEY (ranking_code),
  KEY FK_MEMBER_RANKING_MEMBER (member_code),
  CONSTRAINT FK_MEMBER_RANKING_MEMBER FOREIGN KEY (member_code) REFERENCES member (member_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

DROP TABLE IF EXISTS follow;
CREATE TABLE follow (
  follow_code int(11) NOT NULL AUTO_INCREMENT,
  created_at datetime NOT NULL,
  active tinyint(1) NOT NULL,
  member_code int(11) NOT NULL,
  follow_member_code int(11) NOT NULL,
  PRIMARY KEY (follow_code),
  KEY FK_FOLLOW_MEMBER (member_code),
  KEY FK_FOLLOW_FOLLOW_MEMBER (follow_member_code),
  CONSTRAINT FK_FOLLOW_FOLLOW_MEMBER FOREIGN KEY (follow_member_code) REFERENCES member (member_code),
  CONSTRAINT FK_FOLLOW_MEMBER FOREIGN KEY (member_code) REFERENCES member (member_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

DROP TABLE IF EXISTS note_room;
CREATE TABLE note_room (
  note_room_code int(11) NOT NULL AUTO_INCREMENT,
  created_at datetime NOT NULL,
  active tinyint(1) NOT NULL,
  creater int(11) NOT NULL,
  participant int(11) NOT NULL,
  PRIMARY KEY (note_room_code),
  KEY FK_NOTE_ROOM_CREATER (creater),
  KEY FK_NOTE_ROOM_PARTICIPANT (participant),
  CONSTRAINT FK_NOTE_ROOM_CREATER FOREIGN KEY (creater) REFERENCES member (member_code),
  CONSTRAINT FK_NOTE_ROOM_PARTICIPANT FOREIGN KEY (participant) REFERENCES member (member_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

DROP TABLE IF EXISTS note;
CREATE TABLE note (
  note_code int(11) NOT NULL AUTO_INCREMENT,
  note_content varchar(255) NOT NULL,
  is_read tinyint(1) NOT NULL,
  created_at datetime NOT NULL,
  active tinyint(1) NOT NULL,
  note_room_code int(11) NOT NULL,
  sender int(11) NOT NULL,
  receiver int(11) NOT NULL,
  PRIMARY KEY (note_code),
  KEY FK_NOTE_SENDER (sender),
  KEY FK_NOTE_RECEIVER (receiver),
  KEY FK_NOTE_ROOM (note_room_code),
  CONSTRAINT FK_NOTE_RECEIVER FOREIGN KEY (receiver) REFERENCES member (member_code),
  CONSTRAINT FK_NOTE_ROOM FOREIGN KEY (note_room_code) REFERENCES note_room (note_room_code),
  CONSTRAINT FK_NOTE_SENDER FOREIGN KEY (sender) REFERENCES member (member_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

DROP TABLE IF EXISTS community_post;
CREATE TABLE community_post (
  community_post_code int(11) NOT NULL AUTO_INCREMENT,
  title varchar(255) NOT NULL,
  content varchar(1024) NOT NULL,
  created_at datetime NOT NULL,
  active tinyint(1) NOT NULL,
  member_code int(11) NOT NULL,
  PRIMARY KEY (community_post_code),
  KEY FK_COMMUNITY_POST_MEMBER (member_code),
  CONSTRAINT FK_COMMUNITY_POST_MEMBER FOREIGN KEY (member_code) REFERENCES member (member_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

DROP TABLE IF EXISTS comment;
CREATE TABLE comment (
  comment_code int(11) NOT NULL AUTO_INCREMENT,
  active tinyint(1) NOT NULL,
  created_at datetime NOT NULL,
  content varchar(255) NOT NULL,
  member_code int(11) NOT NULL,
  community_post_code int(11) NOT NULL,
  PRIMARY KEY (comment_code),
  KEY FK_COMMENT_MEMBER (member_code),
  KEY FK_COMMENT_COMMUNITY_POST (community_post_code),
  CONSTRAINT FK_COMMENT_COMMUNITY_POST FOREIGN KEY (community_post_code) REFERENCES community_post (community_post_code),
  CONSTRAINT FK_COMMENT_MEMBER FOREIGN KEY (member_code) REFERENCES member (member_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

DROP TABLE IF EXISTS community_file;
CREATE TABLE community_file (
  community_file_code int(11) NOT NULL AUTO_INCREMENT,
  url varchar(255) NOT NULL,
  created_at datetime NOT NULL,
  active tinyint(1) NOT NULL,
  community_post_code int(11) NOT NULL,
  PRIMARY KEY (community_file_code),
  KEY FK_COMMUNITY_FILE_POST (community_post_code),
  CONSTRAINT FK_COMMUNITY_FILE_POST FOREIGN KEY (community_post_code) REFERENCES community_post (community_post_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

DROP TABLE IF EXISTS community_like;
CREATE TABLE community_like (
  member_code int(11) NOT NULL,
  community_post_code int(11) NOT NULL,
  created_at datetime NOT NULL,
  active tinyint(1) NOT NULL,
  PRIMARY KEY (member_code,community_post_code),
  KEY FK_COMMUNITY_LIKE_POST (community_post_code),
  CONSTRAINT FK_COMMUNITY_LIKE_MEMBER FOREIGN KEY (member_code) REFERENCES member (member_code),
  CONSTRAINT FK_COMMUNITY_LIKE_POST FOREIGN KEY (community_post_code) REFERENCES community_post (community_post_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

DROP TABLE IF EXISTS genre;
CREATE TABLE genre (
  genre_code int(11) NOT NULL AUTO_INCREMENT,
  active tinyint(1) NOT NULL,
  created_at datetime NOT NULL,
  name varchar(255) NOT NULL,
  PRIMARY KEY (genre_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

DROP TABLE IF EXISTS notice_post;
CREATE TABLE notice_post (
  notice_post_code int(11) NOT NULL AUTO_INCREMENT,
  active tinyint(1) NOT NULL,
  created_at datetime NOT NULL,
  title varchar(255) NOT NULL,
  content varchar(1024) NOT NULL,
  member_code int(11) NOT NULL,
  PRIMARY KEY (notice_post_code),
  KEY FK_NOTICE_POST_MEMBER (member_code),
  CONSTRAINT FK_NOTICE_POST_MEMBER FOREIGN KEY (member_code) REFERENCES member (member_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

DROP TABLE IF EXISTS notice_file;
CREATE TABLE notice_file (
  notice_file_code int(11) NOT NULL AUTO_INCREMENT,
  url varchar(255) NOT NULL,
  active tinyint(1) NOT NULL,
  created_at datetime NOT NULL,
  notice_post_code int(11) NOT NULL,
  PRIMARY KEY (notice_file_code),
  KEY FK_NOTICE_FILE_NOTICE_POST (notice_post_code),
  CONSTRAINT FK_NOTICE_FILE_NOTICE_POST FOREIGN KEY (notice_post_code) REFERENCES notice_post (notice_post_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

DROP TABLE IF EXISTS report;
CREATE TABLE report (
  report_code int(11) NOT NULL AUTO_INCREMENT,
  target_code int(11) NOT NULL,
  target_type enum('MEMBER','POST','REVIEW','COMMENT') NOT NULL COMMENT '게시글인지, 유저인지 등',
  type enum('VIOLENCE','SWEARING','SENSATIONAL','SPOILERS','OTHERS') NOT NULL COMMENT '신고 유형에 대한 정의가 되어야 함',
  content varchar(255) NOT NULL,
  created_at datetime NOT NULL,
  active tinyint(1) NOT NULL,
  reporter int(11) NOT NULL,
  PRIMARY KEY (report_code),
  KEY FK_REPORT_MEMBER_REPORTER (reporter),
  CONSTRAINT FK_REPORT_MEMBER_REPORTER FOREIGN KEY (reporter) REFERENCES member (member_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

DROP TABLE IF EXISTS store;
CREATE TABLE store (
  store_code int(11) NOT NULL AUTO_INCREMENT,
  active tinyint(1) NOT NULL,
  created_at datetime NOT NULL,
  name varchar(255) NOT NULL COMMENT '브랜드+지점명',
  address varchar(1024) NOT NULL,
  page_url varchar(1024) DEFAULT NULL,
  image varchar(255) DEFAULT NULL,
  PRIMARY KEY (store_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

DROP TABLE IF EXISTS theme;
CREATE TABLE theme (
  theme_code int(11) NOT NULL AUTO_INCREMENT,
  active tinyint(1) NOT NULL,
  created_at datetime NOT NULL,
  name varchar(255) NOT NULL,
  level int(11) NOT NULL,
  timelimit int(11) NOT NULL COMMENT '분 단위',
  story varchar(1024) NOT NULL,
  price int(11) DEFAULT NULL,
  poster_image varchar(255) NOT NULL,
  headcount varchar(255) DEFAULT NULL,
  store_code int(11) NOT NULL,
  PRIMARY KEY (theme_code),
  KEY FK_THEME_STORE (store_code),
  CONSTRAINT FK_THEME_STORE FOREIGN KEY (store_code) REFERENCES store (store_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

DROP TABLE IF EXISTS theme_genre;
CREATE TABLE theme_genre (
  theme_code int(11) NOT NULL,
  genre_code int(11) NOT NULL,
  PRIMARY KEY (theme_code,genre_code),
  KEY FK_THEME_GENRE_GENRE (genre_code),
  CONSTRAINT FK_THEME_GENRE_GENRE FOREIGN KEY (genre_code) REFERENCES genre (genre_code),
  CONSTRAINT FK_THEME_GENRE_THEME FOREIGN KEY (theme_code) REFERENCES theme (theme_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

DROP TABLE IF EXISTS event_post;
CREATE TABLE event_post (
  event_post_code int(11) NOT NULL AUTO_INCREMENT,
  active tinyint(1) NOT NULL,
  created_at datetime NOT NULL,
  title varchar(255) NOT NULL,
  content varchar(1024) NOT NULL,
  category varchar(255) NOT NULL,
  theme_code int(11) NOT NULL,
  member_code int(11) NOT NULL,
  PRIMARY KEY (event_post_code),
  KEY FK_EVENT_POST_MEMBER (member_code),
  KEY FK_EVENT_POST_THEME (theme_code),
  CONSTRAINT FK_EVENT_POST_MEMBER FOREIGN KEY (member_code) REFERENCES member (member_code),
  CONSTRAINT FK_EVENT_POST_THEME FOREIGN KEY (theme_code) REFERENCES theme (theme_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

DROP TABLE IF EXISTS event_file;
CREATE TABLE event_file (
  event_file_code int(11) NOT NULL AUTO_INCREMENT,
  url varchar(255) NOT NULL,
  active tinyint(1) NOT NULL,
  created_at datetime NOT NULL,
  event_post_code int(11) NOT NULL,
  PRIMARY KEY (event_file_code),
  KEY FK_EVENT_FILE_EVENT_POST (event_post_code),
  CONSTRAINT FK_EVENT_FILE_EVENT_POST FOREIGN KEY (event_post_code) REFERENCES event_post (event_post_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

DROP TABLE IF EXISTS review;
CREATE TABLE review (
  review_code int(11) NOT NULL AUTO_INCREMENT,
  active tinyint(1) NOT NULL,
  created_at datetime NOT NULL,
  headcount int(11) NOT NULL,
  taken_time int(11) DEFAULT NULL COMMENT '분 단위',
  composition enum('ONE','TWO','THREE','FOUR','FIVE') NOT NULL,
  level enum('ONE','TWO','THREE','FOUR','FIVE') NOT NULL,
  horror_level enum('ONE','TWO','THREE','FOUR','FIVE') NOT NULL,
  activity enum('ONE','TWO','THREE','FOUR','FIVE') NOT NULL,
  total_score int(11) NOT NULL,
  interior enum('ONE','TWO','THREE','FOUR','FIVE') NOT NULL,
  probability enum('ONE','TWO','THREE','FOUR','FIVE') DEFAULT NULL,
  content varchar(1024) NOT NULL,
  member_code int(11) NOT NULL,
  theme_code int(11) NOT NULL,
  PRIMARY KEY (review_code),
  KEY FK_REVIEW_MEMBER (member_code),
  KEY FK_REVIEW_THEME (theme_code),
  CONSTRAINT FK_REVIEW_MEMBER FOREIGN KEY (member_code) REFERENCES member (member_code),
  CONSTRAINT FK_REVIEW_THEME FOREIGN KEY (theme_code) REFERENCES theme (theme_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

DROP TABLE IF EXISTS review_file;
CREATE TABLE review_file (
  review_file_code int(11) NOT NULL AUTO_INCREMENT,
  url varchar(255) NOT NULL,
  active tinyint(1) NOT NULL,
  created_at datetime NOT NULL,
  review_code int(11) NOT NULL,
  PRIMARY KEY (review_file_code),
  KEY FK_REVIEW_FILE_REVIEW (review_code),
  CONSTRAINT FK_REVIEW_FILE_REVIEW FOREIGN KEY (review_code) REFERENCES review (review_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

DROP TABLE IF EXISTS review_like;
CREATE TABLE review_like (
  member_code int(11) NOT NULL,
  review_code int(11) NOT NULL,
  created_at datetime NOT NULL,
  active tinyint(1) NOT NULL,
  PRIMARY KEY (member_code,review_code),
  KEY FK_REVIEW_LIKE_REVIEW (review_code),
  CONSTRAINT FK_REVIEW_LIKE_MEMBER FOREIGN KEY (member_code) REFERENCES member (member_code),
  CONSTRAINT FK_REVIEW_LIKE_REVIEW FOREIGN KEY (review_code) REFERENCES review (review_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

DROP TABLE IF EXISTS review_ranking;
CREATE TABLE review_ranking (
  ranking_code int(11) NOT NULL AUTO_INCREMENT,
  active tinyint(1) NOT NULL,
  created_at datetime NOT NULL,
  review_code int(11) NOT NULL,
  PRIMARY KEY (ranking_code),
  KEY FK_REVIEW_RANKING_REVIEW (review_code),
  CONSTRAINT FK_REVIEW_RANKING_REVIEW FOREIGN KEY (review_code) REFERENCES review (review_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

DROP TABLE IF EXISTS social_login;
CREATE TABLE social_login (
  social_login_code int(11) NOT NULL AUTO_INCREMENT,
  social_id varchar(255) NOT NULL,
  social_type varchar(255) NOT NULL,
  created_at datetime NOT NULL,
  active tinyint(1) NOT NULL,
  member_code int(11) NOT NULL,
  PRIMARY KEY (social_login_code),
  KEY FK_SOCIAL_LOGIN_MEMBER (member_code),
  CONSTRAINT FK_SOCIAL_LOGIN_MEMBER FOREIGN KEY (member_code) REFERENCES member (member_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

DROP TABLE IF EXISTS subscribe_post;
CREATE TABLE subscribe_post (
  member_code int(11) NOT NULL,
  community_post_code int(11) NOT NULL,
  created_at datetime NOT NULL,
  active tinyint(1) NOT NULL,
  PRIMARY KEY (member_code,community_post_code),
  KEY FK_SUBSCRIBE_POST_COMMUNITY (community_post_code),
  CONSTRAINT FK_SUBSCRIBE_POST_COMMUNITY FOREIGN KEY (community_post_code) REFERENCES community_post (community_post_code),
  CONSTRAINT FK_SUBSCRIBE_POST_MEMBER FOREIGN KEY (member_code) REFERENCES member (member_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

DROP TABLE IF EXISTS tendency;
CREATE TABLE tendency (
  tendency_code int(11) NOT NULL AUTO_INCREMENT,
  age int(11) NOT NULL,
  be_skilled enum('CILDREN','BEGINNERS','INTERMEDIATE','ADVANCED') NOT NULL,
  situation enum('SINGLE','FRIEND','COUPLE','STRANGER','CHALLENGER') DEFAULT NULL COMMENT '혼자, 친구, 연인, 낯선사람, 도전자',
  element varchar(255) NOT NULL,
  created_at datetime NOT NULL,
  active tinyint(1) NOT NULL,
  member_code int(11) NOT NULL,
  PRIMARY KEY (tendency_code),
  KEY FK_TENDENCY_MEMBER (member_code),
  CONSTRAINT FK_TENDENCY_MEMBER FOREIGN KEY (member_code) REFERENCES member (member_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

DROP TABLE IF EXISTS tendency_genre;
CREATE TABLE tendency_genre (
  tendency_code int(11) NOT NULL,
  genre_code int(11) NOT NULL,
  PRIMARY KEY (tendency_code,genre_code),
  KEY FK_TENDENCY_GENRE_GENRE (genre_code),
  CONSTRAINT FK_TENDENCY_GENRE_GENRE FOREIGN KEY (genre_code) REFERENCES genre (genre_code),
  CONSTRAINT FK_TENDENCY_GENRE_TENDENCY FOREIGN KEY (tendency_code) REFERENCES tendency (tendency_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

DROP TABLE IF EXISTS theme_reaction;
CREATE TABLE theme_reaction (
  theme_code int(11) NOT NULL,
  member_code int(11) NOT NULL,
  reaction enum('LIKE','SCRAP','SCRAPLIKE') NOT NULL,
  created_at datetime NOT NULL,
  active tinyint(1) NOT NULL,
  PRIMARY KEY (theme_code,member_code),
  KEY FK_THEME_REACTION_MEMBER (member_code),
  CONSTRAINT FK_THEME_REACTION_MEMBER FOREIGN KEY (member_code) REFERENCES member (member_code),
  CONSTRAINT FK_THEME_REACTION_THEME FOREIGN KEY (theme_code) REFERENCES theme (theme_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
