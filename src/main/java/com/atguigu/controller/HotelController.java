package com.atguigu.controller;


import com.atguigu.dao.HotelDTO;
import com.atguigu.service.HotelsService;
import com.atguigu.utils.Result;
import com.atguigu.utils.ResultCodeEnum;
import com.atguigu.vo.HotelVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("hotel")
@CrossOrigin
public class HotelController {

    @Autowired
    private HotelsService hotelService;

    /**
     * 根据hotelId获取酒店信息
     */

    @GetMapping("/hotelId")
    public Result getHotelInfo(@RequestParam int hotelId) {
        System.out.println("hotelId = " + hotelId);
        try {
            List<HotelVO> hotelInfo = hotelService.getHotelsByHotelId(hotelId);
            return Result.ok(hotelInfo);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.build(null,ResultCodeEnum.PASSWORD_ERROR);
        }
    }



    /**
     * 酒店搜索
     * @param hotelDTO
     * @return
     */
    @PostMapping("/search")
    public Result<List<HotelVO>> searchHotels(@RequestBody HotelDTO hotelDTO) {
        System.out.println("hotelDTO = " + hotelDTO);
        List<HotelVO> hotels = hotelService.searchHotels(hotelDTO);
        System.out.println("hotels864 = " + hotels);
        return Result.ok(hotels);
    }

    /**
     * 刷新酒店详情界面
     *
     */

    @PostMapping("/update")
    public Result<List<HotelVO>> updateHotels(@RequestParam int hotelId){
        //System.out.println("hotelId = " + hotelId);

        List<HotelVO> hotels = hotelService.getHotelsByHotelId(hotelId);
        System.out.println("通过id搜索hotels = " + hotels);
        return Result.ok(hotels);
    }

}
