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
INSERT INTO `feedback` VALUES (9,'zzy','test@example.com','测试反馈功能','验证userId=2是否可以成功提交反馈','2025-12-03 18:58:04','2025-12-03 18:58:04'),(10,'zzy','test@test.com','测试反馈','这是测试内容','2025-12-03 19:20:33','2025-12-03 19:20:33'),(11,'zzy','test@test.com','测试反馈','这是测试内容','2025-12-03 19:26:24','2025-12-03 19:26:24'),(12,'admin','user@example.com','建议增加药品搜索功能','希望可以按药品名称快速搜索...','2025-12-04 20:38:00','2025-12-04 20:38:00'),(14,'zhangsan','user@example.com','建议增加药品搜索功能1111111111','希望可以按药品名称快速搜索...','2025-12-06 04:26:22','2025-12-06 04:26:22');
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
INSERT INTO `history` VALUES (208,1,'便秘',2,'2026-02-21 08:48:47','2026-02-21 08:48:47'),(209,1,'湿疹',2,'2026-02-21 08:50:15','2026-04-28 17:41:35'),(210,1,'病毒性感冒',2,'2026-02-21 08:50:49','2026-05-05 12:39:00'),(211,1,'牙周炎',2,'2026-02-21 08:51:09','2026-02-24 06:57:20'),(212,1,'1111',2,'2026-02-21 08:51:57','2026-02-21 12:29:59'),(213,1,'111',2,'2026-02-21 12:29:54','2026-02-21 12:29:54'),(214,1,'甲硝唑',4,'2026-02-21 13:18:10','2026-02-24 04:53:45'),(215,1,'胃溃疡',2,'2026-02-21 13:20:01','2026-02-21 13:20:01'),(216,1,'胃溃疡',2,'2026-02-21 13:20:01','2026-02-21 13:20:01'),(217,1,'999皮炎平',4,'2026-02-21 13:26:14','2026-04-28 17:52:27'),(218,1,'风寒感冒',2,'2026-02-21 13:26:22','2026-02-21 13:26:22'),(219,1,'风寒感冒',2,'2026-02-21 13:26:22','2026-02-21 13:26:22'),(220,1,'阿莫西林胶囊',4,'2026-02-21 13:26:25','2026-02-21 13:26:25'),(221,1,'999感冒灵颗粒',4,'2026-02-21 13:26:29','2026-05-05 12:07:41'),(222,1,'布洛芬缓释胶囊',4,'2026-02-21 13:26:32','2026-05-05 12:07:27'),(223,1,'三九胃泰颗粒',4,'2026-02-21 13:32:16','2026-02-21 13:32:16'),(224,1,'七步洗手法',5,'2026-02-23 14:12:41','2026-04-28 17:57:24'),(225,1,'用药安全科普',5,'2026-02-23 14:12:51','2026-02-24 09:02:36'),(226,1,'8小时睡眠论可能是错的！盲目追求睡够8小时，或许会产生巨大压力',6,'2026-02-23 16:26:50','2026-04-28 17:58:38'),(227,1,'三款茶饮清湿热、健脾祛湿养心神',6,'2026-02-23 16:27:43','2026-02-23 16:27:55'),(228,1,'睡眠不好该如何对症调理',6,'2026-02-23 17:36:08','2026-02-24 09:05:35'),(229,1,'扁桃体发炎',2,'2026-02-23 17:36:26','2026-02-23 17:36:26'),(230,1,'冬季手脚干裂起皮，不只是干燥缺水',6,'2026-02-24 04:49:56','2026-02-24 04:49:56'),(231,1,'口腔溃疡',2,'2026-03-11 08:41:11','2026-03-19 03:59:41');
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
INSERT INTO `illness` VALUES (1,10,'病毒性感冒','各种导致全身或呼吸道局部防御功能降低的原因，如受凉、淋雨、气候突变、过度疲劳等均可诱发本疲','急性起病，患者主要表现为鼻塞、流涕、咽痛、咳嗽等症状。部分患者可有发热、乏力、头痛、周身酸痛、\n食欲减退、腹胀、便秘或腹泻等全身症状。','急性起病，患者主要表现为鼻塞、流涕、咽痛、咳嗽等症状。部分患者可有发热、乏力、头痛、周身酸痛、\n食欲减退、腹胀、便秘或腹泻等全身症状。',11,'2022-05-01 11:31:10','2025-12-04 17:02:12'),(2,10,'风寒感冒','身体免疫力低下的情况下，体内有寒气入侵，导致感冒','恶寒重、发热轻、无汗、头痛身痛、鼻塞流清涕、咳嗽吐稀白痰、口不渴或渴喜热饮、苔薄白','恶寒重、发热轻、无汗、头痛身痛、畏寒',0,'2022-05-01 11:31:10','2025-12-04 17:02:12'),(3,2,'扁桃体发炎','扁桃体炎形成的原因与多种因素有关，包括感染因素、免疫因素、邻近器官的急性炎症等，细菌和病毒积存\n于扁桃体窝引起该病。扁桃体炎还可继发于某些急性传染病，如猩红热、麻疹、流感等。','发热、咽部不适、咽部疼痛，甚至吞咽、呼吸困难、局部可有咽痛，吞咽时尤为明显，甚至因畏惧疼痛不敢吞咽，疼痛可放射至耳部，幼儿常因不能吞咽而拒食哭闹。','咽痛、咽部不适',0,'2022-05-01 11:31:10','2025-12-04 17:02:12'),(4,3,'偏头痛','偏头痛的病因尚不明确，可能与遗传、内分泌代谢、环境因素、精神因素有关。','偏头痛的常见症状包括：头痛，开始常为隐约疼痛，逐渐变为搏动性疼痛，活动时加重，还可从头的一侧转\n移至另一侧，累及头前部或整个头部；对光线、噪音和气味敏感；伴有恶心、呕吐，胃部不适，腹部疼痛；\n食欲差；感觉非常的暖或冷；肤色苍白；疲劳；头晕；视野模糊；腹泻。比较军见的症状包括发烧、影响正\n常的肢体活动。','左侧疼痛、右侧疼痛、单侧疼痛、一阵一阵的疼痛、像针扎一样',0,'2022-05-01 11:31:10','2025-12-04 17:02:12'),(5,2,'便秘','便秘通常是由于美便在消化道中移动太慢，或无法从直肠中有效清除时，导致类便脱水、变硬和干燥，从而引发的便秘。','排便次数减少、一周内小于3次、粪便干燥或结块、如羊粪、排便因难，如排便时间长、排便时感觉有阻碍、排便后仍有粪便未排尽的感觉、需手按腹部帮助排便等','大便困难、拉不出来',0,'2022-05-01 11:31:10','2025-12-04 17:02:12'),(6,3,'骨折','骨折是由创伤或骨骼痪病所导致，大部分骨折都是由于直接或间接暴力引起。跌倒、撞击、交通意外等暴力因素是导致骨折的常见原因。积累性劳损及骨骼痪病也会增加骨折发生几率，骨骼痪病（如骨髓炎、骨肿瘤）导致骨质破坏，患者受到轻微外力就可能发生骨折。','骨折特有特征为畸形、异常活动和骨擦音（感）。大部分骨折一般只引起局部症状，最常见的症状就是局部\n疼痛、肿胀及功能障碍。严重骨折和多发性骨折可伴随全身症状（如休克、发热）。','骨折的一般表现为局部疼痛、肿胀及功能障碍',0,'2022-05-01 11:31:10','2025-12-04 17:02:12'),(7,17,'牙周炎','牙周炎是一种破坏性庆病，与微生物、宿主反应有关，是导致我国成人牙齿丧失的主要原因，严重影响患者的口腔健康。在局部致病因素中，牙菌斑是最主要的致病因素，而在全身因素中吸烟是高危因素。','健康的牙龈应该呈粉红色，边缘薄且紧贴牙面，质坚韧，探诊不出血。牙周災的主要症状是牙龈红肿、质地\n松软、探诊出血、牙周袋溢脓和牙齿松动。','牙龈出血、牙齿松动、牙龈肿',0,'2022-05-01 11:31:10','2025-12-04 17:02:12'),(8,2,'胃溃疡','胃溃疡是一种常见的消化痪病，任何年龄的人都可能患病。在全球范围内，约占10%的人群一生中都会患有消化性渍疡。在患病人群中，40-60岁的中老年患者最为多见，而且男性多于女性。','胃溃疡的症状较多，包括胃部疼痛、食欲不振、餐后腹胀或胃部不适、体重减轻等等。这些症状的严重程度\n取决于溃疡的严重程度。有些患者可能没有任何症状（如“无症状性溃疡\"），或者是以胃出血、胃穿孔等并\n发症为首发症状。','餐后腹胀、体重减轻、食欲不振',0,'2022-05-01 11:31:10','2025-12-04 17:02:12'),(9,17,'口腔溃疡','口腔渍疡的致病原因尚不明确，多种因素可诱发，包括遗传因素、饮食因素、免疫因素等，且具有明显的个体差异。口腔渍疡经常、反复发作时，严重影响患者的日常生活和工作。','口腔溃疡常见于口腔的唇、脸颊、软腭或牙龈等处的黏膜上，溃疡面一般呈圆形或椭圆形，溃疡面凹陷、有\n白色或黄色的中心、周围充血微红肿，有明显疼痛感。','口腔溃疡常见于口腔的唇、脸颊、软腭或牙龈等处的黏膜上，溃疡面一般呈圆形或椭圆形，溃疡面凹陷、有\n白色或黄色的中心、周围充血微红肿，有明显疼痛感。',2,'2022-05-01 11:31:10','2025-12-04 17:02:12'),(13,7,'湿疹','湿疹的病因目前尚不明确，与机体内因、外因、社会心理因素等都有关。机体内因包括免疫功能异常和系统性痪病（如内分泌痪病、营养障碍、慢性感染等）以及遗传性或获得性 皮肤屏障功能障碍。','急性期表现为红斑、水肿、粟粒大小的丘疹、丘疱疹、水疱，糜烂及渗出；亚急性期表现为红肿和渗出减\n轻，糜烂面结痂、脱屑；慢性期主要表现为粗糙肥厚、苔藓样变。湿疹容易复发，严重影响患者的生活质\n量。','起病较急、发病较快，瘙痒剧烈。',2,'2022-05-03 16:08:58','2025-12-04 17:02:12');
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
INSERT INTO `illness_kind` VALUES (1,'急诊科','急诊科疾病','2022-05-01 11:57:39','2022-05-01 12:01:00'),(2,'内科','内科疾病','2022-05-01 11:57:57','2022-05-01 12:00:59'),(3,'外科','外科疾病','2022-05-01 11:58:26','2022-05-01 12:00:57'),(4,'妇产科','妇产科疾病','2022-05-01 11:58:36','2022-05-01 12:00:56'),(5,'儿科','儿科疾病','2022-05-01 11:58:49','2022-05-01 12:00:54'),(6,'男科','男科疾病','2022-05-01 11:58:59','2022-05-01 12:00:53'),(7,'皮肤科','皮肤科疾病','2022-05-03 16:07:12','2022-05-03 16:07:12'),(9,'肝病','肝病疾病','2022-05-01 11:59:27','2022-05-01 12:00:49'),(10,'传染科','传染科疾病','2022-05-01 11:59:35','2022-05-01 12:00:48'),(16,'耳鼻喉科','耳鼻喉科疾病','2022-05-01 12:00:23','2022-05-01 12:00:41'),(17,'口腔科','口腔科疾病','2022-05-01 12:00:31','2022-05-01 12:00:39');
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
INSERT INTO `illness_medicine` VALUES (6,3,1,'2022-05-03 16:10:35','2022-05-03 16:10:35'),(7,2,1,'2022-05-03 16:10:37','2022-05-03 16:10:37'),(8,1,1,'2022-05-03 16:10:38','2022-05-03 16:10:38'),(9,4,1,'2022-05-03 16:10:42','2022-05-03 16:10:42'),(10,7,1,'2022-05-03 16:10:44','2022-05-03 16:10:44'),(11,1,2,'2022-05-03 16:10:59','2022-05-03 16:10:59'),(12,2,2,'2022-05-03 16:11:01','2022-05-03 16:11:01'),(13,5,3,'2022-05-03 16:11:16','2022-05-03 16:11:16'),(14,13,5,'2022-05-03 16:11:29','2022-05-03 16:11:29'),(15,8,4,'2022-05-03 16:11:39','2022-05-03 16:11:39'),(16,7,6,'2022-05-03 16:11:50','2022-05-03 16:11:50'),(17,4,7,'2022-05-03 16:12:01','2022-05-03 16:12:01'),(18,2,7,'2022-05-03 16:12:03','2022-05-03 16:12:03'),(19,1,7,'2022-05-03 16:12:04','2022-05-03 16:12:04'),(20,3,7,'2022-05-03 16:12:05','2022-05-03 16:12:05');
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
INSERT INTO `medical_news` VALUES (1,'emo刷屏了！负面情绪如何排解','emo刷屏了！负面情绪如何排解','https://img-zzy.oss-cn-beijing.aliyuncs.com/smart-medicine/images/945a19d689f54f3292be2a82fc8eb1d3.jpg','https://img-zzy.oss-cn-beijing.aliyuncs.com/smart-medicine/files/53f1b7a3238d45c0a6ac9e865807d5f9.md','心情','zzy',1,2,'2026-02-23 12:57:27','2026-02-24 00:18:51'),(2,'冬季手脚干裂起皮，不只是干燥缺水','冬季手脚干裂起皮，不只是干燥缺水','https://img-zzy.oss-cn-beijing.aliyuncs.com/smart-medicine/images/6bbf52303c834a8b9c99aa6c365602a6.jpg','https://img-zzy.oss-cn-beijing.aliyuncs.com/smart-medicine/files/372244f8d7b44bcaa3b4af52400e4cb9.md','健康','zzy',1,4,'2026-02-23 22:53:41','2026-02-24 00:19:38'),(3,'三款茶饮清湿热、健脾祛湿养心神','三款茶饮清湿热、健脾祛湿养心神','https://img-zzy.oss-cn-beijing.aliyuncs.com/smart-medicine/images/8194ab7c861b4cf5b94ead1baeb84694.jpg','https://img-zzy.oss-cn-beijing.aliyuncs.com/smart-medicine/files/d5d69dc1240c4820987868d887915ae5.md','养生','zzy',1,8,'2026-02-23 22:54:42','2026-02-24 00:19:26'),(4,'睡眠不好该如何对症调理','睡眠不好该如何对症调理','https://img-zzy.oss-cn-beijing.aliyuncs.com/smart-medicine/images/b6e64563da0449bf8241e3af862ebc60.jpg','https://img-zzy.oss-cn-beijing.aliyuncs.com/smart-medicine/files/18294d3d8a5c43f49ab7bfebf3015b9f.md','睡眠','zzy',1,18,'2026-02-23 23:56:02','2026-02-24 00:19:11'),(5,'8小时睡眠论可能是错的！盲目追求睡够8小时，或许会产生巨大压力','8小时睡眠论可能是错的！盲目追求睡够8小时，或许会产生巨大压力','https://img-zzy.oss-cn-beijing.aliyuncs.com/smart-medicine/images/cf4ba1c8cf27425680ae521ab063c35b.jpg','https://img-zzy.oss-cn-beijing.aliyuncs.com/smart-medicine/files/aae27832c60d438588879ba10f16c5ec.md','睡眠','zzy',1,3,'2026-02-23 23:58:12','2026-03-11 16:53:23');
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
INSERT INTO `medical_news_content` VALUES (1,1,'emo刷屏了！负面情绪如何排解 关于“emo”的含义 　　医疗资讯网了解到有一种观点认为，它原本是一种情绪化的音乐风格，在互联网世界里，则衍生出“忧郁”“伤感”“心情低落”等多重含义。 　　也有一种解释是，“emo”是英文emotional的缩写，形容一些不稳定的情绪时都可以用这个词，相关意思可以延展为“不开心”“我太难了”等等。 　　不过，伴随着这个词的流行，其使用范围也可能会脱离原本的含义，甚至有时没有什么特别的意思，只是使用者的一种自嘲，或者用来互相调侃。 　　虽然是一个用来表达情绪的词，但心理学专家宋广文认为，心理学目前对其没有明确的界定。最初出现时，网友可能会觉得这个新词有趣，从而感兴趣。 　　“另外，现代社会大家都很忙，工作不顺等可能常常使人处于一种紧张或压力的状态中，当‘emo’出现时，恰好符合人们表达情绪的需求。”他说。 　　互相打气 告别“emo” 　　不过 ，除了“emo”这个词本身，从心理学角度来说，情绪本身没有对错，但如果负面情绪长期积累，就有可能影响正常生活节奏，甚至可能引发疾病。 　　医疗资讯网了解到生活中不如意之事太过常见，需要以乐观心态面对。因此，很多网友开启了告别“emo”的模式，或者录一段积极生活的短视频，互相打气，“消解负面情绪，和心情乌云说拜拜！” 　　有人会在感觉到委屈和颓废时，翻一翻经典名著，“最近常看《道德经》，学习古人的智慧，读到‘上善若水’，会觉得比较治愈。也提醒自己不要过于追求完美，学会包容别人。” 　　或者，如果一段时间心情烦躁，有人会选择找个安静的地方独处、放空，“选择一条安全的路线，坐公交车到最后一站，看看风景，内心会平和很多。” 　　也有人选择正视那些令人“emo”的情绪，“负面情绪让我们不舒服，但不代表它是错的。只不过，情绪的表达方式有高下，我们可以学习更多适合自己的健康表达方式。” 　　了解自身需要 寻找合适的排解方式 　　的确，情绪是我们内心需要的表达，首先察觉它、接纳它，通过情绪去更多地了解自己，然后才可能对情绪做出调整。 　　“对一些网友来说，经常使用‘emo’这个词可能是寻求情感上的共鸣，也可能是寻求别人对自己情绪的关注，增加跟其他人交流中共性的东西。”宋广文解释。 　　但他提到，无论积极情绪还是消极情绪，都有感染性或辐射性。比如一个家庭有五口人，有四个人的情绪很低落，那么其余人也容易被这种消极的“场效应”影响，高兴不起来。 　　基于此，宋广文认为，人要有一种对自我需要的全面了解，调节自身需求，学会“知足常乐”，不为小事纠结，降低负面情绪产生的可能性，保持阳光的心态，同时把快乐带给别人。 　　另外，在感觉负面情绪较多时，可以找朋友聊天，及时将不良情绪倾诉出来；听一听和缓的音乐、放松心情，“或者适当参加体育活动等，找到适合自己的排解方式。”他说。','9fc3825a774355f5de6c1f98915bb20005c0adee255ee62e2849be1a364f9248','2026-05-05 11:42:50','2026-05-05 11:42:50'),(2,2,'在进入冬季的时候，有很多人的手脚部位总是会出现干裂、起皮的现象。严重时，还会出现明显的裂口，并伴有一定的疼痛感。 有很多人认为，出现这种现象的原因，是由于手脚部位的皮肤过度干燥导致，这是真的吗？下面，就给大家详细介绍一下，希望引起这类人群的警惕。 冬季手脚干裂起皮的原因有哪些？ 一、感染真菌 手脚部位感染上真菌之后，就很容易出现去皮、干裂等现象。比如在足部感染了红色毛癣菌的时候，就会引起皮肤真菌感染，从而患上足癣，患者的双脚部位会出现一些水疱，或者是脱屑以及瘙痒现象。 有些患者也会出现皮肤角化鳞屑以及起皮、开裂、干燥等症状。而如果手部感染毛癣菌的话，也会引起手癣的出现，从而导致患者的手部出现一些水疱，或者是出现过度角化性皮损以及脱屑、干裂等现象。 二、湿疹 湿疹是一种慢性炎症性的皮肤疾病，好发于手部、脚部，以及面部、颈部、背部等部位，发病率非常的高。 一旦患上湿疹，患者的患病部位就会出现一些丘疹或者是水疱，并且还会有着明显的疼痛感，如果出现感染，就会形成一些脓疱，而一旦转成慢性湿疹； 患者的皮损部位会出现一些脱屑，以及皮肤粗糙、皲裂等现象，并且还会伴有程度不同的瘙痒感。 三、皲裂症 皲裂症也被称为手足皲裂，指的是手部和脚部的皮肤，由于各种因素刺激，皮下汗腺的分泌会大量减少，导致皮肤过度干燥，皮肤的角质层增厚并且失去弹性的现象。 气温较低的冬季是皲裂症的高发季节，妇女和老年人群最为多见。一旦患上皲裂症，患者的手部和脚部的皮肤就会出现开裂、起皮、皮肤异常增厚现象。 四、神经性皮炎 在患有神经性皮炎之后，患者的手脚部位也会出现开裂、起皮等现象，是一种因为精神神经因素从而引起的皮肤疾病。 一旦患病，在患者的背部、颈部、腰部以及手脚、四肢、头部等各个部位，都会出现非常剧烈的瘙痒感。 而随着皮肤被患者挠抓，就会发生典型的苔藓样改变。并且会伴有皮肤增厚、过度干燥以及色素沉着和开裂等现象。 五、缺乏维生素 在进入冬季之后，有很多人不喜欢吃一些蔬菜和水果，这样一来，就会使身体内缺乏大量的维生素。 一旦体内缺乏维生素，就会引起一系列皮肤问题，比如皮肤容易开裂、脱皮等现象。 比如在缺乏维生素a的时候，皮肤组织细胞就会过度干燥，从而出现角质层增厚以及皮脂腺萎缩现象，就会引起较为明显的手脚开裂、起皮症状。 如果缺乏维生素E和维生素B族时，也会出现这种现象，并且很容易患上过敏性皮炎。 总而言之，如果进入冬季之后，手脚部位经常出现开裂、起皮现象的时候，一定要引起警惕，一般与以上5种因素有关。患者需要前往正规医院就诊，然后根据病因给予相对应治疗。 除此之外，患者必须做好手脚部位皮肤的卫生工作，每天晚上可用温热水浸泡10分钟左右并擦拭干净，涂抹温和的护手霜或护手油。 还应该适当的多补充一些水分，并且多吃一些蔬菜、水果等食物。对于手脚部位皮肤开裂脱皮现象，有着一定的改善作用。','436df90cca23d09b19a893af789a233f8b8f4cd10a4e5f99257f0bdd11b4bbb6','2026-05-05 11:42:51','2026-05-05 11:42:51'),(3,3,'三款茶饮清湿热、健脾祛湿养心神 茯苓薏莲茶 配方：茯苓、薏苡仁、莲子。 做法：材料洗净后浸泡10分钟，开火煮沸15分钟，取清汤分次代茶饮。 功效：茯苓健脾利湿，薏苡仁渗湿健脾，莲子养心安神。适用于脾虚湿重导致的困倦、食欲缺乏、烦躁。 藿佩陈皮茶 配方：藿香、佩兰、陈皮。 做法：沸水冲泡代茶饮，每日1 3杯。 功效：藿香解暑化湿，佩兰清热和中，陈皮理气健脾，适合湿热内蕴、口黏苔腻、水肿痰多者。脾胃虚寒者需减少用量。 菊花佩兰茶 配方：菊花、佩兰、绿茶。 做法：沸水冲泡10分钟后饮用。 功效：菊花疏风清热解毒，佩兰化湿解暑，绿茶辅助清热。适合暑热伤津、口苦咽干、头目昏沉者。阳虚体质者慎用。 以上三种茶饮可交替配合饮用，如茯苓薏莲茶与藿佩陈皮茶，加强祛湿效果；茯苓薏莲茶与菊花佩兰茶，解表清里，内外兼治。','bf439ea2f2e7a1b26c0292448e6b8b81ca1b515b36e73ad4c3e52bc07635a5b2','2026-05-05 11:42:51','2026-05-05 11:42:51'),(4,4,'亚健康睡眠紊乱状态是由心理、生理等多方面因素的作用导致，与生活方式、工作节奏和社会压力密切相关，好发人群为30 50岁的已婚人士、脑力劳动者、企事业及医疗卫生工作者、本科学历及中级职称者。 中医调理亚健康睡眠紊乱状态以“调和营卫、安神定志”为原则，尽早干预，以主动调节为主，联合心理疏导、食疗、足浴、耳穴、艾灸、针刺、按摩、拔罐等非药物疗法，对于持续时间较长的亚健康人群，可采用多种中医药治疗方法联合干预的方式，以改善亚健康睡眠紊乱状态，恢复正常睡眠，促进整体健康。 创造良好睡眠环境 环境舒适、光线暗淡、温度适宜、没有噪音的睡眠环境是改善睡眠的必要准备。 培养良好睡眠习惯 安排合理规律的作息时间，不在卧室及床上从事与睡眠无关的活动，无论夜间睡眠质量如何，早上按时起床，白天不卧床、不补眠，调节身体昼夜节律与自然统一。 放松训练 睡前进行简单的身体拉伸，听轻松柔和的音乐，有意识地放空思绪，将注意力放在心跳、呼吸、肢体感受上。 刺激控制 日间适当增加体育活动，睡前避免进行刺激性活动，忌饮咖啡、茶、可乐等兴奋性饮品，晚餐不宜过迟、过饱、进食刺激性或难消化食物。','b61ee6996e803757e720730d4583c6930826911de593130664691ec84b41a880','2026-05-05 11:42:51','2026-05-05 11:42:51'),(5,5,'8小时睡眠论可能是错的！盲目追求睡够8小时，或许会产生巨大压力 你是不是每天晚上都在数羊，希望能早点入睡？你是不是每天早上都在拼命按闹钟，希望能多睡一会儿？你是不是觉得每天晚上要睡足8个小时才能精神抖擞？这叫“8小时睡眠论”，听起来挺有道理的。可是，你知道吗，这个说法其实不一定对。 有些人可能需要多睡一会儿，有些人可能少睡一点就够了。这都是因为每个人的身体和习惯都不一样。有个专家叫尼克·利特尔黑尔斯，他写了本书叫《睡眠革命》，他说，“8小时只是一个平均数，不是一个标准。如果你非要硬着头皮去追求8小时的话，反而会给自己增加压力，影响你的睡眠质量。” 所以呢，我们应该听从自己的生物钟，找到适合自己的作息时间。别老担心自己是否达到了“标准”的睡眠时长。还有其他专家也说过，“8小时睡眠论”并没有什么科学依据。比如美国哈佛大学医学院教授丹尼尔·科恩就说过，“我们没有证据证明每个人都需要每天晚上连续7 8个小时的睡眠。”他觉得分段式或多项式的睡眠模式可能更适合我们。 总之呢，我们要根据自己的身体和感觉来安排自己的睡眠时间和质量，并且保持良好和规律的生活习惯。如果你觉得6小时就够了，那就别勉强自己再多躺一会儿；如果你觉得需要多休息一下，那也没什么不好意思的。记住，“8小时睡眠论”只是一个说法，并不是铁律。','9b185ef46816cafc1ab85a5d1d0de99dc911d62fa51eaaae26f41877597bfcd4','2026-05-05 11:42:51','2026-05-05 11:42:51');
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
INSERT INTO `medicine` VALUES (1,'阿莫西林胶囊','发炎、感冒','本品尚可用于治疗伤寒、伤寒带菌者及钩端螺旋体病；阿莫西林亦可与克拉霉素、兰索拉唑三联用药根除胃、十二指肠幽门螺杆菌，降低消化道溃疡复发率。','仁和','1．丙磺舒竞争性地减少本品的肾小管分泌，两者同时应用可引起阿莫西林血浓度升高、半衰期延长。\n2．氯霉素、大环内酯类、磺胺类和四环素类药物在体外干扰阿莫西林的抗菌作用，但其临床意义不明。','1. 青霉素类口服药可引起过敏性休克，有多见于青霉素或头孢菌素过敏史的患者。用药前必须详细询问药物过敏史并做青霉素皮肤试验。如发生过敏性休克，应就地抢救，予以保持气道畅通、吸氧及应用肾上腺素、糖皮质激素等治疗措施。\n2.传染性单核细胞增多症患者应用本品易发生皮疹，应避免使用。\n3.疗程较长患者应检查肝、肾功能和血常规。\n4.阿莫西林可导致采用Benedict或Fehling试剂的尿糖试验出现假阳性。\n5.下列情况应慎用：\n(1)有哮喘、枯草热等过敏性疾病史者。\n(2)老年人和肾功能严重损害时可能须调整剂量。','成人的具体使用剂量为0.5g/次，每6-8小时重复用药，24小时内服用剂量不能超过4g。儿童一日用药剂量按照患者实际体重为20-40mg/kg，重复用药间隔时长为8h/次。',1,'https://img-zzy.oss-cn-beijing.aliyuncs.com/4/383c091c980d42abb169a48557fd001e.jpg',14.00,'2022-05-02 11:46:00','2025-10-22 20:00:20'),(2,'999感冒灵颗粒','感冒药、流鼻涕、发烧','解热镇痛功效，用于因感冒引起的头痛，发热，鼻塞，流涕，咽痛等症状。','999','三九感冒灵颗粒是复方药，里面含有中西药成分，不宜和西药感冒药同服。如果两种药中含有同一种成分，就只能选择服用其中一种，以免使摄入药量加倍，增加毒性，成为重复用药 [3]  。比如，三九感冒灵颗粒和西药泰诺，都含有解热镇痛效果的“扑热息痛”成分，若是两种药一起吃，过量的“扑热息痛”会对人体肝脏造成损害。','1.忌烟，酒及辛辣，生冷，油腻食物。\n2.不宜在服药期间同时服用滋补性中成药\n3. 高血压、心脏病、肝病、肾病等慢性病严重者应在医师指导下服用。\n4.本品含对乙氨基酚，马来酸氨苯那敏，咖啡因。服用本品期间不得饮酒或含有酒精的饮料；不能同时服用与本品成分相似的其它抗感冒药；肝，肾功能不全者慎用；膀胱颈梗阻，甲状腺功能亢进，青光眼，高血压和前列腺肥大者慎用；孕妇及哺乳期妇女慎用；服药期间不得驾驶机，车，船，从事高空作业，机械作业及操作精密仪器。\n5.脾胃虚寒，症见腹痛，喜暖，泄泻者慎用。\n6.糖尿病患者、消化道溃疡患者、膀胱颈梗阻、幽门十二指肠梗阻、甲状腺机能亢进、青光眼以及前列腺肥大等患者慎用。\n7.儿童，年老体弱者应在医师指导下使用。\n8.服药3天后症状无改善，或症状加重，或出现新的严重症状如胸闷，心悸等应立即停药，并去医院就诊。\n9.对本药过敏者禁用，过敏体质者慎用。\n10. 本品性状发生改变时禁止使用。\n11.儿童必须在成人监护下使用。\n12.请将本品放在儿童不能接触的地方。\n13.如正在使用其他药品，使用本品前请咨询医师或药师.','开水冲服，一次1袋，一日3次。小儿减量或遵医嘱。',2,'https://img-zzy.oss-cn-beijing.aliyuncs.com/4/1b07af6a7fb64978bfc251c942f68828.jpg',39.80,'2022-05-02 11:50:13','2025-10-22 19:57:22'),(3,'开塞露','便秘','都是利用甘油或山梨醇的高浓度，即高渗作用，软化大便，刺激肠壁，反射性地引起排便反应，再加上其具有润滑作用，能使大便容易排出','信龙','','1.刺破或剪开后的注药导管的开口应光滑，以免擦伤肛门或直肠。\n2.对本品过敏者禁用，过敏体质者慎用。\n3.本品性状发生改变时禁止使用。\n4.请将本品放在儿童不能接触的地方。\n5.儿童必须在成人监护下使用。\n6.如正在使用其他药品，使用本品前请咨询医师或药师。','将容器顶端刺破或剪开，涂以油脂少许，缓慢插入肛门，然后将药液挤入直肠内，成人一次1支，儿童一次\n0.5支。',0,'https://img-zzy.oss-cn-beijing.aliyuncs.com/4/b0abe5faf47f432abb51a0ae4058f67c.jpg',18.00,'2022-05-02 12:52:13','2025-10-27 22:10:25'),(4,'三九胃泰颗粒','胃胀、胃痛、胃不舒服','清热祛湿，消炎止痛，理气除胀，养胃益肠。','999',NULL,'1． 服药期间，忌食辛辣，油炸，过酸食物及酒类等刺激性食品。\n2． 十五天为一疗程，初显疗效后不宜立即停药，建议再服3—4个疗程以巩固疗效。\n3．胃寒患者慎用。','开水冲服。一次1袋，一日2次。',0,'https://img-zzy.oss-cn-beijing.aliyuncs.com/smart-medicine/images/c12673c9a430497fb393967671750a1e.jpg',15.00,'2022-05-02 12:58:32','2025-12-04 17:10:49'),(5,'999皮炎平','皮肤瘙痒','用于局限性瘙痒症、神经性皮炎、接触性皮炎、脂溢性皮炎以及慢性湿疹。','999',NULL,'1.患处已破溃、化脓或有明显渗出者禁用。\n2.病毒感染者（如有疱疹、水痘）禁用。\n3.对本品成分过敏者禁用。','皮肤外用。取少量涂于患处，并轻揉片刻；一日1~2次，病情较重或慢性炎症患者，每日5-8次或遵医嘱。',0,'https://img-zzy.oss-cn-beijing.aliyuncs.com/smart-medicine/images/4e4c723716124cbb9b32fd6163aa110b.jpg',15.21,'2022-05-02 13:01:34','2025-12-05 20:03:34'),(6,'甲硝唑','牙痛','适应症为用于治疗肠道和肠外阿米巴病（如阿米巴肝脓肿、胸膜阿米巴病等）。还可用于治疗阴道滴虫病、小袋虫病和皮肤利什曼病、麦地那龙线虫感染等。目前还广泛用于厌氧菌感染的治疗','奥可安','本品能增强华法林等抗凝药物的作用。与土霉素合用可干扰甲硝唑清除阴道滴虫的作用。','有活动性中枢神经系统疾患和血液病者禁用。','成人一次两片，一日三次',0,'https://img-zzy.oss-cn-beijing.aliyuncs.com/smart-medicine/images/0f7c517628254d3480d5385cf151f4b4.jpg',28.50,'2022-05-02 13:03:27','2025-12-05 20:03:21'),(7,'布洛芬缓释胶囊','头疼、缓解痛','用于缓解轻至中度疼痛如头痛、偏头痛、牙痛、痛经、关节痛、肌肉痛、神经痛，也用于普通感冒或流行性感冒引起的发热','芬必得','.本品与其他解热、镇痛、抗炎药物同用时可增加胃肠道不良反应，并可能导致溃疡。 2.本品与肝素、双香豆素类(如华法林)等抗凝药 同用时，可导致凝血酶原时间延长，增加出血倾向。 3.本品与地高辛、甲氨蝶呤、口服降血糖药物同用 时，能使这些药物的血药浓度增高，不宜同用。 ','1.对其他非甾休抗炎药过敏者禁用。 2.孕妇及晡乳期妇女禁用。 3.对阿司匹林过敏的哮喘患者禁用。 4.严重肝肾功能不全者或严重心力衰竭者禁用。 5.正在服用其他含有布洛芬或其他非甾休抗炎药， 包括服用已知是特异性环氧化酶-2抑制剂药物的患者禁用。除非医生建议使用。 6.既往有与使用非甾体类抗炎药治疗相关的上消化道出血或穿孔史者禁用。 7.活动性或既往有消化性溃疡史，胃肠道出血或穿孔的患者禁用。','口服。成人，一次1片，一日2次（早晚各一次）。',1,'https://img-zzy.oss-cn-beijing.aliyuncs.com/smart-medicine/images/a0873da70b974a44bedbab190ea53ea4.jpg',1.00,'2022-05-02 13:10:47','2025-12-05 20:03:12');
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
INSERT INTO `pageview` VALUES (5,15,1),(6,75,13),(7,4,4),(8,6,2),(9,4,3),(10,3,5),(11,3,6),(12,5,7),(13,11,8),(14,14,9),(15,1,15),(16,1,16),(17,1,17);
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
INSERT INTO `science_video` VALUES (1,'用药安全科普','讲解常见药物使用误区','https://img-zzy.oss-cn-beijing.aliyuncs.com/smart-medicine/images/1673f7cb761a4105a0acd74378b4e389.jpg','https://img-zzy.oss-cn-beijing.aliyuncs.com/4/658bc7e0c3c649ea8fd3ceb635c4794d.mp4','2025-10-22 19:05:53','2025-12-05 20:04:52'),(2,'七步洗手法','洗干净手','https://img-zzy.oss-cn-beijing.aliyuncs.com/smart-medicine/images/850d70272dd547deb5336d00ecd832d9.png','https://img-zzy.oss-cn-beijing.aliyuncs.com/4/2c24c9b82e36416abcee9e7980229c14.mp4','2025-10-22 19:11:57','2025-12-15 03:07:13');
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
INSERT INTO `user` VALUES (1,'admin','admin','$2a$10$HJq8gJC0yrkkkDg9hyIUWO7kXeHY5qpMzUWqliRFv0vGXlzoZZWbq',23,'男','2545946621@qq.com','18155441812',1,'https://img-zzy.oss-cn-beijing.aliyuncs.com/smart-medicine/images/dcdd2705b7104c208a2c1707278f05b2.jpg','2025-10-22 19:14:29','2026-02-20 12:23:39'),(19,'11111111','456','$2a$10$hEYyH9AYFxMdhHdncztiDOVhhiI6oipDtnABUFSXif7OxXQIVPMyy',NULL,NULL,'3352103660@qq.com',NULL,0,NULL,'2026-03-11 13:43:02','2026-03-11 13:43:02');
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
