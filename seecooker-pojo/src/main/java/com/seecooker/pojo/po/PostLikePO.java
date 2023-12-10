package com.seecooker.pojo.po;

import jakarta.persistence.*;
import lombok.*;

/**
 * 帖子点赞持久层PO类
 *
 * @author xueruichen
 * @date 2023.12.10
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "post_like")
public class PostLikePO {
    /**
     * id
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    /**
     * 点赞用户id
     */
    private Long userId;

    /**
     * 获赞帖子id
     */
    private Long postId;
}
