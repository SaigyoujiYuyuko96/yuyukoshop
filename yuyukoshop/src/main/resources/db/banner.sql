-- 首页轮播图表
-- 支持后台动态配置轮播图

USE yuyukoshop;

-- 轮播图表
CREATE TABLE IF NOT EXISTS `banner` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `title` VARCHAR(100) NOT NULL COMMENT '轮播图标题',
    `subtitle` VARCHAR(200) COMMENT '副标题/描述',
    `image_url` VARCHAR(500) NOT NULL COMMENT '图片URL',
    `link_url` VARCHAR(500) COMMENT '跳转链接',
    `sort_order` INT DEFAULT 0 COMMENT '排序（越小越靠前）',
    `status` TINYINT DEFAULT 1 COMMENT '状态: 0-禁用 1-启用',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX `idx_status` (`status`),
    INDEX `idx_sort` (`sort_order`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='首页轮播图表';

-- 插入默认轮播图数据
INSERT INTO `banner` (`title`, `subtitle`, `image_url`, `link_url`, `sort_order`, `status`) VALUES
('幽幽子Shop', '你的专属动漫周边商城', '/images/banner1.jpg', '/products', 1, 1),
('热门手办', '精美手办，还原经典角色', '/images/banner2.jpg', '/products?category=1', 2, 1),
('社区互动', '与同好分享你的收藏与热爱', '/images/banner3.jpg', '/community', 3, 1);
