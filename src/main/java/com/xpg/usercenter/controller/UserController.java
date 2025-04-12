package com.xpg.usercenter.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.xpg.usercenter.model.domain.User;
import com.xpg.usercenter.model.domain.request.UserLoginRequest;
import com.xpg.usercenter.model.domain.request.UserRegisterRequest;
import com.xpg.usercenter.service.UserService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.catalina.Session;
import org.apache.catalina.Wrapper;
import org.apache.catalina.filters.ExpiresFilter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.xpg.usercenter.contant.UserContant.ADMIN_ROLE;
import static com.xpg.usercenter.contant.UserContant.USER_LOGIN_STATE;

/**
 * 用户接口
 *
 * @author xpg
 */
@RestController
@RequestMapping("/user")
public class UserController {

    @Resource
    private UserService userService;

    @PostMapping("/register")
    public Long userRegister(@RequestBody UserRegisterRequest userRegisterRequest) {

        if (userRegisterRequest == null) {
            return null;
        }
        System.out.println(userRegisterRequest);
        String userAccount = userRegisterRequest.getUserAccount();
        String userPassword = userRegisterRequest.getUserPassword();
        String checkPassword = userRegisterRequest.getCheckPassword();
        if (StringUtils.isAnyBlank(userAccount, userPassword, checkPassword)) {
            return null;
        }
        return userService.userRegister(userAccount, userPassword, checkPassword);

    }

    @PostMapping("/login")
    public User userLogin(@RequestBody UserLoginRequest userLoginRequest, HttpServletRequest request) {

        if (userLoginRequest == null) {
            return null;
        }
        String userAccount = userLoginRequest.getUserAccount();
        String userPassword = userLoginRequest.getUserPassword();
        if (StringUtils.isAnyBlank(userAccount, userPassword)) {
            return null;
        }
        return userService.userLogin(userAccount, userPassword, request);

    }

    @PostMapping("/search")
    public List<User> searchUser(@RequestBody String username, HttpServletRequest request) {

        //鉴权
        if (!isAdmin(request)) {
            return new ArrayList<>();
        }

        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        if (StringUtils.isNotBlank(username)) {
            queryWrapper.like("username",username);
        }
        List<User> userList = userService.list(queryWrapper);
        userList = userList.stream().map(user -> userService.getSafetyUser(user)).collect(Collectors.toList());
        return userList;

    }

    @PostMapping("/delete")
    public boolean deleteUser(@RequestBody long id, HttpServletRequest request) {

        if (!isAdmin(request)) {
            return false;
        }

        if (id <= 0) {
            return false;
        }

        return userService.removeById(id);

    }

    public boolean isAdmin(HttpServletRequest request) {
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        User user = (User) userObj;
        return user != null && user.getUserrole() == ADMIN_ROLE;
    }
}
