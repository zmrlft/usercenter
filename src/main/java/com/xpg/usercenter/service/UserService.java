package com.xpg.usercenter.service;

import com.xpg.usercenter.model.domain.User;
import com.baomidou.mybatisplus.extension.service.IService;
import jakarta.servlet.http.HttpServletRequest;

/**
* @author admin
* @description 针对表【user(用户表)】的数据库操作Service,只是一个api
* @createDate 2025-04-07 21:19:07
*/
public interface UserService extends IService<User> {

    long userRegister(String userAccount, String userPassword, String checkPassword);

    User userLogin(String userAccount, String userPassword, HttpServletRequest request);

    User getSafetyUser(User user);
}
