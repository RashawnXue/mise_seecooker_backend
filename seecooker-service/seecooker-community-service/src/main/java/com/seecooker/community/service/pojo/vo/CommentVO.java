package com.seecooker.community.service.pojo.vo;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * 评论VO类
 *
 * @author xueruichen
 * @date 2023.11.26
 */
@Getter
@Setter
@Builder
public class CommentVO {
    /**
     * 评论者id
     */
    private Long commenterId;

    /**
     * 评论者头像url
     */
    private String commenterAvatar;

    /**
     * 评论者用户名
     */
    private String commenterName;

    /**
     * 评论时间
     */
    private String commentTime;

    /**
     * 评论内容
     */
    private String content;
}
