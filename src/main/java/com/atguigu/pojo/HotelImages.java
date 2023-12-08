package com.atguigu.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Data;

/**
 * @TableName hotel_images
 */
@TableName(value ="hotel_images")
@Data
public class HotelImages implements Serializable {
    @TableId
    private Integer imageId;

    private Integer hotelId;

    private Integer roomTypeId;

    private String imageUrl;

    private String description;

    private String imageType;

    private static final long serialVersionUID = 1L;
}