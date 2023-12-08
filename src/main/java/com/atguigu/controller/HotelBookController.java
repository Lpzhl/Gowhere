package com.atguigu.controller;


import com.atguigu.dao.HotelDTO;
import com.atguigu.pojo.Bookings;
import com.atguigu.service.BookingsService;
import com.atguigu.utils.Result;
import com.atguigu.vo.HotelVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("hotel")
@CrossOrigin
public class HotelBookController {
    @Autowired
    private BookingsService bookingsService;

    /**
     * 删除订单
     */

    /**
     * 获取指定用户的订单信息
     *
     */
    @GetMapping("/getUserOrders")
    public Result getUserOrders(@RequestParam int userId){
        System.out.println("userId = " + userId);
        List<Bookings> bookings =  bookingsService.getUserOrders(userId);
        System.out.println("bookings = " + bookings);
        return Result.ok(bookings);
    }

    @GetMapping("/getOrderInfo")
    public Result getBooking(@RequestParam int bookingId){
        System.out.println("bookingId = " + bookingId);
        Result result = bookingsService.getOrderInfo(bookingId);
        System.out.println("result = " + result);
        return result;
    }

    /**
     * 处理前端发送的生成订单请求
     * 首先查看该房间是否有剩余
     *   1.如果有剩余则可以生成订单
     *        把生成的订单信息返回给前端
     *            这里会存在高并发和超脉的问题，所以我该怎么处理
     *                    1.请你帮我解决这个问题
     *   2.房间数量不够则不可以生成订单 返回库存不足给前端
     *
     *
     */
    @PostMapping("/book")
    public Result bookHotel(@RequestBody Bookings booking) {
        System.out.println("booking = " + booking);
        Result result = bookingsService.createBooking(booking);
        System.out.println("result = " + result);
        return result;
    }




    /**
     * 新增一个处理过期处理过期订单的
     */
    @PostMapping("/handleExpiredBooking")
    public Result handleExpiredBooking(@RequestParam int bookingId) {
        return bookingsService.handleExpiredBooking(bookingId);
    }

    /**
     * 处理取消订单
     */
    @PostMapping("handleOffBooking")
    public Result handleOffBooking(@RequestParam int bookingId){
        return bookingsService.handleOffBooking(bookingId);
    }
}
