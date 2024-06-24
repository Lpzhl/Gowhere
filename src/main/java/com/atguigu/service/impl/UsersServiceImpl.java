package com.atguigu.service.impl;

import com.atguigu.utils.JwtHelper;
import com.atguigu.utils.MD5Util;
import com.atguigu.utils.Result;
import com.atguigu.utils.ResultCodeEnum;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.pojo.Users;
import com.atguigu.service.UsersService;
import com.atguigu.mapper.UsersMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
* @author LoveF
* @description 针对表【users】的数据库操作Service实现
* @createDate 2023-10-28 22:18:38
*/
@Service
public class UsersServiceImpl extends ServiceImpl<UsersMapper, Users>
    implements UsersService{



    @Autowired
    private UsersMapper userMapper;

    @Autowired
    private JwtHelper jwtHelper;


    @Override
    public Result getAllUsers() {
        List<Users> users = userMapper.selectList(null);
        return Result.ok(users);
    }


    @Override
    public boolean isEmailRegistered(String email) {
        // 创建一个查询条件
        QueryWrapper<Users> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("email", email);

        // 执行查询
        int count = Math.toIntExact(baseMapper.selectCount(queryWrapper));

        // 如果 count 大于 0，表示邮箱已注册
        return count > 0;
    }

    @Override
    public Result registerUser(Users user) {
        System.out.println("user = " + user);
        // 将用户插入到数据库
        int rowsAffected = baseMapper.insert(user);

        if (rowsAffected > 0) {
            // 注册成功
            return Result.build(user, ResultCodeEnum.SUCCESS);
        } else {
            // 注册失败
            return Result.build(null, ResultCodeEnum.PASSWORD_ERROR);
        }
    }

    /**
     * 根据token 获取用户数据
     * @param token
     * @return
     */
    @Override
    public Result getUserInfo(String token) {
        //是否过期
        System.out.println("自动登录：token = " + token);
        boolean expiration = jwtHelper.isExpiration(token);
        System.out.println("expiration = " + expiration);
        if(expiration){
            //失败 ，未登录
            return Result.build(null,ResultCodeEnum.NOTLOGIN);
        }
        int userID = jwtHelper.getUserId(token).intValue();
        Users user = userMapper.selectById(userID);

        user.setPassword("");
        System.out.println("token user = " + user);
        Map data = new HashMap();
        data.put("loginUser",user);

        return Result.ok(data);
    }

    // 实现接口中的resetPassword方法
    @Override
    public Result resetPassword(String email, String encryptedPassword) {
        // 根据email查询用户
        Users user = baseMapper.selectOne(new QueryWrapper<Users>().eq("email", email));
        if (user == null) {
            // 用户不存在，返回相应的结果
            return Result.build(null,ResultCodeEnum.USERNAME_ERROR);
        } else {
            // 更新用户的密码
            user.setPassword(encryptedPassword);
            baseMapper.updateById(user);
            return Result.ok("密码重置成功");
        }
    }

    @Override
    public boolean freezeUser(int userId) {
        Users user = new Users();
        user.setIsFrozen(true);  // 设置为冻结状态

        int updateCount = userMapper.update(user, new QueryWrapper<Users>().eq("id", userId));
        return updateCount > 0;  // 如果更新的记录数大于0，则表示操作成功
    }


    @Override
    public boolean unfreezeUser(int userId) {
        Users user = new Users();
        user.setIsFrozen(false);  // 设置为非冻结状态

        int updateCount = userMapper.update(user, new QueryWrapper<Users>().eq("id", userId));
        return updateCount > 0;  // 如果更新的记录数大于0，则表示操作成功
    }



    @Override
    public Result login(Users users) {
        // 查询数据库中是否存在匹配的用户记录
        QueryWrapper<Users> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("email", users.getEmail());
        System.out.println(queryWrapper);
        Users userInDatabase = userMapper.selectOne(queryWrapper);
        System.out.println("用户："+userInDatabase);
        if (userInDatabase == null) {
            // 用户不存在
            return Result.build(null, ResultCodeEnum.USERNAME_ERROR);
        } else {
            // 验证密码
            if (userInDatabase.getPassword().equals(MD5Util.encrypt(users.getPassword()))) {
                // 密码匹配，登录成功

                //登录成功
                // 根据用户id生成 token
                String token = jwtHelper.createToken(Long.valueOf(userInDatabase.getId()));
                System.out.println("token = " + token);
                Map data = new HashMap();


                data.put("token",token);
                data.put("user",userInDatabase);

                //System.out.println("哈哈哈");
                return Result.ok(data);
                // return Result.ok("登录成功");
            } else {
                // 密码不匹配
                return Result.build(null,ResultCodeEnum.PASSWORD_ERROR);
            }
        }
    }

}




