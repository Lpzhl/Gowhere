package com.atguigu.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.pojo.HotelImages;
import com.atguigu.service.HotelImagesService;
import com.atguigu.mapper.HotelImagesMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
* @author LoveF
* @description 针对表【hotel_images】的数据库操作Service实现
* @createDate 2023-11-26 16:57:13
*/
@Service
public class HotelImagesServiceImpl extends ServiceImpl<HotelImagesMapper, HotelImages>
    implements HotelImagesService{

    @Autowired
    private HotelImagesMapper hotelImageMapper;
    @Override
    public boolean removeHotelImage(int imageId, int hotelId) {
        // 实现删除逻辑，这里的具体实现取决于您的数据库设计
        // 例如，您可以使用 MyBatis-Plus 的 remove 方法
        return hotelImageMapper.delete(new QueryWrapper<HotelImages>()
                .eq("image_id", imageId)
                .eq("hotel_id", hotelId)) > 0;
    }

}




