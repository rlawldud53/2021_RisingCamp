-- Exported from QuickDBD: https://www.quickdatabasediagrams.com/
-- Link to schema: https://app.quickdatabasediagrams.com/#/d/CgKWML
-- NOTE! If you have used non-SQL datatypes in your design, you will have to change these here.

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";

CREATE TABLE `User` (
    `user_id` bigint(11)  NOT NULL ,
    `password` varchar(256)  NOT NULL ,
    `email` varchar(50)  NOT NULL ,
    `birthday` date  NOT NULL ,
    `memebership` char(20)  NOT NULL ,
    `card_id` bigint(11)  NOT NULL ,
    `device` varchar(45)  NOT NULL ,
    `created_at` timestamp  NOT NULL ,
    `updated_at` timestamp  NOT NULL ,
    `status` varchar(50)  NOT NULL ,
    PRIMARY KEY (
        `user_id`
    )
);

CREATE TABLE `Profile` (
    `profile_id` bigint(11)  NOT NULL ,
    `user_id` bigint(11)  NOT NULL ,
    `profile_name` varchar(45)  NOT NULL ,
    `password` varchar(256)  NOT NULL ,
    `icon_image` longblob  NULL ,
    `language` varchar(45)  NOT NULL ,
    `kid` tinyint(1)  NOT NULL ,
    `autoplay` tinyint(1)  NOT NULL ,
    `created_at` timestamp  NOT NULL ,
    `updated_at` timestamp  NOT NULL ,
    `status` varchar(50)  NOT NULL ,
    PRIMARY KEY (
        `profile_id`
    )
);

CREATE TABLE `Membership` (
    `membership` char(20)  NOT NULL ,
    `price_per_month` decimal(6,3)  NOT NULL ,
    `hd_available` tinyint(1)  NOT NULL ,
    `screens_num` int(1)  NOT NULL ,
    PRIMARY KEY (
        `membership`
    )
);

CREATE TABLE `History` (
    `profile_id` bigint(11)  NOT NULL ,
    `content_id` bigint(15)  NOT NULL ,
    `progress` TIME  NOT NULL ,
    `created_at` timestamp  NOT NULL ,
    `updated_at` timestamp  NOT NULL ,
    `status` varchar(50)  NOT NULL ,
    PRIMARY KEY (
        `profile_id`,`content_id`
    )
);

CREATE TABLE `Favorite_movies` (
    `profile_id` bigint(11)  NOT NULL ,
    `content_id` bigint(15)  NOT NULL ,
    `created_at` timestamp  NOT NULL ,
    `updated_at` timestamp  NOT NULL ,
    `status` varchar(50)  NOT NULL ,
    PRIMARY KEY (
        `profile_id`,`content_id`
    )
);

CREATE TABLE `Contents` (
    `content_id` bigint(15)  NOT NULL ,
    `title` varchar(108)  NOT NULL ,
    `release_year` year  NOT NULL ,
    `summary` varchar(255)  NOT NULL ,
    `runtime` time  NOT NULL ,
    `rating` int(2)  NOT NULL ,
    `star_rating` int(1)  NOT NULL ,
    `director_id` int(10)  NOT NULL ,
    `scriptwriter_id` int(10)  NOT NULL ,
    `audio_languauge` varchar(45)  NOT NULL ,
    `season_num` int(2)  NULL ,
    `created_at` timestamp  NOT NULL ,
    `updated_at` timestamp  NOT NULL ,
    `status` varchar(50)  NOT NULL ,
    PRIMARY KEY (
        `content_id`
    )
);

CREATE TABLE `Content_genre` (
    `content_id` bigint(15)  NOT NULL ,
    `genre_id` bigint(10)  NOT NULL ,
    `created_at` timestamp  NOT NULL ,
    `updated_at` timestamp  NOT NULL ,
    `status` varchar(50)  NOT NULL ,
    PRIMARY KEY (
        `content_id`,`genre_id`
    )
);

CREATE TABLE `Genre` (
    `genre_id` bigint(10)  NOT NULL ,
    `genre_name` varchar(100)  NOT NULL ,
    `created_at` timestamp  NOT NULL ,
    `updated_at` timestamp  NOT NULL ,
    `status` varchar(50)  NOT NULL ,
    PRIMARY KEY (
        `genre_id`
    )
);

CREATE TABLE `Episode` (
    `episode_id` int(3)  NOT NULL ,
    `episode_title` varchar(45)  NOT NULL ,
    `release_date` date  NOT NULL ,
    `content_id` bigint(15)  NOT NULL ,
    `season` int(2)  NOT NULL ,
    `summary` varchar(255)  NOT NULL ,
    `runtime` time  NOT NULL ,
    PRIMARY KEY (
        `episode_id`
    )
);

CREATE TABLE `Actor` (
    `actor_id` bigint(10)  NOT NULL ,
    `actor_name` varchar(45)  NOT NULL ,
    `gender` varchar(20)  NOT NULL ,
    `birthday` date  NOT NULL ,
    `created_at` timestamp  NOT NULL ,
    `updated_at` timestamp  NOT NULL ,
    `status` varchar(50)  NOT NULL ,
    PRIMARY KEY (
        `actor_id`
    )
);

CREATE TABLE `Cast` (
    `content_id` bigint(10)  NOT NULL ,
    `actor_id` bigint(15)  NOT NULL ,
    `created_at` timestamp  NOT NULL ,
    `updated_at` timestamp  NOT NULL ,
    `status` varchar(50)  NOT NULL ,
    PRIMARY KEY (
        `content_id`,`actor_id`
    )
);

CREATE TABLE `Director` (
    `director_id` int(10)  NOT NULL ,
    `director_name` varchar(45)  NOT NULL ,
    `gender` varchar(20)  NOT NULL ,
    `birthday` date  NOT NULL ,
    `created_at` timestamp  NOT NULL ,
    `updated_at` timestamp  NOT NULL ,
    `status` varchar(50)  NOT NULL ,
    PRIMARY KEY (
        `director_id`
    )
);

CREATE TABLE `Scriptwriter` (
    `writer_id` int(10)  NOT NULL ,
    `writer_name` varchar(45)  NOT NULL ,
    `gender` varchar(20)  NOT NULL ,
    `birthday` date  NOT NULL ,
    `created_at` timestamp  NOT NULL ,
    `updated_at` timestamp  NOT NULL ,
    `status` varchar(50)  NOT NULL ,
    PRIMARY KEY (
        `writer_id`
    )
);

CREATE TABLE `Subtitle` (
    `subtitle_id` bigint(10)  NOT NULL ,
    `content_id` bigint(15)  NOT NULL ,
    `language` varchar(45)  NOT NULL ,
    `creator_name` varchar(45)  NOT NULL ,
    `created_at` timestamp  NOT NULL ,
    `updated_at` timestamp  NOT NULL ,
    `status` varchar(50)  NOT NULL ,
    PRIMARY KEY (
        `subtitle_id`
    )
);

CREATE TABLE `Payment` (
    `card_id` bigint(11)  NOT NULL ,
    `card_type` char(10)  NOT NULL ,
    `card_exp_date` date  NOT NULL ,
    `bank` varchar(20)  NOT NULL ,
    `card_num` char(20)  NOT NULL ,
    `created_at` timestamp  NOT NULL ,
    `updated_at` timestamp  NOT NULL ,
    `status` varchar(50)  NOT NULL ,
    PRIMARY KEY (
        `card_id`
    )
);

ALTER TABLE `User` ADD CONSTRAINT `fk_User_memebership` FOREIGN KEY(`memebership`)
REFERENCES `Membership` (`membership`);

ALTER TABLE `Profile` ADD CONSTRAINT `fk_Profile_user_id` FOREIGN KEY(`user_id`)
REFERENCES `User` (`user_id`);

ALTER TABLE `History` ADD CONSTRAINT `fk_History_profile_id` FOREIGN KEY(`profile_id`)
REFERENCES `Profile` (`profile_id`);

ALTER TABLE `History` ADD CONSTRAINT `fk_History_content_id` FOREIGN KEY(`content_id`)
REFERENCES `Contents` (`content_id`);

ALTER TABLE `Favorite_movies` ADD CONSTRAINT `fk_Favorite_movies_profile_id` FOREIGN KEY(`profile_id`)
REFERENCES `Profile` (`profile_id`);

ALTER TABLE `Favorite_movies` ADD CONSTRAINT `fk_Favorite_movies_content_id` FOREIGN KEY(`content_id`)
REFERENCES `Contents` (`content_id`);

ALTER TABLE `Contents` ADD CONSTRAINT `fk_Contents_director_id` FOREIGN KEY(`director_id`)
REFERENCES `Director` (`director_id`);

ALTER TABLE `Contents` ADD CONSTRAINT `fk_Contents_scriptwriter_id` FOREIGN KEY(`scriptwriter_id`)
REFERENCES `Scriptwriter` (`writer_id`);

ALTER TABLE `Content_genre` ADD CONSTRAINT `fk_Content_genre_content_id` FOREIGN KEY(`content_id`)
REFERENCES `Contents` (`content_id`);

ALTER TABLE `Content_genre` ADD CONSTRAINT `fk_Content_genre_genre_id` FOREIGN KEY(`genre_id`)
REFERENCES `Genre` (`genre_id`);

ALTER TABLE `Episode` ADD CONSTRAINT `fk_Episode_content_id` FOREIGN KEY(`content_id`)
REFERENCES `Contents` (`content_id`);

ALTER TABLE `Cast` ADD CONSTRAINT `fk_Cast_content_id` FOREIGN KEY(`content_id`)
REFERENCES `Contents` (`content_id`);

ALTER TABLE `Cast` ADD CONSTRAINT `fk_Cast_actor_id` FOREIGN KEY(`actor_id`)
REFERENCES `Actor` (`actor_id`);

ALTER TABLE `Subtitle` ADD CONSTRAINT `fk_Subtitle_content_id` FOREIGN KEY(`content_id`)
REFERENCES `Contents` (`content_id`);

ALTER TABLE `Payment` ADD CONSTRAINT `fk_Payment_card_id` FOREIGN KEY(`card_id`)
REFERENCES `User` (`card_id`);

