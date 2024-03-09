package com.seecooker.community.service.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.seecooker.common.core.model.Result;
import com.seecooker.community.service.pojo.po.EsPostPO;
import com.seecooker.community.service.pojo.vo.*;
import com.seecooker.community.service.service.PostService;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


/**
 * 帖子相关业务控制层类
 *
 * @author xueruichen
 * @date 2023.11.25
 */
@Slf4j
@RestController
@RequestMapping("/v2/")
public class PostController {
    private final PostService postService;
    private final RabbitTemplate rabbitTemplate;

    public PostController(PostService postService, RabbitTemplate rabbitTemplate) {
        this.postService = postService;
        this.rabbitTemplate = rabbitTemplate;
    }

    /**
     * 发布帖子
     *
     * @return 响应结果
     */
    @PostMapping("community/post")
    public Result<Void> publishPost(@NotNull String title, @NotNull String content, MultipartFile[] images) throws Exception{
        // 未登录不能发布帖子
        postService.addPost(title, content, images);
        return Result.success();
    }

    /**
     * 获取帖子
     *
     * @return 响应结果
     */
    @GetMapping("community/posts")
    public Result<List<PostListVO>> getPosts() {
        List<PostListVO> posts = postService.getPosts();
        return Result.success(posts);
    }

    /**
     * 分页获取帖子
     *
     * @param pageNo 页码
     * @return 结果
     */
    @GetMapping("community/posts/page/{pageNo}")
    public Result<List<PostListVO>> getPostsByPage(@PathVariable @NotNull Integer pageNo) {
        List<PostListVO> posts = postService.getPostsByPage(pageNo);
        return Result.success(posts);
    }

    /**
     * 获取帖子详情
     *
     * @param id 帖子id
     * @return 响应结果
     */
    @GetMapping("community/post/{id}")
    public Result<PostDetailVO> getPostDetail(@PathVariable @NotNull Long id) {
        PostDetailVO post = postService.getPostDetail(id);
        return Result.success(post);
    }

    /**
     * 发表评论
     *
     * @param postComment 评论发表VO
     * @return 响应结果
     */
    @PostMapping("community/comment")
    public Result<CommentVO> postComment(@RequestBody @Validated PostCommentVO postComment) {
        // 检查是否登陆，未登陆不能发表评论
        CommentVO comment = postService.addComment(postComment);
        return Result.success(comment);
    }

    /**
     * 获取帖子评论
     *
     * @param postId 帖子id
     * @return 响应结果
     */
    @GetMapping("community/comments/{postId}")
    public Result<List<CommentVO>> getComments(@PathVariable @NotNull Long postId) {
        List<CommentVO> comments = postService.getCommentsByPostId(postId);
        return Result.success(comments);
    }

    /**
     * 点赞或取消点赞帖子
     * 若未点赞，则点赞帖子；反之则取消点赞帖子
     *
     * @param postId 点赞的帖子id
     * @return 响应结果-交互后的点赞状态
     */
    @PutMapping("community/like/{postId}")
    public Result<Void> likePost(@PathVariable @NotNull Long postId) {
        // 检查是否登陆，未登陆不能点赞
        rabbitTemplate.convertAndSend("likePostQueue", StpUtil.getLoginIdAsLong() + ":" + postId);
//        Boolean result = postService.likePost(postId);
        return Result.success();
    }

    /**
     * 删除帖子
     *
     * @param id 帖子id
     * @return 响应结果
     */
    @DeleteMapping("community/post/{id}")
    public Result<Void> deletePost(@PathVariable @NotNull Long id) {
        postService.deletePost(id);
        return Result.success();
    }

    /**
     * 根据用户id获取用户发布的帖子
     *
     * @param userId 用户id
     * @return 用户发布的帖子
     */
    @GetMapping("community/user/posts/{userId}")
    public Result<List<PostListVO>> getUserPosts(@PathVariable @NotNull Long userId) {
        List<PostListVO> result = postService.getUserPosts(userId);
        return Result.success(result);
    }

    @GetMapping("community/posts/search")
    public Result<List<EsPostVO>> searchPosts(@RequestParam String keyword) {
        return Result.success(postService.searchPosts(keyword));
    }
}
