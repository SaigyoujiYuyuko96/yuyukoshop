package com.yuyukoshop.yuyukoshop.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yuyukoshop.yuyukoshop.dto.PostCreateDto;
import com.yuyukoshop.yuyukoshop.entity.Post;
import com.yuyukoshop.yuyukoshop.entity.User;
import com.yuyukoshop.yuyukoshop.exception.BusinessException;
import com.yuyukoshop.yuyukoshop.mapper.PostMapper;
import com.yuyukoshop.yuyukoshop.mapper.UserMapper;
import com.yuyukoshop.yuyukoshop.vo.PostVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Service
public class PostService {

    @Autowired
    private PostMapper postMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired(required = false)
    private RedisTemplate<String, Object> redisTemplate;

    public IPage<PostVo> getPostPage(Long page, Long size, Long forumId, Integer isEssence, String sort, String keyword) {
        Page<Post> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<Post> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Post::getStatus, 1);

        if (forumId != null) {
            wrapper.eq(Post::getForumId, forumId);
        }
        if (isEssence != null) {
            wrapper.eq(Post::getIsEssence, isEssence);
        }

                // 关键字搜索
                if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w.like(Post::getTitle, keyword)
                              .or()
                              .like(Post::getContent, keyword));
        }

        if ("hot".equals(sort)) {
            wrapper.orderByDesc(Post::getViewCount);
        } else if ("popular".equals(sort)) {
            wrapper.orderByDesc(Post::getLikeCount);
        } else {
            wrapper.orderByDesc(Post::getIsTop, Post::getCreateTime);
        }

        IPage<Post> postPage = postMapper.selectPage(pageParam, wrapper);
        List<PostVo> voList = postPage.getRecords().stream()
                .map(this::convertToVo)
                .collect(Collectors.toList());

        Page<PostVo> result = new Page<>(postPage.getCurrent(), postPage.getSize(), postPage.getTotal());
        result.setRecords(voList);
        return result;
    }

    public PostVo getPostDetail(Long id) {
        Post post = postMapper.selectById(id);
        if (post == null || post.getStatus() != 1) {
            throw new BusinessException("帖子不存在");
        }
        postMapper.incrementViewCount(id);
        post.setViewCount(post.getViewCount() + 1);
        return convertToVo(post);
    }

    public Post createPost(Long userId, PostCreateDto dto) {
        Post post = Post.builder()
                .userId(userId)
                .forumId(dto.getForumId())
                .title(dto.getTitle())
                .content(dto.getContent())
                .viewCount(0)
                .likeCount(0)
                .commentCount(0)
                .isEssence(0)
                .isTop(0)
                .status(1)
                .createTime(LocalDateTime.now())
                .updateTime(LocalDateTime.now())
                .build();
        postMapper.insert(post);
        return post;
    }

    public void likePost(Long userId, Long postId) {
        if (redisTemplate == null) {
            postMapper.incrementLikeCount(postId);
            return;
        }
        String key = "post:like:" + postId + ":" + userId;
        try {
            Boolean hasLiked = redisTemplate.hasKey(key);
            if (Boolean.TRUE.equals(hasLiked)) {
                redisTemplate.delete(key);
                postMapper.decrementLikeCount(postId);
            } else {
                redisTemplate.opsForValue().set(key, 1, 7, TimeUnit.DAYS);
                postMapper.incrementLikeCount(postId);
            }
        } catch (Exception e) {
            log.warn("Redis操作失败，回退到数据库点赞: {}", e.getMessage());
            postMapper.incrementLikeCount(postId);
        }
    }

    public boolean hasLiked(Long userId, Long postId) {
        if (redisTemplate == null) return false;
        String key = "post:like:" + postId + ":" + userId;
        try {
            return Boolean.TRUE.equals(redisTemplate.hasKey(key));
        } catch (Exception e) {
            log.warn("Redis读取失败: {}", e.getMessage());
            return false;
        }
    }

    public List<PostVo> getUserPosts(Long userId) {
        List<Post> posts = postMapper.selectList(
                new LambdaQueryWrapper<Post>()
                        .eq(Post::getUserId, userId)
                        .eq(Post::getStatus, 1)
                        .orderByDesc(Post::getCreateTime)
        );
        return posts.stream().map(this::convertToVo).collect(Collectors.toList());
    }

    public List<PostVo> getHotPosts(int limit) {
        List<Post> posts = postMapper.selectList(
                new LambdaQueryWrapper<Post>()
                        .eq(Post::getStatus, 1)
                        .orderByDesc(Post::getViewCount)
                        .last("LIMIT " + limit)
        );
        return posts.stream().map(this::convertToVo).collect(Collectors.toList());
    }

    private PostVo convertToVo(Post post) {
        User user = userMapper.selectById(post.getUserId());
        return PostVo.builder()
                .id(post.getId())
                .userId(post.getUserId())
                .username(user != null ? user.getUsername() : "")
                .userAvatar(user != null ? user.getAvatar() : "")
                .forumId(post.getForumId())
                .title(post.getTitle())
                .content(post.getContent())
                .viewCount(post.getViewCount())
                .likeCount(post.getLikeCount())
                .commentCount(post.getCommentCount())
                .isEssence(post.getIsEssence())
                .isTop(post.getIsTop())
                .status(post.getStatus())
                .createTime(post.getCreateTime())
                .build();
    }
}


