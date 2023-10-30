package com.atguigu.controller;


import com.atguigu.pojo.Users;
import com.atguigu.service.UsersService;
import com.atguigu.utils.EmailVca;
import com.atguigu.utils.MD5Util;
import com.atguigu.utils.Result;
import com.atguigu.utils.ResultCodeEnum;
import com.atguigu.vo.RegisterRequest;
import com.atguigu.vo.ResetPasswordRequest;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

@RestController
@RequestMapping("user")
@CrossOrigin
public class UserController {

    @Autowired
    private UsersService userService;
    @Autowired
    private EmailVca emailVca;

    /**
     * 用于验证登录
     * @param users
     * @return
     */
    @PostMapping("login")
    public Result login(@RequestBody Users users){
        System.out.println("users = " + users);
        Result result = userService.login(users);
        return result;
    }

    /**
     *  根据token获得用户信息
     */
    @GetMapping("getUserInfo")
    public Result getUserInfo(@RequestHeader String token){
        Result result = userService.getUserInfo(token);
        return result;
    }

    /**
     * 注册逻辑
     */
    @PostMapping("register")
    public Result register(@RequestBody RegisterRequest request) {
        String email = request.getEmail();
        String password = request.getPassword();
        String nickname = request.getNickname();
        System.out.println("nickname = " + nickname);
        // 对密码进行MD5加密
        String encryptedPassword = MD5Util.encrypt(password);

        // 首先验证该邮箱是否已经注册
        boolean isEmailRegistered = userService.isEmailRegistered(email);

        System.out.println("isEmailRegistered = " + isEmailRegistered);
        if (isEmailRegistered) {
            // 邮箱已经注册
            return Result.build(null,ResultCodeEnum. ACCOUNT_ALREADY_REGISTERED);
        }else{
            // 验证通过，继续注册逻辑
            Users user = new Users();
            user.setEmail(email);
            user.setPassword(encryptedPassword);
            user.setNickname(nickname);

            Result result = userService.registerUser(user);
            return result;
        }
    }

    /**
     * 重置密码逻辑
     */
    @PostMapping("resetPassword")
    public Result resetPassword(@RequestBody ResetPasswordRequest request) {
        String email = request.getEmail();
        String newPassword = request.getNewPassword();

        // 对密码进行加密，这里使用 MD5Util.encrypt 方法，你需要根据你的实际情况来实现
        String encryptedPassword = MD5Util.encrypt(newPassword);

        // 调用服务层进行密码重置
        Result result = userService.resetPassword(email, encryptedPassword);

        return result;
    }
    /**
     * 用于获取所有用户信息
     * @return
     */
    @GetMapping("all")
    public Result getAllUsers() {
        Result result = userService.getAllUsers();
        return result;
    }


    /**
     * 用于发送验证码
     */
/*    @PostMapping("/sendVerificationCode")
    public Result sendVerificationCode(@RequestParam String email, HttpSession session) {
        System.out.println("email = " + email);
        System.out.println("session = " + session);
        // 输出session的ID
        System.out.println("Session ID in sendVerificationCode: " + session.getId());
        try {
            // 生成一个随机的验证码
            String generatedCode = EmailVca.generateRandomCode();

            // 调用EmailVca发送验证码，将HttpSession传递给EmailVca
            emailVca = new EmailVca(email, generatedCode, session);
            emailVca.call(); // 发送邮件

            return Result.ok("验证码发送成功");
        } catch (Exception e) {
            e.printStackTrace();
            return Result.build(null, ResultCodeEnum.VERIFICATION_CODE_ERROR);
        }
    }*/

    @PostMapping("/sendVerificationCode")
    public Result sendVerificationCode(@RequestParam String email) {
        try {
            // 生成一个随机的验证码
            String generatedCode = EmailVca.generateRandomCode();

            // 将令牌和验证码发送邮件
            emailVca = new EmailVca(email, generatedCode);
            emailVca.call(); // 发送邮件

            // 生成一个令牌并返回给前端
            String token = EmailVca.generateToken(generatedCode);

            System.out.println("token = " + token);

            return Result.ok(token);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.build(null, ResultCodeEnum.VERIFICATION_CODE_ERROR);
        }
    }


    /**
     * 用于校验验证码是否有效
     * 1.验证前端发送过来的验证码是否与后端生成的验证码相同
     * 2.验证验证码是否过期
     */
/*    @PostMapping("verifyCode")
    public Result verifyCode(@RequestParam String code, HttpSession session) {
        // 输出session的ID
        System.out.println("Session ID in verifyCode: " + session.getId());
        // 获取后端生成的验证码和过期时间
        String generatedCode = (String) session.getAttribute("code");
        System.out.println("generatedCode = " + generatedCode);
        LocalDateTime expirationTime = (LocalDateTime) session.getAttribute("expirationTime");

        System.out.println("expirationTime = " + expirationTime);
        if (generatedCode != null && expirationTime != null) {
            // 验证前端发送的验证码是否与后端生成的验证码相同
            if (code.equals(generatedCode)) {
                // 验证验证码是否过期
                LocalDateTime currentTime = LocalDateTime.now();
                if (currentTime.isBefore(expirationTime)) {
                    return Result.ok("验证码有效");
                } else {
                    return Result.build(null, ResultCodeEnum.VERIFICATION_CODE_EXPIRED);
                }
            } else {
                return Result.build(null, ResultCodeEnum.VERIFICATION_CODE_MISMATCH);
            }
        } else {
            return Result.build(null, ResultCodeEnum.VERIFICATION_CODE_INVALID);
        }
    }*/



    @PostMapping("verifyCode")
    public Result verifyCode(@RequestParam String code, @RequestParam String token) {
        // 验证令牌的有效性
        System.out.println("token6666 = " + token);

        // 令牌有效，关联令牌与验证码
        String generatedCode = EmailVca.getCodeFromToken(token);
        System.out.println("generatedCode = " + generatedCode);
        System.out.println("code = " + code);
        LocalDateTime expirationTime = EmailVca.getExpirationTimeFromToken(token);
        System.out.println("expirationTime = " + expirationTime);

        // 继续验证验证码是否有效
        if (code.equals(generatedCode)) {
            LocalDateTime currentTime = LocalDateTime.now();
            System.out.println("currentTime = " + currentTime);

            if (currentTime.isBefore(expirationTime)) {
                return Result.ok("验证码有效");
            } else {
                return Result.build(null, ResultCodeEnum.VERIFICATION_CODE_EXPIRED);
            }
        } else {
            return Result.build(null, ResultCodeEnum.VERIFICATION_CODE_MISMATCH);
        }
    }

}
