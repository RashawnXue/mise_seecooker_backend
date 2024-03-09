package com.seecooker.community.service.pojo.po;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "user_like")
public class UserLikePO {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    private Long postId;

    private Boolean status;

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
