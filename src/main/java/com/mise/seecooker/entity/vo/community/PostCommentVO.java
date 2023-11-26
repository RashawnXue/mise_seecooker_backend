package com.mise.seecooker.entity.vo.community;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * 发布评论VO
 *
 * @author xueruichen
 * @date 2023.11.26
 */
@Getter
@Setter
@Builder
public class PostCommentVO {
    /**
     * 帖子id
     */
    @NotNull
    private Long postId;

    /**
     * 评论内容
     */
    @NotNull
    private String content;
}
