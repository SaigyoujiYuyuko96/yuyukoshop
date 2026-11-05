-- 数据库索引优化建议
-- 为提升查询性能，建议添加以下索引

USE yuyukoshop;

-- 1. 购物车表：添加用户+商品的联合唯一索引，防止重复添加
ALTER TABLE `cart_item` 
ADD UNIQUE INDEX `uk_user_product` (`user_id`, `product_id`);

-- 2. 订单表：添加复合索引优化订单查询
ALTER TABLE `order_info`
ADD INDEX `idx_user_status` (`user_id`, `status`);

-- 3. 订单项表：添加订单ID索引（已有）
-- INDEX `idx_order` (`order_id`) 已存在

-- 4. 商品表：优化库存查询索引
ALTER TABLE `product`
ADD INDEX `idx_status_stock` (`status`, `stock`);

-- 5. 评论表：添加父评论索引优化回复查询
ALTER TABLE `comment`
ADD INDEX `idx_parent` (`parent_id`);

-- 6. 帖子表：优化列表查询
ALTER TABLE `post`
ADD INDEX `idx_status_create` (`status`, `create_time`);

-- 7. 用户地址表：添加用户+默认地址索引
ALTER TABLE `user_address`
ADD INDEX `idx_user_default` (`user_id`, `is_default`);

-- 8. 用户收藏表：已有唯一索引
-- UNIQUE KEY `uk_user_product` (`user_id`, `product_id`) 已存在
