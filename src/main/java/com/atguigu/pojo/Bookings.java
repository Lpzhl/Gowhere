package com.atguigu.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import lombok.Data;

/**
 * @TableName bookings
 */
@TableName(value ="bookings")
@Data
public class Bookings implements Serializable {
    @TableId
    private Integer bookingId;

    private Integer userId;

    private Integer hotelId;

    private String hotelName;

    private Integer roomTypeId;

    private String roomNumber;

    private Date checkInDate;

    private Date checkOutDate;

    private Integer numberOfGuests;

    private String imageUrl;        // 图片链接

    private BigDecimal totalPrice;

    private String bookingStatus;

    private String phoneNumber;

    private String identityCardNumber;

    private Date paymentTime;

    private String customerName;

    private static final long serialVersionUID = 1L;
}