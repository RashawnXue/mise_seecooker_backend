package com.mise.seecooker.controller.community;

import cn.dev33.satoken.stp.StpUtil;
import com.mise.seecooker.entity.Result;
import com.mise.seecooker.service.PostService;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * 帖子相关业务控制层类
 *
 * @author xueruichen
 * @date 2023.11.25
 */
@Slf4j
@RestController
@RequestMapping("/v1")
public class PostController {
    private final PostService postService;

    @Autowired
    public PostController(PostService postService) {
        this.postService = postService;
    }

    /**
     * 发布帖子
     *
     * @return 响应结果
     */
    @PostMapping("/post")
    public Result<Long> publishPost(@NotNull String title, @NotNull String content, MultipartFile[] images) throws Exception{
        // 未登录不能发布帖子
        StpUtil.checkLogin();
        Long postId = postService.addPost(title, content, images);
        return Result.success(postId);
    }

}
