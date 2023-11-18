package com.mise.seecooker.service.impl;

import com.mise.seecooker.dao.UserDao;
import com.mise.seecooker.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 用户业务服务层实现
 *
 * @author xueruichen
 * @date 2023.11.17
 */
@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {
    private UserDao userDao;
}
