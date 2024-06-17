package com.atguigu.controller;


import com.atguigu.dao.HotelDTO;
import com.atguigu.pojo.Bookings;
import com.atguigu.service.BookingsService;
import com.atguigu.utils.Result;
import com.atguigu.vo.HotelVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("hotel")
@CrossOrigin
public class HotelBookController {
    @Autowired
    private BookingsService bookingsService;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;



    // 获取订单信息的接口
    @GetMapping("/hhbookings")
    public List<Bookings> getBookingsByUserAndHotel(@RequestParam Long userId, @RequestParam Long hotelId) {
        return bookingsService.getBookingsByUserAndHotel(userId, hotelId);
    }

    /**
     *更新订单的信息
     *
     */
    @PostMapping("/updateBooking")
    public Result updateBooking(@RequestBody Bookings bookings){
        System.out.println("bookings = " + bookings);
        boolean b = bookingsService.updateBooking(bookings);
        if(b){
            return Result.ok("修改成功");
        }
        else{
            return Result.build("修改失败");
        }

    }

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
