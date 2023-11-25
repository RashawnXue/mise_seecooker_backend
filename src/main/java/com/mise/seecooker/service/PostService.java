package com.mise.seecooker.service;

import com.aliyuncs.exceptions.ClientException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

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
}
