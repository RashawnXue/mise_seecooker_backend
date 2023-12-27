package com.seecooker.service;

import com.seecooker.pojo.vo.community.CommentVO;
import com.seecooker.pojo.vo.community.PostCommentVO;
import com.seecooker.pojo.vo.community.PostDetailVO;
import com.seecooker.pojo.vo.community.PostVO;
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
     * @return 新增帖子id
     */
    Long addPost(String title, String content, MultipartFile[] images) throws Exception;

    /**
     * 获取帖子
     *
     * @return 帖子列表
     */
    List<PostVO> getPosts();

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
}
