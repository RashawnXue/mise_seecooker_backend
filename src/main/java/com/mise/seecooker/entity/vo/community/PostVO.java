package com.mise.seecooker.entity.vo.community;

import lombok.*;

/**
 * 帖子预览VO类
 *
 * @author xueruichen
 * @date 2023.11.25
 */
@Data
@Builder
public class PostVO {
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
     * 发布者头像
     */
    private String posterAvatar;

    /**
     * 发布者用户名
     */
    private String posterName;
}
