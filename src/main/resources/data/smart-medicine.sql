-- MySQL dump 10.13  Distrib 8.0.43, for Win64 (x86_64)
--
-- Host: 127.0.0.1    Database: smart-medicine
-- ------------------------------------------------------
-- Server version	8.0.43

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `feedback`
--

DROP TABLE IF EXISTS `feedback`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `feedback` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `name` varchar(11) DEFAULT NULL COMMENT '反馈用户',
  `email` varchar(255) DEFAULT NULL COMMENT '邮箱地址',
  `title` varchar(255) DEFAULT NULL COMMENT '反馈标题',
  `content` text COMMENT '反馈内容',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `feedback`
--

LOCK TABLES `feedback` WRITE;
/*!40000 ALTER TABLE `feedback` DISABLE KEYS */;
/*!40000 ALTER TABLE `feedback` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `history`
--

DROP TABLE IF EXISTS `history`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `history` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '用户搜索历史主键id',
  `user_id` int DEFAULT NULL COMMENT '用户ID',
  `keyword` varchar(255) DEFAULT NULL COMMENT '搜索关键字',
  `operate_type` int DEFAULT NULL COMMENT '类型：1搜索，2科目，3药品',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=232 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `history`
--

LOCK TABLES `history` WRITE;
/*!40000 ALTER TABLE `history` DISABLE KEYS */;
/*!40000 ALTER TABLE `history` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `illness`
--

DROP TABLE IF EXISTS `illness`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `illness` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '疾病id',
  `kind_id` int DEFAULT NULL COMMENT '疾病分类ID',
  `illness_name` varchar(100) DEFAULT NULL COMMENT '疾病名字',
  `include_reason` mediumtext COMMENT '诱发因素',
  `illness_symptom` mediumtext COMMENT '疾病症状',
  `special_symptom` mediumtext COMMENT '特殊症状',
  `pageviews` int DEFAULT '0' COMMENT '浏览量',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  FULLTEXT KEY `ft_illness_rag` (`illness_name`,`include_reason`,`illness_symptom`,`special_symptom`)
) ENGINE=InnoDB AUTO_INCREMENT=20 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `illness`
--

LOCK TABLES `illness` WRITE;
/*!40000 ALTER TABLE `illness` DISABLE KEYS */;
/*!40000 ALTER TABLE `illness` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `illness_kind`
--

DROP TABLE IF EXISTS `illness_kind`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `illness_kind` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `name` varchar(255) DEFAULT NULL COMMENT '分类名称',
  `info` varchar(255) DEFAULT NULL COMMENT '描述',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=18 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `illness_kind`
--

LOCK TABLES `illness_kind` WRITE;
/*!40000 ALTER TABLE `illness_kind` DISABLE KEYS */;
/*!40000 ALTER TABLE `illness_kind` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `illness_medicine`
--

DROP TABLE IF EXISTS `illness_medicine`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `illness_medicine` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '病和药品关联id',
  `illness_id` int DEFAULT NULL COMMENT '病id',
  `medicine_id` int DEFAULT NULL COMMENT '药品id',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=21 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `illness_medicine`
--

LOCK TABLES `illness_medicine` WRITE;
/*!40000 ALTER TABLE `illness_medicine` DISABLE KEYS */;
/*!40000 ALTER TABLE `illness_medicine` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `medical_news`
--

DROP TABLE IF EXISTS `medical_news`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `medical_news` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `news_name` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '资讯标题',
  `news_summary` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '资讯摘要（列表展示）',
  `cover_oss_path` varchar(500) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '封面图OSS路径（smart-medicine/news/covers/xxx.jpg）',
  `markdown_oss_path` varchar(500) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'Markdown文件OSS路径（smart-medicine/news/content/xxx.md）',
  `category` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '分类',
  `author` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '作者',
  `status` tinyint DEFAULT '0' COMMENT '状态：0-草稿，1-已发布',
  `view_count` int DEFAULT '0' COMMENT '浏览量',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_status` (`status`),
  KEY `idx_category` (`category`),
  KEY `idx_create_time` (`create_time`),
  FULLTEXT KEY `ft_medical_news_rag` (`news_name`,`news_summary`,`category`,`author`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='医疗资讯表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `medical_news`
--

LOCK TABLES `medical_news` WRITE;
/*!40000 ALTER TABLE `medical_news` DISABLE KEYS */;
/*!40000 ALTER TABLE `medical_news` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `medical_news_content`
--

DROP TABLE IF EXISTS `medical_news_content`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `medical_news_content` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `news_id` int NOT NULL COMMENT '医疗资讯ID',
  `plain_content` mediumtext COLLATE utf8mb4_unicode_ci COMMENT '去除Markdown标记后的正文纯文本',
  `content_hash` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '正文内容SHA-256哈希',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_news_id` (`news_id`),
  KEY `idx_update_time` (`update_time`),
  FULLTEXT KEY `ft_plain_content` (`plain_content`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='医疗资讯正文缓存表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `medical_news_content`
--

LOCK TABLES `medical_news_content` WRITE;
/*!40000 ALTER TABLE `medical_news_content` DISABLE KEYS */;
/*!40000 ALTER TABLE `medical_news_content` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `medicine`
--

DROP TABLE IF EXISTS `medicine`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `medicine` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '药品主键ID',
  `medicine_name` varchar(100) DEFAULT NULL COMMENT '药的名字',
  `keyword` varchar(255) DEFAULT NULL COMMENT '关键字搜索',
  `medicine_effect` mediumtext COMMENT '药的功效',
  `medicine_brand` varchar(255) DEFAULT NULL COMMENT '药的品牌',
  `interaction` mediumtext COMMENT '药的相互作用',
  `taboo` mediumtext COMMENT '禁忌',
  `us_age` mediumtext COMMENT '用法用量',
  `medicine_type` int DEFAULT NULL COMMENT '药的类型，0西药，1中药，2中成药',
  `img_path` varchar(255) DEFAULT NULL COMMENT '相关图片路径',
  `medicine_price` decimal(10,2) DEFAULT NULL COMMENT '药的价格',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  FULLTEXT KEY `ft_medicine_rag` (`medicine_name`,`keyword`,`medicine_effect`,`medicine_brand`,`interaction`,`taboo`,`us_age`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `medicine`
--

LOCK TABLES `medicine` WRITE;
/*!40000 ALTER TABLE `medicine` DISABLE KEYS */;
/*!40000 ALTER TABLE `medicine` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `pageview`
--

DROP TABLE IF EXISTS `pageview`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `pageview` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `pageviews` int DEFAULT NULL COMMENT '浏览量',
  `illness_id` int DEFAULT NULL COMMENT '病的id',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=18 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `pageview`
--

LOCK TABLES `pageview` WRITE;
/*!40000 ALTER TABLE `pageview` DISABLE KEYS */;
/*!40000 ALTER TABLE `pageview` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `science_video`
--

DROP TABLE IF EXISTS `science_video`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `science_video` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `title` varchar(255) NOT NULL COMMENT '视频标题',
  `description` varchar(255) DEFAULT NULL COMMENT '视频描述',
  `imgPath` varchar(500) DEFAULT NULL COMMENT '视频展示图片',
  `link` varchar(500) NOT NULL COMMENT '视频链接',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  FULLTEXT KEY `ft_science_video_rag` (`title`,`description`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `science_video`
--

LOCK TABLES `science_video` WRITE;
/*!40000 ALTER TABLE `science_video` DISABLE KEYS */;
/*!40000 ALTER TABLE `science_video` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user`
--

DROP TABLE IF EXISTS `user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '用户主键id',
  `user_account` varchar(255) DEFAULT NULL COMMENT '用户账号',
  `user_name` varchar(255) DEFAULT NULL COMMENT '用户的真实名字',
  `user_pwd` varchar(255) DEFAULT NULL COMMENT '用户密码',
  `user_age` int DEFAULT NULL COMMENT '用户年龄',
  `user_sex` varchar(1) DEFAULT NULL COMMENT '用户性别',
  `user_email` varchar(255) DEFAULT NULL COMMENT '用户邮箱',
  `user_tel` varchar(50) DEFAULT NULL COMMENT '手机号',
  `role_status` int DEFAULT NULL COMMENT '角色状态，1管理员，0普通用户',
  `img_path` varchar(255) DEFAULT NULL COMMENT '用户头像',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `create_time` (`create_time`)
) ENGINE=InnoDB AUTO_INCREMENT=20 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user`
--

LOCK TABLES `user` WRITE;
/*!40000 ALTER TABLE `user` DISABLE KEYS */;
INSERT INTO `user` VALUES (1,'admin','admin','$2a$10$HJq8gJC0yrkkkDg9hyIUWO7kXeHY5qpMzUWqliRFv0vGXlzoZZWbq',23,'男','****@qq.com','18111111111',1,'***.jpg','2025-10-22 19:14:29','2026-02-20 12:23:39');
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

-- Dump completed on 2026-05-09 22:23:25
