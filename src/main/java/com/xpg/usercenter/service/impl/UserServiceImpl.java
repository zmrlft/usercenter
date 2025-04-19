package com.xpg.usercenter.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xpg.usercenter.common.BaseResponse;
import com.xpg.usercenter.common.ErrorCode;
import com.xpg.usercenter.exception.BusinessException;
import com.xpg.usercenter.model.domain.User;
import com.xpg.usercenter.service.UserService;
import com.xpg.usercenter.mapper.UserMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.xpg.usercenter.contant.UserContant.USER_LOGIN_STATE;

/**
* @author admin
* @description 针对表【user(用户表)】的数据库操作Service实现
* @createDate 2025-04-07 21:19:07
*/
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService{

    private static final String SALT = "xpg";

    /**
     * 用户登录状态键
     */


    @Override
    public long userRegister(String userAccount, String userPassword, String checkPassword, String planetCode) {
        if (StringUtils.isAnyBlank(userAccount, userPassword, checkPassword, planetCode)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);

        }

        if (userAccount.length() < 4 || userPassword.length() < 8) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"账号长度小于4或者密码长度小于8");
        }

        String validPattern = "^[a-zA-Z0-9_]{6,16}$";
        Matcher matcher = Pattern.compile(validPattern).matcher(userAccount);
        if (!matcher.find()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"账号只能包含字母、数字、下划线，且长度为6到16位");
        }

        if (!userPassword.equals(checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "两次密码不一致");
        }

        if (planetCode.length() > 5) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"星球编号长度不能大于5");
        }

        //账号不能重复
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount", userAccount);
        long count = this.count(queryWrapper);
        if (count > 0) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "账号重复");
        }

        //星球编号不能重复
        queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("planetCode", planetCode);
        count = this.count(queryWrapper);
        if (count > 0) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "星球编号重复");
        }

        //encrypt
        String encryptPassword = DigestUtils.md5DigestAsHex((userPassword + SALT).getBytes());
        System.out.println(encryptPassword);

        User user = new User();
        user.setUserAccount(userAccount);
        user.setUserPassword(encryptPassword);
        user.setPlanetCode(planetCode);
        boolean result = save(user);
        if (!result) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "注册失败");
        }

        return user.getId();
    }

    @Override
    public User userLogin(String userAccount, String userPassword, HttpServletRequest request) {

        if (StringUtils.isAnyBlank(userAccount, userPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号或者密码为空");
        }

        if (userAccount.length() < 4 || userPassword.length() < 8) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号长度小于4或者密码长度小于8");
        }


        // 账户不能包含特殊字符
        String validPattern = "[`~!@#$%^&*()+=|{}':;',\\\\[\\\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
        Matcher matcher = Pattern.compile(validPattern).matcher(userAccount);
        if (matcher.find()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号不能包含特殊字符");
        }

        String encryptPassword = DigestUtils.md5DigestAsHex((userPassword + SALT).getBytes());
        System.out.println(encryptPassword);

        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount", userAccount);
        queryWrapper.eq("userPassword", encryptPassword);
        User user = baseMapper.selectOne(queryWrapper);
        if (user == null) {
            log.warn("user login failed,userAccount:{} can not match userPassword {}", userAccount,userPassword);
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号或者密码错误");
        }

        request.getSession().setAttribute(USER_LOGIN_STATE, user);
        log.info("user login success");

        return getSafetyUser(user);
        
    }

    /**
     * 获取脱敏后的用户信息
     * @param user 用户信息
     * @return cleanUser 脱敏后的用户信息
     */
    @Override
    public User getSafetyUser(User user) {
        if (user == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        //数据脱敏
        User cleanUser = new User();
        cleanUser.setId(user.getId());
        cleanUser.setUsername(user.getUsername());
        cleanUser.setUserAccount(user.getUserAccount());
        cleanUser.setAvatarUrl(user.getAvatarUrl());
        cleanUser.setGender(user.getGender());
        cleanUser.setUserPassword("");
        cleanUser.setPhone("");
        cleanUser.setEmail("");
        cleanUser.setUserStatus(user.getUserStatus());
        cleanUser.setCreateTime(user.getCreateTime());
        cleanUser.setUserRole(user.getUserRole());
        cleanUser.setPlanetCode(user.getPlanetCode());
        return cleanUser;

    }

    /**
     * 用户退出登录
     * @param request
     * @return
     */
    @Override
    public Integer logOut(HttpServletRequest request) {
        if (request == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        request.getSession().removeAttribute(USER_LOGIN_STATE);
        return 1;
    }


}




