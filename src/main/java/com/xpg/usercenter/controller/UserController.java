package com.xpg.usercenter.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.xpg.usercenter.common.BaseResponse;
import com.xpg.usercenter.common.ErrorCode;
import com.xpg.usercenter.common.ResultUtils;
import com.xpg.usercenter.exception.BusinessException;
import com.xpg.usercenter.model.domain.User;
import com.xpg.usercenter.model.domain.request.UserLoginRequest;
import com.xpg.usercenter.model.domain.request.UserRegisterRequest;
import com.xpg.usercenter.service.UserService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

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
    public BaseResponse<Long> userRegister(@RequestBody UserRegisterRequest userRegisterRequest) {

        if (userRegisterRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        System.out.println(userRegisterRequest);
        String userAccount = userRegisterRequest.getUserAccount();
        String userPassword = userRegisterRequest.getUserPassword();
        String checkPassword = userRegisterRequest.getCheckPassword();
        String planetCode = userRegisterRequest.getPlanetCode();
        if (StringUtils.isAnyBlank(userAccount, userPassword, checkPassword, planetCode)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        long result = userService.userRegister(userAccount, userPassword, checkPassword, planetCode);
        return ResultUtils.success(result);

    }

    @PostMapping("/login")
    public BaseResponse<User> userLogin(@RequestBody UserLoginRequest userLoginRequest, HttpServletRequest request) {

        if (userLoginRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String userAccount = userLoginRequest.getUserAccount();
        String userPassword = userLoginRequest.getUserPassword();
        if (StringUtils.isAnyBlank(userAccount, userPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = userService.userLogin(userAccount, userPassword, request);
        return ResultUtils.success(user);

    }

    @PostMapping("/logout")
    public BaseResponse<Integer> userLogout(HttpServletRequest request) {

        if (request == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Integer result = userService.logOut(request);
        return ResultUtils.success(result);

    }

    @GetMapping("/current")
    public BaseResponse<User> getCurrentUser(HttpServletRequest request) {

        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        User currentUser = (User) userObj;
        if (currentUser == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"currentUser是空的，请确认是否登录了");
        }

        Long userId = currentUser.getId();
        //todo 校验用户是否合法
        User user = userService.getById(userId);
        User safetyUser = userService.getSafetyUser(user);
        return ResultUtils.success(safetyUser);

    }

    /**
     * 用户搜索
     * @param username
     * @param request
     * @return List<User>
     */
    @GetMapping("/search")
    public BaseResponse<List<User>> searchUser(String username, HttpServletRequest request) {

        //鉴权
        if (!isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH,"没有admin权限");
            //todo 这里有问题
        }

        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        if (StringUtils.isNotBlank(username)) {
            queryWrapper.like("username",username);
        }
        List<User> userList = userService.list(queryWrapper);
        userList = userList.stream().map(user -> userService.getSafetyUser(user)).collect(Collectors.toList());
        return ResultUtils.success(userList);

    }

    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteUser(@RequestBody long id, HttpServletRequest request) {

        if (!isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH,"没有admin权限");

        }

        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        boolean b = userService.removeById(id);
        return ResultUtils.success(b);

    }

    /**
     * 根据用户属性搜索用户
     * @param user 用户对象，包含搜索条件
     * @param request HTTP请求
     * @return 符合条件的用户列表
     */
    @PostMapping("/searchByAttributes")
    public BaseResponse<List<User>> searchUserByAttributes(@RequestBody User user, HttpServletRequest request) {
        // 鉴权，只有管理员可以搜索
        if (!isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH, "没有管理员权限");
        }
        
        // 构建查询条件
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        
        // 根据用户ID查询
        if (user.getId() != -1 && user.getId() != 0) {
            queryWrapper.eq("id", user.getId());
        }
        
        // 根据用户名模糊查询
        if (StringUtils.isNotBlank(user.getUsername())) {
            queryWrapper.like("username", user.getUsername());
        }
        
        // 根据账号模糊查询
        if (StringUtils.isNotBlank(user.getUserAccount())) {
            queryWrapper.like("userAccount", user.getUserAccount());
        }
        
        // 根据性别查询
        if (user.getGender() != -1) {
            queryWrapper.eq("gender", user.getGender());
        }
        
        // 根据用户状态查询
        if (user.getUserStatus() != -1) {
            queryWrapper.eq("userStatus", user.getUserStatus());
        }
        
        // 根据用户角色查询
        if (user.getUserRole() != -1) {
            queryWrapper.eq("userRole", user.getUserRole());
        }
        
        // 根据星球编号查询
        if (StringUtils.isNotBlank(user.getPlanetCode())) {
            queryWrapper.eq("planetCode", user.getPlanetCode());
        }
        
        // 根据邮箱模糊查询
        if (StringUtils.isNotBlank(user.getEmail())) {
            queryWrapper.like("email", user.getEmail());
        }
        
        // 根据电话模糊查询
        if (StringUtils.isNotBlank(user.getPhone())) {
            queryWrapper.like("phone", user.getPhone());
        }
        
        // 执行查询
        List<User> userList = userService.list(queryWrapper);
        
        // 对查询结果进行脱敏处理
        List<User> safeUserList = userList.stream()
                .map(userService::getSafetyUser)
                .collect(Collectors.toList());
        
        return ResultUtils.success(safeUserList);
    }
    
    public boolean isAdmin(HttpServletRequest request) {
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        User user = (User) userObj;
        return user != null && user.getUserRole() == ADMIN_ROLE;
    }
}
