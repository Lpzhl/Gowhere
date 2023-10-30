package com.atguigu.vo;

import lombok.Data;

@Data
public class ResetPasswordRequest {
    private String email;
    private String newPassword;
    private String verificationCode;
}
