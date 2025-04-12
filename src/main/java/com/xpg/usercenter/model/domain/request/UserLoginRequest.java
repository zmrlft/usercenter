package com.xpg.usercenter.model.domain.request;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户登录请求
 *
 * author
 */
@Data
public class UserLoginRequest implements Serializable {

    private static final long serialVersionUID = 7233161158225035806L;
    private String userAccount;
    private String userPassword;

}
