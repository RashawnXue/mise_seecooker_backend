package com.mise.seecooker.dao;

import com.mise.seecooker.entity.po.PostPO;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 帖子业务dao层
 *
 * @author xueruichen
 * @date 2023.11.25
 */
public interface PostDao extends JpaRepository<PostPO, Long> {
}
