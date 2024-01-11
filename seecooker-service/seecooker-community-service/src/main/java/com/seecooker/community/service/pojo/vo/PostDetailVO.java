package com.seecooker.community.service.pojo.vo;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 帖子详细内容VO类
 *
 * @author xueruichen
 * @date 2023.11.25
 */
@Getter
@Setter
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
     * 发布者id
     */
    private Long posterId;

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

    /**
     * 当前用户是否点赞，未登陆为false
     */
    private Boolean like;

    /**
     * 点赞数
     */
    private Integer likeNum;

    /**
     * 发布时间
     */
    private String publishTime;
}
