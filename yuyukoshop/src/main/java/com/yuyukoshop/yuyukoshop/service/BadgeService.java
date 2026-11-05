package com.yuyukoshop.yuyukoshop.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.yuyukoshop.yuyukoshop.entity.Badge;
import com.yuyukoshop.yuyukoshop.entity.UserBadge;
import com.yuyukoshop.yuyukoshop.mapper.BadgeMapper;
import com.yuyukoshop.yuyukoshop.mapper.UserBadgeMapper;
import com.yuyukoshop.yuyukoshop.vo.BadgeVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class BadgeService {

    @Autowired
    private BadgeMapper badgeMapper;

    @Autowired
    private UserBadgeMapper userBadgeMapper;

    /**
     * 获取用户的勋章墙（所有勋章 + 是否已获得）
     */
    public List<BadgeVo> getUserBadgeWall(Long userId) {
        // 获取所有启用的勋章
        List<Badge> allBadges = badgeMapper.selectList(
                new LambdaQueryWrapper<Badge>()
                        .eq(Badge::getStatus, 1)
                        .orderByAsc(Badge::getSortOrder)
        );

        // 获取用户已获得的勋章
        List<UserBadge> userBadges = userBadgeMapper.selectList(
                new LambdaQueryWrapper<UserBadge>()
                        .eq(UserBadge::getUserId, userId)
        );

        Map<Long, LocalDateTime> earnedMap = userBadges.stream()
                .collect(Collectors.toMap(UserBadge::getBadgeId, UserBadge::getCreateTime));

        return allBadges.stream()
                .map(badge -> BadgeVo.builder()
                        .id(badge.getId())
                        .name(badge.getName())
                        .description(badge.getDescription())
                        .icon(badge.getIcon())
                        .type(badge.getType())
                        .earned(earnedMap.containsKey(badge.getId()))
                        .earnedTime(earnedMap.get(badge.getId()))
                        .build())
                .collect(Collectors.toList());
    }

    /**
     * 获取用户已获得的勋章列表
     */
    public List<BadgeVo> getMyBadges(Long userId) {
        List<UserBadge> userBadges = userBadgeMapper.selectList(
                new LambdaQueryWrapper<UserBadge>()
                        .eq(UserBadge::getUserId, userId)
                        .orderByDesc(UserBadge::getCreateTime)
        );

        if (userBadges.isEmpty()) {
            return List.of();
        }

        List<Long> badgeIds = userBadges.stream()
                .map(UserBadge::getBadgeId)
                .collect(Collectors.toList());

        List<Badge> badges = badgeMapper.selectBatchIds(badgeIds);
        Map<Long, Badge> badgeMap = badges.stream()
                .collect(Collectors.toMap(Badge::getId, b -> b));

        Map<Long, LocalDateTime> earnedMap = userBadges.stream()
                .collect(Collectors.toMap(UserBadge::getBadgeId, UserBadge::getCreateTime));

        return userBadges.stream()
                .filter(ub -> badgeMap.containsKey(ub.getBadgeId()))
                .map(ub -> {
                    Badge b = badgeMap.get(ub.getBadgeId());
                    return BadgeVo.builder()
                            .id(b.getId())
                            .name(b.getName())
                            .description(b.getDescription())
                            .icon(b.getIcon())
                            .type(b.getType())
                            .earned(true)
                            .earnedTime(ub.getCreateTime())
                            .build();
                })
                .collect(Collectors.toList());
    }

    /**
     * 检查并颁发勋章
     * conditionType: REGISTER, FIRST_ORDER, ORDER_COUNT, POST_COUNT, FOLLOWER_COUNT, FAVORITE_COUNT
     */
    public void checkAndAward(Long userId, String conditionType, int currentValue) {
        List<Badge> badges = badgeMapper.selectList(
                new LambdaQueryWrapper<Badge>()
                        .eq(Badge::getConditionType, conditionType)
                        .eq(Badge::getStatus, 1)
                        .le(Badge::getConditionValue, currentValue)
        );

        for (Badge badge : badges) {
            // 检查是否已拥有
            Long count = userBadgeMapper.selectCount(
                    new LambdaQueryWrapper<UserBadge>()
                            .eq(UserBadge::getUserId, userId)
                            .eq(UserBadge::getBadgeId, badge.getId())
            );
            if (count == 0) {
                UserBadge userBadge = UserBadge.builder()
                        .userId(userId)
                        .badgeId(badge.getId())
                        .createTime(LocalDateTime.now())
                        .build();
                userBadgeMapper.insert(userBadge);
            }
        }
    }

    /**
     * 管理员：获取所有勋章
     */
    public List<Badge> getAllBadges() {
        return badgeMapper.selectList(
                new LambdaQueryWrapper<Badge>().orderByAsc(Badge::getSortOrder)
        );
    }

    /**
     * 管理员：创建勋章
     */
    public void createBadge(Badge badge) {
        badgeMapper.insert(badge);
    }

    /**
     * 管理员：更新勋章
     */
    public void updateBadge(Long id, Badge badge) {
        badge.setId(id);
        badgeMapper.updateById(badge);
    }

    /**
     * 管理员：删除勋章
     */
    public void deleteBadge(Long id) {
        badgeMapper.deleteById(id);
        userBadgeMapper.delete(
                new LambdaQueryWrapper<UserBadge>().eq(UserBadge::getBadgeId, id)
        );
    }
}
