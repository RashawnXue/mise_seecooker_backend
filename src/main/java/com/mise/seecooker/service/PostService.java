package com.mise.seecooker.service;

import com.mise.seecooker.entity.vo.community.PostDetailVO;
import com.mise.seecooker.entity.vo.community.PostVO;
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
}
