package com.atguigu.service;

import com.atguigu.dao.HotelDTO;
import com.atguigu.pojo.Hotels;
import com.atguigu.vo.HotelVO;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
* @author LoveF
* @description 针对表【hotels】的数据库操作Service
* @createDate 2023-11-26 15:11:43
*/
public interface HotelsService extends IService<Hotels> {
    List<HotelVO> searchHotels(HotelDTO hotelDTO);

    List<HotelVO> getHotelsByHotelId(int hotelId);


}
