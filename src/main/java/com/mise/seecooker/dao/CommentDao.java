package com.mise.seecooker.dao;

import com.mise.seecooker.entity.po.CommentPO;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 评论业务dao层
 *
 * @author xueruichen
 * @date 2023.11.25
 */
public interface CommentDao extends JpaRepository<CommentPO, Long> {
}
