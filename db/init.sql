-- init.sql (mariadb/init.sql)
CREATE DATABASE IF NOT EXISTS bangflix;

CREATE USER IF NOT EXISTS 'varc'@'%' IDENTIFIED BY 'varcpw';

GRANT ALL PRIVILEGES ON bangflix.* TO 'varc'@'%';

FLUSH PRIVILEGES;

USE bangflix;

CREATE TABLE community_post (
	community_post_code	INT	NOT NULL,
	title	VARCHAR(255)	NOT NULL,
	content	VARCHAR(1024)	NOT NULL,
	created_at	DATETIME	NOT NULL,
	active	BOOLEAN	NOT NULL,
	member_code	INT	NOT NULL
);

CREATE TABLE theme (
	theme_code	INT	NOT NULL,
	active	BOOLEAN	NOT NULL,
	created_at	DATETIME	NOT NULL,
	name	VARCHAR(255)	NOT NULL,
	level	INT	NOT NULL,
	timelimit	INT	NOT NULL	COMMENT '분 단위',
	story	VARCHAR(1024)	NOT NULL,
	price INT NULL,
	poster_image	VARCHAR(255)	NOT NULL,
	headcount VARCHAR(255) NULL,
	store_code	INT	NOT NULL
);

CREATE TABLE store (
	store_code	INT	NOT NULL,
	active	BOOLEAN	NOT NULL,
	created_at	DATETIME	NOT NULL,
	name	VARCHAR(255)	NOT NULL	COMMENT '브랜드+지점명',
	address	VARCHAR(1024)	NOT NULL,
	page_url	VARCHAR(1024)	NULL,
	image VARCHAR(255)	NULL
);

CREATE TABLE member (
	member_code	INT	NOT NULL,
	id	VARCHAR(255)	NOT NULL,
	password	VARCHAR(1024)	NOT NULL,
	nickname	VARCHAR(255)	NOT NULL,
	email	VARCHAR(512)	NOT NULL,
	is_admin	BOOLEAN	NOT NULL,
	image	VARCHAR(1024)	NULL,	
	point	INT	NOT NULL	COMMENT '기본 0값',
	created_at	DATETIME	NOT NULL,
	active	BOOLEAN	NOT NULL
);

CREATE TABLE event_post (
	event_post_code	INT	NOT NULL,
	active	BOOLEAN	NOT NULL,
	created_at	DATETIME	NOT NULL,
	title	VARCHAR(255)	NOT NULL,
	content	VARCHAR(1024)	NOT NULL,
	category	VARCHAR(255)	NOT NULL,
	theme_code INT	NOT NULL,
	member_code	INT	NOT NULL
);

CREATE TABLE social_login (
	social_login_code	INT	NOT NULL,
	social_id	VARCHAR(255)	NOT NULL,
	social_type	VARCHAR(255)	NOT NULL,
	created_at	DATETIME	NOT NULL,
	active	BOOLEAN	NOT NULL,
	member_code	INT	NOT NULL
);

CREATE TABLE notice_post (
	notice_post_code	INT	NOT NULL,
	active	BOOLEAN	NOT NULL,
	created_at	DATETIME	NOT NULL,
	title	VARCHAR(255)	NOT NULL,
	content	VARCHAR(1024)	NOT NULL,
	member_code	INT	NOT NULL
);

CREATE TABLE report (
	report_code	INT	NOT NULL,
	target_code	INT	NOT NULL,
	target_type	ENUM('MEMBER', 'POST', 'REVIEW', 'COMMENT')	NOT NULL	COMMENT '게시글인지, 유저인지 등',
	type	ENUM('VIOLENCE', 'SWEARING', 'SENSATIONAL', 'SPOILERS', 'OTHERS')	NOT NULL	COMMENT '신고 유형에 대한 정의가 되어야 함',
	content	VARCHAR(255)	NOT NULL,
	created_at	DATETIME	NOT NULL,
	active	BOOLEAN	NOT NULL,
	reporter	INT	NOT NULL
);

CREATE TABLE review (
	review_code	INT	NOT NULL,
	active	BOOLEAN	NOT NULL,
	created_at	DATETIME	NOT NULL,
	headcount	INT	NOT NULL,
	taken_time	INT	NULL	COMMENT '분 단위',
	composition	ENUM('ONE', 'TWO', 'THREE', 'FOUR', 'FIVE')	NOT NULL,
	level	ENUM('ONE', 'TWO', 'THREE', 'FOUR', 'FIVE')		NOT NULL	,
	horror_level	ENUM('ONE', 'TWO', 'THREE', 'FOUR', 'FIVE')		NOT NULL	,
	activity	ENUM('ONE', 'TWO', 'THREE', 'FOUR', 'FIVE')	 NOT NULL	,
	total_score	INT	NOT NULL	,
	interior	ENUM('ONE', 'TWO', 'THREE', 'FOUR', 'FIVE')		NOT NULL	,
	probability	ENUM('ONE', 'TWO', 'THREE', 'FOUR', 'FIVE')		,
	content	VARCHAR(1024)	NOT NULL,
	member_code	INT	NOT NULL,
	theme_code	INT	NOT NULL
);

CREATE TABLE tendency (
	tendency_code	INT	NOT NULL,
	age	INT	NOT NULL,
	be_skilled	ENUM('CILDREN', 'BEGINNERS', 'INTERMEDIATE', 'ADVANCED')	NOT NULL,
	situation	ENUM('SINGLE', 'FRIEND', 'COUPLE', 'STRANGER', 'CHALLENGER')	NULL	COMMENT '혼자, 친구, 연인, 낯선사람, 도전자',
	element VARCHAR(255) NOT NULL,
	created_at	DATETIME	NOT NULL,
	active	BOOLEAN	NOT NULL,
	member_code	INT	NOT NULL
);

CREATE TABLE genre (
	genre_code	INT	NOT NULL,
	active	BOOLEAN	NOT NULL,
	created_at	DATETIME	NOT NULL,
	name	VARCHAR(255)	NOT NULL
);

CREATE TABLE comment (
	comment_code	INT	NOT NULL,
	active	BOOLEAN	NOT NULL,
	created_at	DATETIME	NOT NULL,
	content	VARCHAR(255)	NOT NULL,
	member_code	INT	NOT NULL,
	community_post_code	INT	NOT NULL
);

CREATE TABLE review_file (
	review_file_code	INT	NOT NULL,
	url	VARCHAR(255)	NOT NULL,
	active	BOOLEAN	NOT NULL,
	created_at	DATETIME	NOT NULL,
	review_code	INT	NOT NULL
);

CREATE TABLE note (
	note_code	INT	NOT NULL,
	note_content	VARCHAR(255)	NOT NULL,
	is_read	BOOLEAN	NOT NULL,
	created_at	DATETIME	NOT NULL,
	active	BOOLEAN	NOT NULL,
	note_room_code	INT	NOT NULL,
	sender	INT	NOT NULL,
	receiver	INT	NOT NULL
);

CREATE TABLE note_room (
	note_room_code	INT	NOT NULL,
	created_at	DATETIME	NOT NULL,
	active	BOOLEAN	NOT NULL,
	creater	INT	NOT NULL,
	participant	INT	NOT NULL
);

CREATE TABLE follow (
	follow_code	INT	NOT NULL,
	created_at	DATETIME	NOT NULL,
	active	BOOLEAN	NOT NULL,
	member_code	INT	NOT NULL,
	follow_member_code	INT	NOT NULL
);

CREATE TABLE theme_reaction (
	theme_code	INT	NOT NULL,
	member_code	INT	NOT NULL,
	reaction ENUM('LIKE', 'SCRAP', 'SCRAPLIKE') NOT NULL,
	created_at	DATETIME	NOT NULL,
	active	BOOLEAN	NOT NULL
);

CREATE TABLE community_like (
	member_code	INT	NOT NULL,
	community_post_code	INT	NOT NULL,
	created_at	DATETIME	NOT NULL,
	active	BOOLEAN	NOT NULL
);

CREATE TABLE member_ranking (
	ranking_code	INT	NOT NULL,
	created_at	DATETIME	NOT NULL,
	active	BOOLEAN	NOT NULL,
	member_code	INT	NOT NULL
);

CREATE TABLE review_ranking (
	ranking_code	INT	NOT NULL,
	active	BOOLEAN	NOT NULL,
	created_at	DATETIME	NOT NULL,
	review_code	INT	NOT NULL
);

CREATE TABLE subscribe_post (
	member_code	INT	NOT NULL,
	community_post_code	INT	NOT NULL,
	created_at	DATETIME	NOT NULL,
	active	BOOLEAN	NOT NULL
);

CREATE TABLE tendency_genre (
	tendency_code	INT	NOT NULL,
	genre_code	INT	NOT NULL
);

CREATE TABLE theme_genre (
	theme_code	INT	NOT NULL,
	genre_code	INT	NOT NULL
);

CREATE TABLE review_like (
	member_code	INT	NOT NULL,
	review_code	INT	NOT NULL,
	created_at	DATETIME	NOT NULL,
	active	BOOLEAN	NOT NULL

);

CREATE TABLE event_file (
	event_file_code	INT	NOT NULL, 
	url	VARCHAR(255)	NOT NULL,
	active	BOOLEAN	NOT NULL,
	created_at	DATETIME	NOT NULL,
	event_post_code	INT	NOT NULL
);

CREATE TABLE notice_file (
	notice_file_code	INT	NOT NULL,
	url	VARCHAR(255)	NOT NULL,
	active	BOOLEAN	NOT NULL,
	created_at	DATETIME	NOT NULL,
	notice_post_code	INT	NOT NULL
);

CREATE TABLE community_file (
	community_file_code	INT	NOT NULL,
	url	VARCHAR(255)	NOT NULL,
	created_at	DATETIME	NOT NULL,
	active	BOOLEAN	NOT NULL,
	community_post_code	INT	NOT NULL
);

ALTER TABLE community_post ADD CONSTRAINT PK_COMMUNITY_POST PRIMARY KEY (
	community_post_code
);

ALTER TABLE community_post MODIFY community_post_code INT NOT NULL AUTO_INCREMENT;

ALTER TABLE theme ADD CONSTRAINT PK_THEME PRIMARY KEY (
	theme_code
);

ALTER TABLE theme MODIFY theme_code INT NOT NULL AUTO_INCREMENT;

ALTER TABLE store ADD CONSTRAINT PK_STORE PRIMARY KEY (
	store_code
);

ALTER TABLE store MODIFY store_code INT NOT NULL AUTO_INCREMENT;

ALTER TABLE member ADD CONSTRAINT PK_MEMBER PRIMARY KEY (
	member_code
);

ALTER TABLE member MODIFY member_code INT NOT NULL AUTO_INCREMENT;

ALTER TABLE event_post ADD CONSTRAINT PK_EVENT_POST PRIMARY KEY (
	event_post_code
);

ALTER TABLE event_post MODIFY event_post_code INT NOT NULL AUTO_INCREMENT;

ALTER TABLE social_login ADD CONSTRAINT PK_SOCIAL_LOGIN PRIMARY KEY (
	social_login_code
);

ALTER TABLE social_login MODIFY social_login_code INT NOT NULL AUTO_INCREMENT;

ALTER TABLE notice_post ADD CONSTRAINT PK_NOTICE_POST PRIMARY KEY (
	notice_post_code
);

ALTER TABLE notice_post MODIFY notice_post_code INT NOT NULL AUTO_INCREMENT;

ALTER TABLE report ADD CONSTRAINT PK_REPORT PRIMARY KEY (
	report_code
);

ALTER TABLE report MODIFY report_code INT NOT NULL AUTO_INCREMENT;

ALTER TABLE review ADD CONSTRAINT PK_REVIEW PRIMARY KEY (
	review_code
);

ALTER TABLE review MODIFY review_code INT NOT NULL AUTO_INCREMENT;

ALTER TABLE tendency ADD CONSTRAINT PK_TENDENCY PRIMARY KEY (
	tendency_code
);

ALTER TABLE tendency MODIFY tendency_code INT NOT NULL AUTO_INCREMENT;

ALTER TABLE genre ADD CONSTRAINT PK_GENRE PRIMARY KEY (
	genre_code
);

ALTER TABLE genre MODIFY genre_code INT NOT NULL AUTO_INCREMENT;

ALTER TABLE comment ADD CONSTRAINT PK_COMMENT PRIMARY KEY (
	comment_code
);

ALTER TABLE comment MODIFY comment_code INT NOT NULL AUTO_INCREMENT;

ALTER TABLE review_file ADD CONSTRAINT PK_REVIEW_FILE PRIMARY KEY (
	review_file_code
);

ALTER TABLE review_file MODIFY review_file_code INT NOT NULL AUTO_INCREMENT;

ALTER TABLE note ADD CONSTRAINT PK_NOTE PRIMARY KEY (
	note_code
);

ALTER TABLE note MODIFY note_code INT NOT NULL AUTO_INCREMENT;

ALTER TABLE note_room ADD CONSTRAINT PK_NOTE_ROOM PRIMARY KEY (
	note_room_code
);

ALTER TABLE note_room MODIFY note_room_code INT NOT NULL AUTO_INCREMENT;

ALTER TABLE follow ADD CONSTRAINT PK_FOLLOW PRIMARY KEY (
	follow_code
);

ALTER TABLE follow MODIFY follow_code INT NOT NULL AUTO_INCREMENT;

ALTER TABLE theme_reaction ADD CONSTRAINT PK_THEME_REACTION PRIMARY KEY (
	theme_code,
	member_code
);

ALTER TABLE community_like ADD CONSTRAINT PK_COMMUNITY_LIKE PRIMARY KEY (
	member_code,
	community_post_code
);

ALTER TABLE member_ranking ADD CONSTRAINT PK_MEMBER_RANKING PRIMARY KEY (
	ranking_code
);

ALTER TABLE member_ranking MODIFY ranking_code INT NOT NULL AUTO_INCREMENT;

ALTER TABLE review_ranking ADD CONSTRAINT PK_REVIEW_RANKING PRIMARY KEY (
	ranking_code
);

ALTER TABLE review_ranking MODIFY ranking_code INT NOT NULL AUTO_INCREMENT;

ALTER TABLE subscribe_post ADD CONSTRAINT PK_SUBSCRIBE_POST PRIMARY KEY (
	member_code,
	community_post_code
);

ALTER TABLE tendency_genre ADD CONSTRAINT PK_TENDENCY_GENRE PRIMARY KEY (
	tendency_code,
	genre_code
);

ALTER TABLE theme_genre ADD CONSTRAINT PK_THEME_GENRE PRIMARY KEY (
	theme_code,
	genre_code
);

ALTER TABLE review_like ADD CONSTRAINT PK_REVIEW_LIKE PRIMARY KEY (
	member_code,
	review_code
);

ALTER TABLE event_file ADD CONSTRAINT PK_EVENT_FILE PRIMARY KEY (
	event_file_code
);

ALTER TABLE event_file MODIFY event_file_code INT NOT NULL AUTO_INCREMENT;

ALTER TABLE notice_file ADD CONSTRAINT PK_NOTICE_FILE PRIMARY KEY (
	notice_file_code
);

ALTER TABLE notice_file MODIFY notice_file_code INT NOT NULL AUTO_INCREMENT;

ALTER TABLE community_file ADD CONSTRAINT PK_COMMUNITY_FILE PRIMARY KEY (
	community_file_code
);

ALTER TABLE community_file MODIFY community_file_code INT NOT NULL AUTO_INCREMENT;













-- Foreign Key Constraints for community_post table
ALTER TABLE community_post ADD CONSTRAINT FK_COMMUNITY_POST_MEMBER
FOREIGN KEY (member_code) REFERENCES member(member_code);

-- Foreign Key Constraints for theme table
ALTER TABLE theme ADD CONSTRAINT FK_THEME_STORE
FOREIGN KEY (store_code) REFERENCES store(store_code);

-- Foreign Key Constraints for event_post table
ALTER TABLE event_post ADD CONSTRAINT FK_EVENT_POST_MEMBER
FOREIGN KEY (member_code) REFERENCES member(member_code);

-- Foreign Key Constraints for event_post table
ALTER TABLE event_post ADD CONSTRAINT FK_EVENT_POST_THEME
FOREIGN KEY (theme_code) REFERENCES theme(theme_code);

-- Foreign Key Constraints for social_login table
ALTER TABLE social_login ADD CONSTRAINT FK_SOCIAL_LOGIN_MEMBER
FOREIGN KEY (member_code) REFERENCES member(member_code);

-- Foreign Key Constraints for notice_post table
ALTER TABLE notice_post ADD CONSTRAINT FK_NOTICE_POST_MEMBER
FOREIGN KEY (member_code) REFERENCES member(member_code);

-- Foreign Key Constraints for report table
ALTER TABLE report ADD CONSTRAINT FK_REPORT_MEMBER_REPORTER
FOREIGN KEY (reporter) REFERENCES member(member_code);

-- Depending on report_target_type, you can later specify different foreign keys for report_target_code

-- Foreign Key Constraints for review table
ALTER TABLE review ADD CONSTRAINT FK_REVIEW_MEMBER
FOREIGN KEY (member_code) REFERENCES member(member_code);

ALTER TABLE review ADD CONSTRAINT FK_REVIEW_THEME
FOREIGN KEY (theme_code) REFERENCES theme(theme_code);

-- Foreign Key Constraints for tendency table
ALTER TABLE tendency ADD CONSTRAINT FK_TENDENCY_MEMBER
FOREIGN KEY (member_code) REFERENCES member(member_code);

-- Foreign Key Constraints for comment table
ALTER TABLE comment ADD CONSTRAINT FK_COMMENT_MEMBER
FOREIGN KEY (member_code) REFERENCES member(member_code);

ALTER TABLE comment ADD CONSTRAINT FK_COMMENT_COMMUNITY_POST
FOREIGN KEY (community_post_code) REFERENCES community_post(community_post_code);

-- Foreign Key Constraints for review_file table
ALTER TABLE review_file ADD CONSTRAINT FK_REVIEW_FILE_REVIEW
FOREIGN KEY (review_code) REFERENCES review(review_code);

-- Foreign Key Constraints for note table
ALTER TABLE note ADD CONSTRAINT FK_NOTE_SENDER
FOREIGN KEY (sender) REFERENCES member(member_code);

ALTER TABLE note ADD CONSTRAINT FK_NOTE_RECEIVER
FOREIGN KEY (receiver) REFERENCES member(member_code);

ALTER TABLE note ADD CONSTRAINT FK_NOTE_ROOM
FOREIGN KEY (note_room_code) REFERENCES note_room(note_room_code);

-- Foreign Key Constraints for note_room table
ALTER TABLE note_room ADD CONSTRAINT FK_NOTE_ROOM_CREATER
FOREIGN KEY (creater) REFERENCES member(member_code);

ALTER TABLE note_room ADD CONSTRAINT FK_NOTE_ROOM_PARTICIPANT
FOREIGN KEY (participant) REFERENCES member(member_code);

-- Foreign Key Constraints for follow table
ALTER TABLE follow ADD CONSTRAINT FK_FOLLOW_MEMBER
FOREIGN KEY (member_code) REFERENCES member(member_code);

ALTER TABLE follow ADD CONSTRAINT FK_FOLLOW_FOLLOW_MEMBER
FOREIGN KEY (follow_member_code) REFERENCES member(member_code);

-- Foreign Key Constraints for theme_reaction table
ALTER TABLE theme_reaction ADD CONSTRAINT FK_THEME_REACTION_MEMBER
FOREIGN KEY (member_code) REFERENCES member(member_code);

ALTER TABLE theme_reaction ADD CONSTRAINT FK_THEME_REACTION_THEME
FOREIGN KEY (theme_code) REFERENCES theme(theme_code);

-- Foreign Key Constraints for community_like table
ALTER TABLE community_like ADD CONSTRAINT FK_COMMUNITY_LIKE_MEMBER
FOREIGN KEY (member_code) REFERENCES member(member_code);

ALTER TABLE community_like ADD CONSTRAINT FK_COMMUNITY_LIKE_POST
FOREIGN KEY (community_post_code) REFERENCES community_post(community_post_code);

-- Foreign Key Constraints for member_ranking table
ALTER TABLE member_ranking ADD CONSTRAINT FK_MEMBER_RANKING_MEMBER
FOREIGN KEY (member_code) REFERENCES member(member_code);

-- Foreign Key Constraints for review_ranking table
ALTER TABLE review_ranking ADD CONSTRAINT FK_REVIEW_RANKING_REVIEW
FOREIGN KEY (review_code) REFERENCES review(review_code);

-- Foreign Key Constraints for subscribe_post table
ALTER TABLE subscribe_post ADD CONSTRAINT FK_SUBSCRIBE_POST_MEMBER
FOREIGN KEY (member_code) REFERENCES member(member_code);

ALTER TABLE subscribe_post ADD CONSTRAINT FK_SUBSCRIBE_POST_COMMUNITY
FOREIGN KEY (community_post_code) REFERENCES community_post(community_post_code);

-- Foreign Key Constraints for tendency_genre table (composite primary key)
ALTER TABLE tendency_genre ADD CONSTRAINT FK_TENDENCY_GENRE_TENDENCY
FOREIGN KEY (tendency_code) REFERENCES tendency(tendency_code);

ALTER TABLE tendency_genre ADD CONSTRAINT FK_TENDENCY_GENRE_GENRE
FOREIGN KEY (genre_code) REFERENCES genre(genre_code);

-- Foreign Key Constraints for theme_genre table (composite primary key)
ALTER TABLE theme_genre ADD CONSTRAINT FK_THEME_GENRE_THEME
FOREIGN KEY (theme_code) REFERENCES theme(theme_code);

ALTER TABLE theme_genre ADD CONSTRAINT FK_THEME_GENRE_GENRE
FOREIGN KEY (genre_code) REFERENCES genre(genre_code);

-- Foreign Key Constraints for review_like table
ALTER TABLE review_like ADD CONSTRAINT FK_REVIEW_LIKE_MEMBER
FOREIGN KEY (member_code) REFERENCES member(member_code);

ALTER TABLE review_like ADD CONSTRAINT FK_REVIEW_LIKE_REVIEW
FOREIGN KEY (review_code) REFERENCES review(review_code);

-- Foreign Key Constraints for event_file table
ALTER TABLE event_file ADD CONSTRAINT FK_EVENT_FILE_EVENT_POST
FOREIGN KEY (event_post_code) REFERENCES event_post(event_post_code);

-- Foreign Key Constraints for notice_file table
ALTER TABLE notice_file ADD CONSTRAINT FK_NOTICE_FILE_NOTICE_POST
FOREIGN KEY (notice_post_code) REFERENCES notice_post(notice_post_code);

-- Foreign Key Constraints for community_file table
ALTER TABLE community_file ADD CONSTRAINT FK_COMMUNITY_FILE_POST
FOREIGN KEY (community_post_code) REFERENCES community_post(community_post_code);
