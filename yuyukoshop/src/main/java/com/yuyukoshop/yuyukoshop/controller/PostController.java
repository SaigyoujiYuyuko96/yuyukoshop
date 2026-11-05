package com.yuyukoshop.yuyukoshop.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.yuyukoshop.yuyukoshop.common.PageResult;
import com.yuyukoshop.yuyukoshop.common.Result;
import com.yuyukoshop.yuyukoshop.dto.PostCreateDto;
import com.yuyukoshop.yuyukoshop.entity.Post;
import com.yuyukoshop.yuyukoshop.security.CustomUserDetails;
import com.yuyukoshop.yuyukoshop.service.PostService;
import com.yuyukoshop.yuyukoshop.vo.PostVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/forums/posts")
public class PostController {

    @Autowired
    private PostService postService;

    @GetMapping
    public Result<PageResult<PostVo>> getPosts(
            @RequestParam(defaultValue = "1") Long page,
            @RequestParam(defaultValue = "10") Long size,
            @RequestParam(required = false) Long forumId,
            @RequestParam(required = false) Integer isEssence,
            @RequestParam(required = false) String sort,
            @RequestParam(required = false) String keyword) {
        IPage<PostVo> result = postService.getPostPage(page, size, forumId, isEssence, sort, keyword);
        return Result.success(PageResult.of(result));
    }

    @PostMapping
    public Result<Post> createPost(@Valid @RequestBody PostCreateDto dto) {
        Long userId = getCurrentUserId();
        Post post = postService.createPost(userId, dto);
        return Result.success(post);
    }

    @GetMapping("/{id}")
    public Result<PostVo> getPostDetail(@PathVariable Long id) {
        PostVo postVo = postService.getPostDetail(id);
        return Result.success(postVo);
    }

    @PostMapping("/{id}/like")
    public Result<Map<String, Boolean>> likePost(@PathVariable Long id) {
        Long userId = getCurrentUserId();
        postService.likePost(userId, id);
        boolean hasLiked = postService.hasLiked(userId, id);
        Map<String, Boolean> result = new HashMap<>();
        result.put("hasLiked", hasLiked);
        return Result.success(result);
    }

    @GetMapping("/my")
    public Result<List<PostVo>> getMyPosts() {
        Long userId = getCurrentUserId();
        List<PostVo> posts = postService.getUserPosts(userId);
        return Result.success(posts);
    }

    @GetMapping("/user/{userId}")
    public Result<List<PostVo>> getUserPosts(@PathVariable Long userId) {
        List<PostVo> posts = postService.getUserPosts(userId);
        return Result.success(posts);
    }

    @GetMapping("/hot")
    public Result<List<PostVo>> getHotPosts(@RequestParam(defaultValue = "10") int limit) {
        List<PostVo> posts = postService.getHotPosts(limit);
        return Result.success(posts);
    }

    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        return userDetails.getId();
    }
}

