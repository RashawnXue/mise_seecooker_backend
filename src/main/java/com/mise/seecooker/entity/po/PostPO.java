package com.mise.seecooker.entity.po;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import jakarta.persistence.*;

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
    private Long userId;

    /**
     * 帖子标题
     */
    @NotNull
    private String title;

    /**
     * 帖子内容
     */
    @NotNull
    private String content;

    /**
     * 帖子图片
     */
    private List<String> images;

}
