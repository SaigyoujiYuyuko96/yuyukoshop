package com.yuyukoshop.yuyukoshop.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yuyukoshop.yuyukoshop.dto.CommentCreateDto;
import com.yuyukoshop.yuyukoshop.entity.Comment;
import com.yuyukoshop.yuyukoshop.entity.Post;
import com.yuyukoshop.yuyukoshop.entity.User;
import com.yuyukoshop.yuyukoshop.exception.BusinessException;
import com.yuyukoshop.yuyukoshop.mapper.CommentMapper;
import com.yuyukoshop.yuyukoshop.mapper.PostMapper;
import com.yuyukoshop.yuyukoshop.mapper.UserMapper;
import com.yuyukoshop.yuyukoshop.vo.CommentVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CommentService {

    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private PostMapper postMapper;

    @Autowired
    private UserMapper userMapper;

    @Transactional
    public Comment createComment(Long userId, Long postId, CommentCreateDto dto) {
        Post post = postMapper.selectById(postId);
        if (post == null || post.getStatus() != 1) {
            throw new BusinessException("帖子不存在");
        }

        Comment comment = Comment.builder()
                .postId(postId)
                .userId(userId)
                .content(dto.getContent())
                .parentId(dto.getParentId())
                .status(1)
                .createTime(LocalDateTime.now())
                .updateTime(LocalDateTime.now())
                .build();
        commentMapper.insert(comment);

        postMapper.incrementCommentCount(postId);
        return comment;
    }

    public List<CommentVo> getCommentsByPostId(Long postId) {
        List<Comment> comments = commentMapper.selectList(
                new LambdaQueryWrapper<Comment>()
                        .eq(Comment::getPostId, postId)
                        .eq(Comment::getStatus, 1)
                        .orderByAsc(Comment::getCreateTime)
        );

        Map<Long, List<Comment>> replyMap = comments.stream()
                .filter(c -> c.getParentId() != null)
                .collect(Collectors.groupingBy(Comment::getParentId));

        List<CommentVo> result = new ArrayList<>();
        for (Comment comment : comments) {
            if (comment.getParentId() == null) {
                CommentVo vo = convertToVo(comment);
                List<Comment> replies = replyMap.getOrDefault(comment.getId(), new ArrayList<>());
                vo.setReplies(replies.stream().map(this::convertToVo).collect(Collectors.toList()));
                result.add(vo);
            }
        }
        return result;
    }

    public IPage<Comment> getCommentPage(Long page, Long size) {
        Page<Comment> pageParam = new Page<>(page, size);
        return commentMapper.selectPage(pageParam,
                new LambdaQueryWrapper<Comment>()
                        .orderByDesc(Comment::getCreateTime)
        );
    }

    private CommentVo convertToVo(Comment comment) {
        User user = userMapper.selectById(comment.getUserId());
        return CommentVo.builder()
                .id(comment.getId())
                .userId(comment.getUserId())
                .username(user != null ? user.getUsername() : "")
                .userAvatar(user != null ? user.getAvatar() : "")
                .content(comment.getContent())
                .parentId(comment.getParentId())
                .createTime(comment.getCreateTime())
                .build();
    }
}
