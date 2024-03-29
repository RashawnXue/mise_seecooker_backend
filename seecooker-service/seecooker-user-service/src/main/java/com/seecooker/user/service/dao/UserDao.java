package com.seecooker.user.service.dao;

import com.seecooker.user.service.pojo.po.UserPO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * 用户业务dao层
 *
 * @author xueruichen
 * @date 2023.11.17
 */
@Repository
public interface UserDao extends JpaRepository<UserPO, Long> {
    UserPO findByUsername(String username);
}

