package com.atguigu.pojo;

import com.atguigu.vo.HotelImageVO;
import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

import lombok.Data;

/**
 * @TableName room_types
 */
@TableName(value ="room_types")
@Data
public class RoomTypes implements Serializable {
    @TableId(value = "room_type_id")
    private Integer roomTypeId;

    private String description;

    private BigDecimal originalprice;

    private Integer availableRooms;

    private String name;

    private String bedtype;

    private String breakfast;

    private BigDecimal discountprice;

    private Integer version;

    private Integer hotelId;

    private String sku;

    private static final long serialVersionUID = 1L;


}