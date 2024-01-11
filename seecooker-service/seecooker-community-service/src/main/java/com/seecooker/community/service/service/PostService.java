package com.seecooker.community.service.service;

import com.seecooker.community.service.pojo.vo.CommentVO;
import com.seecooker.community.service.pojo.vo.PostCommentVO;
import com.seecooker.community.service.pojo.vo.PostDetailVO;
import com.seecooker.community.service.pojo.vo.PostListVO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 帖子业务服务层接口类
 *
 * @author xueruichen
 * @date 2023.11.25
 */
public interface PostService {
    /**
     * 增加帖子
     *
     * @param title 标题
     * @param content 内容
     * @param images 帖子图片
     */
    void addPost(String title, String content, MultipartFile[] images) throws Exception;

    /**
     * 获取帖子
     *
     * @return 帖子列表
     */
    List<PostListVO> getPosts();

    /**
     * 获取帖子详情
     *
     * @param id 帖子id
     * @return 帖子详情
     */
    PostDetailVO getPostDetail(Long id);

    /**
     * 添加评论
     *
     * @param postComment 评论相关信息
     * @return 新增评论id
     */
    CommentVO addComment(PostCommentVO postComment);

    /**
     * 根据帖子id获取评论
     *
     * @param postId 帖子id
     * @return 评论列表
     */
    List<CommentVO> getCommentsByPostId(Long postId);

    /**
     * 点赞或取消点赞帖子
     *
     * @param postId 帖子id
     * @return 帖子状态
     */
    Boolean likePost(Long postId);

    /**
     * 删除帖子
     *
     * @param id 帖子id
     */
    void deletePost(Long id);

    /**
     * 获取用户发布的帖子
     *
     * @param userId 用户id
     * @return 用户发布的帖子
     */
    List<PostListVO> getUserPosts(Long userId);

    /**
     * 分页获取帖子
     *
     * @param pageNo 页码
     * @return 结果
     */
    List<PostListVO> getPostsByPage(Integer pageNo);
}
