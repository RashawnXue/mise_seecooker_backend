package com.seecooker.community.service.dao;


import com.seecooker.community.service.pojo.po.CommentPO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 评论业务dao层
 *
 * @author xueruichen
 * @date 2023.11.25
 */
@Repository
public interface CommentDao extends JpaRepository<CommentPO, Long> {
}
