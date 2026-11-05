package com.yuyukoshop.yuyukoshop.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.yuyukoshop.yuyukoshop.entity.User;
import com.yuyukoshop.yuyukoshop.entity.UserFollow;
import com.yuyukoshop.yuyukoshop.exception.BusinessException;
import com.yuyukoshop.yuyukoshop.mapper.UserFollowMapper;
import com.yuyukoshop.yuyukoshop.mapper.UserMapper;
import com.yuyukoshop.yuyukoshop.vo.UserVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserFollowService {

    @Autowired
    private UserFollowMapper userFollowMapper;

    @Autowired
    private UserMapper userMapper;

    public void follow(Long userId, Long followedUserId) {
        if (userId.equals(followedUserId)) {
            throw new BusinessException("不能关注自己");
        }

        User followedUser = userMapper.selectById(followedUserId);
        if (followedUser == null) {
            throw new BusinessException("用户不存在");
        }

        Long count = userFollowMapper.selectCount(
                new LambdaQueryWrapper<UserFollow>()
                        .eq(UserFollow::getUserId, userId)
                        .eq(UserFollow::getFollowedUserId, followedUserId)
        );

        if (count > 0) {
            throw new BusinessException("已关注该用户");
        }

        UserFollow follow = UserFollow.builder()
                .userId(userId)
                .followedUserId(followedUserId)
                .createTime(LocalDateTime.now())
                .build();

        userFollowMapper.insert(follow);
    }

    public void unfollow(Long userId, Long followedUserId) {
        userFollowMapper.delete(
                new LambdaQueryWrapper<UserFollow>()
                        .eq(UserFollow::getUserId, userId)
                        .eq(UserFollow::getFollowedUserId, followedUserId)
        );
    }

    public List<UserVo> getFollowingList(Long userId) {
        List<UserFollow> follows = userFollowMapper.selectList(
                new LambdaQueryWrapper<UserFollow>()
                        .eq(UserFollow::getUserId, userId)
                        .orderByDesc(UserFollow::getCreateTime)
        );

        List<Long> followedUserIds = follows.stream()
                .map(UserFollow::getFollowedUserId)
                .collect(Collectors.toList());

        if (followedUserIds.isEmpty()) {
            return List.of();
        }

        List<User> users = userMapper.selectBatchIds(followedUserIds);
        return users.stream()
                .filter(u -> u.getStatus() == 1)
                .map(this::convertToVo)
                .collect(Collectors.toList());
    }

    public List<UserVo> getFollowerList(Long userId) {
        List<UserFollow> follows = userFollowMapper.selectList(
                new LambdaQueryWrapper<UserFollow>()
                        .eq(UserFollow::getFollowedUserId, userId)
                        .orderByDesc(UserFollow::getCreateTime)
        );

        List<Long> followerIds = follows.stream()
                .map(UserFollow::getUserId)
                .collect(Collectors.toList());

        if (followerIds.isEmpty()) {
            return List.of();
        }

        List<User> users = userMapper.selectBatchIds(followerIds);
        return users.stream()
                .filter(u -> u.getStatus() == 1)
                .map(this::convertToVo)
                .collect(Collectors.toList());
    }

    public boolean isFollowing(Long userId, Long followedUserId) {
        Long count = userFollowMapper.selectCount(
                new LambdaQueryWrapper<UserFollow>()
                        .eq(UserFollow::getUserId, userId)
                        .eq(UserFollow::getFollowedUserId, followedUserId)
        );
        return count > 0;
    }

    public Long getFollowingCount(Long userId) {
        return userFollowMapper.selectCount(
                new LambdaQueryWrapper<UserFollow>()
                        .eq(UserFollow::getUserId, userId)
        );
    }

    public Long getFollowerCount(Long userId) {
        return userFollowMapper.selectCount(
                new LambdaQueryWrapper<UserFollow>()
                        .eq(UserFollow::getFollowedUserId, userId)
        );
    }

    private UserVo convertToVo(User user) {
        UserVo vo = new UserVo();
        vo.setId(user.getId());
        vo.setUsername(user.getUsername());
        vo.setNickname(user.getNickname());
        vo.setAvatar(user.getAvatar());
        return vo;
    }
}
