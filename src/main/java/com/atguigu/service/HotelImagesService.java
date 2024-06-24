package com.atguigu.service;

import com.atguigu.pojo.HotelImages;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author LoveF
* @description 针对表【hotel_images】的数据库操作Service
* @createDate 2023-11-26 16:57:13
*/
public interface HotelImagesService extends IService<HotelImages> {

    boolean removeHotelImage(int imageId, int hotelId);
}
