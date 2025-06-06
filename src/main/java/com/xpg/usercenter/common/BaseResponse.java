package com.xpg.usercenter.common;

import lombok.Data;

/**
 * 封装返回结果，通用的返回类
 *
 * @author xpg
 */
@Data
public class BaseResponse<T> {

    private int code;

    private T data;

    private String message;

    private String description;

    public BaseResponse(int code, T data, String message, String description) {
        this.code = code;
        this.data = data;
        this.message = message;
        this.description = description;
    }

    public BaseResponse(int code, T data, String message) {
        this(code, data, message,"");
    }

    public BaseResponse(int code, T data) {
        this(code, data, "","");
    }

    public BaseResponse(ErrorCode errorCode) {
        this(errorCode.getCode(), null, errorCode.getMessage(), errorCode.getDescription());
    }
}
