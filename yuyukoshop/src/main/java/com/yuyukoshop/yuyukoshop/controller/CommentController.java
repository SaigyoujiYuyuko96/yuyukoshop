package com.yuyukoshop.yuyukoshop.controller;

import com.yuyukoshop.yuyukoshop.common.Result;
import com.yuyukoshop.yuyukoshop.dto.CommentCreateDto;
import com.yuyukoshop.yuyukoshop.entity.Comment;
import com.yuyukoshop.yuyukoshop.security.CustomUserDetails;
import com.yuyukoshop.yuyukoshop.service.CommentService;
import com.yuyukoshop.yuyukoshop.vo.CommentVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/forums/posts/{postId}/comments")
public class CommentController {

    @Autowired
    private CommentService commentService;

    @GetMapping
    public Result<List<CommentVo>> getComments(@PathVariable Long postId) {
        List<CommentVo> comments = commentService.getCommentsByPostId(postId);
        return Result.success(comments);
    }

    @PostMapping
    public Result<Comment> createComment(@PathVariable Long postId,
                                          @Valid @RequestBody CommentCreateDto dto) {
        Long userId = getCurrentUserId();
        Comment comment = commentService.createComment(userId, postId, dto);
        return Result.success(comment);
    }

    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        return userDetails.getId();
    }
}
