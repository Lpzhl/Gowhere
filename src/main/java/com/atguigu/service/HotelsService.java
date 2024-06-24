package com.atguigu.service;

import com.atguigu.dao.HotelDTO;
import com.atguigu.pojo.Hotels;
import com.atguigu.vo.HotelVO;
import com.atguigu.vo.HotelVO2;
import com.atguigu.vo.HotelsVO;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
* @author LoveF
* @description 针对表【hotels】的数据库操作Service
* @createDate 2023-11-26 15:11:43
*/
public interface HotelsService extends IService<Hotels> {
    List<HotelVO2> searchHotels(HotelDTO hotelDTO);

    List<HotelVO> getHotelsByHotelId(int hotelId);

    List<HotelVO2> getHotelsByOwnerId(int hotelId);

    Hotels saveOrUpdateHotel(Hotels hotel);

    boolean disableMerchant(int hotelId);

    List<Integer> getHotelIdByUserId(int userId);


    String getHotelNameById(int hotelId);

    Hotels getHotelById(int hotelId);

    List<HotelsVO> getHotelsByHot();

}
