package com.atguigu.service;

import com.atguigu.pojo.Users;
import com.atguigu.utils.Result;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author LoveF
* @description 针对表【users】的数据库操作Service
* @createDate 2023-10-28 22:18:38
*/
public interface UsersService extends IService<Users> {


    Result login(Users users);

    Result getAllUsers();

    boolean isEmailRegistered(String email);

    Result registerUser(Users user);

    Result getUserInfo(String token);

    Result resetPassword(String email, String encryptedPassword);

    boolean freezeUser(int userId);

    boolean unfreezeUser(int userId);
}
