USE yuyukoshop;

-- ============================================================
-- 丰富数据库测试数据
-- 密码统一为 123456（BCrypt加密）
-- ============================================================

-- 1. 插入普通用户
INSERT INTO `user` (`username`, `password`, `nickname`, `avatar`, `email`, `phone`, `role`, `status`, `create_time`, `update_time`) VALUES
('sakura', '$2a$10$EixZaYVK1fsbw1ZfbX3OXePaWxn96p36Kz1HEi6RDYMhYu/5GH0C6', '小樱', 'https://example.com/avatar/sakura.jpg', 'sakura@yuyukoshop.com', '13800000001', 'USER', 1, NOW() - INTERVAL 30 DAY, NOW()),
('naruto_fan', '$2a$10$EixZaYVK1fsbw1ZfbX3OXePaWxn96p36Kz1HEi6RDYMhYu/5GH0C6', '鸣人迷', 'https://example.com/avatar/naruto.jpg', 'naruto@yuyukoshop.com', '13800000002', 'USER', 1, NOW() - INTERVAL 25 DAY, NOW()),
('miku_lover', '$2a$10$EixZaYVK1fsbw1ZfbX3OXePaWxn96p36Kz1HEi6RDYMhYu/5GH0C6', '未来酱', 'https://example.com/avatar/miku.jpg', 'miku@yuyukoshop.com', '13800000003', 'USER', 1, NOW() - INTERVAL 20 DAY, NOW()),
('otaku_king', '$2a$10$EixZaYVK1fsbw1ZfbX3OXePaWxn96p36Kz1HEi6RDYMhYu/5GH0C6', '宅王', 'https://example.com/avatar/otaku.jpg', 'otaku@yuyukoshop.com', '13800000004', 'USER', 1, NOW() - INTERVAL 15 DAY, NOW()),
('anime_girl', '$2a$10$EixZaYVK1fsbw1ZfbX3OXePaWxn96p36Kz1HEi6RDYMhYu/5GH0C6', '二次元少女', 'https://example.com/avatar/girl.jpg', 'girl@yuyukoshop.com', '13800000005', 'USER', 1, NOW() - INTERVAL 10 DAY, NOW()),
('gundam_pilot', '$2a$10$EixZaYVK1fsbw1ZfbX3OXePaWxn96p36Kz1HEi6RDYMhYu/5GH0C6', '高达驾驶员', 'https://example.com/avatar/gundam.jpg', 'gundam@yuyukoshop.com', '13800000006', 'USER', 1, NOW() - INTERVAL 7 DAY, NOW()),
('cosplay_star', '$2a$10$EixZaYVK1fsbw1ZfbX3OXePaWxn96p36Kz1HEi6RDYMhYu/5GH0C6', 'COS之星', 'https://example.com/avatar/cosplay.jpg', 'cosplay@yuyukoshop.com', '13800000007', 'USER', 1, NOW() - INTERVAL 5 DAY, NOW()),
('dragon_ball', '$2a$10$EixZaYVK1fsbw1ZfbX3OXePaWxn96p36Kz1HEi6RDYMhYu/5GH0C6', '龙珠迷', 'https://example.com/avatar/dragonball.jpg', 'dragonball@yuyukoshop.com', '13800000008', 'USER', 1, NOW() - INTERVAL 3 DAY, NOW());

-- 2. 插入更多商品（在已有10个基础上添加）
INSERT INTO `product` (`category_id`, `name`, `description`, `price`, `stock`, `sales`, `thumbnail`, `status`, `create_time`, `update_time`) VALUES
-- 比例手办(category_id=6)
(6, '绫波丽 1/8 手办', '新世纪福音战士绫波丽战斗服版本，高度约23cm', 699.00, 60, 35, 'https://example.com/rei.jpg', 1, NOW() - INTERVAL 28 DAY, NOW()),
(6, '明日香 1/7 手办', 'EVA明日香红色战斗服经典造型，高度约25cm', 729.00, 45, 28, 'https://example.com/asuka.jpg', 1, NOW() - INTERVAL 25 DAY, NOW()),
(6, '炭治郎 1/8 手办', '鬼灭之刃水之呼吸造型，高度约22cm', 559.00, 80, 65, 'https://example.com/tanjiro_fig.jpg', 1, NOW() - INTERVAL 20 DAY, NOW()),
(6, '富冈义勇 1/8 手办', '鬼灭之刃水柱经典战斗姿态，高度约26cm', 659.00, 40, 22, 'https://example.com/tomioka.jpg', 1, NOW() - INTERVAL 15 DAY, NOW()),
-- Q版手办(category_id=7)
(7, '初音未来 Q版手办', '可爱Q版初音，附带底座和微缩吉他', 149.00, 300, 200, 'https://example.com/miku_q.jpg', 1, NOW() - INTERVAL 22 DAY, NOW()),
(7, '灶门祢豆子 Q版手办', '鬼灭之刃祢豆子萌系造型，附竹子配件', 169.00, 250, 180, 'https://example.com/nezuko_q.jpg', 1, NOW() - INTERVAL 18 DAY, NOW()),
(7, '利威尔兵长 Q版手办', '进击的巨人人类最强兵长Q版，附双刀', 159.00, 180, 95, 'https://example.com/levi_q.jpg', 1, NOW() - INTERVAL 12 DAY, NOW()),
-- T恤(category_id=8)
(8, '咒术回战 五条悟T恤', '纯棉面料，正面五条悟术式反转图案', 139.00, 400, 250, 'https://example.com/gojo.jpg', 1, NOW() - INTERVAL 16 DAY, NOW()),
(8, '火影忍者 木叶村T恤', '经典木叶村标志，多色可选', 119.00, 600, 380, 'https://example.com/konoha.jpg', 1, NOW() - INTERVAL 10 DAY, NOW()),
(8, '间谍过家家 劳埃德一家T恤', '温馨家庭主题印花，亲子款可选', 135.00, 350, 190, 'https://example.com/forger.jpg', 1, NOW() - INTERVAL 5 DAY, NOW()),
-- 卫衣(category_id=9)
(9, '鬼灭之刃 鬼杀队卫衣', '黑色加厚款，胸前鬼杀队标志刺绣', 279.00, 120, 70, 'https://example.com/demon_slayer_hoodie.jpg', 1, NOW() - INTERVAL 14 DAY, NOW()),
(9, '咒术回战 东京都立咒术高专卫衣', '校服风格设计，帽子后方有校徽', 289.00, 100, 55, 'https://example.com/jujutsu_hoodie.jpg', 1, NOW() - INTERVAL 8 DAY, NOW()),
-- 钥匙扣(category_id=10)
(10, '咒术回战 五条悟钥匙扣', '亚克力材质双面印刷，附金属扣环', 35.00, 1500, 800, 'https://example.com/gojo_key.jpg', 1, NOW() - INTERVAL 18 DAY, NOW()),
(10, '鬼灭之刃 全角色钥匙扣', '9款套装，含主角团+柱', 89.00, 800, 520, 'https://example.com/kimetsu_key.jpg', 1, NOW() - INTERVAL 12 DAY, NOW()),
-- 徽章(category_id=11)
(11, '进击的巨人 调查兵团徽章套装', '5枚套装含自由之翼、立体机动装置等', 49.00, 1200, 650, 'https://example.com/aot_badge.jpg', 1, NOW() - INTERVAL 20 DAY, NOW()),
(11, '火影忍者 晓组织徽章', '黑底红云纹路，直径58mm金属材质', 39.00, 900, 480, 'https://example.com/akatsuki_badge.jpg', 1, NOW() - INTERVAL 14 DAY, NOW()),
-- 毛绒玩具(category_id=2)
(2, '龙猫 中号毛绒玩具', '吉卜力正版授权，高度约30cm', 259.00, 100, 60, 'https://example.com/totoro.jpg', 1, NOW() - INTERVAL 24 DAY, NOW()),
(2, '哆啦A梦 毛绒抱枕', '高度约50cm，超柔软PP棉填充', 189.00, 150, 85, 'https://example.com/doraemon.jpg', 1, NOW() - INTERVAL 18 DAY, NOW()),
-- 海报挂画(category_id=4)
(4, '千与千寻 海报套装', '3张A2尺寸高清海报，含千寻、白龙、无脸男', 99.00, 400, 210, 'https://example.com/spirited_away.jpg', 1, NOW() - INTERVAL 22 DAY, NOW()),
(4, '你的名字 晴天挂画', '布面挂画含画框，60x40cm', 129.00, 200, 120, 'https://example.com/yourname_wall.jpg', 1, NOW() - INTERVAL 15 DAY, NOW());

-- 3. 插入社区帖子
INSERT INTO `post` (`user_id`, `forum_id`, `title`, `content`, `view_count`, `like_count`, `comment_count`, `is_essence`, `is_top`, `status`, `create_time`, `update_time`) VALUES
(2, 0, '初音未来新1/7手办入手！开箱分享', '等了三个月终于到货了！这次初音未来的1/7手办做工真的超级精细，头发渐变色效果超赞，裙摆的褶皱也很自然。强烈推荐给初音粉！唯一的小遗憾是底座稍微有点松，但不影响整体效果。', 580, 45, 12, 1, 0, 1, NOW() - INTERVAL 25 DAY, NOW()),
(3, 0, '鬼灭之刃全系列手办对比测评', '我把目前市面上出的鬼灭手办基本都入手了，从低价的Q版到高端的1/8比例，给大家做一个全面的对比测评。首先是性价比之王...', 920, 78, 23, 1, 1, 1, NOW() - INTERVAL 22 DAY, NOW()),
(4, 0, '求推荐500元以内的动漫周边礼物', '朋友过生日，预算500以内，想送点动漫相关的礼物，大家有什么推荐吗？最好是实用一点的，不太想要纯摆件。', 350, 22, 18, 0, 0, 1, NOW() - INTERVAL 18 DAY, NOW()),
(5, 0, 'EVA新剧场版周边收藏展示', '作为一个20年EVA老粉，来展示一下我的收藏，从初号机模型到各种限定周边，慢慢积累了不少。最得意的是这个签名版海报！', 670, 56, 15, 1, 0, 1, NOW() - INTERVAL 15 DAY, NOW()),
(6, 0, '咒术回战五条悟手办值不值得买？', '最近纠结要不要入五条悟的手办，看网上评价参差不齐。有没有买过的小伙伴来说说做工怎么样？特别是头发部分的涂装。', 420, 33, 20, 0, 0, 1, NOW() - INTERVAL 12 DAY, NOW()),
(2, 0, '动漫T恤日常穿搭分享', '买了很多动漫T恤，分享一下日常怎么搭配不会显得太宅。关键是要选图案不那么花哨的款式，纯色底+小logo的最好搭配。', 290, 19, 8, 0, 0, 1, NOW() - INTERVAL 10 DAY, NOW()),
(7, 0, 'COS道具制作教程：如何做一把日轮刀', '作为一个资深COSER，来教大家如何用EVA泡沫做一把逼真的日轮刀。材料费不超过50元，效果却很惊艳！', 810, 67, 25, 1, 0, 1, NOW() - INTERVAL 8 DAY, NOW()),
(8, 0, '海贼王草帽团钥匙扣开箱', '这套钥匙扣质量超出预期，每个角色都很还原。最喜欢乔巴的造型，太可爱了！附赠的收纳袋也很实用。', 230, 15, 6, 0, 0, 1, NOW() - INTERVAL 6 DAY, NOW()),
(3, 0, '推荐几个小众但品质很好的动漫周边品牌', '大家都在买大厂的手办，但我发现有几个小众品牌做工不输大厂，价格还便宜一半。这里推荐三个我亲测不错的品牌...', 560, 42, 14, 0, 0, 1, NOW() - INTERVAL 5 DAY, NOW()),
(4, 0, '2026年必入手动漫手办预测', '根据各厂商公布的情报，2026年会有很多重磅手办发售，包括 fate 新系列、鬼灭无限列车篇等。提前做好钱包准备！', 450, 38, 16, 0, 0, 1, NOW() - INTERVAL 4 DAY, NOW()),
(5, 0, '手办保养小贴士：防尘防黄变', '手办放久了容易积灰和黄变，分享几个我的保养方法：1. 用亚克力展示盒 2. 避免阳光直射 3. 定期用气吹清理', 310, 28, 9, 0, 0, 1, NOW() - INTERVAL 3 DAY, NOW()),
(6, 0, '进击的巨人完结纪念：我的收藏清单', '虽然巨人在动画方面已经完结了，但周边还在持续出。整理了一下我的全部巨人收藏，从第一季到最终季的都有。', 280, 20, 7, 0, 0, 1, NOW() - INTERVAL 2 DAY, NOW()),
(7, 0, '动漫周边二手市场避坑指南', '入坑两年，在二手市场踩过不少坑。假货、翻新、缺件等问题都有遇到过。整理了一份避坑指南，希望对新手有帮助。', 380, 35, 11, 0, 0, 1, NOW() - INTERVAL 1 DAY, NOW()),
(8, 0, '卫衣穿搭：从宅家到出街一套搞定', '最近入的鬼杀队卫衣真的太舒服了，加厚面料冬天穿正合适。而且黑色底配刺绣标志，出门穿也不会太突兀。', 180, 12, 5, 0, 0, 1, NOW() - INTERVAL 12 HOUR, NOW()),
(9, 0, '新人入坑动漫手办应该注意什么？', '作为一个刚入坑半年的萌新，分享一下我的经验教训。最重要的是：不要一上来就买最贵的！先从Q版和景品入手，确认自己真的喜欢再投钱。', 250, 18, 13, 0, 0, 1, NOW() - INTERVAL 6 HOUR, NOW());

-- 4. 插入评论
INSERT INTO `comment` (`post_id`, `user_id`, `content`, `parent_id`, `status`, `create_time`, `update_time`) VALUES
-- 帖子1的评论
(1, 3, '这个渐变色效果我也超喜欢！请问是在哪入的？', NULL, 1, NOW() - INTERVAL 24 DAY, NOW()),
(1, 2, '在某宝的官方旗舰店买的，等了好久', 1, 1, NOW() - INTERVAL 24 DAY, NOW()),
(1, 4, '底座松的问题可以用蓝胶固定一下', 1, 1, NOW() - INTERVAL 23 DAY, NOW()),
-- 帖子2的评论
(2, 5, '坐等更新！鬼灭的手办我一直想入', NULL, 1, NOW() - INTERVAL 21 DAY, NOW()),
(2, 6, 'Q版性价比确实高，我入了一整套', NULL, 1, NOW() - INTERVAL 21 DAY, NOW()),
(2, 3, '1/8比例的做工差距确实明显，但价格也差很多', NULL, 1, NOW() - INTERVAL 20 DAY, NOW()),
-- 帖子3的评论
(3, 2, '推荐亚克力立牌，好看又不贵', NULL, 1, NOW() - INTERVAL 17 DAY, NOW()),
(3, 5, '钥匙扣套装也不错，实用又好看', NULL, 1, NOW() - INTERVAL 17 DAY, NOW()),
(3, 7, '我觉得T恤是最好的选择，天天能穿', NULL, 1, NOW() - INTERVAL 16 DAY, NOW()),
(3, 8, '卫衣了解一下，实用度拉满', 9, 1, NOW() - INTERVAL 16 DAY, NOW()),
-- 帖子4的评论
(4, 6, '这个签名版太羡慕了！', NULL, 1, NOW() - INTERVAL 14 DAY, NOW()),
(4, 3, 'EVA的周边真的值得收藏，升值空间也大', NULL, 1, NOW() - INTERVAL 14 DAY, NOW()),
-- 帖子5的评论
(5, 4, '头发涂装还行，但面部表情稍微有点怪', NULL, 1, NOW() - INTERVAL 11 DAY, NOW()),
(5, 7, '我入了，整体还是可以的，瑕不掩瑜', NULL, 1, NOW() - INTERVAL 11 DAY, NOW()),
(5, 2, '建议等促销的时候再入，性价比更高', NULL, 1, NOW() - INTERVAL 10 DAY, NOW()),
-- 帖子7的评论
(7, 2, '教程太详细了！已收藏', NULL, 1, NOW() - INTERVAL 7 DAY, NOW()),
(7, 4, 'EVA泡沫哪里买比较便宜？', NULL, 1, NOW() - INTERVAL 7 DAY, NOW()),
(7, 8, '做出来的效果真的和正版道具一样', NULL, 1, NOW() - INTERVAL 6 DAY, NOW()),
-- 帖子9的评论
(9, 5, '求品牌名称！', NULL, 1, NOW() - INTERVAL 4 DAY, NOW()),
(9, 6, '小众品牌确实有惊喜，我之前入过一个很不错的', NULL, 1, NOW() - INTERVAL 4 DAY, NOW()),
-- 帖子13的评论
(13, 2, '二手水太深了，感谢分享', NULL, 1, NOW() - INTERVAL 20 HOUR, NOW()),
(13, 4, '买二手一定要录开箱视频留证据', NULL, 1, NOW() - INTERVAL 18 HOUR, NOW()),
-- 帖子15的评论
(15, 3, '说得太对了，我就是一开始买太贵后悔了', NULL, 1, NOW() - INTERVAL 5 HOUR, NOW()),
(15, 6, '景品手办真的是新手的最佳选择', NULL, 1, NOW() - INTERVAL 4 HOUR, NOW()),
(15, 7, '还要注意辨别正版和盗版，新手容易被骗', 25, 1, NOW() - INTERVAL 3 HOUR, NOW());

-- 5. 插入用户地址
INSERT INTO `user_address` (`user_id`, `receiver_name`, `receiver_phone`, `province`, `city`, `district`, `detail_address`, `is_default`, `create_time`, `update_time`) VALUES
(2, '张小樱', '13800000001', '上海市', '上海市', '浦东新区', '张江高科技园区碧波路690号', 1, NOW(), NOW()),
(2, '张小樱', '13800000001', '北京市', '北京市', '海淀区', '中关村大街1号', 0, NOW(), NOW()),
(3, '李鸣人', '13800000002', '广东省', '广州市', '天河区', '天河路385号太古汇', 1, NOW(), NOW()),
(4, '王未来', '13800000003', '浙江省', '杭州市', '西湖区', '文三路478号华星科技大厦', 1, NOW(), NOW()),
(5, '赵宅王', '13800000004', '江苏省', '南京市', '鼓楼区', '中山北路283号南京新百', 1, NOW(), NOW()),
(6, '刘二次元', '13800000005', '四川省', '成都市', '武侯区', '天府大道999号', 1, NOW(), NOW()),
(7, '陈高达', '13800000006', '湖北省', '武汉市', '洪山区', '光谷大道77号', 1, NOW(), NOW()),
(8, '杨COS', '13800000007', '湖南省', '长沙市', '芙蓉区', '五一大道568号', 1, NOW(), NOW()),
(9, '吴龙珠', '13800000008', '福建省', '厦门市', '思明区', '厦禾路888号', 1, NOW(), NOW());

-- 6. 插入购物车数据
INSERT INTO `cart_item` (`user_id`, `product_id`, `quantity`, `price`, `create_time`, `update_time`) VALUES
(2, 1, 1, 599.00, NOW(), NOW()),
(2, 11, 2, 699.00, NOW(), NOW()),
(3, 5, 3, 59.00, NOW(), NOW()),
(4, 13, 1, 559.00, NOW(), NOW()),
(5, 3, 1, 129.00, NOW(), NOW()),
(5, 19, 1, 279.00, NOW(), NOW()),
(6, 17, 1, 149.00, NOW(), NOW()),
(8, 21, 2, 35.00, NOW(), NOW()),
(9, 9, 1, 899.00, NOW(), NOW());

-- 7. 插入订单数据
INSERT INTO `order_info` (`user_id`, `order_no`, `total_amount`, `status`, `payment_method`, `shipping_address`, `receiver_name`, `receiver_phone`, `tracking_no`, `remark`, `pay_time`, `ship_time`, `create_time`, `update_time`) VALUES
(2, 'ORD20260401001', 599.00, 3, 'ALIPAY', '上海市浦东新区张江高科技园区碧波路690号', '张小樱', '13800000001', 'SF1234567890', NULL, NOW() - INTERVAL 35 DAY, NOW() - INTERVAL 33 DAY, NOW() - INTERVAL 36 DAY, NOW() - INTERVAL 30 DAY),
(2, 'ORD20260410001', 728.00, 3, 'WECHAT', '北京市海淀区中关村大街1号', '张小樱', '13800000001', 'SF2345678901', '请小心轻放', NOW() - INTERVAL 25 DAY, NOW() - INTERVAL 23 DAY, NOW() - INTERVAL 26 DAY, NOW() - INTERVAL 20 DAY),
(3, 'ORD20260412001', 177.00, 3, 'ALIPAY', '广东省广州市天河区天河路385号太古汇', '李鸣人', '13800000002', 'YT3456789012', NULL, NOW() - INTERVAL 22 DAY, NOW() - INTERVAL 20 DAY, NOW() - INTERVAL 23 DAY, NOW() - INTERVAL 18 DAY),
(4, 'ORD20260415001', 899.00, 2, 'ALIPAY', '浙江省杭州市西湖区文三路478号华星科技大厦', '王未来', '13800000003', 'SF4567890123', NULL, NOW() - INTERVAL 18 DAY, NOW() - INTERVAL 16 DAY, NOW() - INTERVAL 19 DAY, NOW() - INTERVAL 16 DAY),
(5, 'ORD20260420001', 388.00, 1, 'WECHAT', '江苏省南京市鼓楼区中山北路283号南京新百', '赵宅王', '13800000004', NULL, NULL, NOW() - INTERVAL 14 DAY, NULL, NOW() - INTERVAL 15 DAY, NOW() - INTERVAL 14 DAY),
(6, 'ORD20260425001', 149.00, 1, 'ALIPAY', '四川省成都市武侯区天府大道999号', '刘二次元', '13800000005', NULL, '希望有初音的贴纸', NOW() - INTERVAL 8 DAY, NULL, NOW() - INTERVAL 9 DAY, NOW() - INTERVAL 8 DAY),
(7, 'ORD20260428001', 518.00, 0, NULL, '湖北省武汉市洪山区光谷大道77号', '陈高达', '13800000006', NULL, NULL, NULL, NULL, NOW() - INTERVAL 3 DAY, NOW() - INTERVAL 3 DAY),
(8, 'ORD20260430001', 70.00, 0, NULL, '湖南省长沙市芙蓉区五一大道568号', '杨COS', '13800000007', NULL, NULL, NULL, NULL, NOW() - INTERVAL 1 DAY, NOW() - INTERVAL 1 DAY);

-- 8. 插入订单项
INSERT INTO `order_item` (`order_id`, `product_id`, `product_name`, `product_thumbnail`, `quantity`, `price`, `create_time`) VALUES
(1, 1, '初音未来 1/7 手办', 'https://example.com/miku.jpg', 1, 599.00, NOW() - INTERVAL 36 DAY),
(2, 2, '雷姆 Q版手办', 'https://example.com/rem.jpg', 1, 199.00, NOW() - INTERVAL 26 DAY),
(2, 6, '间谍过家家 阿尼亚徽章', 'https://example.com/anya.jpg', 2, 29.00, NOW() - INTERVAL 26 DAY),
(2, 5, '海贼王 草帽团钥匙扣套装', 'https://example.com/keychain.jpg', 1, 59.00, NOW() - INTERVAL 26 DAY),
(3, 7, '皮卡丘 大号毛绒玩具', 'https://example.com/pikachu.jpg', 1, 299.00, NOW() - INTERVAL 23 DAY),
(3, 8, '你的名字 星空海报', 'https://example.com/poster.jpg', 1, 49.00, NOW() - INTERVAL 23 DAY),
(4, 9, 'Saber 1/7 手办', 'https://example.com/saber.jpg', 1, 899.00, NOW() - INTERVAL 19 DAY),
(5, 3, '鬼灭之刃 炭治郎T恤', 'https://example.com/tanjiro.jpg', 1, 129.00, NOW() - INTERVAL 15 DAY),
(5, 23, '鬼灭之刃 鬼杀队卫衣', 'https://example.com/demon_slayer_hoodie.jpg', 1, 259.00, NOW() - INTERVAL 15 DAY),
(6, 16, '初音未来 Q版手办', 'https://example.com/miku_q.jpg', 1, 149.00, NOW() - INTERVAL 9 DAY),
(7, 14, '炭治郎 1/8 手办', 'https://example.com/tanjiro_fig.jpg', 1, 559.00, NOW() - INTERVAL 3 DAY),
(7, 21, '咒术回战 五条悟钥匙扣', 'https://example.com/gojo_key.jpg', 1, 35.00, NOW() - INTERVAL 3 DAY),
(8, 21, '咒术回战 五条悟钥匙扣', 'https://example.com/gojo_key.jpg', 2, 35.00, NOW() - INTERVAL 1 DAY);

-- 9. 插入用户收藏
INSERT INTO `user_favorite` (`user_id`, `product_id`, `create_time`) VALUES
(2, 1, NOW() - INTERVAL 20 DAY),
(2, 9, NOW() - INTERVAL 15 DAY),
(2, 13, NOW() - INTERVAL 10 DAY),
(3, 5, NOW() - INTERVAL 18 DAY),
(3, 15, NOW() - INTERVAL 12 DAY),
(4, 9, NOW() - INTERVAL 25 DAY),
(4, 10, NOW() - INTERVAL 20 DAY),
(4, 14, NOW() - INTERVAL 8 DAY),
(5, 3, NOW() - INTERVAL 15 DAY),
(5, 23, NOW() - INTERVAL 10 DAY),
(5, 27, NOW() - INTERVAL 5 DAY),
(6, 16, NOW() - INTERVAL 12 DAY),
(6, 17, NOW() - INTERVAL 8 DAY),
(7, 24, NOW() - INTERVAL 10 DAY),
(8, 21, NOW() - INTERVAL 6 DAY),
(9, 9, NOW() - INTERVAL 3 DAY);


-- 勋章初始数据
INSERT INTO adge (
ame, description, icon, 	ype, condition_type, condition_value, sort_order, status) VALUES
('新人入坑', '完成注册成为幽幽子Shop的一员', '/badges/newbie.png', 'SYSTEM', 'REGISTER', 1, 1, 1),
('首次下单', '完成第一笔订单', '/badges/first_order.png', 'ACHIEVEMENT', 'FIRST_ORDER', 1, 2, 1),
('购物达人', '累计完成5笔订单', '/badges/shopper.png', 'ACHIEVEMENT', 'ORDER_COUNT', 5, 3, 1),
('购物狂人', '累计完成20笔订单', '/badges/shopaholic.png', 'ACHIEVEMENT', 'ORDER_COUNT', 20, 4, 1),
('社区新人', '发布第一篇帖子', '/badges/post_newbie.png', 'ACHIEVEMENT', 'POST_COUNT', 1, 5, 1),
('论坛达人', '累计发布10篇帖子', '/badges/forum_expert.png', 'ACHIEVEMENT', 'POST_COUNT', 10, 6, 1),
('人气之星', '粉丝数达到10人', '/badges/popular.png', 'ACHIEVEMENT', 'FOLLOWER_COUNT', 10, 7, 1),
('社交蝴蝶', '粉丝数达到50人', '/badges/social_butterfly.png', 'ACHIEVEMENT', 'FOLLOWER_COUNT', 50, 8, 1),
('收藏家', '收藏商品达到20件', '/badges/collector.png', 'ACHIEVEMENT', 'FAVORITE_COUNT', 20, 9, 1);
