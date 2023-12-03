package com.atguigu.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
@Data
public class HotelVO {
    // 酒店基本信息
    private Integer hotelId;
    private String name;
    private String address;
    private String phoneNumber;
    private String description;
    private BigDecimal rating;
    private Boolean active;
    private Integer availableRooms;
    private String transport;
    private String tag;

    private BigDecimal price;
    // 房间信息列表
    private List<RoomTypeVO> roomTypes;

    // 照片信息列表
    private List<HotelImageVO> images;

}
