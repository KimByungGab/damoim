-- MySQL dump 10.13  Distrib 8.0.34, for Win64 (x86_64)
--
-- Host: 127.0.0.1    Database: damoim
-- ------------------------------------------------------
-- Server version	8.0.34

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `sproject_post`
--

DROP TABLE IF EXISTS `sproject_post`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sproject_post` (
  `idx` bigint NOT NULL AUTO_INCREMENT COMMENT '인덱스',
  `user_idx` bigint NOT NULL COMMENT '사용자 인덱스',
  `title` varchar(200) NOT NULL COMMENT '제목',
  `content` text NOT NULL COMMENT '내용',
  `is_opened` tinyint NOT NULL DEFAULT '1' COMMENT '사이드 프로젝트 모집 여부',
  `created_at` datetime NOT NULL COMMENT '등록일자',
  PRIMARY KEY (`idx`),
  UNIQUE KEY `idx_UNIQUE` (`idx`),
  KEY `sproject_post_user_idx_idx` (`user_idx`),
  CONSTRAINT `sproject_post_user_idx` FOREIGN KEY (`user_idx`) REFERENCES `user` (`idx`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='사이드 프로젝트 게시글';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sproject_post`
--

LOCK TABLES `sproject_post` WRITE;
/*!40000 ALTER TABLE `sproject_post` DISABLE KEYS */;
/*!40000 ALTER TABLE `sproject_post` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sproject_recruit_group`
--

DROP TABLE IF EXISTS `sproject_recruit_group`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sproject_recruit_group` (
  `idx` bigint NOT NULL AUTO_INCREMENT COMMENT '인덱스',
  `post_idx` bigint NOT NULL COMMENT '사이드 프로젝트 게시글 인덱스',
  `name` varchar(45) NOT NULL COMMENT '구인 그룹명',
  `recruit_count` int NOT NULL DEFAULT '0' COMMENT '구인 인원 수',
  PRIMARY KEY (`idx`),
  UNIQUE KEY `idx_UNIQUE` (`idx`),
  KEY `sproject_recruit_group_post_idx_idx` (`post_idx`),
  CONSTRAINT `sproject_recruit_group_post_idx` FOREIGN KEY (`post_idx`) REFERENCES `sproject_post` (`idx`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='사이드 프로젝트 구인 그룹';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sproject_recruit_group`
--

LOCK TABLES `sproject_recruit_group` WRITE;
/*!40000 ALTER TABLE `sproject_recruit_group` DISABLE KEYS */;
/*!40000 ALTER TABLE `sproject_recruit_group` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sproject_reply`
--

DROP TABLE IF EXISTS `sproject_reply`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sproject_reply` (
  `idx` bigint NOT NULL AUTO_INCREMENT COMMENT '인덱스',
  `user_idx` bigint NOT NULL COMMENT '사용자 인덱스',
  `post_idx` bigint NOT NULL COMMENT '사이드 프로젝트 게시글 인덱스',
  `content` varchar(500) NOT NULL COMMENT '내용',
  `parent_reply_idx` bigint NOT NULL DEFAULT '0' COMMENT '답글의 원본 댓글 인덱스',
  `created_at` datetime NOT NULL COMMENT '등록일자',
  PRIMARY KEY (`idx`),
  UNIQUE KEY `idx_UNIQUE` (`idx`),
  KEY `post_idx_idx` (`post_idx`),
  KEY `sproject_reply_user_idx_idx` (`user_idx`),
  CONSTRAINT `sproject_reply_post_idx` FOREIGN KEY (`post_idx`) REFERENCES `sproject_post` (`idx`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `sproject_reply_user_idx` FOREIGN KEY (`user_idx`) REFERENCES `user` (`idx`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='사이드 프로젝트 댓글';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sproject_reply`
--

LOCK TABLES `sproject_reply` WRITE;
/*!40000 ALTER TABLE `sproject_reply` DISABLE KEYS */;
/*!40000 ALTER TABLE `sproject_reply` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sproject_volunteer`
--

DROP TABLE IF EXISTS `sproject_volunteer`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sproject_volunteer` (
  `idx` bigint NOT NULL AUTO_INCREMENT COMMENT '인덱스',
  `group_idx` bigint NOT NULL COMMENT '지원 그룹 인덱스',
  `user_idx` bigint NOT NULL COMMENT '사용자 인덱스',
  `result` varchar(1) NOT NULL COMMENT '결과',
  `created_at` datetime NOT NULL COMMENT '등록일자',
  PRIMARY KEY (`idx`),
  UNIQUE KEY `idx_UNIQUE` (`idx`),
  KEY `sproject_volunteer_group_idx_idx` (`group_idx`),
  KEY `sproject_volunteer_user_idx_idx` (`user_idx`),
  CONSTRAINT `sproject_volunteer_group_idx` FOREIGN KEY (`group_idx`) REFERENCES `sproject_recruit_group` (`idx`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `sproject_volunteer_user_idx` FOREIGN KEY (`user_idx`) REFERENCES `user` (`idx`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='사이드 프로젝트 지원자';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sproject_volunteer`
--

LOCK TABLES `sproject_volunteer` WRITE;
/*!40000 ALTER TABLE `sproject_volunteer` DISABLE KEYS */;
/*!40000 ALTER TABLE `sproject_volunteer` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user`
--

DROP TABLE IF EXISTS `user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user` (
  `idx` bigint NOT NULL AUTO_INCREMENT COMMENT '인덱스',
  `email` varchar(45) NOT NULL COMMENT '이메일',
  `password` varchar(1000) NOT NULL COMMENT '비밀번호',
  `nickname` varchar(45) NOT NULL COMMENT '닉네임',
  `role` int NOT NULL COMMENT '권한 역할',
  PRIMARY KEY (`idx`),
  UNIQUE KEY `idx_UNIQUE` (`idx`),
  UNIQUE KEY `email_UNIQUE` (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='계정 정보';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user`
--

LOCK TABLES `user` WRITE;
/*!40000 ALTER TABLE `user` DISABLE KEYS */;
/*!40000 ALTER TABLE `user` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2023-11-23  4:53:33
