package com.seecooker.community.service.pojo.po;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

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
    @Field(type = FieldType.Keyword)
    private String title;

    /**
     * 帖子内容
     */
    @NotNull
    @Column(length = 3000)
    @Field(type = FieldType.Keyword)
    private String content;

    /**
     * 点赞数
     */
    private Integer likeNum;

    /**
     * 帖子图片
     */
    private List<String> images;

    /**
     * 评论id列表
     */
    private List<Long> commentIdList;

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
