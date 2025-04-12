package com.xpg.usercenter.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
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
    public long userRegister(String userAccount, String userPassword, String checkPassword) {
        if (StringUtils.isAnyBlank(userAccount, userPassword, checkPassword)) {
            return -1;
        }

        if (userAccount.length() < 4 || userPassword.length() < 8) {
            return -1;
        }

        String validPattern = "^[a-zA-Z0-9_]{6,16}$";
        Matcher matcher = Pattern.compile(validPattern).matcher(userAccount);
        if (!matcher.find()) {
            return -1;
        }

        if (userPassword == checkPassword) {
            return -1;
        }

        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount", userAccount);
        long count = this.count(queryWrapper);
        if (count > 0) {
            return -1;
        }

        //encrypt
        String encryptPassword = DigestUtils.md5DigestAsHex((userPassword + SALT).getBytes());
        System.out.println(encryptPassword);

        User user = new User();
        user.setUseraccount(userAccount);
        user.setUserpassword(encryptPassword);
        boolean result = save(user);
        if (!result) {
            return -1;
        }

        return user.getId();
    }

    @Override
    public User userLogin(String userAccount, String userPassword, HttpServletRequest request) {

        if (StringUtils.isAnyBlank(userAccount, userPassword)) {
            return null;
        }

        if (userAccount.length() < 4 || userPassword.length() < 8) {
            return null;
        }


        // 账户不能包含特殊字符
        String validPattern = "[`~!@#$%^&*()+=|{}':;',\\\\[\\\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
        Matcher matcher = Pattern.compile(validPattern).matcher(userAccount);
        if (matcher.find()) {
            return null;
        }

        String encryptPassword = DigestUtils.md5DigestAsHex((userPassword + SALT).getBytes());
        System.out.println(encryptPassword);

        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount", userAccount);
        queryWrapper.eq("userpassword", encryptPassword);
        User user = baseMapper.selectOne(queryWrapper);
        if (user == null) {
            log.warn("user login failed,userAccount:{} can not match userPassword {}", userAccount,userPassword);
            return null;
        }

        request.getSession().setAttribute(USER_LOGIN_STATE, user);
        log.info("user login success");

        return getSafetyUser(user);
        
    }

    @Override
    public User getSafetyUser(User user) {
        //数据脱敏
        User cleanUser = new User();
        cleanUser.setId(user.getId());
        cleanUser.setUsername(user.getUsername());
        cleanUser.setUseraccount(user.getUseraccount());
        cleanUser.setAvatarurl(user.getAvatarurl());
        cleanUser.setGender(user.getGender());
        cleanUser.setUserpassword("");
        cleanUser.setPhone("");
        cleanUser.setEmail("");
        cleanUser.setUserstatus(user.getUserstatus());
        cleanUser.setCreatetime(user.getCreatetime());
        cleanUser.setUserrole(user.getUserrole());
        return cleanUser;

    }


}




