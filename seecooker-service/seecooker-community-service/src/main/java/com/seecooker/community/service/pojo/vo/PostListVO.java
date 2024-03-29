package com.seecooker.community.service.pojo.vo;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * 帖子预览VO类
 *
 * @author xueruichen
 * @date 2023.11.25
 */
@Getter
@Setter
@Builder
public class PostListVO {
    /**
     * 帖子id
     */
    private Long postId;

    /**
     * 封面图url（默认选取图片第一张）
     */
    private String cover;

    /**
     * 标题
     */
    private String title;

    /**
     * 发布者id
     */
    private Long posterId;

    /**
     * 发布者头像
     */
    private String posterAvatar;

    /**
     * 发布者用户名
     */
    private String posterName;

    /**
     * 发布时间
     */
    private String publishTime;

    /**
     * 用户是否点赞
     */
    private Boolean like;

    /**
     * 点赞数
     */
    private Integer likeNum;

    /**
     * 评论数
     */
    private Integer commentNum;

    /**
     * 内容
     */
    private String content;
}
