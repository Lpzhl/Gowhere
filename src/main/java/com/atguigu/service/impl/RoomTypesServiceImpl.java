package com.atguigu.service.impl;

import com.atguigu.mapper.HotelImagesMapper;
import com.atguigu.pojo.HotelImages;
import com.atguigu.vo.HotelImageVO;
import com.atguigu.vo.RoomTypeVO;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.pojo.RoomTypes;
import com.atguigu.service.RoomTypesService;
import com.atguigu.mapper.RoomTypesMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
* @author LoveF
* @description 针对表【room_types】的数据库操作Service实现
* @createDate 2023-11-26 14:54:11
*/
@Service
public class RoomTypesServiceImpl extends ServiceImpl<RoomTypesMapper, RoomTypes>
    implements RoomTypesService{
    @Autowired
    private RoomTypesMapper roomTypesMapper;


    @Override
    public List<RoomTypes> getRoomsByHotelId(int hotelId) {

        return roomTypesMapper.selectByHotelId1(hotelId);
    }

    @Override
    public void addRoomType(RoomTypes roomType) {
        this.save(roomType);
    }

    @Override
    public boolean updateRoomType(RoomTypes roomType) {

        boolean updated = this.updateById(roomType);
        return updated;
    }


    @Override
    public void deleteRoomType(int roomId) {
        this.removeById(roomId);
    }

    @Override
    public RoomTypes getRoomInfoById(int roomId) {

        RoomTypes roomTypes = new RoomTypes();
        try {
            roomTypes = roomTypesMapper.selectById(roomId);
        }catch (Exception e){
            e.printStackTrace();
        }
        return roomTypes;
    }




}




