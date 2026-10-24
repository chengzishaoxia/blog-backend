-- MySQL dump 10.13  Distrib 8.0.19, for Win64 (x86_64)
--
-- Host: 150.158.48.185    Database: wing-blog
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
-- Table structure for table `blog_carousel`
--

DROP TABLE IF EXISTS `blog_carousel`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `blog_carousel` (
                                 `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '主键ID',
                                 `file_id` bigint unsigned NOT NULL COMMENT '轮播图文件ID(file_info.id)',
                                 `create_by` bigint DEFAULT NULL COMMENT '创建人ID',
                                 `update_by` bigint DEFAULT NULL COMMENT '更新人ID',
                                 `is_deleted` tinyint NOT NULL DEFAULT '0' COMMENT '逻辑删除 0-未删除 1-已删除',
                                 `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                 `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                 PRIMARY KEY (`id`),
                                 KEY `idx_file` (`file_id`),
                                 KEY `idx_del` (`is_deleted`),
                                 KEY `idx_create_time` (`create_time`)
) COMMENT='站点轮播图';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `blog_carousel`
--

LOCK TABLES `blog_carousel` WRITE;
/*!40000 ALTER TABLE `blog_carousel` DISABLE KEYS */;
INSERT INTO `blog_carousel` VALUES (1,32,1,NULL,1,'2025-10-26 23:00:48','2025-10-27 08:20:04'),(2,26,1,NULL,1,'2025-10-26 23:00:48','2025-10-27 08:20:02'),(3,25,1,NULL,1,'2025-10-26 23:00:48','2025-10-27 08:20:01'),(4,30,1,NULL,1,'2025-10-26 23:00:48','2025-10-27 08:19:59'),(5,29,1,NULL,1,'2025-10-26 23:00:48','2025-10-27 08:19:56'),(6,23,1,NULL,1,'2025-10-26 23:05:23','2025-10-27 08:19:53'),(7,51,1,NULL,1,'2025-10-27 08:20:17','2025-10-27 11:58:06'),(8,55,1,NULL,1,'2025-10-27 08:20:17','2026-01-26 11:33:05'),(9,60,1,NULL,1,'2025-10-27 08:20:17','2026-01-26 11:33:06'),(10,56,1,NULL,1,'2025-10-27 08:20:17','2025-10-27 11:58:13'),(11,59,1,NULL,1,'2025-10-27 08:20:17','2026-01-26 11:33:08'),(12,57,1,NULL,1,'2025-10-27 08:20:17','2025-10-27 11:58:15'),(13,63,1,NULL,1,'2025-10-27 11:58:22','2026-01-26 11:33:01'),(14,64,1,NULL,1,'2025-10-27 11:58:22','2026-01-26 11:33:02'),(15,62,1,NULL,1,'2025-10-27 11:58:22','2026-01-26 11:33:04'),(16,96,1,NULL,0,'2026-01-26 11:36:34','2026-01-26 11:36:34'),(17,92,1,NULL,1,'2026-01-26 11:36:34','2026-01-26 11:44:22'),(18,95,1,NULL,1,'2026-01-26 11:36:34','2026-01-26 11:44:20'),(19,94,1,NULL,1,'2026-01-26 11:36:34','2026-01-26 11:44:24'),(20,93,1,NULL,1,'2026-01-26 11:36:34','2026-01-26 11:44:26'),(21,101,1,NULL,0,'2026-01-26 11:42:41','2026-01-26 11:42:41'),(22,100,1,NULL,0,'2026-01-26 11:42:42','2026-01-26 11:42:42'),(23,103,1,NULL,0,'2026-01-26 11:46:33','2026-01-26 11:46:33'),(24,102,1,NULL,0,'2026-01-26 11:46:33','2026-01-26 11:46:33'),(25,104,1,NULL,0,'2026-01-26 11:46:33','2026-01-26 11:46:33');
/*!40000 ALTER TABLE `blog_carousel` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `blog_comment`
--

DROP TABLE IF EXISTS `blog_comment`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `blog_comment` (
                                `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '主键ID',
                                `post_id` bigint unsigned NOT NULL COMMENT '文章ID',
                                `parent_id` bigint unsigned NOT NULL DEFAULT '0' COMMENT '父评论ID(0为根评论)',
                                `user_id` bigint unsigned DEFAULT NULL COMMENT '评论用户ID',
                                `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '评论内容',
                                `like_count` bigint unsigned NOT NULL DEFAULT '0' COMMENT '点赞数',
                                `status` tinyint NOT NULL DEFAULT '1' COMMENT '状态 1-显示 0-隐藏',
                                `is_deleted` tinyint NOT NULL DEFAULT '0' COMMENT '逻辑删除 0-否 1-是',
                                `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                PRIMARY KEY (`id`),
                                KEY `idx_comment_post` (`post_id`),
                                KEY `idx_comment_parent` (`parent_id`),
                                KEY `idx_comment_user` (`user_id`),
                                KEY `idx_comment_status` (`status`)
) COMMENT='文章评论';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `blog_comment`
--

LOCK TABLES `blog_comment` WRITE;
/*!40000 ALTER TABLE `blog_comment` DISABLE KEYS */;
INSERT INTO `blog_comment` VALUES (1,1,0,1,'看到这条评论 意味着你的杜林不会歪',0,1,0,'2025-10-26 23:33:59','2025-10-26 23:33:59'),(2,2,0,1,'奈芙尔：雅珂达，你去把猎月人干掉。',0,1,0,'2025-10-26 23:34:16','2025-10-26 23:34:16'),(3,2,2,1,'顺便把天理解决了',0,1,0,'2025-10-26 23:34:35','2025-10-26 23:34:35'),(4,3,0,1,'如果可以的话本丘下次也想品鉴一下芙宁娜爱吃的甜品呀~',0,1,0,'2025-10-26 23:39:30','2025-10-26 23:39:30'),(5,4,0,1,'自由的风！也正在召唤酱酱！真是能给大家安全感的大团长姆，酱酱也要燃起来啦！（姆姆姆，不是像火酱那样燃起来！）',0,1,0,'2026-02-25 18:31:08','2026-02-25 18:31:08'),(6,4,5,1,'sb',0,1,1,'2026-03-13 19:10:54','2026-03-13 11:10:57');
/*!40000 ALTER TABLE `blog_comment` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `blog_friend_link`
--

DROP TABLE IF EXISTS `blog_friend_link`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `blog_friend_link` (
                                    `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '主键ID',
                                    `name` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '站点名称',
                                    `url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '站点地址',
                                    `avatar` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '头像地址',
                                    `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '站点描述',
                                    `sort` int NOT NULL DEFAULT '0' COMMENT '排序值(小在前)',
                                    `status` tinyint NOT NULL DEFAULT '1' COMMENT '状态 1-启用 0-停用',
                                    `is_deleted` tinyint NOT NULL DEFAULT '0' COMMENT '逻辑删除 0-否 1-是',
                                    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                    `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                    PRIMARY KEY (`id`),
                                    KEY `idx_friend_link_status` (`status`)
) COMMENT='博客友链';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `blog_friend_link`
--

LOCK TABLES `blog_friend_link` WRITE;
/*!40000 ALTER TABLE `blog_friend_link` DISABLE KEYS */;
INSERT INTO `blog_friend_link` VALUES (1,'可变羽翼文档','https://gitee.com/variable-wing-core/klee-blog','https://avatars.githubusercontent.com/u/9919?s=200&v=4','项目源码与开发记录',1,1,0,'2026-03-12 10:00:00','2026-03-12 10:00:00'),(2,'Vue 官方','https://cn.vuejs.org/','https://cn.vuejs.org/logo.svg','前端框架文档与最佳实践',2,1,0,'2026-03-12 10:00:00','2026-03-12 10:00:00'),(3,'Spring','https://spring.io/projects/spring-boot','https://spring.io/img/spring.svg','后端基础设施与生态',3,1,0,'2026-03-12 10:00:00','2026-03-12 10:00:00'),(4,'Element Plus','https://element-plus.org/zh-CN/','https://element-plus.org/images/element-plus-logo.svg','Portal 当前使用的组件库',4,1,0,'2026-03-12 10:00:00','2026-03-12 10:00:00'),(5,'Vite 官方','https://cn.vite.dev/','https://vite.dev/logo.svg','更快的前端构建工具与脚手架',5,1,0,'2026-03-12 10:00:00','2026-03-12 10:00:00'),(6,'Pinia','https://pinia.vuejs.org/zh/','https://pinia.vuejs.org/logo.svg','Vue 状态管理方案，适合 Portal 数据流',6,1,0,'2026-03-12 10:00:00','2026-03-12 10:00:00'),(7,'MyBatis-Plus','https://baomidou.com/','https://baomidou.com/assets/asset.cIbiVTt_.svg','后端持久层增强工具，项目当前使用',7,1,0,'2026-03-12 10:00:00','2026-03-12 20:48:00'),(8,'SpringDoc','https://springdoc.org/','https://spring.io/img/spring.svg','OpenAPI 文档生成与接口调试支持',8,1,0,'2026-03-12 10:00:00','2026-03-12 20:49:29');
/*!40000 ALTER TABLE `blog_friend_link` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `blog_message`
--

DROP TABLE IF EXISTS `blog_message`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `blog_message` (
                                `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '主键ID',
                                `user_id` bigint unsigned DEFAULT NULL COMMENT '留言用户ID',
                                `avatar_file_id` bigint unsigned DEFAULT NULL COMMENT '留言头像文件ID(关联 file_info.id)',
                                `nickname` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '留言展示昵称',
                                `submit_ip` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '提交IP',
                                `ip_region` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT 'IP归属地',
                                `content` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '留言内容',
                                `audit_status` tinyint NOT NULL DEFAULT '0' COMMENT '审核状态 0-待审核 1-通过 2-驳回',
                                `audit_by` bigint unsigned DEFAULT NULL COMMENT '审核人ID',
                                `audit_remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '审核备注',
                                `audit_time` datetime DEFAULT NULL COMMENT '审核时间',
                                `is_deleted` tinyint NOT NULL DEFAULT '0' COMMENT '逻辑删除 0-否 1-是',
                                `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                PRIMARY KEY (`id`),
                                KEY `idx_message_user` (`user_id`),
                                KEY `idx_message_audit_status` (`audit_status`),
                                KEY `idx_message_create_time` (`create_time`),
                                KEY `uk_message_submit_ip` (`submit_ip`) USING BTREE
) COMMENT='博客留言';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `blog_message`
--

LOCK TABLES `blog_message` WRITE;
/*!40000 ALTER TABLE `blog_message` DISABLE KEYS */;
/*!40000 ALTER TABLE `blog_message` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `blog_post`
--

DROP TABLE IF EXISTS `blog_post`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `blog_post` (
                             `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '主键ID',
                             `title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '标题',
                             `summary` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '摘要',
                             `content` mediumtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '正文内容(Markdown/HTML)',
                             `cover_image_file_id` bigint DEFAULT NULL COMMENT '封面图文件ID(关联 file_info.id)',
                             `author_id` bigint unsigned DEFAULT NULL COMMENT '作者用户ID',
                             `daily_date` date DEFAULT NULL COMMENT '日报日期(绑定到某一天)',
                             `view_count` bigint unsigned NOT NULL DEFAULT '0' COMMENT '访问量',
                             `status` tinyint NOT NULL DEFAULT '0' COMMENT '状态 0-草稿 1-已发布',
                             `pinned` tinyint NOT NULL DEFAULT '0' COMMENT '置顶 0-否 1-是',
                             `is_recommend` tinyint NOT NULL DEFAULT '0' COMMENT '推荐 0-否 1-是',
                             `is_deleted` tinyint NOT NULL DEFAULT '0' COMMENT '逻辑删除 0-否 1-是',
                             `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                             `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                             PRIMARY KEY (`id`),
                             KEY `idx_post_status` (`status`),
                             KEY `idx_post_author` (`author_id`),
                             KEY `idx_post_author_daily_date` (`author_id`,`daily_date`),
                             KEY `idx_post_create_time` (`create_time`),
                             KEY `idx_post_pinned_create` (`pinned`,`create_time`)
) COMMENT='博客文章';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `blog_post`
--

LOCK TABLES `blog_post` WRITE;
/*!40000 ALTER TABLE `blog_post` DISABLE KEYS */;
INSERT INTO `blog_post` VALUES (1,'如果在冬夜，一个旅人」「月之四」版本活动速递第二期','','<p style=\"text-align: left;\"><br></p><p style=\"text-align: left;\">旅行者好呀~一起来看看近期即将开启的活动吧！</p><p style=\"text-align: left;\">*更多详情内容可见「月之四」版本公告</p><p style=\"text-align: left;\"><img src=\"http://150.158.48.185:9000/klee/20260225/3e5821a3f86144ebba1a7fa6e048c34d.png\" alt=\"image.png\" data-href=\"http://150.158.48.185:9000/klee/20260225/3e5821a3f86144ebba1a7fa6e048c34d.png\" width=\"\" height=\"\" style=\"\"></p><p style=\"text-align: left;\"><img src=\"http://150.158.48.185:9000/klee/20260225/748cc77b50c54d9aaf64b2e757645033.png\" alt=\"image.png\" data-href=\"http://150.158.48.185:9000/klee/20260225/748cc77b50c54d9aaf64b2e757645033.png\" width=\"\" height=\"\" style=\"\"></p>',128,1,NULL,28,1,0,1,0,'2025-10-26 23:29:18','2026-03-15 09:07:21'),(2,'珊瑚宫心海生日快乐 | 一直想与你分享这个海底的「秘密基地」','','<p style=\"text-align: left;\"><br></p><p style=\"text-align: left;\"><img src=\"http://150.158.48.185:9000/klee/20260225/526501005df1450fbed0795b348e0843.png\" alt=\"image.png\" data-href=\"http://150.158.48.185:9000/klee/20260225/526501005df1450fbed0795b348e0843.png\" width=\"\" height=\"\" style=\"\"></p><p style=\"text-align: left;\">一直想与你分享这个海底的「秘密基地」，今天终于找到合适的时机邀请你前来。</p><p style=\"text-align: left;\">每当我心绪难宁的时候，只要潜入水中，看着水母翩跹、鱼群悠游，烦忧便渐渐消散了。</p><p style=\"text-align: left;\">瞧，这位朋友似乎很想带我们去什么地方，不如一起跟上它游一段吧？</p>',106,1,NULL,52,1,0,0,0,'2025-10-26 23:32:25','2026-03-15 22:55:33'),(3,'来自风的邀约——「晨风之诗」祈愿','','<p style=\"text-align: center;\"><br></p><p style=\"text-align: center;\"><img src=\"http://150.158.48.185:9000/klee/20260225/e3fc4cad3a5342ad9cca7598bc5369f6.png\" alt=\"image.png\" data-href=\"http://150.158.48.185:9000/klee/20260225/e3fc4cad3a5342ad9cca7598bc5369f6.png\" width=\"\" height=\"\" style=\"\"></p><p style=\"text-align: center;\"><span style=\"color: rgb(30, 31, 32); background-color: rgb(255, 255, 255); font-size: 16px; font-family: -apple-system, BlinkMacSystemFont, &quot;Helvetica Neue&quot;, Helvetica, &quot;Segoe UI&quot;, Arial, Roboto, &quot;PingFang SC&quot;, miui, &quot;Hiragino Sans GB&quot;, &quot;Microsoft Yahei&quot;, sans-serif;\">集录祈愿「</span><span style=\"color: rgb(26, 26, 26); background-color: rgb(255, 255, 255); font-size: 16px; font-family: -apple-system, BlinkMacSystemFont, &quot;Helvetica Neue&quot;, Helvetica, &quot;Segoe UI&quot;, Arial, Roboto, &quot;PingFang SC&quot;, miui, &quot;Hiragino Sans GB&quot;, &quot;Microsoft Yahei&quot;, sans-serif;\">晨风之诗</span><span style=\"color: rgb(30, 31, 32); background-color: rgb(255, 255, 255); font-size: 16px; font-family: -apple-system, BlinkMacSystemFont, &quot;Helvetica Neue&quot;, Helvetica, &quot;Segoe UI&quot;, Arial, Roboto, &quot;PingFang SC&quot;, miui, &quot;Hiragino Sans GB&quot;, &quot;Microsoft Yahei&quot;, sans-serif;\">」即将开启！活动期间，旅行者可以在集录祈愿中获得更多角色与武器，一起来看看吧~</span></p><p style=\"text-align: center;\"><img src=\"http://150.158.48.185:9000/klee/20260225/9716e42eafde4eaab0830b12fa9c234c.png\" alt=\"image.png\" data-href=\"http://150.158.48.185:9000/klee/20260225/9716e42eafde4eaab0830b12fa9c234c.png\" width=\"\" height=\"\" style=\"\"></p><p style=\"text-align: center;\"><img src=\"http://150.158.48.185:9000/klee/20260225/fd84e9c681f2454cb17e42e1126e8a69.png\" alt=\"image.png\" data-href=\"http://150.158.48.185:9000/klee/20260225/fd84e9c681f2454cb17e42e1126e8a69.png\" width=\"\" height=\"\" style=\"\"></p><p style=\"text-align: center;\"><img src=\"http://150.158.48.185:9000/klee/20260225/463c5fecc8574eceaade4a596753f9ff.png\" alt=\"image.png\" data-href=\"http://150.158.48.185:9000/klee/20260225/463c5fecc8574eceaade4a596753f9ff.png\" width=\"\" height=\"\" style=\"\"></p><p style=\"text-align: center;\"><img src=\"http://150.158.48.185:9000/klee/20260225/8dabc197fdf84954a6100dbe9d1060ef.png\" alt=\"image.png\" data-href=\"http://150.158.48.185:9000/klee/20260225/8dabc197fdf84954a6100dbe9d1060ef.png\" width=\"\" height=\"\" style=\"\"></p><p style=\"text-align: center;\"><img src=\"http://150.158.48.185:9000/klee/20260225/8c42e4b7c291405d960e6719a6a78214.png\" alt=\"image.png\" data-href=\"http://150.158.48.185:9000/klee/20260225/8c42e4b7c291405d960e6719a6a78214.png\" width=\"\" height=\"\" style=\"\"></p><p style=\"text-align: center;\"><img src=\"http://150.158.48.185:9000/klee/20260225/69f9f68df4f942e5892525c5e8f78c0f.png\" alt=\"image.png\" data-href=\"http://150.158.48.185:9000/klee/20260225/69f9f68df4f942e5892525c5e8f78c0f.png\" width=\"\" height=\"\" style=\"\"></p><p style=\"text-align: center;\"><img src=\"http://150.158.48.185:9000/klee/20260225/28bdfffa82394e96b12f472e12eeec83.png\" alt=\"image.png\" data-href=\"http://150.158.48.185:9000/klee/20260225/28bdfffa82394e96b12f472e12eeec83.png\" width=\"\" height=\"\" style=\"\"></p><p style=\"text-align: center;\"><img src=\"http://150.158.48.185:9000/klee/20260225/f3ecc1a1b78b49148bf8f7875ac0a18c.png\" alt=\"image.png\" data-href=\"http://150.158.48.185:9000/klee/20260225/f3ecc1a1b78b49148bf8f7875ac0a18c.png\" width=\"\" height=\"\" style=\"\"></p><p style=\"text-align: center;\">点击了解更多「月之五」版本内容：</p><p style=\"text-align: center;\"> <a href=\"https://webstatic.mihoyo.com/ys/event/e20210601blue_post/vert.html?page_sn=78a4df7f1c1c4f5d&amp;bbs_presentation_style=fullscreen&amp;utm_source=bbs&amp;utm_medium=ys&amp;utm_campaign=pgc#/\" target=\"_blank\">&gt;&gt;原神「『空月之歌·变奏』捕风的归客」内容专题页&lt;&lt;</a> </p>',105,1,NULL,49,1,0,1,0,'2025-10-26 23:37:40','2026-03-15 23:06:19'),(4,'「捕风的归客」「月之五」版本活动速递第一期','','<p style=\"text-align: left;\"><span style=\"color: rgb(0, 0, 0);\">旅行者好呀~「月之五」版本「捕风的归客」即将开启，一起来看看新版本中有哪些福利和活动吧！</span></p><p style=\"text-align: left;\"><span style=\"color: rgb(0, 0, 0);\">*更多详情内容可见「月之五」版本公告。</span></p><p style=\"text-align: left;\"><img src=\"http://150.158.48.185:9000/klee/20260225/de13471ba6284c03bd5280aebc8a6583.png\" alt=\"image.png\" data-href=\"http://150.158.48.185:9000/klee/20260225/de13471ba6284c03bd5280aebc8a6583.png\" width=\"\" height=\"\" style=\"\"></p><p style=\"text-align: left;\"><img src=\"http://150.158.48.185:9000/klee/20260225/c0d9cd9c97234cdfb8e6ec3a73ddbe6c.png\" alt=\"image.png\" data-href=\"http://150.158.48.185:9000/klee/20260225/c0d9cd9c97234cdfb8e6ec3a73ddbe6c.png\" width=\"\" height=\"\" style=\"\"></p><p style=\"text-align: left;\"><img src=\"http://150.158.48.185:9000/klee/20260225/61e2ee351b2c49c4b381b378e1e2e9d5.png\" alt=\"image.png\" data-href=\"http://150.158.48.185:9000/klee/20260225/61e2ee351b2c49c4b381b378e1e2e9d5.png\" width=\"\" height=\"\" style=\"\"></p><p style=\"text-align: left;\"><img src=\"http://150.158.48.185:9000/klee/20260225/686467a4d5014d31bc7fa58e89f9b937.png\" alt=\"image.png\" data-href=\"http://150.158.48.185:9000/klee/20260225/686467a4d5014d31bc7fa58e89f9b937.png\" width=\"\" height=\"\" style=\"\"></p><p style=\"text-align: left;\"><img src=\"http://150.158.48.185:9000/klee/20260225/4aca1d287eb1438582006d1f00f83510.png\" alt=\"image.png\" data-href=\"http://150.158.48.185:9000/klee/20260225/4aca1d287eb1438582006d1f00f83510.png\" width=\"\" height=\"\" style=\"\"></p><p style=\"text-align: left;\"><img src=\"http://150.158.48.185:9000/klee/20260225/6efedf61d26a418c833c2f826407654c.png\" alt=\"image.png\" data-href=\"http://150.158.48.185:9000/klee/20260225/6efedf61d26a418c833c2f826407654c.png\" width=\"\" height=\"\" style=\"\"></p><p style=\"text-align: left;\"><img src=\"http://150.158.48.185:9000/klee/20260225/9af113dc628342db9a9db6ba331afd77.png\" alt=\"image.png\" data-href=\"http://150.158.48.185:9000/klee/20260225/9af113dc628342db9a9db6ba331afd77.png\" width=\"\" height=\"\" style=\"\"></p><p style=\"text-align: left;\"><img src=\"http://150.158.48.185:9000/klee/20260225/c35a7c9648dc4981a089a7da668bdcab.png\" alt=\"image.png\" data-href=\"http://150.158.48.185:9000/klee/20260225/c35a7c9648dc4981a089a7da668bdcab.png\" width=\"\" height=\"\" style=\"\"></p>',117,1,NULL,42,1,0,1,0,'2026-02-25 18:26:37','2026-03-15 23:06:36'),(5,'「捕风的归客」「月之五」版本活动速递第二期','急急急，我要抽卡','<p style=\"text-align: left;\">旅行者好呀~一起来看看近期都有哪些活动吧！</p><p style=\"text-align: left;\">区域探索限时奖励活动还在进行中哦，还没有完成「荒崖与墟壑的诗篇」探索任务的旅行者千万不要错过。</p><p style=\"text-align: left;\"><img src=\"http://150.158.48.185:9000/klee/20260315/2f9c45a541c84147b4fa58a339f38bc4.png\" alt=\"image.png\" data-href=\"http://150.158.48.185:9000/klee/20260315/2f9c45a541c84147b4fa58a339f38bc4.png\" width=\"\" height=\"\" style=\"\"></p><p style=\"text-align: left;\"><img src=\"http://150.158.48.185:9000/klee/20260315/fce63e45ed754089b928596195d7a8fd.png\" alt=\"image.png\" data-href=\"http://150.158.48.185:9000/klee/20260315/fce63e45ed754089b928596195d7a8fd.png\" width=\"\" height=\"\" style=\"\"></p><p style=\"text-align: left;\"><img src=\"http://150.158.48.185:9000/klee/20260315/74bf13fa9e8348399178b0154762c3a0.png\" alt=\"image.png\" data-href=\"http://150.158.48.185:9000/klee/20260315/74bf13fa9e8348399178b0154762c3a0.png\" width=\"\" height=\"\" style=\"\"></p><p style=\"text-align: left;\"><img src=\"http://150.158.48.185:9000/klee/20260315/345d267e7435414590a0f25bd16ff0c2.png\" alt=\"image.png\" data-href=\"http://150.158.48.185:9000/klee/20260315/345d267e7435414590a0f25bd16ff0c2.png\" width=\"\" height=\"\" style=\"\"></p><p style=\"text-align: center;\">点击了解更多「月之五」版本内容：</p><p style=\"text-align: center;\"><a href=\"https://webstatic.mihoyo.com/ys/event/e20210601blue_post/vert.html?page_sn=78a4df7f1c1c4f5d&amp;bbs_presentation_style=fullscreen&amp;utm_source=bbs&amp;utm_medium=ys&amp;utm_campaign=pgc\" target=\"_blank\">&gt;&gt;原神「『空月之歌·变奏』捕风的归客」内容专题页&lt;&lt;</a></p>',143,1,NULL,2,1,1,1,0,'2026-03-15 23:05:54','2026-03-15 16:39:31'),(6,'「虚星临渡」祈愿：「虚渊暗星·丝柯克(冰)」概率UP！','急急急，我要抽卡','<p style=\"text-align: left;\"><span style=\"color: rgb(51, 51, 51);\">活动期间，旅行者可以在活动祈愿中获得更多角色与武器，组建强大的队伍！</span></p><p style=\"text-align: left;\"><span style=\"color: rgb(51, 51, 51);\"> </span></p><p style=\"text-align: left;\"><span style=\"color: rgb(51, 51, 51);\">〓祈愿介绍〓</span></p><p style=\"text-align: left;\"><img src=\"http://150.158.48.185:9000/klee/20260315/18badbd5c732447d83fba727d3cdd01c.png\" alt=\"image.png\" data-href=\"http://150.158.48.185:9000/klee/20260315/18badbd5c732447d83fba727d3cdd01c.png\" width=\"\" height=\"\" style=\"\"></p><p style=\"text-align: left;\"><span style=\"color: rgb(51, 51, 51);\">●活动期间，</span><span style=\"color: rgb(194, 84, 99);\">限定</span><span style=\"color: rgb(51, 51, 51);\">5星角色</span><span style=\"color: rgb(173, 114, 14);\">「虚渊暗星·丝柯克(冰)」</span><span style=\"color: rgb(51, 51, 51);\">的祈愿获取概率将</span><span style=\"color: rgb(194, 84, 99);\">大幅提升</span><span style=\"color: rgb(51, 51, 51);\">！</span></p><p style=\"text-align: left;\"><span style=\"color: rgb(51, 51, 51);\">●活动期间，4星角色</span><span style=\"color: rgb(173, 114, 14);\">「颂礼祝祭·塔利雅(水)」「浮金的誓愿·坎蒂丝(水)」「朗镜索真·夏洛蒂(冰)」</span><span style=\"color: rgb(51, 51, 51);\">的祈愿获取概率将</span><span style=\"color: rgb(194, 84, 99);\">大幅提升</span><span style=\"color: rgb(51, 51, 51);\">！</span></p><p style=\"text-align: left;\"><span style=\"color: rgb(194, 84, 99);\">※ 以上角色中，限定角色不会进入「奔行世间」常驻祈愿。</span></p><p style=\"text-align: left;\"><span style=\"color: rgb(194, 84, 99);\">※ 本祈愿属于「角色活动祈愿」，「角色活动祈愿」和「角色活动祈愿-2」的祈愿次数保底完全共享，会一直共同累计在「角色活动祈愿」和「角色活动祈愿-2」中，与其他祈愿的祈愿次数保底相互独立计算，互不影响。</span></p><p style=\"text-align: left;\"><span style=\"color: rgb(51, 51, 51);\">※ 祈愿开启期间，还将开启相应的「且试身手」角色试用活动，旅行者可以使用包含试用角色的固定阵容进入指定的关卡进行体验，挑战成功后即可获得对应奖励！</span></p><p style=\"text-align: left;\"><br></p><p style=\"text-align: left;\"><span style=\"color: rgb(51, 51, 51);\">※ 更多祈愿信息可点击祈愿界面左下角【详情】按钮进行查询。</span></p>',148,1,NULL,5,1,0,1,0,'2026-03-15 23:08:21','2026-03-15 17:10:08');
/*!40000 ALTER TABLE `blog_post` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `blog_post_tag`
--

DROP TABLE IF EXISTS `blog_post_tag`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `blog_post_tag` (
                                 `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '主键ID',
                                 `post_id` bigint unsigned NOT NULL COMMENT '文章ID',
                                 `tag_id` bigint unsigned NOT NULL COMMENT '标签ID',
                                 `is_deleted` tinyint NOT NULL DEFAULT '0' COMMENT '逻辑删除 0-否 1-是',
                                 `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                 `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                 PRIMARY KEY (`id`),
                                 KEY `idx_post_tag_post` (`post_id`),
                                 KEY `idx_post_tag_tag` (`tag_id`)
) COMMENT='文章-标签关联';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `blog_post_tag`
--

LOCK TABLES `blog_post_tag` WRITE;
/*!40000 ALTER TABLE `blog_post_tag` DISABLE KEYS */;
INSERT INTO `blog_post_tag` VALUES (1,1,1,0,'2025-10-26 23:29:18','2025-10-26 23:29:18'),(2,2,1,0,'2025-10-26 23:32:25','2025-10-26 23:32:25'),(3,3,1,0,'2025-10-26 23:37:40','2025-10-26 23:37:40'),(4,4,1,0,'2026-02-25 18:26:37','2026-02-25 18:26:37'),(5,4,2,0,'2026-02-25 18:26:37','2026-02-25 18:26:37'),(6,5,1,0,'2026-03-15 23:05:54','2026-03-15 23:05:54'),(7,6,1,0,'2026-03-15 23:08:21','2026-03-15 23:08:21');
/*!40000 ALTER TABLE `blog_post_tag` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `blog_tag`
--

DROP TABLE IF EXISTS `blog_tag`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `blog_tag` (
                            `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '主键ID',
                            `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '标签名称',
                            `sort` int NOT NULL DEFAULT '0' COMMENT '排序值(小在前)',
                            `status` tinyint NOT NULL DEFAULT '1' COMMENT '状态 1-启用 0-停用',
                            `is_deleted` tinyint NOT NULL DEFAULT '0' COMMENT '逻辑删除 0-否 1-是',
                            `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                            `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                            PRIMARY KEY (`id`),
                            UNIQUE KEY `uk_tag_name` (`name`),
                            KEY `idx_tag_status` (`status`)
) COMMENT='博客标签';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `blog_tag`
--

LOCK TABLES `blog_tag` WRITE;
/*!40000 ALTER TABLE `blog_tag` DISABLE KEYS */;
INSERT INTO `blog_tag` VALUES (1,'原神',0,1,0,'2025-10-26 23:27:23','2025-10-26 23:27:23'),(2,'STEAM',0,1,0,'2025-10-26 23:27:30','2025-10-26 23:27:30');
/*!40000 ALTER TABLE `blog_tag` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `chat_emoji`
--

DROP TABLE IF EXISTS `chat_emoji`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `chat_emoji` (
                              `id` bigint NOT NULL AUTO_INCREMENT,
                              `user_id` bigint DEFAULT NULL COMMENT '所属用户ID null=全局',
                              `file_id` bigint NOT NULL COMMENT '文件记录ID',
                              `width` int DEFAULT NULL COMMENT '宽',
                              `height` int DEFAULT NULL COMMENT '高',
                              `md5` varchar(32) DEFAULT NULL COMMENT '文件内容MD5',
                              `is_deleted` tinyint(1) DEFAULT '0' COMMENT '逻辑删除 0=否 1=是',
                              `create_time` datetime DEFAULT NULL,
                              `update_time` datetime DEFAULT NULL,
                              PRIMARY KEY (`id`),
                              UNIQUE KEY `uk_user_md5` (`user_id`,`md5`),
                              KEY `idx_user` (`user_id`),
                              KEY `idx_del` (`is_deleted`)
) COMMENT='聊天表情';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `chat_emoji`
--

LOCK TABLES `chat_emoji` WRITE;
/*!40000 ALTER TABLE `chat_emoji` DISABLE KEYS */;
/*!40000 ALTER TABLE `chat_emoji` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `chat_history`
--

DROP TABLE IF EXISTS `chat_history`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `chat_history` (
                                `id` bigint NOT NULL AUTO_INCREMENT,
                                `type` varchar(20) NOT NULL COMMENT '消息类型',
                                `from_user_id` bigint DEFAULT NULL COMMENT '发送者用户ID',
                                `from_username` varchar(64) DEFAULT NULL COMMENT '发送者用户名',
                                `from_nickname` varchar(64) DEFAULT NULL COMMENT '发送者昵称',
                                `from_avatar` varchar(255) DEFAULT NULL COMMENT '发送者头像URL',
                                `to_user_id` bigint DEFAULT NULL COMMENT '接收者用户ID(空=广播)',
                                `content` text COMMENT '消息内容',
                                `error` varchar(100) DEFAULT NULL COMMENT '错误描述(type=ERROR)',
                                `is_private` tinyint(1) DEFAULT NULL COMMENT '是否私聊 0/1',
                                `send_timestamp` bigint DEFAULT NULL COMMENT '发送时间戳(ms)',
                                `create_time` datetime DEFAULT NULL,
                                `update_time` datetime DEFAULT NULL,
                                PRIMARY KEY (`id`),
                                KEY `idx_from_user` (`from_user_id`),
                                KEY `idx_to_user` (`to_user_id`),
                                KEY `idx_timestamp` (`send_timestamp`),
                                KEY `idx_type` (`type`)
) COMMENT='聊天历史';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `chat_history`
--

LOCK TABLES `chat_history` WRITE;
/*!40000 ALTER TABLE `chat_history` DISABLE KEYS */;
/*!40000 ALTER TABLE `chat_history` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `file_info`
--

DROP TABLE IF EXISTS `file_info`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `file_info` (
                             `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
                             `name` varchar(255) DEFAULT NULL COMMENT '原始文件名称',
                             `url` varchar(1024) DEFAULT NULL COMMENT '文件访问URL',
                             `usage_type` varchar(50) DEFAULT NULL COMMENT '文件用途类型',
                             `size` bigint DEFAULT NULL COMMENT '文件大小(字节)',
                             `content_type` varchar(100) DEFAULT NULL COMMENT '内容类型',
                             `uploader_user_id` bigint DEFAULT NULL COMMENT '上传者用户ID',
                             `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                             `update_time` datetime DEFAULT NULL COMMENT '更新时间',
                             `is_deleted` tinyint DEFAULT '0' COMMENT '删除标识',
                             PRIMARY KEY (`id`)
) COMMENT='文件信息记录表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `file_info`
--

LOCK TABLES `file_info` WRITE;
/*!40000 ALTER TABLE `file_info` DISABLE KEYS */;
INSERT INTO `file_info` VALUES (91,'09.png','http://150.158.48.185:9000/klee/20260126/2be447499de44b4abd843acdbaaad0a7.png','AVATAR',1572658,'image/png',1,'2026-01-26 11:33:56','2026-01-26 11:33:56',0),(92,'05.png','http://150.158.48.185:9000/klee/20260126/d206668e975d4a119b0171e63b55af8c.png','CAROUSEL',1420816,'image/png',1,'2026-01-26 11:34:12','2026-01-26 11:34:12',0),(93,'12.png','http://150.158.48.185:9000/klee/20260126/8318fd9d21934682b26881c7fc335bd4.png','AVATAR',1292984,'image/png',1,'2026-01-26 11:34:28','2026-01-26 11:34:28',0),(94,'16.png','http://150.158.48.185:9000/klee/20260126/c4f3bf58ae6d47668ec14b574850b376.png','CAROUSEL',3529109,'image/png',1,'2026-01-26 11:34:59','2026-01-26 11:34:59',0),(95,'03.png','http://150.158.48.185:9000/klee/20260126/a292de376ee24fc9b0f3345353cea562.png','CAROUSEL',2225645,'image/png',1,'2026-01-26 11:35:26','2026-01-26 11:35:26',0),(96,'37.jpg','http://150.158.48.185:9000/klee/20260126/f656203ce1e34c4fb98d8e0d9d4048a6.jpg','CAROUSEL',246081,'image/jpeg',1,'2026-01-26 11:35:43','2026-01-26 11:35:43',0),(97,'绮良良.png','http://150.158.48.185:9000/klee/20260126/2f18cbf5524a43a4b63ce85436481b69.png','AVATAR',783520,'image/png',1,'2026-01-26 11:37:59','2026-01-26 11:37:59',0),(98,'茜特菈莉.png','http://150.158.48.185:9000/klee/20260126/6d76dd9acb4e4b74b49c55de7a8e7ba3.png','AVATAR',1353471,'image/png',1,'2026-01-26 11:38:44','2026-01-26 11:38:44',0),(99,'迪奥娜.png','http://150.158.48.185:9000/klee/20260126/b5f37415127a4f758cdd0dca58755141.png','AVATAR',1285194,'image/png',1,'2026-01-26 11:39:29','2026-01-26 11:39:29',0),(100,'哥伦比娅.png','http://150.158.48.185:9000/klee/20260126/80429cafae47441fab971e7a627771a4.png','CAROUSEL',3416887,'image/png',1,'2026-01-26 11:40:43','2026-01-26 11:40:43',0),(101,'芙宁娜.png','http://150.158.48.185:9000/klee/20260126/2fa1d9427f2340c48c2a8e775197c0de.png','CAROUSEL',4348064,'image/png',1,'2026-01-26 11:41:37','2026-01-26 11:41:37',0),(102,'哥伦比娅立绘.png','http://150.158.48.185:9000/klee/20260126/13afe865395d4367a9f15d37a463b42e.png','CAROUSEL',3423720,'image/png',1,'2026-01-26 11:44:52','2026-01-26 11:44:52',0),(103,'希格雯.png','http://150.158.48.185:9000/klee/20260126/868f51968f05422db226b8652b221eb7.png','CAROUSEL',421843,'image/png',1,'2026-01-26 11:45:49','2026-01-26 11:45:49',0),(104,'茜特菈莉网页活动.png','http://150.158.48.185:9000/klee/20260126/5ec83b1d6e904bc190613d8fb1f1321b.png','CAROUSEL',969724,'image/png',1,'2026-01-26 11:46:07','2026-01-26 11:46:07',0),(105,'image (8).png','http://150.158.48.185:9000/klee/20260126/f0d1571ba87a4617b6a0675f10e09a0b.png','BLOG_COVER',3203583,'image/png',1,'2026-01-26 11:48:00','2026-01-26 11:48:00',0),(106,'image (9).png','http://150.158.48.185:9000/klee/20260126/53e4a8534a684061ac4c4c4b5dd728f2.png','BLOG_COVER',522276,'image/png',1,'2026-01-26 11:49:16','2026-01-26 11:49:16',0),(107,'image (10).png','http://150.158.48.185:9000/klee/20260126/78931e4b9ecf47cb83f8cdd53a3fed19.png','BLOG_COVER',4038463,'image/png',1,'2026-01-26 11:50:38','2026-01-26 11:50:38',0),(108,'image.png','http://150.158.48.185:9000/klee/20260225/fef2f65331024a4bb0029c5e0b67eee4.png','OTHER',378173,'image/png',1,'2026-02-25 18:24:16','2026-02-25 18:24:16',0),(109,'image.png','http://150.158.48.185:9000/klee/20260225/de13471ba6284c03bd5280aebc8a6583.png','OTHER',463425,'image/png',1,'2026-02-25 18:24:43','2026-02-25 18:24:43',0),(110,'image.png','http://150.158.48.185:9000/klee/20260225/c0d9cd9c97234cdfb8e6ec3a73ddbe6c.png','OTHER',1394037,'image/png',1,'2026-02-25 18:24:53','2026-02-25 18:24:53',0),(111,'image.png','http://150.158.48.185:9000/klee/20260225/61e2ee351b2c49c4b381b378e1e2e9d5.png','OTHER',877497,'image/png',1,'2026-02-25 18:25:01','2026-02-25 18:25:01',0),(112,'image.png','http://150.158.48.185:9000/klee/20260225/686467a4d5014d31bc7fa58e89f9b937.png','OTHER',1526649,'image/png',1,'2026-02-25 18:25:14','2026-02-25 18:25:14',0),(113,'image.png','http://150.158.48.185:9000/klee/20260225/4aca1d287eb1438582006d1f00f83510.png','OTHER',1237185,'image/png',1,'2026-02-25 18:25:28','2026-02-25 18:25:28',0),(114,'image.png','http://150.158.48.185:9000/klee/20260225/6efedf61d26a418c833c2f826407654c.png','OTHER',1639599,'image/png',1,'2026-02-25 18:25:39','2026-02-25 18:25:39',0),(115,'image.png','http://150.158.48.185:9000/klee/20260225/9af113dc628342db9a9db6ba331afd77.png','OTHER',1088936,'image/png',1,'2026-02-25 18:25:48','2026-02-25 18:25:48',0),(116,'image.png','http://150.158.48.185:9000/klee/20260225/c35a7c9648dc4981a089a7da668bdcab.png','OTHER',1286641,'image/png',1,'2026-02-25 18:25:58','2026-02-25 18:25:58',0),(117,'image (11).png','http://150.158.48.185:9000/klee/20260225/61d957bd485a4ae29e07858ae6e24f03.png','BLOG_COVER',230210,'image/png',1,'2026-02-25 18:26:31','2026-02-25 18:26:31',0),(118,'image.png','http://150.158.48.185:9000/klee/20260225/e3fc4cad3a5342ad9cca7598bc5369f6.png','OTHER',418089,'image/png',1,'2026-02-25 18:32:20','2026-02-25 18:32:20',0),(119,'image.png','http://150.158.48.185:9000/klee/20260225/9716e42eafde4eaab0830b12fa9c234c.png','OTHER',1505126,'image/png',1,'2026-02-25 18:32:37','2026-02-25 18:32:37',0),(120,'image.png','http://150.158.48.185:9000/klee/20260225/fd84e9c681f2454cb17e42e1126e8a69.png','OTHER',1098239,'image/png',1,'2026-02-25 18:33:12','2026-02-25 18:33:12',0),(121,'image.png','http://150.158.48.185:9000/klee/20260225/463c5fecc8574eceaade4a596753f9ff.png','OTHER',1048776,'image/png',1,'2026-02-25 18:33:24','2026-02-25 18:33:24',0),(122,'image.png','http://150.158.48.185:9000/klee/20260225/8dabc197fdf84954a6100dbe9d1060ef.png','OTHER',1123746,'image/png',1,'2026-02-25 18:33:32','2026-02-25 18:33:32',0),(123,'image.png','http://150.158.48.185:9000/klee/20260225/8c42e4b7c291405d960e6719a6a78214.png','OTHER',1078803,'image/png',1,'2026-02-25 18:33:41','2026-02-25 18:33:41',0),(124,'image.png','http://150.158.48.185:9000/klee/20260225/69f9f68df4f942e5892525c5e8f78c0f.png','OTHER',1058134,'image/png',1,'2026-02-25 18:33:49','2026-02-25 18:33:49',0),(125,'image.png','http://150.158.48.185:9000/klee/20260225/28bdfffa82394e96b12f472e12eeec83.png','OTHER',1137644,'image/png',1,'2026-02-25 18:33:58','2026-02-25 18:33:58',0),(126,'image.png','http://150.158.48.185:9000/klee/20260225/f3ecc1a1b78b49148bf8f7875ac0a18c.png','OTHER',8125,'image/png',1,'2026-02-25 18:34:05','2026-02-25 18:34:05',0),(127,'image.png','http://150.158.48.185:9000/klee/20260225/526501005df1450fbed0795b348e0843.png','OTHER',728431,'image/png',1,'2026-02-25 18:37:55','2026-02-25 18:37:55',0),(128,'月之四.png','http://150.158.48.185:9000/klee/20260225/e197745814ef423f90418448bc818cdc.png','BLOG_COVER',395768,'image/png',1,'2026-02-25 18:40:15','2026-02-25 18:40:15',0),(129,'image.png','http://150.158.48.185:9000/klee/20260225/3e5821a3f86144ebba1a7fa6e048c34d.png','OTHER',561491,'image/png',1,'2026-02-25 18:40:35','2026-02-25 18:40:35',0),(130,'image.png','http://150.158.48.185:9000/klee/20260225/748cc77b50c54d9aaf64b2e757645033.png','OTHER',4381947,'image/png',1,'2026-02-25 18:40:53','2026-02-25 18:40:53',0),(131,'image (3).png','http://150.158.48.185:9000/klee/20260225/d53f3b9a026542c281d0e80fab49a066.png','EMOJI',11446405,'image/png',1,'2026-02-25 18:51:49','2026-02-25 18:51:49',0),(132,'image (2).png','http://150.158.48.185:9000/klee/20260312/412eebcbbadd4669ba3fed9c15cf38f0.png','OTHER',1285194,'image/png',1,'2026-03-12 18:11:54','2026-03-12 18:11:54',0),(133,'image.png','http://150.158.48.185:9000/klee/20260312/1d332c55edeb4c8d8275e36d0ec7123a.png','OTHER',783520,'image/png',1,'2026-03-12 18:12:04','2026-03-12 18:12:04',0),(134,'image (1).png','http://150.158.48.185:9000/klee/20260312/703b92cf1ce44e23bece025005e44c0e.png','OTHER',1353471,'image/png',1,'2026-03-12 18:12:17','2026-03-12 18:12:17',0),(135,'image (8).png','http://150.158.48.185:9000/klee/20260312/7560059e0bc14c9a9a22c833132fd7cf.png','OTHER',3203583,'image/png',1,'2026-03-12 18:12:43','2026-03-12 18:12:43',0),(136,'Image_1704377464371.jpg','http://150.158.48.185:9000/klee/20260312/3f856c172f5e4a9db9fb20eed7f0c5a3.jpg','OTHER',157628,'image/jpeg',1,'2026-03-12 23:14:06','2026-03-12 23:14:06',1),(137,'Image_1704377464371 (1).jpg','http://150.158.48.185:9000/klee/20260312/3548ad1fe03f4eff952006454c7a0adc.jpg','OTHER',157628,'image/jpeg',1,'2026-03-12 23:30:47','2026-03-12 23:30:47',1),(138,'1.jpg','http://150.158.48.185:9000/klee/20260312/68bacb61d7ac489f888ddecddd29d293.jpg','OTHER',230627,'image/jpeg',1,'2026-03-12 23:31:49','2026-03-12 23:31:49',1),(139,'image (6).png','http://150.158.48.185:9000/klee/20260313/63a6f42f646649ffac0006f8c2bf84ed.png','OTHER',421843,'image/png',1,'2026-03-13 14:44:30','2026-03-13 14:44:30',0),(140,'21.jpg','http://150.158.48.185:9000/klee/20260313/bd3bbad2fac6475db7c2168229472fd9.jpg','OTHER',244120,'image/jpeg',1,'2026-03-13 21:06:38','2026-03-13 21:06:38',0),(141,'01.PNG','http://150.158.48.185:9000/klee/20260315/387a2062964948e9b22660619f7dd13a.PNG','OTHER',1291204,'image/png',1,'2026-03-15 15:42:15','2026-03-15 15:42:15',0),(142,'05.png','http://150.158.48.185:9000/klee/20260315/24993ba5ad844eacbddf608b5bafc41f.png','OTHER',1420816,'image/png',1,'2026-03-15 15:43:25','2026-03-15 15:43:25',0),(143,'月之五.png','http://150.158.48.185:9000/klee/20260315/58b4173feefe40119c74bceba6f17142.png','BLOG_COVER',422984,'image/png',1,'2026-03-15 23:04:20','2026-03-15 23:04:20',0),(144,'image.png','http://150.158.48.185:9000/klee/20260315/2f9c45a541c84147b4fa58a339f38bc4.png','OTHER',2004683,'image/png',1,'2026-03-15 23:04:36','2026-03-15 23:04:36',0),(145,'image.png','http://150.158.48.185:9000/klee/20260315/fce63e45ed754089b928596195d7a8fd.png','OTHER',2044902,'image/png',1,'2026-03-15 23:04:49','2026-03-15 23:04:49',0),(146,'image.png','http://150.158.48.185:9000/klee/20260315/74bf13fa9e8348399178b0154762c3a0.png','OTHER',1738970,'image/png',1,'2026-03-15 23:05:01','2026-03-15 23:05:01',0),(147,'image.png','http://150.158.48.185:9000/klee/20260315/345d267e7435414590a0f25bd16ff0c2.png','OTHER',8125,'image/png',1,'2026-03-15 23:05:24','2026-03-15 23:05:24',0),(148,'丝柯克卡池.jpg','http://150.158.48.185:9000/klee/20260315/0f557405d7f34a329d377048d1b06f49.jpg','BLOG_COVER',82389,'image/jpeg',1,'2026-03-15 23:07:57','2026-03-15 23:07:57',0),(149,'image.png','http://150.158.48.185:9000/klee/20260315/18badbd5c732447d83fba727d3cdd01c.png','OTHER',84704,'image/png',1,'2026-03-15 23:08:10','2026-03-15 23:08:10',0);
/*!40000 ALTER TABLE `file_info` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `gen_config`
--

DROP TABLE IF EXISTS `gen_config`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `gen_config` (
                              `id` bigint NOT NULL AUTO_INCREMENT,
                              `table_name` varchar(100) NOT NULL COMMENT '表名',
                              `module_name` varchar(100) DEFAULT NULL COMMENT '模块名',
                              `package_name` varchar(255) NOT NULL COMMENT '包名',
                              `business_name` varchar(100) NOT NULL COMMENT '业务名',
                              `entity_name` varchar(100) NOT NULL COMMENT '实体类名',
                              `author` varchar(50) NOT NULL COMMENT '作者',
                              `parent_menu_id` bigint DEFAULT NULL COMMENT '上级菜单ID，对应sys_menu的id ',
                              `remove_table_prefix` varchar(20) DEFAULT NULL COMMENT '要移除的表前缀，如: sys_',
                              `page_type` varchar(20) DEFAULT NULL COMMENT '页面类型(classic|curd)',
                              `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                              `update_time` datetime DEFAULT NULL COMMENT '更新时间',
                              `is_deleted` tinyint DEFAULT '0' COMMENT '是否删除',
                              PRIMARY KEY (`id`),
                              UNIQUE KEY `uk_tablename` (`table_name`)
) COMMENT='代码生成基础配置表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `gen_config`
--

LOCK TABLES `gen_config` WRITE;
/*!40000 ALTER TABLE `gen_config` DISABLE KEYS */;
INSERT INTO `gen_config` VALUES (1,'chat_emoji','system','com.klee','聊天情','ChatEmoji','youlaitech',NULL,NULL,NULL,'2025-10-17 18:25:46','2025-10-17 18:25:46',0);
/*!40000 ALTER TABLE `gen_config` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `gen_field_config`
--

DROP TABLE IF EXISTS `gen_field_config`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `gen_field_config` (
                                    `id` bigint NOT NULL AUTO_INCREMENT,
                                    `config_id` bigint NOT NULL COMMENT '关联的配置ID',
                                    `column_name` varchar(100) DEFAULT NULL,
                                    `column_type` varchar(50) DEFAULT NULL,
                                    `column_length` int DEFAULT NULL,
                                    `field_name` varchar(100) NOT NULL COMMENT '字段名称',
                                    `field_type` varchar(100) DEFAULT NULL COMMENT '字段类型',
                                    `field_sort` int DEFAULT NULL COMMENT '字段排序',
                                    `field_comment` varchar(255) DEFAULT NULL COMMENT '字段描述',
                                    `max_length` int DEFAULT NULL,
                                    `is_required` tinyint(1) DEFAULT NULL COMMENT '是否必填',
                                    `is_show_in_list` tinyint(1) DEFAULT '0' COMMENT '是否在列表显示',
                                    `is_show_in_form` tinyint(1) DEFAULT '0' COMMENT '是否在表单显示',
                                    `is_show_in_query` tinyint(1) DEFAULT '0' COMMENT '是否在查询条件显示',
                                    `query_type` tinyint DEFAULT NULL COMMENT '查询方式',
                                    `form_type` tinyint DEFAULT NULL COMMENT '表单类型',
                                    `dict_type` varchar(50) DEFAULT NULL COMMENT '字典类型',
                                    `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                                    `update_time` datetime DEFAULT NULL COMMENT '更新时间',
                                    PRIMARY KEY (`id`),
                                    KEY `config_id` (`config_id`)
) COMMENT='代码生成字段配置表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `gen_field_config`
--

LOCK TABLES `gen_field_config` WRITE;
/*!40000 ALTER TABLE `gen_field_config` DISABLE KEYS */;
INSERT INTO `gen_field_config` VALUES (1,1,'id','bigint',NULL,'id','Long',1,'',NULL,1,1,1,0,1,1,NULL,'2025-10-17 18:25:47','2025-10-17 18:25:47'),(2,1,'user_id','bigint',NULL,'userId','Long',2,'所属用户ID null=全局',NULL,0,1,1,0,1,1,NULL,'2025-10-17 18:25:47','2025-10-17 18:25:47'),(3,1,'image_url','varchar',NULL,'imageUrl','String',3,'图片URL',255,1,1,1,0,1,1,NULL,'2025-10-17 18:25:47','2025-10-17 18:25:47'),(4,1,'width','int',NULL,'width','Integer',4,'宽',NULL,0,1,1,0,1,1,NULL,'2025-10-17 18:25:47','2025-10-17 18:25:47'),(5,1,'height','int',NULL,'height','Integer',5,'高',NULL,0,1,1,0,1,1,NULL,'2025-10-17 18:25:47','2025-10-17 18:25:47'),(6,1,'md5','varchar',NULL,'md5','String',6,'文件内容MD5',32,0,1,1,0,1,1,NULL,'2025-10-17 18:25:47','2025-10-17 18:25:47'),(7,1,'is_deleted','tinyint',NULL,'isDeleted','Integer',7,'逻辑删除 0=否 1=是',NULL,0,1,1,0,1,1,NULL,'2025-10-17 18:25:47','2025-10-17 18:25:47'),(8,1,'create_time','datetime',NULL,'createTime','LocalDateTime',8,'',NULL,0,1,1,0,1,9,NULL,'2025-10-17 18:25:47','2025-10-17 18:25:47'),(9,1,'update_time','datetime',NULL,'updateTime','LocalDateTime',9,'',NULL,0,1,1,0,1,9,NULL,'2025-10-17 18:25:47','2025-10-17 18:25:47');
/*!40000 ALTER TABLE `gen_field_config` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_config`
--

DROP TABLE IF EXISTS `sys_config`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_config` (
                              `id` bigint NOT NULL AUTO_INCREMENT,
                              `config_name` varchar(50) NOT NULL COMMENT '配置名称',
                              `config_key` varchar(50) NOT NULL COMMENT '配置key',
                              `config_value` varchar(100) NOT NULL COMMENT '配置值',
                              `remark` varchar(255) DEFAULT NULL COMMENT '备注',
                              `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                              `create_by` bigint DEFAULT NULL COMMENT '创建人ID',
                              `update_time` datetime DEFAULT NULL COMMENT '更新时间',
                              `update_by` bigint DEFAULT NULL COMMENT '更新人ID',
                              `is_deleted` tinyint NOT NULL DEFAULT '0' COMMENT '逻辑删除标识(0-未删除 1-已删除)',
                              PRIMARY KEY (`id`)
) COMMENT='系统配置表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_config`
--

LOCK TABLES `sys_config` WRITE;
/*!40000 ALTER TABLE `sys_config` DISABLE KEYS */;
INSERT INTO `sys_config` VALUES (1,'系统限流QPS','IP_QPS_THRESHOLD_LIMIT','10','单个IP请求的最大每秒查询数（QPS）阈值Key','2025-10-09 06:10:11',1,NULL,NULL,0),(2,'标签页首眉标题','blog.site.tags.hero.eyebrow','TAGS','博客站点配置-标签页首','2026-03-12 18:12:45',1,'2026-03-12 18:12:45',1,0),(3,'标签页首标题','blog.site.tags.hero.title','标签','博客站点配置-标签页首','2026-03-12 18:12:45',1,'2026-03-12 18:12:45',1,0),(4,'标签页首副标题','blog.site.tags.hero.subtitle','把站内主题拆成更易浏览的入口，保留一些轻量的标签墙氛围。','博客站点配置-标签页首','2026-03-12 18:12:45',1,'2026-03-12 18:12:45',1,0),(5,'标签页首图片','blog.site.tags.hero.imageFileId','132','博客站点配置-标签页首图片文件ID','2026-03-12 18:12:45',1,'2026-03-12 18:12:45',1,0),(6,'归档页首眉标题','blog.site.archives.hero.eyebrow','ARCHIVES','博客站点配置-归档页首','2026-03-12 18:12:45',1,'2026-03-12 18:12:45',1,0),(7,'归档页首标题','blog.site.archives.hero.title','归档','博客站点配置-归档页首','2026-03-12 18:12:45',1,'2026-03-12 18:12:45',1,0),(8,'归档页首副标题','blog.site.archives.hero.subtitle','把最近三年的文章按照年份和月份整理在一起，保留一点旧博客的仪式感。','博客站点配置-归档页首','2026-03-12 18:12:45',1,'2026-03-12 18:12:45',1,0),(9,'归档页首图片','blog.site.archives.hero.imageFileId','133','博客站点配置-归档页首图片文件ID','2026-03-12 18:12:45',1,'2026-03-12 18:12:45',1,0),(10,'友链页首眉标题','blog.site.friend-links.hero.eyebrow','LINKS','博客站点配置-友链页首','2026-03-12 18:12:46',1,'2026-03-12 18:12:46',1,0),(11,'友链页首标题','blog.site.friend-links.hero.title','友链','博客站点配置-友链页首','2026-03-12 18:12:46',1,'2026-03-12 18:12:46',1,0),(12,'友链页首副标题','blog.site.friend-links.hero.subtitle','把常逛的站点独立整理成一页，作为博客外延阅读的入口。','博客站点配置-友链页首','2026-03-12 18:12:46',1,'2026-03-12 18:12:46',1,0),(13,'友链页首图片','blog.site.friend-links.hero.imageFileId','134','博客站点配置-友链页首图片文件ID','2026-03-12 18:12:46',1,'2026-03-12 18:12:46',1,0),(14,'聊天室页首眉标题','blog.site.chat.hero.eyebrow','CHAT','博客站点配置-聊天室页首','2026-03-12 18:12:46',1,'2026-03-12 18:12:46',1,0),(15,'聊天室页首标题','blog.site.chat.hero.title','聊天室','博客站点配置-聊天室页首','2026-03-12 18:12:46',1,'2026-03-12 18:12:46',1,0),(16,'聊天室页首副标题','blog.site.chat.hero.subtitle','保留站点统一的波浪页首，下面进入实时聊天和在线状态。','博客站点配置-聊天室页首','2026-03-12 18:12:46',1,'2026-03-12 18:12:46',1,0),(17,'聊天室页首图片','blog.site.chat.hero.imageFileId','135','博客站点配置-聊天室页首图片文件ID','2026-03-12 18:12:46',1,'2026-03-12 18:12:46',1,0),(18,'关于模块标题','blog.site.about.title','关于','博客站点配置-关于模块','2026-03-12 18:12:46',1,'2026-03-12 18:12:46',1,0),(19,'关于模块仓库标签','blog.site.about.repoLabel','项目开源地址：','博客站点配置-关于模块','2026-03-12 18:12:46',1,'2026-03-12 18:12:46',1,0),(20,'关于模块仓库名称','blog.site.about.repoName','KLEE - BLOG','博客站点配置-关于模块','2026-03-12 18:12:46',1,'2026-03-12 18:12:46',1,0),(21,'关于模块仓库链接','blog.site.about.repoUrl','https://gitee.com/variable-wing-core/klee-blog','博客站点配置-关于模块','2026-03-12 18:12:46',1,'2026-03-12 18:12:46',1,0),(22,'页脚主文案','blog.site.footer.primaryText','© 2026 My Blog · Powered by Vue3 + Vite · Theme Inspired by Shoka','博客站点配置-页脚','2026-03-12 18:12:46',1,'2026-03-12 18:12:46',1,0),(23,'页脚次文案','blog.site.footer.secondaryText','本站演示数据仅供测试，不代表真实内容。','博客站点配置-页脚','2026-03-12 18:12:46',1,'2026-03-12 18:12:46',1,0),(24,'留言板背景图','blog.site.message-board.backgroundFileId','140','博客站点配置-留言板背景图文件ID','2026-03-12 23:14:09',1,'2026-03-12 23:14:09',1,0),(25,'首页页首眉标题','blog.site.home.hero.eyebrow','HOME','博客站点配置-首页页首','2026-03-15 15:42:22',1,'2026-03-15 15:42:22',1,0),(26,'首页页首标题','blog.site.home.hero.title','可变羽翼核心','博客站点配置-首页页首','2026-03-15 15:42:22',1,'2026-03-15 15:42:22',1,0),(27,'首页页首副标题','blog.site.home.hero.subtitle','就算一次也好，我想在这颗星球上尽情奔跑。','博客站点配置-首页页首','2026-03-15 15:42:22',1,'2026-03-15 15:42:22',1,0),(28,'站点作者头像','blog.site.sidebar.profile.avatarFileId','142','博客站点配置-侧栏作者卡','2026-03-15 15:42:23',1,'2026-03-15 15:42:23',1,0),(29,'站点作者名称','blog.site.sidebar.profile.name','可变羽翼核心','博客站点配置-侧栏作者卡','2026-03-15 15:42:23',1,'2026-03-15 15:42:23',1,0),(30,'站点作者简介','blog.site.sidebar.profile.intro','b站号发原神节奏视频被封了一年QAQ','博客站点配置-侧栏作者卡','2026-03-15 15:42:23',1,'2026-03-15 15:42:23',1,0),(31,'侧栏公告标题','blog.site.sidebar.notice.title','公告','博客站点配置-侧栏公告','2026-03-15 15:42:23',1,'2026-03-15 15:42:23',1,0),(32,'侧栏公告内容','blog.site.sidebar.notice.content','关注塔菲喵！关注永雏塔菲谢谢喵！！','博客站点配置-侧栏公告','2026-03-15 15:42:23',1,'2026-03-15 15:42:23',1,0),(33,'侧栏联系QQ','blog.site.contact.qq','1143134686','博客站点配置-侧栏联系信息','2026-03-15 15:42:23',1,'2026-03-15 15:42:23',1,0),(34,'侧栏GitHub','blog.site.contact.github','https://github.com/changeable-wing-core','博客站点配置-侧栏联系信息','2026-03-15 15:42:23',1,'2026-03-15 15:42:23',1,0),(35,'侧栏Gitee','blog.site.contact.gitee','https://gitee.com/variable-wing-core/klee-blog','博客站点配置-侧栏联系信息','2026-03-15 15:42:23',1,'2026-03-15 15:42:23',1,0),(36,'侧栏邮箱','blog.site.contact.email','1143134686@qq.com','博客站点配置-侧栏联系信息','2026-03-15 15:42:23',1,'2026-03-15 15:42:23',1,0),(37,'侧栏微信','blog.site.contact.wechat','QQ1143134686','博客站点配置-侧栏联系信息','2026-03-15 15:42:23',1,'2026-03-15 15:42:23',1,0),(38,'侧栏CSDN','blog.site.contact.csdn','','博客站点配置-侧栏联系信息','2026-03-15 15:42:23',1,'2026-03-15 15:42:23',1,0),(39,'侧栏QQ群','blog.site.contact.qqGroup','','博客站点配置-侧栏联系信息','2026-03-15 15:42:23',1,'2026-03-15 15:42:23',1,0),(40,'页脚版权文案','blog.site.footer.copyrightText','© 可变羽翼核心 2022','博客站点配置-页脚','2026-03-15 23:23:00',1,'2026-03-15 23:23:00',1,0),(41,'页脚建站时间','blog.site.footer.siteStartTime','2022-01-01T00:00:00','博客站点配置-页脚','2026-03-15 23:23:01',1,'2026-03-15 23:23:01',1,0);
/*!40000 ALTER TABLE `sys_config` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_dept`
--

DROP TABLE IF EXISTS `sys_dept`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_dept` (
                            `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
                            `name` varchar(100) NOT NULL COMMENT '部门名称',
                            `code` varchar(100) NOT NULL COMMENT '部门编号',
                            `parent_id` bigint DEFAULT '0' COMMENT '父节点id',
                            `tree_path` varchar(255) NOT NULL COMMENT '父节点id路径',
                            `sort` smallint DEFAULT '0' COMMENT '显示顺序',
                            `status` tinyint DEFAULT '1' COMMENT '状态(1-正常 0-禁用)',
                            `create_by` bigint DEFAULT NULL COMMENT '创建人ID',
                            `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                            `update_by` bigint DEFAULT NULL COMMENT '修改人ID',
                            `update_time` datetime DEFAULT NULL COMMENT '更新时间',
                            `is_deleted` tinyint DEFAULT '0' COMMENT '逻辑删除标识(1-已删除 0-未删除)',
                            PRIMARY KEY (`id`) USING BTREE,
                            UNIQUE KEY `uk_code` (`code`) USING BTREE COMMENT '部门编号唯一索引'
) COMMENT='部门表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_dept`
--

LOCK TABLES `sys_dept` WRITE;
/*!40000 ALTER TABLE `sys_dept` DISABLE KEYS */;
INSERT INTO `sys_dept` VALUES (1,'可变羽翼核心','WIMG',0,'0',1,1,1,NULL,1,'2025-10-26 12:35:43',0),(2,'研发部门','RD001',1,'0,1',1,1,2,NULL,2,'2025-10-09 06:09:40',0),(3,'测试部门','QA001',1,'0,1',1,1,2,NULL,2,'2025-10-09 06:09:41',0);
/*!40000 ALTER TABLE `sys_dept` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_dict`
--

DROP TABLE IF EXISTS `sys_dict`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_dict` (
                            `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键 ',
                            `dict_code` varchar(50) DEFAULT NULL COMMENT '类型编码',
                            `name` varchar(50) DEFAULT NULL COMMENT '类型名称',
                            `status` tinyint(1) DEFAULT '0' COMMENT '状态(0:正常;1:禁用)',
                            `remark` varchar(255) DEFAULT NULL COMMENT '备注',
                            `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                            `create_by` bigint DEFAULT NULL COMMENT '创建人ID',
                            `update_time` datetime DEFAULT NULL COMMENT '更新时间',
                            `update_by` bigint DEFAULT NULL COMMENT '修改人ID',
                            `is_deleted` tinyint DEFAULT '0' COMMENT '是否删除(1-删除，0-未删除)',
                            PRIMARY KEY (`id`) USING BTREE,
                            KEY `idx_dict_code` (`dict_code`)
) COMMENT='字典表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_dict`
--

LOCK TABLES `sys_dict` WRITE;
/*!40000 ALTER TABLE `sys_dict` DISABLE KEYS */;
INSERT INTO `sys_dict` VALUES (1,'gender','性别',1,NULL,'2025-10-09 06:09:41',1,'2025-10-09 06:09:41',1,0),(2,'notice_type','通知类型',1,NULL,'2025-10-09 06:09:41',1,'2025-10-09 06:09:41',1,0),(3,'notice_level','通知级别',1,NULL,'2025-10-09 06:09:41',1,'2025-10-09 06:09:41',1,0);
/*!40000 ALTER TABLE `sys_dict` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_dict_item`
--

DROP TABLE IF EXISTS `sys_dict_item`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_dict_item` (
                                 `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
                                 `dict_code` varchar(50) DEFAULT NULL COMMENT '关联字典编码，与sys_dict表中的dict_code对应',
                                 `value` varchar(50) DEFAULT NULL COMMENT '字典项值',
                                 `label` varchar(100) DEFAULT NULL COMMENT '字典项标签',
                                 `tag_type` varchar(50) DEFAULT NULL COMMENT '标签类型，用于前端样式展示（如success、warning等）',
                                 `status` tinyint DEFAULT '0' COMMENT '状态（1-正常，0-禁用）',
                                 `sort` int DEFAULT '0' COMMENT '排序',
                                 `remark` varchar(255) DEFAULT NULL COMMENT '备注',
                                 `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                                 `create_by` bigint DEFAULT NULL COMMENT '创建人ID',
                                 `update_time` datetime DEFAULT NULL COMMENT '更新时间',
                                 `update_by` bigint DEFAULT NULL COMMENT '修改人ID',
                                 PRIMARY KEY (`id`) USING BTREE
) COMMENT='字典项表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_dict_item`
--

LOCK TABLES `sys_dict_item` WRITE;
/*!40000 ALTER TABLE `sys_dict_item` DISABLE KEYS */;
INSERT INTO `sys_dict_item` VALUES (1,'gender','1','男','primary',1,1,NULL,'2025-10-09 06:09:42',1,'2025-10-09 06:09:42',1),(2,'gender','2','女','danger',1,2,NULL,'2025-10-09 06:09:42',1,'2025-10-09 06:09:42',1),(3,'gender','0','保密','info',1,3,NULL,'2025-10-09 06:09:42',1,'2025-10-09 06:09:42',1),(4,'notice_type','1','系统升级','success',1,1,'','2025-10-09 06:09:42',1,'2025-10-09 06:09:42',1),(5,'notice_type','2','系统维护','primary',1,2,'','2025-10-09 06:09:42',1,'2025-10-09 06:09:42',1),(6,'notice_type','3','安全警告','danger',1,3,'','2025-10-09 06:09:43',1,'2025-10-09 06:09:43',1),(7,'notice_type','4','假期通知','success',1,4,'','2025-10-09 06:09:43',1,'2025-10-09 06:09:43',1),(8,'notice_type','5','公司新闻','primary',1,5,'','2025-10-09 06:09:43',1,'2025-10-09 06:09:43',1),(9,'notice_type','99','其他','info',1,99,'','2025-10-09 06:09:43',1,'2025-10-09 06:09:43',1),(10,'notice_level','L','低','info',1,1,'','2025-10-09 06:09:43',1,'2025-10-09 06:09:43',1),(11,'notice_level','M','中','warning',1,2,'','2025-10-09 06:09:43',1,'2025-10-09 06:09:43',1),(12,'notice_level','H','高','danger',1,3,'','2025-10-09 06:09:43',1,'2025-10-09 06:09:43',1);
/*!40000 ALTER TABLE `sys_dict_item` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_log`
--

DROP TABLE IF EXISTS `sys_log`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_log` (
                           `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
                           `module` varchar(50) NOT NULL COMMENT '日志模块',
                           `request_method` varchar(64) NOT NULL COMMENT '请求方式',
                           `request_params` text COMMENT '请求参数(批量请求参数可能会超过text)',
                           `response_content` mediumtext COMMENT '返回参数',
                           `content` varchar(255) NOT NULL COMMENT '日志内容',
                           `request_uri` varchar(255) DEFAULT NULL COMMENT '请求路径',
                           `method` varchar(255) DEFAULT NULL COMMENT '方法名',
                           `ip` varchar(45) DEFAULT NULL COMMENT 'IP地址',
                           `province` varchar(100) DEFAULT NULL COMMENT '省份',
                           `city` varchar(100) DEFAULT NULL COMMENT '城市',
                           `execution_time` bigint DEFAULT NULL COMMENT '执行时间(ms)',
                           `browser` varchar(100) DEFAULT NULL COMMENT '浏览器',
                           `browser_version` varchar(100) DEFAULT NULL COMMENT '浏览器版本',
                           `os` varchar(100) DEFAULT NULL COMMENT '终端系统',
                           `create_by` bigint DEFAULT NULL COMMENT '创建人ID',
                           `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                           `is_deleted` tinyint DEFAULT '0' COMMENT '逻辑删除标识(1-已删除 0-未删除)',
                           PRIMARY KEY (`id`) USING BTREE,
                           KEY `idx_create_time` (`create_time`)
) COMMENT='系统日志表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_log`
--

LOCK TABLES `sys_log` WRITE;
/*!40000 ALTER TABLE `sys_log` DISABLE KEYS */;
/*!40000 ALTER TABLE `sys_log` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_menu`
--

DROP TABLE IF EXISTS `sys_menu`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_menu` (
                            `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'ID',
                            `parent_id` bigint NOT NULL COMMENT '父菜单ID',
                            `tree_path` varchar(255) DEFAULT NULL COMMENT '父节点ID路径',
                            `name` varchar(64) NOT NULL COMMENT '菜单名称',
                            `type` tinyint NOT NULL COMMENT '菜单类型（1-菜单 2-目录 3-外链 4-按钮）',
                            `route_name` varchar(255) DEFAULT NULL COMMENT '路由名称（Vue Router 中用于命名路由）',
                            `route_path` varchar(128) DEFAULT NULL COMMENT '路由路径（Vue Router 中定义的 URL 路径）',
                            `component` varchar(128) DEFAULT NULL COMMENT '组件路径（组件页面完整路径，相对于 src/views/，缺省后缀 .vue）',
                            `perm` varchar(128) DEFAULT NULL COMMENT '【按钮】权限标识',
                            `always_show` tinyint DEFAULT '0' COMMENT '【目录】只有一个子路由是否始终显示（1-是 0-否）',
                            `keep_alive` tinyint DEFAULT '0' COMMENT '【菜单】是否开启页面缓存（1-是 0-否）',
                            `visible` tinyint(1) DEFAULT '1' COMMENT '显示状态（1-显示 0-隐藏）',
                            `sort` int DEFAULT '0' COMMENT '排序',
                            `icon` varchar(64) DEFAULT NULL COMMENT '菜单图标',
                            `redirect` varchar(128) DEFAULT NULL COMMENT '跳转路径',
                            `is_deleted` tinyint DEFAULT '0',
                            `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                            `update_time` datetime DEFAULT NULL COMMENT '更新时间',
                            `params` varchar(255) DEFAULT NULL COMMENT '路由参数',
                            PRIMARY KEY (`id`) USING BTREE
) COMMENT='菜单管理';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_menu`
--

LOCK TABLES `sys_menu` WRITE;
/*!40000 ALTER TABLE `sys_menu` DISABLE KEYS */;
INSERT INTO `sys_menu` VALUES (1,0,'0','系统管理',2,'','/system','Layout',NULL,NULL,NULL,1,1,'project','/system/user',0,'2025-10-09 06:09:44','2025-10-21 22:48:27',NULL),(2,1,'0,1','用户管理',1,'User','user','system/user/index',NULL,NULL,1,1,1,'wechat',NULL,0,'2025-10-09 06:09:44','2025-10-21 22:48:53',NULL),(3,1,'0,1','角色管理',1,'Role','role','system/role/index',NULL,NULL,1,1,2,'role',NULL,0,'2025-10-09 06:09:44','2025-10-09 06:09:44',NULL),(4,1,'0,1','菜单管理',1,'SysMenu','menu','system/menu/index',NULL,NULL,1,1,3,'menu',NULL,0,'2025-10-09 06:09:45','2025-10-09 06:09:45',NULL),(5,1,'0,1','部门管理',1,'Dept','dept','system/dept/index',NULL,NULL,1,1,4,'tree',NULL,0,'2025-10-09 06:09:45','2025-10-09 06:09:45',NULL),(6,1,'0,1','字典管理',1,'Dict','dict','system/dict/index',NULL,NULL,1,1,5,'dict',NULL,0,'2025-10-09 06:09:45','2025-10-09 06:09:45',NULL),(20,0,'0','多级菜单',2,NULL,'/multi-level','Layout',NULL,1,NULL,1,9,'cascader','',1,'2025-10-09 06:09:45','2025-10-09 06:09:45',NULL),(21,20,'0,20','菜单一级',2,NULL,'multi-level1','Layout',NULL,1,NULL,1,1,'','',1,'2025-10-09 06:09:45','2025-10-09 06:09:45',NULL),(22,21,'0,20,21','菜单二级',2,NULL,'multi-level2','Layout',NULL,0,NULL,1,1,'',NULL,1,'2025-10-09 06:09:45','2025-10-09 06:09:45',NULL),(23,22,'0,20,21,22','菜单三级-1',1,NULL,'multi-level3-1','demo/multi-level/children/children/level3-1',NULL,0,1,1,1,'','',1,'2025-10-09 06:09:45','2025-10-09 06:09:45',NULL),(24,22,'0,20,21,22','菜单三级-2',1,NULL,'multi-level3-2','demo/multi-level/children/children/level3-2',NULL,0,1,1,2,'','',1,'2025-10-09 06:09:45','2025-10-09 06:09:45',NULL),(26,0,'0','平台文档',2,'','/doc','Layout',NULL,NULL,NULL,1,8,'document','https://juejin.cn/post/7228990409909108793',1,'2025-10-09 06:09:46','2025-10-09 06:09:46',NULL),(30,26,'0,26','平台文档(外链)',3,NULL,'https://juejin.cn/post/7228990409909108793','',NULL,NULL,NULL,1,2,'document','',1,'2025-10-09 06:09:46','2025-10-09 06:09:46',NULL),(31,2,'0,1,2','用户新增',4,NULL,'',NULL,'sys:user:add',NULL,NULL,1,1,'','',0,'2025-10-09 06:09:46','2025-10-09 06:09:46',NULL),(32,2,'0,1,2','用户编辑',4,NULL,'',NULL,'sys:user:edit',NULL,NULL,1,2,'','',0,'2025-10-09 06:09:46','2025-10-09 06:09:46',NULL),(33,2,'0,1,2','用户删除',4,NULL,'',NULL,'sys:user:delete',NULL,NULL,1,3,'','',0,'2025-10-09 06:09:46','2025-10-09 06:09:46',NULL),(36,0,'0','组件封装',2,NULL,'/component','Layout',NULL,NULL,NULL,1,10,'menu','',1,'2025-10-09 06:09:46','2025-10-09 06:09:46',NULL),(37,36,'0,36','富文本编辑器',1,'WangEditor','wang-editor','demo/wang-editor',NULL,NULL,1,1,2,'','',1,NULL,NULL,NULL),(38,36,'0,36','图片上传',1,'Upload','upload','demo/upload',NULL,NULL,1,1,3,'','',1,'2025-10-09 06:09:46','2025-10-09 06:09:46',NULL),(39,36,'0,36','图标选择器',1,'IconSelect','icon-select','demo/icon-select',NULL,NULL,1,1,4,'','',1,'2025-10-09 06:09:47','2025-10-09 06:09:47',NULL),(40,0,'0','接口文档',2,NULL,'/api','Layout',NULL,1,NULL,1,7,'api','',1,'2025-10-09 06:09:47','2025-10-09 06:09:47',NULL),(41,40,'0,40','Apifox',1,'Apifox','apifox','demo/api/apifox',NULL,NULL,1,1,1,'api','',1,'2025-10-09 06:09:47','2025-10-09 06:09:47',NULL),(70,3,'0,1,3','角色新增',4,NULL,'',NULL,'sys:role:add',NULL,NULL,1,2,'',NULL,0,'2025-10-09 06:09:47','2025-10-09 06:09:47',NULL),(71,3,'0,1,3','角色编辑',4,NULL,'',NULL,'sys:role:edit',NULL,NULL,1,3,'',NULL,0,'2025-10-09 06:09:47','2025-10-09 06:09:47',NULL),(72,3,'0,1,3','角色删除',4,NULL,'',NULL,'sys:role:delete',NULL,NULL,1,4,'',NULL,0,'2025-10-09 06:09:47','2025-10-09 06:09:47',NULL),(73,4,'0,1,4','菜单新增',4,NULL,'',NULL,'sys:menu:add',NULL,NULL,1,1,'',NULL,0,'2025-10-09 06:09:47','2025-10-09 06:09:47',NULL),(74,4,'0,1,4','菜单编辑',4,NULL,'',NULL,'sys:menu:edit',NULL,NULL,1,3,'',NULL,0,'2025-10-09 06:09:47','2025-10-09 06:09:47',NULL),(75,4,'0,1,4','菜单删除',4,NULL,'',NULL,'sys:menu:delete',NULL,NULL,1,3,'',NULL,0,'2025-10-09 06:09:47','2025-10-09 06:09:47',NULL),(76,5,'0,1,5','部门新增',4,NULL,'',NULL,'sys:dept:add',NULL,NULL,1,1,'',NULL,0,'2025-10-09 06:09:48','2025-10-09 06:09:48',NULL),(77,5,'0,1,5','部门编辑',4,NULL,'',NULL,'sys:dept:edit',NULL,NULL,1,2,'',NULL,0,'2025-10-09 06:09:48','2025-10-09 06:09:48',NULL),(78,5,'0,1,5','部门删除',4,NULL,'',NULL,'sys:dept:delete',NULL,NULL,1,3,'',NULL,0,'2025-10-09 06:09:48','2025-10-09 06:09:48',NULL),(79,6,'0,1,6','字典新增',4,NULL,'',NULL,'sys:dict:add',NULL,NULL,1,1,'',NULL,0,'2025-10-09 06:09:48','2025-10-09 06:09:48',NULL),(81,6,'0,1,6','字典编辑',4,NULL,'',NULL,'sys:dict:edit',NULL,NULL,1,2,'',NULL,0,'2025-10-09 06:09:48','2025-10-09 06:09:48',NULL),(84,6,'0,1,6','字典删除',4,NULL,'',NULL,'sys:dict:delete',NULL,NULL,1,3,'',NULL,0,'2025-10-09 06:09:48','2025-10-09 06:09:48',NULL),(88,2,'0,1,2','重置密码',4,NULL,'',NULL,'sys:user:reset-password',NULL,NULL,1,4,'',NULL,0,'2025-10-09 06:09:48','2025-10-09 06:09:48',NULL),(89,0,'0','功能演示',2,NULL,'/function','Layout',NULL,NULL,NULL,1,12,'menu','',1,'2025-10-09 06:09:48','2025-10-09 06:09:48',NULL),(90,89,'0,89','Websocket',1,'WebSocket','/function/websocket','demo/websocket',NULL,NULL,1,1,3,'','',1,'2025-10-09 06:09:48','2025-10-09 06:09:48',NULL),(95,36,'0,36','字典组件',1,'DictDemo','dict-demo','demo/dictionary',NULL,NULL,1,1,4,'','',1,'2025-10-09 06:09:49','2025-10-09 06:09:49',NULL),(97,89,'0,89','Icons',1,'IconDemo','icon-demo','demo/icons',NULL,NULL,1,1,2,'el-icon-Notification','',1,'2025-10-09 06:09:49','2025-10-09 06:09:49',NULL),(102,26,'0,26','document',3,NULL,'internal-doc','demo/internal-doc',NULL,NULL,NULL,1,1,'document','',1,'2025-10-09 06:09:49','2025-10-09 06:09:49',NULL),(105,2,'0,1,2','用户查询',4,NULL,'',NULL,'sys:user:query',0,0,1,0,'',NULL,0,'2025-10-09 06:09:49','2025-10-09 06:09:49',NULL),(106,2,'0,1,2','用户导入',4,NULL,'',NULL,'sys:user:import',NULL,NULL,1,5,'',NULL,0,'2025-10-09 06:09:49','2025-10-09 06:09:49',NULL),(107,2,'0,1,2','用户导出',4,NULL,'',NULL,'sys:user:export',NULL,NULL,1,6,'',NULL,0,'2025-10-09 06:09:49','2025-10-09 06:09:49',NULL),(108,36,'0,36','增删改查',1,'Curd','curd','demo/curd/index',NULL,NULL,1,1,0,'','',1,NULL,NULL,NULL),(109,36,'0,36','列表选择器',1,'TableSelect','table-select','demo/table-select/index',NULL,NULL,1,1,1,'','',1,NULL,NULL,NULL),(110,0,'0','路由参数',2,NULL,'/route-param','Layout',NULL,1,1,1,11,'el-icon-ElementPlus',NULL,1,'2025-10-09 06:09:50','2025-10-09 06:09:50',NULL),(111,110,'0,110','参数(type=1)',1,'RouteParamType1','route-param-type1','demo/route-param',NULL,0,1,1,1,'el-icon-Star',NULL,1,'2025-10-09 06:09:50','2025-10-09 06:09:50','{\"type\": \"1\"}'),(112,110,'0,110','参数(type=2)',1,'RouteParamType2','route-param-type2','demo/route-param',NULL,0,1,1,2,'el-icon-StarFilled',NULL,1,'2025-10-09 06:09:50','2025-10-09 06:09:50','{\"type\": \"2\"}'),(117,1,'0,1','系统日志',1,'Log','log','system/log/index',NULL,0,1,1,6,'document',NULL,0,'2025-10-09 06:09:50','2025-10-09 06:09:50',NULL),(118,0,'0','系统工具',2,NULL,'/codegen','Layout',NULL,0,1,1,2,'menu',NULL,0,'2025-10-09 06:09:50','2025-10-09 06:09:50',NULL),(119,118,'0,118','代码生成',1,'Codegen','codegen','codegen/index',NULL,0,1,1,1,'code',NULL,0,'2025-10-09 06:09:50','2025-10-09 06:09:50',NULL),(120,1,'0,1','系统配置',1,'Config','config','system/config/index',NULL,0,1,1,7,'setting',NULL,0,'2025-10-09 06:09:50','2025-10-09 06:09:50',NULL),(121,120,'0,1,120','系统配置查询',4,NULL,'',NULL,'sys:config:query',0,1,1,1,'',NULL,0,'2025-10-09 06:09:51','2025-10-09 06:09:51',NULL),(122,120,'0,1,120','系统配置新增',4,NULL,'',NULL,'sys:config:add',0,1,1,2,'',NULL,0,'2025-10-09 06:09:51','2025-10-09 06:09:51',NULL),(123,120,'0,1,120','系统配置修改',4,NULL,'',NULL,'sys:config:update',0,1,1,3,'',NULL,0,'2025-10-09 06:09:51','2025-10-09 06:09:51',NULL),(124,120,'0,1,120','系统配置删除',4,NULL,'',NULL,'sys:config:delete',0,1,1,4,'',NULL,0,'2025-10-09 06:09:51','2025-10-09 06:09:51',NULL),(125,120,'0,1,120','系统配置刷新',4,NULL,'',NULL,'sys:config:refresh',0,1,1,5,'',NULL,0,'2025-10-09 06:09:51','2025-10-09 06:09:51',NULL),(126,1,'0,1','通知公告',1,'Notice','notice','system/notice/index',NULL,NULL,NULL,1,9,'',NULL,0,'2025-10-09 06:09:51','2025-10-09 06:09:51',NULL),(127,126,'0,1,126','通知查询',4,NULL,'',NULL,'sys:notice:query',NULL,NULL,1,1,'',NULL,0,'2025-10-09 06:09:51','2025-10-09 06:09:51',NULL),(128,126,'0,1,126','通知新增',4,NULL,'',NULL,'sys:notice:add',NULL,NULL,1,2,'',NULL,0,'2025-10-09 06:09:51','2025-10-09 06:09:51',NULL),(129,126,'0,1,126','通知编辑',4,NULL,'',NULL,'sys:notice:edit',NULL,NULL,1,3,'',NULL,0,'2025-10-09 06:09:51','2025-10-09 06:09:51',NULL),(130,126,'0,1,126','通知删除',4,NULL,'',NULL,'sys:notice:delete',NULL,NULL,1,4,'',NULL,0,'2025-10-09 06:09:52','2025-10-09 06:09:52',NULL),(133,126,'0,1,126','通知发布',4,NULL,'',NULL,'sys:notice:publish',0,1,1,5,'',NULL,0,'2025-10-09 06:09:52','2025-10-09 06:09:52',NULL),(134,126,'0,1,126','通知撤回',4,NULL,'',NULL,'sys:notice:revoke',0,1,1,6,'',NULL,0,'2025-10-09 06:09:52','2025-10-09 06:09:52',NULL),(135,1,'0,1','字典项',1,'DictItem','dict-item','system/dict/dict-item',NULL,0,1,0,6,'',NULL,0,'2025-10-09 06:09:52','2025-10-09 06:09:52',NULL),(136,135,'0,1,135','字典项新增',4,NULL,'',NULL,'sys:dict-item:add',NULL,NULL,1,2,'',NULL,0,'2025-10-09 06:09:52','2025-10-09 06:09:52',NULL),(137,135,'0,1,135','字典项编辑',4,NULL,'',NULL,'sys:dict-item:edit',NULL,NULL,1,3,'',NULL,0,'2025-10-09 06:09:52','2025-10-09 06:09:52',NULL),(138,135,'0,1,135','字典项删除',4,NULL,'',NULL,'sys:dict-item:delete',NULL,NULL,1,4,'',NULL,0,'2025-10-09 06:09:52','2025-10-09 06:09:52',NULL),(139,3,'0,1,3','角色查询',4,NULL,'',NULL,'sys:role:query',NULL,NULL,1,1,'',NULL,0,'2025-10-09 06:09:52','2025-10-09 06:09:52',NULL),(140,4,'0,1,4','菜单查询',4,NULL,'',NULL,'sys:menu:query',NULL,NULL,1,1,'',NULL,0,'2025-10-09 06:09:53','2025-10-09 06:09:53',NULL),(141,5,'0,1,5','部门查询',4,NULL,'',NULL,'sys:dept:query',NULL,NULL,1,1,'',NULL,0,'2025-10-09 06:09:53','2025-10-09 06:09:53',NULL),(142,6,'0,1,6','字典查询',4,NULL,'',NULL,'sys:dict:query',NULL,NULL,1,1,'',NULL,0,'2025-10-09 06:09:53','2025-10-09 06:09:53',NULL),(143,135,'0,1,135','字典项查询',4,NULL,'',NULL,'sys:dict-item:query',NULL,NULL,1,1,'',NULL,0,'2025-10-09 06:09:53','2025-10-09 06:09:53',NULL),(144,26,'0,26','后端文档',3,NULL,'https://youlai.blog.csdn.net/article/details/145178880','',NULL,NULL,NULL,1,3,'document','',1,'2025-10-09 06:09:53','2025-10-09 06:09:53',NULL),(145,26,'0,26','移动端文档',3,NULL,'https://youlai.blog.csdn.net/article/details/143222890','',NULL,NULL,NULL,1,4,'document','',1,'2025-10-09 06:09:53','2025-10-09 06:09:53',NULL),(146,36,'0,36','拖拽组件',1,'Drag','drag','demo/drag',NULL,NULL,NULL,1,5,'','',1,'2025-10-09 06:09:53','2025-10-09 06:09:53',NULL),(147,36,'0,36','滚动文本',1,'TextScroll','text-scroll','demo/text-scroll',NULL,NULL,NULL,1,6,'','',1,'2025-10-09 06:09:53','2025-10-09 06:09:53',NULL),(148,89,'0,89','字典实时同步',1,'DictSync','dict-sync','demo/dict-sync',NULL,NULL,NULL,1,3,'','',1,'2025-10-09 06:09:54','2025-10-09 06:09:54',NULL),(149,89,'0,89','VxeTable',1,'VxeTable','vxe-table','demo/vxe-table/index',NULL,NULL,1,1,0,'el-icon-MagicStick','',1,'2025-10-09 06:09:54','2025-10-09 06:09:54',NULL),(150,36,'0,36','自适应表格操作列',1,'AutoOperationColumn','operation-column','demo/auto-operation-column',NULL,NULL,1,1,1,'','',1,'2025-10-09 06:09:54','2025-10-09 06:09:54',NULL),(151,89,'0,89','CURD单文件',1,'CurdSingle','curd-single','demo/curd-single',NULL,NULL,1,1,7,'el-icon-Reading','',1,'2025-10-09 06:09:54','2025-10-09 06:09:54',NULL),(152,0,'0','博客管理',2,NULL,'/blog','Layout',NULL,0,1,1,1,'bilibili','/blog/list',0,'2025-10-10 17:08:31','2025-10-21 22:47:54',NULL),(153,152,'0,152','博客列表',1,'List','list','blog/list/index',NULL,0,1,1,1,'todo',NULL,0,'2025-10-10 17:10:23','2025-10-21 22:48:06',NULL),(154,152,'0,152','写博客',1,'Write','write','blog/write/index',NULL,0,1,1,1,'java',NULL,0,'2025-10-10 17:10:56','2025-10-21 22:48:13',NULL),(155,154,'0,152,154','博客新增',4,NULL,NULL,NULL,'blog:write:add',0,1,1,1,NULL,NULL,1,'2025-10-19 19:18:19','2025-10-19 19:18:19',NULL),(156,154,'0,152,154','博客编辑',4,NULL,NULL,NULL,'blog:write:upsert',0,1,1,1,NULL,NULL,0,'2025-10-19 19:18:47','2025-10-19 20:38:50',NULL),(157,154,'0,152,154','博客查询',4,NULL,NULL,NULL,'blog:write:query',0,1,1,1,NULL,NULL,0,'2025-10-19 19:20:31','2025-10-19 19:20:31',NULL),(158,1,'0,1','文件管理',1,'File','file','system/file/index',NULL,0,1,1,1,'file',NULL,0,'2025-10-21 22:12:27','2025-10-21 22:12:27',NULL),(159,158,'0,1,158','文件查询',4,NULL,NULL,NULL,'file:query',0,1,1,1,NULL,NULL,0,'2025-10-21 22:29:14','2025-10-21 22:29:14',NULL),(160,158,'0,1,158','文件上传',4,NULL,NULL,NULL,'file:upload',0,1,1,1,NULL,NULL,0,'2025-10-21 22:29:45','2025-10-21 22:29:45',NULL),(161,158,'0,1,158','文件编辑',4,NULL,NULL,NULL,'file:edit',0,1,1,1,NULL,NULL,0,'2025-10-21 22:30:14','2025-10-21 22:30:14',NULL),(162,158,'0,1,158','文件删除',4,NULL,NULL,NULL,'file:delete',0,1,1,1,NULL,NULL,0,'2025-10-21 22:30:32','2025-10-21 22:30:32',NULL),(163,152,'0,152','标签管理',1,'Tag','tag','blog/tag/index',NULL,0,1,1,1,'juejin',NULL,0,'2025-10-26 11:41:58','2025-10-26 11:42:23',NULL),(164,163,'0,152,163','标签新增',4,NULL,NULL,NULL,'blog:tag:add',0,1,1,1,NULL,NULL,0,'2025-10-26 12:21:59','2025-10-26 12:21:59',NULL),(165,163,'0,152,163','标签编辑',4,NULL,NULL,NULL,'blog:tag:update',0,1,1,1,NULL,NULL,0,'2025-10-26 12:22:23','2025-10-26 12:22:23',NULL),(166,163,'0,152,163','标签删除',4,NULL,NULL,NULL,'blog:tag:delete',0,1,1,1,NULL,NULL,0,'2025-10-26 12:22:39','2025-10-26 12:22:39',NULL),(167,163,'0,152,163','标签查询',4,NULL,NULL,NULL,'blog:tag:query',0,1,1,1,NULL,NULL,0,'2025-10-26 12:23:37','2025-10-26 12:23:37',NULL),(168,152,'0,152','轮播图管理',1,'Carousel','carousel','blog/carousel/index',NULL,0,1,1,1,'cnblogs',NULL,0,'2025-10-26 23:19:44','2025-10-26 23:19:44',NULL),(169,168,'0,152,168','轮播图查询',4,NULL,NULL,NULL,'blog:carousel:query',0,1,1,1,NULL,NULL,0,'2025-10-26 23:20:24','2025-10-26 23:20:24',NULL),(170,168,'0,152,168','轮播图新增',4,NULL,NULL,NULL,'blog:carousel:add',0,1,1,1,NULL,NULL,0,'2025-10-26 23:20:45','2025-10-26 23:20:45',NULL),(171,168,'0,152,168','轮播图更新',4,NULL,NULL,NULL,'blog:carousel:update',0,1,1,1,NULL,NULL,0,'2025-10-26 23:21:04','2025-10-26 23:21:04',NULL),(172,168,'0,152,168','轮播图删除',4,NULL,NULL,NULL,'blog:carousel:delete',0,1,1,1,NULL,NULL,0,'2025-10-26 23:21:19','2025-10-26 23:21:19',NULL),(173,152,'0,152','友链管理',1,'FriendLink','friend-link','blog/friend-link/index',NULL,0,1,1,4,'csdn',NULL,0,'2026-03-12 10:00:00','2026-03-12 13:29:29',NULL),(174,173,'0,152,173','友链查询',4,NULL,NULL,NULL,'blog:friend-link:query',0,1,1,1,NULL,NULL,0,'2026-03-12 10:00:00','2026-03-12 10:00:00',NULL),(175,173,'0,152,173','友链新增',4,NULL,NULL,NULL,'blog:friend-link:add',0,1,1,2,NULL,NULL,0,'2026-03-12 10:00:00','2026-03-12 10:00:00',NULL),(176,173,'0,152,173','友链编辑',4,NULL,NULL,NULL,'blog:friend-link:update',0,1,1,3,NULL,NULL,0,'2026-03-12 10:00:00','2026-03-12 10:00:00',NULL),(177,173,'0,152,173','友链删除',4,NULL,NULL,NULL,'blog:friend-link:delete',0,1,1,4,NULL,NULL,0,'2026-03-12 10:00:00','2026-03-12 10:00:00',NULL),(178,152,'0,152','站点配置',1,'SiteConfig','site-config','blog/site-config/index',NULL,0,1,1,5,'setting',NULL,0,'2026-03-12 16:00:00','2026-03-12 16:00:00',NULL),(179,178,'0,152,178','站点配置查询',4,NULL,NULL,NULL,'blog:site-config:query',0,1,1,1,NULL,NULL,0,'2026-03-12 16:00:00','2026-03-12 16:00:00',NULL),(180,178,'0,152,178','站点配置修改',4,NULL,NULL,NULL,'blog:site-config:update',0,1,1,2,NULL,NULL,0,'2026-03-12 16:00:00','2026-03-12 16:00:00',NULL),(181,152,'0,152','留言审核',1,'Message','message','blog/message/index',NULL,0,1,1,6,'message',NULL,0,'2026-03-12 18:00:00','2026-03-12 18:00:00',NULL),(182,181,'0,152,181','留言查询',4,NULL,NULL,NULL,'blog:message:query',0,1,1,1,NULL,NULL,0,'2026-03-12 18:00:00','2026-03-12 18:00:00',NULL),(183,181,'0,152,181','留言审核',4,NULL,NULL,NULL,'blog:message:audit',0,1,1,2,NULL,NULL,0,'2026-03-12 18:00:00','2026-03-12 18:00:00',NULL),(184,181,'0,152,181','留言删除',4,NULL,NULL,NULL,'blog:message:delete',0,1,1,3,NULL,NULL,0,'2026-03-12 18:00:00','2026-03-12 18:00:00',NULL);
/*!40000 ALTER TABLE `sys_menu` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_notice`
--

DROP TABLE IF EXISTS `sys_notice`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_notice` (
                              `id` bigint NOT NULL AUTO_INCREMENT,
                              `title` varchar(50) DEFAULT NULL COMMENT '通知标题',
                              `content` text COMMENT '通知内容',
                              `type` tinyint NOT NULL COMMENT '通知类型（关联字典编码：notice_type）',
                              `level` varchar(5) NOT NULL COMMENT '通知等级（字典code：notice_level）',
                              `target_type` tinyint NOT NULL COMMENT '目标类型（1: 全体, 2: 指定）',
                              `target_user_ids` varchar(255) DEFAULT NULL COMMENT '目标人ID集合（多个使用英文逗号,分割）',
                              `publisher_id` bigint DEFAULT NULL COMMENT '发布人ID',
                              `publish_status` tinyint DEFAULT '0' COMMENT '发布状态（0: 未发布, 1: 已发布, -1: 已撤回）',
                              `publish_time` datetime DEFAULT NULL COMMENT '发布时间',
                              `revoke_time` datetime DEFAULT NULL COMMENT '撤回时间',
                              `create_by` bigint NOT NULL COMMENT '创建人ID',
                              `create_time` datetime NOT NULL COMMENT '创建时间',
                              `update_by` bigint DEFAULT NULL COMMENT '更新人ID',
                              `update_time` datetime DEFAULT NULL COMMENT '更新时间',
                              `is_deleted` tinyint(1) DEFAULT '0' COMMENT '是否删除（0: 未删除, 1: 已删除）',
                              PRIMARY KEY (`id`) USING BTREE
) COMMENT='通知公告表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_notice`
--

LOCK TABLES `sys_notice` WRITE;
/*!40000 ALTER TABLE `sys_notice` DISABLE KEYS */;
INSERT INTO `sys_notice` VALUES (1,'v2.12.0 新增系统日志，访问趋势统计功能。','<p>1. 消息通知</p><p>2. 字典重构</p><p>3. 代码生成</p>',1,'L',1,'2',1,-1,'2025-10-09 06:10:12','2025-10-19 21:00:55',2,'2025-10-09 06:10:12',2,'2025-10-19 21:00:57',1),(2,'v2.13.0 新增菜单搜索。','<p>1. 消息通知</p><p>2. 字典重构</p><p>3. 代码生成</p>',1,'L',1,'2',1,-1,'2025-10-09 06:10:12','2025-10-19 21:00:52',2,'2025-10-09 06:10:12',2,'2025-10-19 21:00:54',1),(3,'v2.14.0 新增个人中心。','<p>1. 消息通知</p><p>2. 字典重构</p><p>3. 代码生成</p>',1,'L',1,'2',2,1,'2025-10-09 06:10:12','2025-10-09 06:10:12',2,'2025-10-09 06:10:12',2,'2025-10-09 06:10:12',0),(4,'v2.15.0 登录页面改造。','<p>1. 消息通知</p><p>2. 字典重构</p><p>3. 代码生成</p>',1,'L',1,'2',2,1,'2025-10-09 06:10:12','2025-10-09 06:10:12',2,'2025-10-09 06:10:12',2,'2025-10-09 06:10:12',0),(5,'v2.16.0 通知公告、字典翻译组件。','<p>1. 消息通知</p><p>2. 字典重构</p><p>3. 代码生成</p>',1,'L',1,'2',2,1,'2025-10-09 06:10:12','2025-10-09 06:10:12',2,'2025-10-09 06:10:12',2,'2025-10-09 06:10:12',0),(6,'系统将于本周六凌晨 2 点进行维护，预计维护时间为 2 小时。','<p>1. 消息通知</p><p>2. 字典重构</p><p>3. 代码生成</p>',2,'H',1,'2',2,1,'2025-10-09 06:10:12','2025-10-09 06:10:12',2,'2025-10-09 06:10:12',2,'2025-10-09 06:10:12',0),(7,'最近发现一些钓鱼邮件，请大家提高警惕，不要点击陌生链接。','<p>1. 消息通知</p><p>2. 字典重构</p><p>3. 代码生成</p>',3,'L',1,'2',2,1,'2025-10-18 13:15:45','2025-10-18 13:15:42',2,'2025-10-09 06:10:13',2,'2025-10-09 06:10:13',0),(8,'国庆假期从 10 月 1 日至 10 月 7 日放假，共 7 天。','<p>1. 消息通知</p><p>2. 字典重构</p><p>3. 代码生成</p>',4,'L',1,'2',2,1,'2025-10-09 06:10:13','2025-10-09 06:10:13',2,'2025-10-09 06:10:13',2,'2025-10-09 06:10:13',0),(9,'公司将在 10 月 15 日举办新产品发布会，敬请期待。','公司将在 10 月 15 日举办新产品发布会，敬请期待。',5,'H',1,'2',2,1,'2025-10-09 06:10:13','2025-10-09 06:10:13',2,'2025-10-09 06:10:13',2,'2025-10-09 06:10:13',0),(10,'v2.16.1 版本发布。','v2.16.1 版本修复了 WebSocket 重复连接导致的后台线程阻塞问题，优化了通知公告。',1,'M',1,'2',2,1,'2025-10-09 06:10:13','2025-10-09 06:10:13',2,'2025-10-09 06:10:13',2,'2025-10-09 06:10:13',0);
/*!40000 ALTER TABLE `sys_notice` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_role`
--

DROP TABLE IF EXISTS `sys_role`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_role` (
                            `id` bigint NOT NULL AUTO_INCREMENT,
                            `name` varchar(64) NOT NULL COMMENT '角色名称',
                            `code` varchar(32) NOT NULL COMMENT '角色编码',
                            `sort` int DEFAULT NULL COMMENT '显示顺序',
                            `status` tinyint(1) DEFAULT '1' COMMENT '角色状态(1-正常 0-停用)',
                            `data_scope` tinyint DEFAULT NULL COMMENT '数据权限(1-所有数据 2-部门及子部门数据 3-本部门数据 4-本人数据)',
                            `create_by` bigint DEFAULT NULL COMMENT '创建人 ID',
                            `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                            `update_by` bigint DEFAULT NULL COMMENT '更新人ID',
                            `update_time` datetime DEFAULT NULL COMMENT '更新时间',
                            `is_deleted` tinyint(1) DEFAULT '0' COMMENT '逻辑删除标识(0-未删除 1-已删除)',
                            PRIMARY KEY (`id`) USING BTREE,
                            UNIQUE KEY `uk_name` (`name`) USING BTREE COMMENT '角色名称唯一索引',
                            UNIQUE KEY `uk_code` (`code`) USING BTREE COMMENT '角色编码唯一索引'
) COMMENT='角色表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_role`
--

LOCK TABLES `sys_role` WRITE;
/*!40000 ALTER TABLE `sys_role` DISABLE KEYS */;
INSERT INTO `sys_role` VALUES (1,'超级管理员','ROOT',1,1,1,NULL,'2025-10-09 06:09:55',NULL,'2025-10-09 06:09:55',0),(2,'系统管理员','ADMIN',2,1,1,NULL,'2025-10-09 06:09:55',NULL,'2025-10-19 20:45:43',0),(3,'访问游客','GUEST',3,1,3,NULL,'2025-10-09 06:09:55',NULL,'2025-10-19 19:48:33',0),(4,'系统管理员1','ADMIN1',4,1,1,NULL,'2025-10-09 06:09:55',NULL,NULL,0),(5,'系统管理员2','ADMIN2',5,1,1,NULL,'2025-10-09 06:09:55',NULL,NULL,0),(6,'系统管理员3','ADMIN3',6,1,1,NULL,'2025-10-09 06:09:55',NULL,NULL,0),(7,'系统管理员4','ADMIN4',7,1,1,NULL,'2025-10-09 06:09:55',NULL,NULL,0),(8,'系统管理员5','ADMIN5',8,1,1,NULL,'2025-10-09 06:09:56',NULL,NULL,0),(9,'系统管理员6','ADMIN6',9,1,1,NULL,'2025-10-09 06:09:56',NULL,NULL,0),(10,'系统管理员7','ADMIN7',10,1,1,NULL,'2025-10-09 06:09:56',NULL,NULL,0),(11,'系统管理员8','ADMIN8',11,1,1,NULL,'2025-10-09 06:09:56',NULL,NULL,0),(12,'系统管理员9','ADMIN9',12,1,1,NULL,'2025-10-09 06:09:56',NULL,NULL,0);
/*!40000 ALTER TABLE `sys_role` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_role_menu`
--

DROP TABLE IF EXISTS `sys_role_menu`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_role_menu` (
                                 `role_id` bigint NOT NULL COMMENT '角色ID',
                                 `menu_id` bigint NOT NULL COMMENT '菜单ID',
                                 UNIQUE KEY `uk_roleid_menuid` (`role_id`,`menu_id`) USING BTREE COMMENT '角色菜单唯一索引'
) COMMENT='角色和菜单关联表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_role_menu`
--

LOCK TABLES `sys_role_menu` WRITE;
/*!40000 ALTER TABLE `sys_role_menu` DISABLE KEYS */;
INSERT INTO `sys_role_menu` VALUES (2,1),(2,2),(2,3),(2,4),(2,5),(2,6),(2,31),(2,32),(2,33),(2,70),(2,71),(2,72),(2,73),(2,74),(2,75),(2,76),(2,77),(2,78),(2,79),(2,81),(2,84),(2,88),(2,105),(2,106),(2,107),(2,117),(2,118),(2,119),(2,120),(2,121),(2,122),(2,123),(2,124),(2,125),(2,126),(2,127),(2,128),(2,129),(2,130),(2,133),(2,134),(2,135),(2,136),(2,137),(2,138),(2,139),(2,140),(2,141),(2,142),(2,143),(2,152),(2,153),(2,154),(2,156),(2,157),(2,158),(2,159),(2,160),(2,161),(2,162),(2,173),(2,174),(2,175),(2,176),(2,177),(3,1),(3,2),(3,3),(3,4),(3,5),(3,6),(3,105),(3,117),(3,120),(3,121),(3,126),(3,127),(3,135),(3,139),(3,140),(3,141),(3,142),(3,143),(3,152),(3,153),(3,154),(3,157),(3,158),(3,159),(3,163),(3,167),(3,168),(3,169),(3,173),(3,174),(3,178),(3,179);
/*!40000 ALTER TABLE `sys_role_menu` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_user`
--

DROP TABLE IF EXISTS `sys_user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_user` (
                            `id` bigint NOT NULL AUTO_INCREMENT,
                            `username` varchar(64) DEFAULT NULL COMMENT '用户名',
                            `nickname` varchar(64) DEFAULT NULL COMMENT '昵称',
                            `gender` tinyint(1) DEFAULT '1' COMMENT '性别((1-男 2-女 0-保密)',
                            `password` varchar(100) DEFAULT NULL COMMENT '密码',
                            `dept_id` int DEFAULT NULL COMMENT '部门ID',
                            `avatar_file_id` bigint DEFAULT '1' COMMENT '头像文件ID(关联 file_info.id)',
                            `mobile` varchar(20) DEFAULT NULL COMMENT '联系方式',
                            `status` tinyint(1) DEFAULT '1' COMMENT '状态(1-正常 0-禁用)',
                            `email` varchar(128) DEFAULT NULL COMMENT '用户邮箱',
                            `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                            `create_by` bigint DEFAULT NULL COMMENT '创建人ID',
                            `update_time` datetime DEFAULT NULL COMMENT '更新时间',
                            `update_by` bigint DEFAULT NULL COMMENT '修改人ID',
                            `is_deleted` tinyint(1) DEFAULT '0' COMMENT '逻辑删除标识(0-未删除 1-已删除)',
                            `openid` char(28) DEFAULT NULL COMMENT '微信 openid',
                            PRIMARY KEY (`id`) USING BTREE,
                            KEY `login_name` (`username`)
) COMMENT='用户信息表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_user`
--

LOCK TABLES `sys_user` WRITE;
/*!40000 ALTER TABLE `sys_user` DISABLE KEYS */;
INSERT INTO `sys_user` VALUES (1,'可变羽翼核心','可变羽翼核心',1,'$2a$10$Nzc2W1st8K82zD5lizYk0eAj8YMQLnzvmZPDKpAR7RGKZbU1vQUjK',1,97,'13799785418',1,'1143134686@qq.com','2025-10-09 06:10:09',NULL,'2025-10-21 18:56:52',NULL,0,NULL),(3,'test','测试小用户',1,'$2a$10$xVWsNOhHrCxh5UbpCE7/HuJ.PAOKcYAqRxD2CO2nVnJS.IAXkr5aq',3,97,'13799785418',1,'2427297375@qq.com','2025-10-09 06:10:09',NULL,'2025-10-09 06:10:09',NULL,0,NULL);
/*!40000 ALTER TABLE `sys_user` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_user_notice`
--

DROP TABLE IF EXISTS `sys_user_notice`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_user_notice` (
                                   `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'id',
                                   `notice_id` bigint NOT NULL COMMENT '公共通知id',
                                   `user_id` bigint NOT NULL COMMENT '用户id',
                                   `is_read` bigint DEFAULT '0' COMMENT '读取状态（0: 未读, 1: 已读）',
                                   `read_time` datetime DEFAULT NULL COMMENT '阅读时间',
                                   `create_time` datetime NOT NULL COMMENT '创建时间',
                                   `update_time` datetime DEFAULT NULL COMMENT '更新时间',
                                   `is_deleted` tinyint DEFAULT '0' COMMENT '逻辑删除(0: 未删除, 1: 已删除)',
                                   PRIMARY KEY (`id`) USING BTREE
) COMMENT='用户通知公告表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_user_notice`
--

LOCK TABLES `sys_user_notice` WRITE;
/*!40000 ALTER TABLE `sys_user_notice` DISABLE KEYS */;
/*!40000 ALTER TABLE `sys_user_notice` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_user_role`
--

DROP TABLE IF EXISTS `sys_user_role`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_user_role` (
                                 `user_id` bigint NOT NULL COMMENT '用户ID',
                                 `role_id` bigint NOT NULL COMMENT '角色ID',
                                 PRIMARY KEY (`user_id`,`role_id`) USING BTREE
) COMMENT='用户和角色关联表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_user_role`
--

LOCK TABLES `sys_user_role` WRITE;
/*!40000 ALTER TABLE `sys_user_role` DISABLE KEYS */;
INSERT INTO `sys_user_role` VALUES (1,1),(3,3);
/*!40000 ALTER TABLE `sys_user_role` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping routines for database 'wing-blog'
--
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-03-16 10:18:52
