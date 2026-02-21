-- ===================================================================
--                     医疗资讯表 (medical_news)
-- ===================================================================
-- 说明：
-- 1. Markdown 文件包含所有内容（文字、图片、视频链接）
-- 2. 封面图单独存储，用于轮播图展示
-- 3. OSS 目录结构：
--    - smart-medicine/news/covers/     封面图
--    - smart-medicine/news/content/    Markdown 文件
--    - smart-medicine/news/images/     Markdown 内容中引用的图片
--    - smart-medicine/news/videos/     Markdown 内容中引用的视频
-- ===================================================================

CREATE TABLE `medical_news` (
    `id` INT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `news_name` VARCHAR(255) NOT NULL COMMENT '资讯标题',
    `news_summary` VARCHAR(500) DEFAULT NULL COMMENT '资讯摘要（列表展示）',
    `cover_oss_path` VARCHAR(500) NOT NULL COMMENT '封面图OSS路径（smart-medicine/news/covers/xxx.jpg）',
    `markdown_oss_path` VARCHAR(500) NOT NULL COMMENT 'Markdown文件OSS路径（smart-medicine/news/content/xxx.md）',

    `category` VARCHAR(50) DEFAULT NULL COMMENT '分类',
    `author` VARCHAR(100) DEFAULT NULL COMMENT '作者',
    `status` TINYINT DEFAULT 0 COMMENT '状态：0-草稿，1-已发布',
    `view_count` INT DEFAULT 0 COMMENT '浏览量',

    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',

    PRIMARY KEY (`id`),
    INDEX `idx_status` (`status`),
    INDEX `idx_category` (`category`),
    INDEX `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='医疗资讯表';
