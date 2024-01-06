package com.seecooker.community.service.dao;


import com.seecooker.community.service.pojo.po.PostPO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * 帖子业务dao层
 *
 * @author xueruichen
 * @date 2023.11.25
 */
@Repository
public interface PostDao extends JpaRepository<PostPO, Long> {
}
