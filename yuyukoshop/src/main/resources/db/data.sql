USE yuyukoshop;

-- 插入管理员用户 (密码: admin123)
INSERT INTO `user` (`username`, `password`, `nickname`, `email`, `role`, `status`, `create_time`, `update_time`) VALUES
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EO', '管理员', 'admin@yuyukoshop.com', 'ADMIN', 1, NOW(), NOW());

-- 插入商品分类
INSERT INTO `product_category` (`parent_id`, `name`, `sort_order`, `status`, `create_time`, `update_time`) VALUES
(0, '手办模型', 1, 1, NOW(), NOW()),
(0, '毛绒玩具', 2, 1, NOW(), NOW()),
(0, '服饰配件', 3, 1, NOW(), NOW()),
(0, '海报挂画', 4, 1, NOW(), NOW()),
(0, '动漫周边', 5, 1, NOW(), NOW()),
(1, '比例手办', 1, 1, NOW(), NOW()),
(1, 'Q版手办', 2, 1, NOW(), NOW()),
(3, 'T恤', 1, 1, NOW(), NOW()),
(3, '卫衣', 2, 1, NOW(), NOW()),
(5, '钥匙扣', 1, 1, NOW(), NOW()),
(5, '徽章', 2, 1, NOW(), NOW());

-- 插入测试商品
INSERT INTO `product` (`category_id`, `name`, `description`, `price`, `stock`, `sales`, `thumbnail`, `status`, `create_time`, `update_time`) VALUES
(6, '初音未来 1/7 手办', '精美初音未来比例手办，高度约25cm，细节精致', 599.00, 100, 50, 'https://example.com/miku.jpg', 1, NOW(), NOW()),
(7, '雷姆 Q版手办', '可爱Q版雷姆手办，高度约10cm', 199.00, 200, 120, 'https://example.com/rem.jpg', 1, NOW(), NOW()),
(8, '鬼灭之刃 炭治郎T恤', '纯棉材质，舒适透气，印有炭治郎图案', 129.00, 500, 300, 'https://example.com/tanjiro.jpg', 1, NOW(), NOW()),
(9, '进击的巨人 调查兵团卫衣', '加厚连帽卫衣，背面印有自由之翼标志', 259.00, 150, 80, 'https://example.com/survey.jpg', 1, NOW(), NOW()),
(10, '海贼王 草帽团钥匙扣套装', '一套9个，包含路飞、索隆、娜美等角色', 59.00, 1000, 600, 'https://example.com/keychain.jpg', 1, NOW(), NOW()),
(11, '间谍过家家 阿尼亚徽章', '金属材质，直径58mm，附带安全别针', 29.00, 800, 400, 'https://example.com/anya.jpg', 1, NOW(), NOW()),
(2, '皮卡丘 大号毛绒玩具', '正版授权，高度约45cm，超柔软材质', 299.00, 80, 45, 'https://example.com/pikachu.jpg', 1, NOW(), NOW()),
(4, '你的名字 星空海报', 'A2尺寸，高清印刷，附赠海报筒', 49.00, 300, 150, 'https://example.com/poster.jpg', 1, NOW(), NOW()),
(6, ' Saber 1/7 手办', 'Fate系列Saber阿尔托莉雅手办', 899.00, 50, 30, 'https://example.com/saber.jpg', 1, NOW(), NOW()),
(8, 'EVA 初号机T恤', '夜光印花设计，纯棉面料', 159.00, 200, 100, 'https://example.com/eva.jpg', 1, NOW(), NOW());
