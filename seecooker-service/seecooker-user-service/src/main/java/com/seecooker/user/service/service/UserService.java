package com.seecooker.user.service.service;

import com.seecooker.common.core.model.dto.user.UserDTO;
import com.seecooker.user.service.pojo.vo.UserInfoVO;
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
     * @param avatar 用户头像url
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
    UserInfoVO getUserInfoById(Long id);

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
     * 获取当前登陆的用户id
     *
     * @return 当前登陆的用户id
     */
    Long getCurrentLoginUserId();

    /**
     * 上传头像，返回url
     *
     * @param avatar 头像文件
     * @return 上传成功返回url，失败返回null
     */
    String uploadAvatar(MultipartFile avatar) throws Exception;

    /**
     * 修改用户名
     *
     * @param username 原用户名
     * @param newUsername 新用户名
     */
    void modifyUsername(String username,String newUsername);
    /**
     * 修改密码
     * @param username 用户名
     * @param password 新密码
     * @param newPassword 新密码
     */
    void modifyPassword(String username,String password,String newPassword);

    /**
     * 修改头像
     * @param username 用户名
     * @param url 头像url
     */
    void modifyAvatar(String username,String url);

    /**
     * 修改用户签名
     *
     * @param signature 签名
     */
    void modifySignature(String signature);
}
