package com.mise.seecooker.entity.po;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import jakarta.persistence.*;

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
     * 发布者id
     */
    private Long userId;

    /**
     * 菜谱名
     */
    @NotNull
    @Column(unique = true)
    private String name;

    /**
     * 菜谱封面
     */
    private String cover;

    /**
     * 步骤图
     */
    @NotNull
    private List<String> stepImages;

    /**
     * 步骤内容
     */
    @NotNull
    private List<String> stepContents;

}
