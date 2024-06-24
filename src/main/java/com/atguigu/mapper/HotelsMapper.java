package com.atguigu.mapper;

import com.atguigu.pojo.Hotels;
import com.atguigu.vo.HotelsVO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
* @author LoveF
* @description 针对表【hotels】的数据库操作Mapper
* @createDate 2023-11-26 15:11:43
* @Entity com.atguigu.pojo.Hotels
*/
public interface HotelsMapper extends BaseMapper<Hotels> {

    @Select("SELECT h.*, " +
            "(SELECT hi.image_url FROM hotel_images hi WHERE hi.hotel_id = h.hotel_id LIMIT 1) AS image, " +
            "COUNT(b.booking_id) AS order_count " +
            "FROM hotels h " +
            "JOIN bookings b ON h.hotel_id = b.hotel_id " +
            "GROUP BY h.hotel_id " +
            "ORDER BY order_count DESC " +
            "LIMIT 10")
    List<HotelsVO> getTopHotelsByBookings();

}




