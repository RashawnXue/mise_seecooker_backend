package com.mise.seecooker.entity.vo.community;

import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * 帖子详细内容VO类
 *
 * @author xueruichen
 * @date 2023.11.25
 */
@Data
@Builder
public class PostDetailVO {
    /**
     * 帖子标题
     */
    private String title;

    /**
     * 帖子内容
     */
    private String content;

    /**
     * 发布者头像url
     */
    private String posterAvatar;

    /**
     * 发布者用户名
     */
    private String posterName;

    /**
     * 帖子图片
     */
    private List<String> images;
}
