package com.atguigu.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;


@Data
public class RoomTypeVO {
    private Integer roomTypeId;        // 房间类型ID
    private String typeName;           // 房间类型名称
    private String description;        // 房间描述
    private BigDecimal originalprice;  // 原始价格
    private BigDecimal discountprice;  // 折扣价格
    private Integer availableRooms;    // 可用房间数量
    private String name;               // 房间名称
    private String bedtype;            // 床型
    private String breakfast;          // 早餐信息
    private Integer hotelId;           // 所属酒店ID

    private Integer version;
    private String sku;
    // 照片信息列表
    private List<HotelImageVO> images;
}
