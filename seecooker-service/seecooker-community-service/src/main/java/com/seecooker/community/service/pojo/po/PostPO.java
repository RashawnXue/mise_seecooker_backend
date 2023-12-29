package com.seecooker.community.service.pojo.po;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;


/**
 * 帖子持久层实体类
 *
 * @author xueruichen
 * @date 2023.11.16
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "post")
public class PostPO {
    /**
     * 帖子id
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 帖子发布者id
     */
    private Long posterId;

    /**
     * 帖子标题
     */
    @NotNull
    @Column(length = 100)
    private String title;

    /**
     * 帖子内容
     */
    @NotNull
    @Column(length = 3000)
    private String content;

    /**
     * 帖子图片
     */
    private List<String> images;

    /**
     * 点赞用户id列表
     */
    private List<Long> likeUserIdList;

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
