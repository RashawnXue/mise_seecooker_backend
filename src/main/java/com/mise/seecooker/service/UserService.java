package com.mise.seecooker.service;

import com.mise.seecooker.entity.vo.user.UserInfoVO;
import org.springframework.web.multipart.MultipartFile;

/**
 * 用户业务服务层接口
 *
 * @author xueruichen
 * @date 2023.11.17
 */
public interface UserService {

    /**
     * 添加用户
     *
     * @param username 用户名
     * @param password 用户密码
     * @param avatar
     * @return 新增用户id
     */
    Long addUser(String username, String password, String avatar);

    /**
     * 用户登陆
     *
     * @param username 用户名
     * @param password 用户密码
     */
    void login(String username, String password);

    /**
     * 根据用户id获取用户信息
     *
     * @param id 用户id
     * @return 用户信息
     */
    UserInfoVO getUserById(Long id);

    /**
     * 用户登出
     */
    void logout();

    /**
     * 获取当前登陆的用户信息
     *
     * @return 当前登陆的用户信息
     */
    UserInfoVO getCurrentLoginUser();

    /**
     * 上传头像，返回url
     *
     * @param avatar 头像文件
     * @return 上传成功返回url，失败返回null
     */
    String uploadAvatar(MultipartFile avatar) throws Exception;
}
