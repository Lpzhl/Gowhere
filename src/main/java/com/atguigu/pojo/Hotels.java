package com.atguigu.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.math.BigDecimal;
import lombok.Data;

/**
 * @TableName hotels
 */
@TableName(value ="hotels")
@Data
public class Hotels implements Serializable {
    @TableId
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

    private Integer ownerId;

    private Boolean merchantDisabled;

    private Boolean adminDisabled;

    private static final long serialVersionUID = 1L;
}