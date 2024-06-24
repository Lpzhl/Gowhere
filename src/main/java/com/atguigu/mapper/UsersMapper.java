package com.atguigu.mapper;

import com.atguigu.pojo.Users;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

/**
* @author LoveF
* @description 针对表【users】的数据库操作Mapper
* @createDate 2023-10-28 22:18:38
* @Entity com.atguigu.pojo.Users
*/
public interface UsersMapper extends BaseMapper<Users> {

        @Update("UPDATE users SET nickname = #{nickname}, avatar = #{avatar} WHERE id = #{userId}")
        void updateUserProfile(@Param("userId") Long userId, @Param("nickname") String nickname, @Param("avatar") String avatar);


}






