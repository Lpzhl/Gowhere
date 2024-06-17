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
 * @TableName ticket_bookings
 */
@TableName(value ="ticket_bookings")
@Data
public class TicketBookings implements Serializable {
    @TableId
    private Integer bookingId;

    private Integer userId;

    private Integer flightId;

    private String identityCardNumber;

    private String phoneNumber;

    private Date bookingDate;

    private BigDecimal totalPrice;

    private String status;

    private String flightNumber;

    private String departureAirport;

    private String arrivalAirport;

    private Date departureTime;

    private Date arrivalTime;

    private String captainName;

    private String mainpassName;

    private String seatName;

    private Integer number;

    private static final long serialVersionUID = 1L;
}