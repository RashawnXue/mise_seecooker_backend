package com.seecooker.recipe.service.pojo.po;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;


/**
 * 菜谱持久层实体类
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
@Table(name = "recipe")
public class RecipePO {
    /**
     * 菜谱id
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 作者id
     */
    private Long authorId;

    /**
     * 菜谱名
     */
    @NotNull
    @Column(length = 20)
    private String name;

    /**
     * 菜谱封面
     */
    private String cover;

    /**
     * 菜谱介绍
     */
    @Column(length = 500)
    private String introduction;

    /**
     * 评分
     */
    @NotNull
    private Double score;

    /**
     * 步骤图
     */
    @NotNull
    private List<String> stepImages;

    /**
     * 步骤内容
     */
    @NotNull
    @Column(length = 500)
    private List<String> stepContents;

    /**
     * 收藏数
     */
    private Integer favoriteNum;

    /**
     * 配料
     */
    private List<String> ingredientList;

    /**
     * 配料量
     */
    private List<String> amountList;

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
