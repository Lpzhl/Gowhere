package com.atguigu.service;

import com.atguigu.pojo.Bookings;
import com.atguigu.utils.Result;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
* @author LoveF
* @description 针对表【bookings】的数据库操作Service
* @createDate 2023-12-03 14:35:31
*/
public interface BookingsService extends IService<Bookings> {

    Result createBooking(Bookings booking);

    boolean updateBookingStatus(int parseInt, String s);

    Result getOrderInfo(int  bookingId);

    Result handleExpiredBooking(int bookingId);

    Result handleOffBooking (int bookingId);

    List<Bookings> getUserOrders(int userId);
}