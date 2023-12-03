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

}
