package com.seecooker.pojo.po;

import lombok.*;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 用户持久层实体类
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
@Table(name = "\"user\"")
public class UserPO {
    /**
     * 用户id
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 用户昵称
     */
    @NotNull
    @Column(unique = true, length = 20)
    private String username;

    /**
     * 用户密码
     */
    @NotNull
    @Column(length = 60)
    private String password;

    /**
     * 用户头像url
     */
    private String avatar;

    /**
     * 用户收藏的菜谱id列表
     */
    private List<Long> favoriteRecipes;

    /**
     * 用户发布的菜谱id列表
     */
    private List<Long> postRecipes;

    /**
     * 用户发布的帖子id列表
     */
    private List<Long> posts;

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
