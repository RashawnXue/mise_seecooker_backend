package com.seecooker.controller.community;

import cn.dev33.satoken.stp.StpUtil;
import com.aliyuncs.exceptions.ClientException;
import com.seecooker.pojo.vo.community.CommentVO;
import com.seecooker.pojo.vo.community.PostCommentVO;
import com.seecooker.pojo.vo.community.PostDetailVO;
import com.seecooker.pojo.vo.community.PostVO;
import com.seecooker.common.core.Result;
import com.seecooker.service.PostService;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
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
@RequestMapping("/v1/")
public class PostController {
    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    /**
     * 发布帖子
     *
     * @return 响应结果
     */
    @PostMapping("post")
    public Result<Long> publishPost(@NotNull String title, @NotNull String content, MultipartFile[] images) throws Exception{
        // 未登录不能发布帖子
        StpUtil.checkLogin();
        Long postId = postService.addPost(title, content, images);
        return Result.success(postId);
    }

    /**
     * 获取帖子，每次至多获取10条
     *
     * @return 响应结果
     */
    @GetMapping("posts")
    public Result<List<PostVO>> getPosts() {
        // TODO: 目前为直接获取所有帖子，后续迭代中修改为获取分页推荐10条帖子
        List<PostVO> posts = postService.getPosts();
        return Result.success(posts);
    }

    /**
     * 获取帖子详情
     *
     * @param id 帖子id
     * @return 响应结果
     */
    @GetMapping("post/{id}")
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
    @PostMapping("comment")
    public Result<CommentVO> postComment(@RequestBody @Validated PostCommentVO postComment) {
        // 检查是否登陆，未登陆不能发表评论
        StpUtil.checkLogin();
        CommentVO comment = postService.addComment(postComment);
        return Result.success(comment);
    }

    /**
     * 获取帖子评论
     *
     * @param postId 帖子id
     * @return 响应结果
     */
    @GetMapping("comments/{postId}")
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
    @PutMapping("post/like/{postId}")
    public Result<Boolean> likePost(@PathVariable @NotNull Long postId) {
        // 检查是否登陆，未登陆不能点赞
        StpUtil.checkLogin();
        Boolean result = postService.likePost(postId);
        return Result.success(result);
    }
}
