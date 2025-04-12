package com.xpg.usercenter.model.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * 用户表
 * @TableName user
 */
@TableName(value ="user")
@Data
public class User {
    /**
     * id
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户昵称
     */
    private String username;

    /**
     * 账号
     */
    private String useraccount;

    /**
     * 用户头像
     */
    private String avatarurl;

    /**
     * 性别
     */
    private Integer gender;

    /**
     * 密码
     */
    private String userpassword;

    /**
     * 电话
     */
    private String phone;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 状态0正常
     */
    private Integer userstatus;

    /**
     * 创建时间
     */
    private Date createtime;

    /**
     * 
     */
    private Date updatetime;

    /**
     * 是否删除
     */
    @TableLogic
    private Integer isdelete;

    /**
     * 用户角色0普通，1管理员
     */
    private Integer userrole;
}