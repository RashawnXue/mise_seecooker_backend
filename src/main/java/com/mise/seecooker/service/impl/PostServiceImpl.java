package com.mise.seecooker.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.aliyuncs.exceptions.ClientException;
import com.mise.seecooker.dao.PostDao;
import com.mise.seecooker.dao.UserDao;
import com.mise.seecooker.entity.po.PostPO;
import com.mise.seecooker.entity.po.UserPO;
import com.mise.seecooker.enums.ImageType;
import com.mise.seecooker.service.PostService;
import com.mise.seecooker.util.AliOSSUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 帖子业务服务层实现类
 *
 * @author xueruichen
 * @date 2023.11.25
 */
@Service
@Slf4j
public class PostServiceImpl implements PostService {
    private final PostDao postDao;
    private final UserDao userDao;

    @Autowired
    public PostServiceImpl(PostDao postDao, UserDao userDao) {
        this.postDao = postDao;
        this.userDao = userDao;
    }

    @Override
    public Long addPost(String title, String content, MultipartFile[] images) throws IOException, ClientException {
        List<String> postImages = null;
        if (images != null) {
            postImages = new ArrayList<>();
            // 上传图片
            for (MultipartFile image : images) {
                String s = AliOSSUtil.uploadFile(image, ImageType.POST_IMAGE);
                postImages.add(s);
            }
        }
        Long posterId = StpUtil.getLoginIdAsLong();
        PostPO post = PostPO.builder()
                .title(title)
                .content(content)
                .posterId(posterId)
                .images(postImages)
                .createTime(LocalDateTime.now())
                .updateTime(LocalDateTime.now())
                .build();
        Long postId = postDao.save(post).getId();

        // 在poster发布的帖子内插入id
        UserPO poster = userDao.findById(posterId).get();
        poster.getPosts().add(postId);
        userDao.save(poster);

        return postId;
    }
}
