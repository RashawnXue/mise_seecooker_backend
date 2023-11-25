package com.mise.seecooker.entity.po;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * 评论持久层实体类PO
 *
 * @author xueruichen
 * @date 2023.11.25
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "comment")
public class CommentPO {
    /**
     * 评论id
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 评论者id
     */
    @NotNull
    private Long commenterId;

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

    /**
     * 数据创建的时间戳
     */
    @CreationTimestamp
    private LocalDateTime createTime;

    /**
     * 数据最后一次更新的时间戳
     */
    @UpdateTimestamp
    private LocalDateTime updateTime;
}
