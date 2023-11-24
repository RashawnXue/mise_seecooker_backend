package com.mise.seecooker.dao;

import com.mise.seecooker.entity.po.UserPO;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 用户业务dao层
 *
 * @author xueruichen
 * @date 2023.11.17
 */
public interface UserDao extends JpaRepository<UserPO, Long> {
    UserPO findByUsername(String username);
}
