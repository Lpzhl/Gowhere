package com.atguigu.service;

import com.atguigu.pojo.RoomTypes;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
* @author LoveF
* @description 针对表【room_types】的数据库操作Service
* @createDate 2023-11-26 14:54:11
*/
public interface RoomTypesService extends IService<RoomTypes> {

    void addRoomType(RoomTypes roomType);

    boolean updateRoomType(RoomTypes roomType);

    void deleteRoomType(int roomId);

    RoomTypes getRoomInfoById(int roomId);

    List<RoomTypes> getRoomsByHotelId(int hotelId);
}
