package com.atguigu.vo;

import lombok.Data;

@Data
public class HotelImageVO {
    private Integer imageId;        // 图片ID
    private Integer hotelId;        // 酒店ID
    private Integer roomTypeId;     // 房间类型ID
    private String imageUrl;        // 图片链接
    private String description;     // 图片描述
    private String imageType;       // 图片类型（'cover'或'room'）

}
