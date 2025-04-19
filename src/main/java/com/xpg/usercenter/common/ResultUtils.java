package com.xpg.usercenter.common;

/**
 * 返回工具类
 *
 * @author xpg
 * @create 2023-04-02 21:08
 **/
public class ResultUtils {

    /**
     * 返回成功
     *
     * @param data
     * @param <T>
     * @return
     */
    public static <T> BaseResponse<T> success(T data) {
        return new BaseResponse<>(0, data, "ok");
    }

    public static  BaseResponse error(ErrorCode errorCode){
        return new BaseResponse<>(errorCode);
    }

    public static  BaseResponse error(ErrorCode errorCode, String message, String description){
        return new BaseResponse<>(errorCode.getCode(), null, errorCode.getMessage(), errorCode.getDescription());
    }

    public static BaseResponse error(ErrorCode errorCode, String description){
        return new BaseResponse<>(errorCode.getCode(), null, errorCode.getMessage(), description);
    }

    public static  BaseResponse error(int errorCode, String message, String description){
        return new BaseResponse<>(errorCode, null, message, description);
    }


}
