package com.mise.seecooker.entity.po;

import lombok.*;

import jakarta.persistence.*;


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
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

}
