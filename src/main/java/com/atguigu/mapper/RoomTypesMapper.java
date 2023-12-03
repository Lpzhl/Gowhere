package com.atguigu.mapper;

import com.atguigu.pojo.RoomTypes;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
* @author LoveF
* @description 针对表【room_types】的数据库操作Mapper
* @createDate 2023-11-26 14:54:11
* @Entity com.atguigu.pojo.RoomTypes
*/

public interface RoomTypesMapper extends BaseMapper<RoomTypes> {


    RoomTypes selectById1(Integer roomTypeId);

}




