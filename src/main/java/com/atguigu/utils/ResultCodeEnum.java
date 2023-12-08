package com.atguigu.utils;

/**
 * 统一返回结果状态信息类
 *
 */
public enum ResultCodeEnum {
    SUCCESS(200, "成功"),
    LOGIN_SUCCESS(200, "登录成功"),
    USERNAME_ERROR(501, "账号错误"),
    PASSWORD_ERROR(503, "密码错误"),
    NOTLOGIN(504, "没有登陆"),
    USERNAME_USED(505, "用户名已注册"),
    VERIFICATION_CODE_ERROR(506, "验证码发送失败"),
    VERIFICATION_CODE_MISMATCH(507, "验证码错误"),
    VERIFICATION_CODE_EXPIRED(508, "验证码已过期"),
    VERIFICATION_CODE_INVALID(509, "无效验证码"),
    KUCHUNBUZHU(500, "库存不足"),
    ACCOUNT_ALREADY_REGISTERED(510, "邮箱已经注册"),
    UN_FIND_ORDER(500, "未找到订单");

    private Integer code;
    private String message;

    private ResultCodeEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public Integer getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
